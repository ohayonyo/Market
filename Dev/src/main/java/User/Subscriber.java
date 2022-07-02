package User;

import DAL.Repo;
import Exceptions.*;
import Notifications.ApproveOwnerAppointmentNotification;
import Notifications.FinalAppointOwnerNotification;
import Notifications.Notification;
import Store.Store;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import Notifications.Observer;

import javax.persistence.*;
import User.Roles.Role;

@Entity
@Table(name = "subscribers")
public class Subscriber extends User implements Serializable {

    //private static int count = 0;
    @Id
    @GeneratedValue
    private int Id;
    @Id
    private String userName;
    @OneToMany(cascade = CascadeType.ALL)
    private Map<Store, Roles> roles;
    @ElementCollection
    private final Map<Notification, Boolean> notifications = new HashMap<>();
    @Transient
    private Observer observer;
    @Transient
    private Observer adminObserver;
    @Transient
    private Map<Store,LinkedHashMap <Subscriber,Boolean>> ownersAppointmentApproval;


    public Subscriber() {
    }

    public Subscriber(String userName) {
        //this.Id = ++count;
        this.userName = userName;
        this.roles = new ConcurrentHashMap<>();
        ownersAppointmentApproval = null;
//        try {
//            Repo.persist(this);
//        } catch (Exception e) {
//            System.out.println(e);
//        }

    }


    public Subscriber(ConcurrentHashMap<Store, ShoppingBag> cart, String userName, ConcurrentHashMap<Store, Roles> roles) {
        super(cart);
        this.userName = userName;
        this.roles = roles;
        ownersAppointmentApproval = null;
        try {
            Repo.merge(this);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void setAdminObserver(Observer observer) {
        this.adminObserver = observer;
    }

    public void notifyVisitors(Notification notification) {
        if(adminObserver != null) {
            adminObserver.notify(notification);
        }
    }

    public boolean isStoreOwnerOrFounder(Store store) {
        Roles roles = getRoles().get(store);
        if(roles == null)
            return false;
        return roles.contains(Roles.Role.STORE_OWNER) || roles.contains(Roles.Role.STORE_FOUNDER);
    }

    public boolean isRegularSubscriber() {
        return this.roles.isEmpty();
    }

    public void removeRole(Store store, Roles.Role toRemove) throws LostConnectionException {
        if(this.getRoles() != null && this.getRoles().get(store) != null) {
            if(toRemove == Role.STORE_OWNER || toRemove == Role.STORE_MANAGER)
            {
                this.getRoles().clear();
            }
            else {
                this.getRoles().get(store).remove(toRemove);
                Repo.merge(this.roles.get(store));
            }
        }
    }

    public void addStoreFounder(Store store) throws LostConnectionException {
        Roles rolesList = new Roles();
        rolesList.add(Roles.Role.STORE_FOUNDER);
        rolesList.add(Roles.Role.STORE_OWNER);
        rolesList.add(Roles.Role.STORE_MANAGER);
        rolesList.add(Roles.Role.INVENTORY_MANAGEMENT);
        rolesList.add(Roles.Role.STORE_POLICY);
        rolesList.add(Role.STORE_BID);
        this.roles.put(store, rolesList);
        Repo.persist(rolesList);
        store.subscribe(this);
        store.addFounderToOwnersManagers(this);
    }

    public boolean canBeAppointedToOwner(Store store){
        if(ownersAppointmentApproval==null || !ownersAppointmentApproval.containsKey(store))
            return false;
        List<Boolean> list = new ArrayList<Boolean>(ownersAppointmentApproval.get(store).values());
        if(list.contains(false))
            return false;
        return true;
    }
    public void addOwnerApproval(Store store, Subscriber owner) throws NotOwnerException, AllTheOwnersNeedToApproveAppointement, AlreadyOwnerException, LostConnectionException {
        List<Subscriber> owners = store.getOwnersList();
        if(!owners.contains(owner))
            throw new NotOwnerException();
        LinkedHashMap<Subscriber,Boolean> map = new LinkedHashMap<Subscriber, Boolean>();
        if(ownersAppointmentApproval==null) {
            ownersAppointmentApproval = new HashMap<Store, LinkedHashMap<Subscriber, Boolean>>();
        }
        if(ownersAppointmentApproval.get(store)==null){
            map.put(owner,true);
            owners.remove(owner);
            for(Subscriber sub : owners)
                map.put(sub,false);
            ownersAppointmentApproval.put(store,map);
        }
        else ownersAppointmentApproval.get(store).put(owner,true);
        ApproveOwnerAppointmentNotification notification = new ApproveOwnerAppointmentNotification(store.getStoreId(),owner.userName,userName);
        Repo.persist(notification);
        store.notifyApproveOwnerNotification(notification);
        if(canBeAppointedToOwner(store)) {
            FinalAppointOwnerNotification finalAppointOwnerNotification = new FinalAppointOwnerNotification(store.getStoreId(),userName);
            Repo.persist(finalAppointOwnerNotification);
            store.notifyFinalOwnerApproval(finalAppointOwnerNotification);
            addStoreOwner(store, (Subscriber) ownersAppointmentApproval.get(store).keySet().toArray()[0]);
            store.addOwner((Subscriber) ownersAppointmentApproval.get(store).keySet().toArray()[0], this);
        }
    }

    public void addStoreOwner(Store store, Subscriber owner) throws AllTheOwnersNeedToApproveAppointement, NotOwnerException, LostConnectionException {
        if(!canBeAppointedToOwner(store))
            throw new AllTheOwnersNeedToApproveAppointement();
        if (this.roles.containsKey(store)) {
            this.roles.get(store).add(Roles.Role.STORE_OWNER);
            this.roles.get(store).add(Roles.Role.STORE_MANAGER);
            this.roles.get(store).add(Roles.Role.INVENTORY_MANAGEMENT);
            this.roles.get(store).add(Roles.Role.STORE_POLICY);
            this.roles.get(store).add(Roles.Role.STORE_BID);
            Repo.merge(this.roles.get(store));
        } else {
            Roles rolesList = new Roles();
            rolesList.add(Roles.Role.STORE_OWNER);
            rolesList.add(Roles.Role.STORE_MANAGER);
            rolesList.add(Roles.Role.INVENTORY_MANAGEMENT);
            rolesList.add(Roles.Role.STORE_POLICY);
            rolesList.add(Roles.Role.STORE_BID);
            Repo.persist(rolesList);
            this.roles.put(store, rolesList);
        }
    }

    public void addStoreManager(Store store) throws LostConnectionException {
        if (this.roles.containsKey(store)) {
            Roles roles = this.roles.get(store);
            roles.add(Roles.Role.STORE_MANAGER);
            Repo.merge(this.roles.get(store));
        } else {
            Roles rolesList = new Roles();
            rolesList.add(Roles.Role.STORE_MANAGER);
            Repo.persist(rolesList);
            this.roles.put(store, rolesList);
        }
    }

    public void isStoreFounder(Store store) throws NotFounderException {
        if (!this.roles.containsKey(store)) {
            throw new NotFounderException();
        }
        if (!this.roles.get(store).contains(Roles.Role.STORE_FOUNDER))
            throw new NotFounderException();
    }

    public void isStoreOwner(Store store) throws NotOwnerException {
        if (!this.roles.containsKey(store)) {
            throw new NotOwnerException();
        }
        if (!this.roles.get(store).contains(Roles.Role.STORE_OWNER))
            throw new NotOwnerException();
    }

    public boolean isManagerBidPermission(Store store) {
        if (!this.roles.containsKey(store) || !this.roles.get(store).contains(Roles.Role.STORE_BID))
            return false;
        return true;
    }

    public void isStoreInventoryManager(Store store) throws NotInventoryManagementException {
        if (!this.roles.containsKey(store)) {
            throw new NotInventoryManagementException(this);
        }
        if (!this.roles.get(store).contains(Roles.Role.INVENTORY_MANAGEMENT))
            throw new NotInventoryManagementException(this);
    }

    public void isStorePolicyManager(Store store) throws NotStorePolicyException {
        if (!this.roles.containsKey(store))
            throw new NotStorePolicyException(this);

        if (!this.roles.get(store).contains(Roles.Role.STORE_POLICY))
            throw new NotStorePolicyException(this);
    }

    public void isAlreadyStoreOwner(Store store) throws AlreadyOwnerException {
        if (this.roles.containsKey(store)) {
            if (this.roles.get(store).contains(Roles.Role.STORE_OWNER))
                throw new AlreadyOwnerException(userName);
        }
    }
//
    public void isStoreManager(Store store) throws NotManagerException {
        if (!this.roles.containsKey(store))
            throw new NotManagerException();
        if (!this.roles.get(store).contains(Roles.Role.STORE_MANAGER))
            throw new NotManagerException();
    }

    public void isAlreadyStoreManager(Store store) throws AlreadyManagerException {
        if (this.roles.containsKey(store)) {
            if (this.roles.get(store).contains(Roles.Role.STORE_MANAGER))
                throw new AlreadyManagerException(userName);
        }
    }

    public LinkedList<Store> getOwnerStores() {
        LinkedList<Store> stores = new LinkedList<>();
        for (Map.Entry<Store, Roles> entry : roles.entrySet()) {
            if (entry.getValue().contains(Roles.Role.STORE_OWNER))
                stores.add(entry.getKey());
        }
        return stores;
    }

    public void moveCartFromUserToSubscriber(User dest) throws LostConnectionException {
        if (cart.isEmpty()) {
            if(!dest.cart.isEmpty()) {
                for (Map.Entry<Store, ShoppingBag> sh : dest.cart.entrySet()) {
                    ShoppingBag temp = new ShoppingBag(sh.getKey(), this, sh.getValue().getProducts());
                    Repo.persist(temp);
                    cart.put(sh.getKey(), temp);
                }
            }
        }
    }

    public String getUserName() {
        return userName;
    }


    public Map<Store, Roles> getRoles() {
        return roles;
    }

    public void emptyCartAfterPurchase() {
        this.cart.clear();
    }

    public Notification notifyNotification(Notification notification) throws LostConnectionException {
        if (observer != null) {
            observer.notify(notification);
            this.notifications.put(notification, true);
        } else
            this.notifications.put(notification, false);
        Repo.merge(this);
        return notification;
    }

    public Collection<Notification> checkPendingNotifications() {
        Collection<Notification> collection = new LinkedList<>();
        for (Notification n : this.notifications.keySet()) {
            if (this.notifications.get(n) == false) {
                collection.add(n);
                this.notifications.put(n, true);
            }
        }
        return collection;
    }

//    public void approveBidByOwner(Store store, int bidId) throws NotOwnerException, BidException {
//        Bid bid = store.getBidByID(bidId);
//        bid.addOwnerApproval(this);
//        if (bid.isApproved()) {
//            store.notifyApprovedBid(bid);
//            Product oldProduct = bid.getProduct();
//            Product newProduct = new Product(oldProduct);
//            bid.getSubscriber().getCart().get(store).deleteProduct(bid.getProduct());
//            bid.getSubscriber().getCart().get(store).addSingleProduct(newProduct,bid.getAmount());
//        }
//
//    }

    public String toString(){
        return "userName: " + userName + ", userId: " + Id + "\n";
    }


    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    public void addPermissionToStore(Store store, Role role) throws LostConnectionException {
        if (this.roles.containsKey(store)) {
            this.roles.get(store).add(role);
            Repo.merge(this.roles.get(store));
        } else {
            Roles rolesList = new Roles();
            rolesList.add(role);
            Repo.persist(rolesList);
            this.roles.put(store, rolesList);
        }
    }

    public void hasPermissionAtStore(Store store,Role role) throws NoPermissionException {
        if (!this.roles.containsKey(store)) {
            throw new NoPermissionException(this,role);
        }
        if (!this.roles.get(store).contains(role))
            throw new NoPermissionException(this,role);
    }
}

