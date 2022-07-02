package Store;

import DAL.Repo;
import Exceptions.*;
import Notifications.ApproveOwnerAppointmentNotification;
import Notifications.FinalAppointOwnerNotification;
import SpellChecker.Spelling;
import Store.Bid.Bid;
import Store.Discount.*;
import Store.PolicyLimitation.CompoundBuyingPolicy;
import Store.PolicyLimitation.IBuyingPolicy;
import Store.PolicyLimitation.LogicOperators.AndCondition;
import Store.PolicyLimitation.LogicOperators.ConditioningLimitation;
import Store.PolicyLimitation.LogicOperators.OrCondition;
import Store.PolicyLimitation.LogicOperators.XorCondition;
import Store.PolicyLimitation.SimpleLimitationPolicy.NoLimitationPolicy;
import User.*;
import Notifications.Observable;

import javax.persistence.*;
import java.util.*;


@Entity
public class Store {

    private String storeName;
    @Transient
    private static int idCounter = 0;
    @Id
    private int storeId;
//    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "store_managers_by_mapping",
            joinColumns = {@JoinColumn(name = "store_id", referencedColumnName = "storeId")},
            inverseJoinColumns = {@JoinColumn(name = "subscriber_list_id", referencedColumnName = "subscriber_appointments_id")})
    @MapKeyJoinColumn(name = "subscriber_id")
    @MapKeyJoinColumn(name = "subscriber_name")
    private Map<Subscriber,SubscriberList> managersBy;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "store_owners_by_mapping",
            joinColumns = {@JoinColumn(name = "store_id", referencedColumnName = "storeId")},
            inverseJoinColumns = {@JoinColumn(name = "subscriber_list_id", referencedColumnName = "subscriber_appointments_id")})
    @MapKeyJoinColumn(name = "subscriber_id")
    @MapKeyJoinColumn(name = "subscriber_name")
    private Map<Subscriber,SubscriberList> ownersBy;

    @ElementCollection
    @MapKeyJoinColumns({
//            @MapKeyJoinColumn(name="productId"),
            @MapKeyJoinColumn(name="amount")
    })
    private Map<Product,Integer> products;
    @ElementCollection
    private List<String> history;
    @OneToMany
    private List<Bid> bids;

    @OneToOne
    private Observable observable;
    @OneToOne
    private IBuyingPolicy storePolicy;
    @OneToOne
    private IDiscountPolicy storeDiscountPolicy;

    public Store(String storeName) throws IllegalNameException, IllegalDiscountException, LostConnectionException {
        if(storeName == null || storeName.equals("") || storeName.trim().isEmpty() ||
           storeName.charAt(0) >= '0' && storeName.charAt(0) <= '9')
        {
            throw new IllegalNameException(storeName);
        }
        this.storeName = storeName;
        this.storeId = ++idCounter;
        this.isActive = true;
        this.managersBy = new HashMap<>();
        this.ownersBy = new HashMap<>();
        this.products = Collections.synchronizedMap(new HashMap<Product, Integer>());
        this.history = new LinkedList<>();
        this.observable = new Observable();
        Repo.persist(this.observable);
        storePolicy=new NoLimitationPolicy();
        Repo.persist(this.storePolicy);
        storeDiscountPolicy = new NoDiscountPolicy();
        bids = new LinkedList<>();
        Repo.persist(storeDiscountPolicy);
    }

    public Store() {

    }

    public void setIdCounter() {
        Store.idCounter++;
    }

    public Observable getObservable() {
        return observable;
    }

    public IBuyingPolicy getPolicy(){
        return storePolicy;
    }

    public IDiscountPolicy getStoreDiscount(){
        return storeDiscountPolicy;
    }

    public void setStoreDiscount(IDiscountPolicy storeDiscountPolicy){
        this.storeDiscountPolicy=storeDiscountPolicy;
    }

    public List<IBuyingPolicy> getPolicies(){
        List<IBuyingPolicy> policies = new ArrayList<>();
        if(storePolicy==null){
            return null;
        }else if(!(storePolicy instanceof CompoundBuyingPolicy)){
            policies.add(storePolicy);
            return policies;
        }else
            return storePolicy.getPolicies();
    }



    public List<Integer> getPoliciesByID(){
        List<Integer> res = new LinkedList<>();
        List<IBuyingPolicy> policies = getPolicies();
        if(policies == null)
            return new LinkedList<>();
        for(IBuyingPolicy policy : policies)
            res.add(policy.getPolicyID());
        return res;
    }

    public List<IDiscountPolicy> getDiscounts(){
        List<IDiscountPolicy> discounts = new ArrayList<>();
        if(storeDiscountPolicy==null){
            return null;
        }else if(!(storeDiscountPolicy instanceof CompoundDiscountPolicy)){
            discounts.add(storeDiscountPolicy);
            return discounts;
        }else
            return storeDiscountPolicy.getDiscountPolicies();
    }


    public Collection<Integer> getDiscountsByID() {
        List<Integer> res = new LinkedList<>();
        List<IDiscountPolicy> discounts=getDiscounts();
        if(discounts == null)
            return new LinkedList<>();
        for(IDiscountPolicy discountPolicy : discounts)
            res.add(discountPolicy.getDiscountID());
        return res;
    }

    public Collection<String> getDiscountsByIDLeafs() {
        int counter=1;
        List<String> res = new LinkedList<>();
        if(storeDiscountPolicy==null || (storeDiscountPolicy instanceof NoDiscountPolicy))
            return res;
        else if(storeDiscountPolicy instanceof MaxDiscountPolicy){
            res.add("id: " + storeDiscountPolicy.getDiscountID() + ", type: "+storeDiscountPolicy.getTypeName());
            counter++;
        }else if(storeDiscountPolicy instanceof AddDiscountPolicy){
            List<IDiscountPolicy> children = storeDiscountPolicy.getDiscountPolicies();
            if(children!=null) {
                for (IDiscountPolicy discountPolicy : children) {
                    res.add("id: " + discountPolicy.getDiscountID() + ", type: " + discountPolicy.getTypeName());
                    counter++;
                }
            }
        }else
            res.add("id: " + storeDiscountPolicy.getDiscountID() + ", type: "+storeDiscountPolicy.getTypeName());
        return res;
    }

    public Collection<String> getPoliciesByIDLeafs(){
        int counter=1;
        List<String> res = new LinkedList<>();
        if(storePolicy==null || (storePolicy instanceof NoLimitationPolicy))
            return res;
        else if((storePolicy instanceof OrCondition) || (storePolicy instanceof XorCondition) || (storePolicy instanceof ConditioningLimitation)){
            res.add("id: " + storePolicy.getPolicyID() + ", type: "+storePolicy.getTypeName());
            counter++;
        }else if(storePolicy instanceof AndCondition){
            List<IBuyingPolicy> children = storePolicy.getPolicies();
            if(children!=null) {
                for (IBuyingPolicy policy : children) {
                    res.add("id: " + policy.getPolicyID() + ", type: " + policy.getTypeName());
                    counter++;
                }
            }
        }else
            res.add("id: " + storePolicy.getPolicyID() + ", type: "+storePolicy.getTypeName());
        return res;

//        List<Integer> res = new LinkedList<>();
//        List<IBuyingPolicy> buyingPolicies=getPolicies();
//        if(buyingPolicies == null)
//            return new LinkedList<>();
//        for(IBuyingPolicy buyingPolicy : buyingPolicies)
//            if(!(buyingPolicy instanceof NoLimitationPolicy))
//                res.add(buyingPolicy.getPolicyID());
//        return res;
    }

    public Collection<Integer> getDiscountsIDOfStore() {
        List<Integer> res = new LinkedList<>();
        if(storeDiscountPolicy instanceof CompoundDiscountPolicy){
            List<IDiscountPolicy> discountsList = storeDiscountPolicy.getDiscountPolicies();
            for(IDiscountPolicy discount : discountsList)
                res.add(discount.getDiscountID());
        }
        else
            res.add(storeDiscountPolicy.getDiscountID());

        return res;
    }


    public void validateInventory(Map<Product, Integer> bag) throws InsufficientInventory, ProductNotFoundException
    {
        synchronized (products) {
            for (Map.Entry<Product, Integer> entry : bag.entrySet()) {
                if (!products.containsKey(entry.getKey())) {
                    if(entry.getKey().getSubscriberForBid()==null)
                         throw new ProductNotFoundException(entry.getKey().getProductId(), getStoreId());
                }
                if(entry.getKey().getSubscriberForBid()==null)
                     if (entry.getValue() > products.get(entry.getKey()))
                        throw new InsufficientInventory(entry.getKey().getName(), getStoreName());
            }
        }
    }

    public void updateInventoryAfterPurchase(ShoppingBag shoppingBag) throws ProductNotFoundException, InsufficientInventory, LostConnectionException {
        Map<Product,Integer> sold = shoppingBag.getProducts();
        for (Product p : sold.keySet())
                if(!products.containsKey(p))
                     throw new ProductNotFoundException(p.getProductId(), getStoreId());
                else if(products.get(p) < sold.get(p))
                    throw new InsufficientInventory(getStoreName(), p.getName());
                else {
                    int newAmount = products.get(p) - sold.get(p);
                    products.replace(p , products.get(p) , newAmount);
                }
        observable.notifyPurchase(this, shoppingBag.getProducts());
        history.add(shoppingBag.toString());
    }

    public void closeStore() throws StoreNotActiveException, LostConnectionException {
        if(!isActive)
            throw new StoreNotActiveException(storeId);
        isActive = false;
        observable.notifyStoreClosed(storeId);
    }

    public void openStore() throws StoreIsAlreadyActiveException {
        if(isActive)
            throw new StoreIsAlreadyActiveException(storeId);
        isActive = true; 
    }

    public void setStorePolicy(IBuyingPolicy storePolicy){
        this.storePolicy = storePolicy;
    }

    public void addManager(Subscriber owner,Subscriber user) throws AlreadyManagerException, LostConnectionException {
//        user.addStoreManager(this);
        if(managersBy.containsKey(user))
            throw new AlreadyManagerException(user.getUserName());
        SubscriberList appointedUsers=managersBy.get(owner);
        if(appointedUsers==null) {
            SubscriberList newManagers=new SubscriberList();
            newManagers.add(user);
            Repo.persist(newManagers);
            managersBy.put(owner,newManagers);
        }else {
            appointedUsers.add(user);
            Repo.merge(appointedUsers);
        }
        SubscriberList lst = new SubscriberList();
        Repo.persist(lst);
        managersBy.put(user,lst);
        notifyNewRole(owner, user, "Manager");
    }

    public void addOwner(Subscriber owner,Subscriber user) throws AlreadyOwnerException, NotOwnerException, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
//        user.addStoreOwner(this);
        if(ownersBy.containsKey(user))
            throw new AlreadyOwnerException(user.getUserName());
        SubscriberList appointedUsers= ownersBy.get(owner);
        if(appointedUsers==null) {
            SubscriberList newOwners=new SubscriberList();
            newOwners.add(user);
            Repo.persist(newOwners);
            ownersBy.put(owner,newOwners);
        }else {
            appointedUsers.add(user);
            Repo.merge(appointedUsers);
        }
        SubscriberList forUser = new SubscriberList();
        Repo.persist(forUser);
        ownersBy.put(user, forUser);
        notifyNewRole(owner, user, "Owner");
    }

    public void removeManager(Subscriber owner,Subscriber needToRemoveFromManagement) throws NoApointedUsersException, LostConnectionException {
        if(!managersBy.containsKey(owner))
            throw new NoApointedUsersException("",storeName);

        SubscriberList appointedUsers = managersBy.get(owner);
        if (appointedUsers == null || !appointedUsers.contains(needToRemoveFromManagement))
            throw new NoApointedUsersException("", getStoreName());

        appointedUsers.remove(needToRemoveFromManagement);
        if(managersBy.containsKey(needToRemoveFromManagement))
            managersBy.remove(needToRemoveFromManagement);
        observable.notifyRemoveRole(owner, needToRemoveFromManagement, storeId, "manager");
    }


    public void removeOwner(Subscriber owner1, Subscriber owner) throws LostConnectionException {
        SubscriberList managersToRemove = managersBy.get(owner);
        if (managersToRemove != null)
            for (Subscriber manager : managersToRemove.getSubscribers()) {
                try {
                    removeManager(owner, manager);
                }catch(NoApointedUsersException e){

                }
            }

        SubscriberList ownersToRemove = ownersBy.get(owner);
        ownersBy.remove(owner);
        if (ownersToRemove != null &&!ownersToRemove.isEmpty())
            for (Subscriber u : ownersToRemove.getSubscribers()) {
                removeOwner(owner, u);
                //-----------------------------------
                u.removeRole(this, Roles.Role.STORE_OWNER);
                observable.notifyRemoveRole(owner, u, storeId, "owner");
            }
        SubscriberList ownersToRemove1 = ownersBy.get(owner1);
        if (ownersToRemove1 != null &&!ownersToRemove1.isEmpty())
            for (Subscriber u : ownersToRemove1.getSubscribers()) {
                if(u == owner) {
                    ownersToRemove1.remove(u);
                    //----------------------------------------
                    u.removeRole(this, Roles.Role.STORE_OWNER);
                    observable.notifyRemoveRole(owner1, u, storeId, "owner");
                }
            }
    }

    public void addProduct(Product product, int amount) {
        synchronized (products) {
            for(Map.Entry<Product,Integer> entry : products.entrySet()){
                Product p = entry.getKey();
                int oldAmount = entry.getValue();
                if (p.areSameProducts(product)) {
                    products.replace(p, oldAmount, oldAmount + amount);
                    return;
                }
            }
            products.put(product,amount);
        }
    }

    public void setProducts(Map<Product,Integer>products){
        this.products=products;
    }


    public Product findProductByID(int ID) {
        if(products!=null) {
            synchronized (products){
                for (Product p : products.keySet().stream().toList()) {
                    if (p.getProductId() == ID)
                        return p;
                }
            }
        }
        return null;
    }

    //check if user is appointed by owner
    public boolean isOwnerBy(Subscriber owner,Subscriber user){
        if (ownersBy.get(owner) != null)
            return ownersBy.get(owner).contains(user);
        return false;
    }

    public boolean isManagerBy(Subscriber manager,Subscriber user){
        if (managersBy.get(manager) != null)
            return managersBy.get(manager).contains(user);
        return false;
    }

    public boolean areSameProducts(List<Product>products1,List<Product>products2) {
        if(products1==null&&products2==null)
            return true;
        else if(products1==null||products2==null)
            return false;
        for(Product p :products1){
            if(!products2.contains(p))
                return false;
        }
        return true;
    }

    public List<Product> findProductByName(String name, Spelling spelling) {
        String source=name;
        name = spelling.correct(name);
        List<Product> res =new LinkedList<Product>();
        if(products!=null)
           for (Product p : products.keySet().stream().toList()) {
               String productName = p.getName();
              if(productName.equalsIgnoreCase(name)||productName.equalsIgnoreCase(source))
                 res.add(p);
           }
        if(res.isEmpty())
            return new LinkedList<Product>();
        return res;
    }

    public List<Product> findProductsByCategory(String category, Spelling spelling) {
        String source = category;
        category = spelling.correct(category);
        List<Product> ans = new LinkedList<>();
        if(products!=null)
            for (Product p : products.keySet().stream().toList()) {
                String productCategory = p.getCategory();
                if(productCategory.equalsIgnoreCase(category)||productCategory.equalsIgnoreCase(source))
                  ans.add(p);
            }
        if(ans.isEmpty())
            return new LinkedList<>();

        return ans;
    }
    public List<Product> findProductsByKeyWords(String keyWords, Spelling spelling) {
        String source = keyWords;
        keyWords = spelling.correct(keyWords);
        List<Product> ans = new LinkedList<Product>();
        String description = "";
        if(products!=null)
            for (Product p : products.keySet().stream().toList()) {
                String productName = p.getName();
                description = productName.toLowerCase() + " " + p.getDescription().toLowerCase()+ " " + p.getCategory().toLowerCase();
                if(description.contains(keyWords.toLowerCase()) || description.contains(source.toLowerCase()))
                    ans.add(p);
            }
      
        if(ans.isEmpty())
            return new LinkedList<Product>();
        return ans;
    }

    public int getStoreId() {
        return storeId;
    }

    public boolean isStroeActivated() throws StoreNotActiveException {
        if(!isActive){
            throw new StoreNotActiveException(storeId);
        }
        return true;
    }

    public boolean isActivate()  {
        return isActive;
    }


    public void setActive(boolean active) {
        isActive = active;
    }

    public Map<Subscriber, SubscriberList> getManagersBy() {
        return managersBy;
    }

    public Map<Subscriber, SubscriberList> getOwnersBy() {
        return ownersBy;
    }

    public String getStoreName() {
        return storeName;
    }

    public Set<Product> getProducts() {
        return products.keySet();
    }

    public Map<Product,Integer> getProductsAndAmount(){ return products;}

    public Map<Product,Integer> getProductsHashMap(){
        return products;
    }

    public Product getProductByID(int productID) throws ProductNotFoundException
    {
        for (Map.Entry<Product, Integer> entry : products.entrySet())
        {
            if(entry.getKey().getProductId() == productID)
                return entry.getKey();
        }
        throw new ProductNotFoundException(productID, this.storeId);
    }

    public List<String> getHistory()
    {
        return this.history;
    }

    public void removeProduct(int productID) throws ProductNotFoundException {
        for (Map.Entry<Product, Integer> entry : products.entrySet())
        {
            synchronized(this) {
                if (entry.getKey().getProductId() == productID) {
                    products.remove(entry.getKey());
                    return;
                }
            }
        }
        throw new ProductNotFoundException(productID, this.storeId);
    }

    public void updateProduct(int productID, String newCategory, String description, Integer newAmount, Double newPrice) throws ProductNotFoundException, IllegalNameException, IllegalPriceException, InsufficientInventory, LostConnectionException {
        for (Map.Entry<Product, Integer> entry : products.entrySet())
        {
            if(entry.getKey().getProductId() == productID)
            {
                synchronized(this) {
                    entry.getKey().setCategory(newCategory);
                    entry.getKey().setDescription(description);
                    entry.getKey().setPrice(newPrice);
                    if (newAmount < 0)
                        throw new InsufficientInventory(this.storeName, entry.getKey().getName());
                    entry.setValue(newAmount);
                }
                Repo.merge(entry.getKey());
                return;
            }
        }
        throw new ProductNotFoundException(productID, this.storeId);
    }

    public void backProducts(Map<Product, Integer> products) {
        for (Map.Entry<Product, Integer> entry: products.entrySet()) {
            this.products.replace(entry.getKey(), this.products.get(entry.getKey()) + entry.getValue());
        }
        unlockItems(products.keySet());
    }

    public void unlockItems(Set<Product> products) {
        for (Product product: products) {
            product.unlock();
        }
    }

    public void notifyNewRole(Subscriber subscriber, Subscriber value, String role) throws LostConnectionException {
        observable.notifyNewRole(subscriber, value, storeId, role);
    }

    public void notifyRemoveRole(Subscriber subscriber, Subscriber value, String role) throws LostConnectionException {
        observable.notifyRemoveRole(subscriber, value, storeId, role);
    }

    public void subscribe(Subscriber subscriber) throws LostConnectionException {
        observable.subscribe(subscriber);
    }

    public void unsubscribe(Subscriber subscriber) throws LostConnectionException {
        observable.unsubscribe(subscriber);
    }

    public int addBid(Subscriber sub, Product product,int amount, double price) throws ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, LostConnectionException {
        if(sub.getCart().get(this)==null || !sub.getCart().get(this).getProducts().containsKey(product))
            throw new  ProductShouldBeInCartBeforeBidOnIt();
        if( products == null || products.isEmpty() || !products.containsKey(product))
            throw new  ProductNotFoundException(product.getProductId(),this.storeId);
        Bid newBid = new Bid(sub,product, amount, price, this);
        Repo.persist(newBid);
        bids.add(newBid);
        Repo.merge(this);
        observable.notifyNewBid(newBid);
        return newBid.getBidId();
    }

    public void removeBid(Bid bid){
        bids.remove(bid);
    }

    public Bid getBidByID(int bidID) {
        for(Bid b : bids) {
            if (b.getBidId() == bidID)
                return b;
            for(Bid bid :b.getOwnerCounteredProposal().values())
                if (bid.getBidId() == bidID)
                    return bid;
        }
        return null;
    }


    public List<Bid> getBids(){
        return bids;
    }

    public List<String> getBidsString() {
        LinkedList<String> bidsList = new LinkedList<>();
        for (Bid b : bids) {
            bidsList.add(b.toString());
            for (Bid bid : b.getOwnerCounteredProposal().values() )
                bidsList.add(bid.toString());
        }
        return bidsList;
    }

    public List<String> getBidsForAddCounteredString() {
        LinkedList<String> bidsList = new LinkedList<>();
        for (Bid b : bids) {
                if(!b.isApproved())
                    bidsList.add(b.toString());
//            for (Bid bid : b.getOwnerCounteredProposal().values() )
//                if(bid.getProduct().getSubscriberForBid()!=null)
//                     bidsList.add(bid.toString());
        }
        return bidsList;
    }

    public List<String> getBidsForAddNormalBidString() {
        LinkedList<String> bidsList = new LinkedList<>();
        for (Bid b : bids) {
            if(b.getProduct().getSubscriberForBid()==null)
                bidsList.add(b.toString());
            for (Bid bid : b.getOwnerCounteredProposal().values() )
                if(bid.getProduct().getSubscriberForBid()==null)
                    bidsList.add(bid.toString());
        }
        return bidsList;
    }

    public void addFounderToOwnersManagers(Subscriber subscriber) {
        ownersBy.put(subscriber,new SubscriberList());
        managersBy.put(subscriber,new SubscriberList());
    }

    public void notifyNewBid(Bid bid) throws LostConnectionException {
        observable.notifyNewBid(bid); }

    public void notifyApprovedBid(Bid bid) throws LostConnectionException {
        observable.notifyApprovedBid(bid); }

    public void notifyRejectedBid(Bid bid) throws LostConnectionException {
        observable.notifyDeclinedBid(bid); }

    public void notifyCounterOffer(Bid bid) throws LostConnectionException {
        observable.notifyCounterBid(bid);
    }

    public Set<Subscriber> getStoreOwners(){
        return getOwnersBy().keySet();

    }

    public List<Subscriber> getOwnersList() {
        List<Subscriber> ownersList = new LinkedList<Subscriber>();
        for (Subscriber sub : ownersBy.keySet())
            ownersList.add(sub);
        return ownersList;
    }

    public String getOwnersString() {
        List<Subscriber> ownersList = new LinkedList<Subscriber>();
        String users = "";
        for (Subscriber sub : ownersBy.keySet())
            users += sub.toString();
        return users;
    }


    public boolean containsProductWithDetails(int storeId, String productName, String category, String description, double price) {
        for(Map.Entry<Product,Integer> entry : products.entrySet()){
            Product p = entry.getKey();
            if (p.getStoreId()==storeId &&p.getName().equals(productName)&&p.getPrice()==price&&p.getCategory().equals(category)&&p.getDescription().equals(description)) {
                return true;
            }
        }
        return false;
    }

    public void notifyApproveOwnerNotification(ApproveOwnerAppointmentNotification notification) throws LostConnectionException {
        observable.notifyApproveOwnerNotification(notification);
    }

    public void notifyFinalOwnerApproval(FinalAppointOwnerNotification finalAppointOwnerNotification) throws LostConnectionException {
        observable.notifyFinalOwnerApproval(finalAppointOwnerNotification);
    }


    public List<String> getNotApprovedYetBids() {
        LinkedList<String> bidsList = new LinkedList<>();
        for (Bid b : bids) {
            if(!b.isApproved())
                 bidsList.add(b.toString());
            for (Bid bid : b.getOwnerCounteredProposal().values() )
                if(!bid.isApproved())
                   bidsList.add(bid.toString());
        }
        return bidsList;
    }

    public void updateBidListAfterRemoval() throws ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, IllegalPermissionsAccess, LostConnectionException {
        for (Bid b : bids) {
            b.updateBidAfterRemoval();
            for (Bid bid : b.getOwnerCounteredProposal().values() )
                bid.updateBidAfterRemoval();
         }

    }
}
