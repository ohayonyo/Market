package System;

import DAL.*;
import Exceptions.*;
import Notifications.NewRoleNotification;
import Notifications.Notification;
import Notifications.VisitorsNotification;
import OutResources.DeliveryAdapter;
import OutResources.DeliveryDetails;
import OutResources.PaymentAdapter;
import OutResources.PaymentDetails;
import Security.UserValidation;
import Service.MarketService;
import SpellChecker.Spelling;
import Store.Bid.Bid;
import Store.Discount.*;
import Store.PolicyLimitation.CompoundBuyingPolicy;
import Store.PolicyLimitation.IBuyingPolicy;
import Store.PolicyLimitation.LogicOperators.AndCondition;
import Store.PolicyLimitation.LogicOperators.OrCondition;
import Store.PolicyLimitation.LogicOperators.XorCondition;
import Store.PolicyLimitation.SimpleLimitationPolicy.*;
import Store.Store;
import Store.SubscriberList;
import User.User;
import User.Visitors;
import User.Roles;
import User.Subscriber;
import User.ShoppingBag;
import Store.Product;
import Store.PairInteger;
import org.apache.log4j.Logger;
import User.Roles.Role;
import java.util.concurrent.locks.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class System {

    private final DeliveryAdapter deliveryAdapter;
    private final PaymentAdapter paymentAdapter;
    private final UserValidation userValidation;

    private final Map<String, User> users; //Key is connectionId
    private final Map<String, Subscriber> subscribers; //Key is connectionId
    private final Map<Integer, Store> stores;
    private final Map<String, User> systemAdmins;
    private final Spelling spelling = new Spelling();

    private Visitors visitors_in_system;
    private final ConcurrentHashMap<String, Map<String, Integer>> visitors; // key: date, value: Map of visitors per type


    public System(DeliveryAdapter deliveryAdapter, PaymentAdapter paymentAdapter,
                  UserValidation userValidation, Map<String, User> users, Map<String,
                  Subscriber> subscribers, Map<Integer, Store> stores, Map<String,
                  User> systemAdmins) throws AdminNotFoundException{
        this.deliveryAdapter = deliveryAdapter;
        this.paymentAdapter = paymentAdapter;
        this.userValidation = userValidation;
        this.users = users;
        this.subscribers = subscribers;
        this.stores = stores;
        this.systemAdmins = validateAdminExist(systemAdmins);
        this.visitors = new ConcurrentHashMap<>();
        this.visitors_in_system = new Visitors();
    }

    public void setVisitors_in_system(Visitors visitors_in_system) {
        this.visitors_in_system = visitors_in_system;
    }

    public Map<String, User> validateAdminExist(Map<String, User> systemAdmins) throws AdminNotFoundException{
        if ((systemAdmins.isEmpty()) || (systemAdmins.get("Admin") == null)){
            throw new AdminNotFoundException();
        }
        return systemAdmins;
    }

    public User getUser(String connectionId) throws ConnectionNotFoundException
    {
        if(!this.users.containsKey(connectionId))
            throw new ConnectionNotFoundException();
        return this.users.get(connectionId);
    }

    public Subscriber getSubscriber(String userName) throws UserNameNotExistException
    {
        if(!this.subscribers.containsKey(userName))
            throw new UserNameNotExistException(userName);
        return this.subscribers.get(userName);
    }

    public Store getStore(int storeId) throws StoreNotFoundException
    {
        if(!this.stores.containsKey(storeId))
            throw new StoreNotFoundException();
        return this.stores.get(storeId);
    }

    public Collection<String> getStoreProductsName(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException, NoPermissionException {
        Store store = getStore(storeID);
        User user = getUser(connectionID);
        if(!(user instanceof Subscriber)) {
            throw new UserNotSubscriberException();
        }
        Subscriber subscriber = (Subscriber)user;
        try {
            subscriber.hasPermissionAtStore(store, Role.STORE_POLICY);
        }catch(Exception e1){
            try {
                subscriber.isStoreOwner(store);
            }catch(Exception e2){
                throw e1;
            }
        }
        LinkedList<String> products = new LinkedList<>();
        for (Product p: store.getProducts()) {
            products.add("id: " + p.getProductId() + ", name: " + p.getName());
        }
        return products;
    }

    public Collection<String> getStoreProductsNameNoOwner(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException, NoPermissionException {
        Store store = getStore(storeID);
        User user = getUser(connectionID);
        if(!(user instanceof Subscriber)) {
            throw new UserNotSubscriberException();
        }
        Subscriber subscriber = (Subscriber)user;
        LinkedList<String> products = new LinkedList<>();
        for (Product p: store.getProducts()) {
            if(p.getSubscriberForBid()==null)
                  products.add("id: " + p.getProductId() + ", name: " + p.getName());
        }
        return products;
    }

    public Collection<String> getStoreProductsCategory(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException {
        Store store = getStore(storeID);
        User user = getUser(connectionID);
        if(!(user instanceof Subscriber)) {
            throw new UserNotSubscriberException();
        }
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreOwner(store);
        LinkedList<String> storeCategories = new LinkedList<>();
        LinkedList<String> result = new LinkedList<>();
        for (Product p: store.getProducts()) {
            String category = p.getCategory();
            if(!storeCategories.contains(category)){
                storeCategories.add(category);
                result.add("id: " + p.getProductId() + ", category: " + p.getCategory());
            }
        }
        return result;
    }

    public Collection<String> getStoreDiscountsID(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException {
        Store store = getStore(storeID);
        User user = getUser(connectionID);
        if(!(user instanceof Subscriber)) {
            throw new UserNotSubscriberException();
        }
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreOwner(store);
        return store.getDiscountsByIDLeafs();
    }

    public Collection<String> getStorePoliciesID(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException {
        Store store = getStore(storeID);
        User user = getUser(connectionID);
        if(!(user instanceof Subscriber)) {
            throw new UserNotSubscriberException();
        }
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreOwner(store);
        return store.getPoliciesByIDLeafs();
    }

    public void hasPermissions(String connectionID, int storeID) throws ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotStorePolicyException {
        Store store = getStore(storeID);
        User user = getUser(connectionID);
        if(!(user instanceof Subscriber)) {
            throw new UserNotSubscriberException();
        }
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
    }

    public void register(String userName, String password) throws UserExistsException, ConnectionNotFoundException, LostConnectionException {
        OperationsQueue operationsQueue = new OperationsQueue();
        userValidation.register(userName, password);
        Subscriber s = new Subscriber(userName);
        operationsQueue.addOperation(userValidation, Operation.Type.MERGE);
        operationsQueue.addOperation(s, Operation.Type.PERSISTENCE);
        Repo.operation(operationsQueue);
//        Repo.register(userValidation,s);
//        Repo.merge(userValidation);
//        Repo.persist(s);
        subscribers.put(userName, s);
    }

    public void registerAdmin(String userName, String password) throws UserExistsException, LostConnectionException {
        userValidation.register(userName, password);
        Subscriber s = new Subscriber(userName);
        Repo.merge(userValidation);
        Repo.persist(s);
        subscribers.put(userName, s);
        systemAdmins.put(userName, subscribers.get(userName));
    }

    public void makeAdmin(String userName) throws AlreadyAdminException  {
        try{
            isAdmin(userName);
        }

        catch (NotAdminException e){
            subscribers.put(userName, new Subscriber(userName));
            systemAdmins.put(userName, subscribers.get(userName));
        }
        throw new AlreadyAdminException(userName);
    }

    public List<String> getAllLoggedInSubscribers(){
        List<String> conSubscribers= new LinkedList<String>();
/*        ReadWriteLock lock = new ReentrantReadWriteLock();

        lock.writeLock().lock();
        lock.readLock().lock();
        try {
            sleep(1);
            for (User user : users.values())
                if (user instanceof Subscriber) conSubscribers.add(((Subscriber) user).getUserName());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
            lock.readLock().unlock();
        }*/

       //Iterator it = users.values().iterator();
/*        while (it.hasNext()) {
            Object item = it.next();
            if (item instanceof Subscriber) conSubscribers.add(((Subscriber) item).getUserName());
            it.remove();
        }*/
/*        for (Iterator<User> iterator = users.values().iterator(); iterator.hasNext();){
            User user=iterator.next();
            if (user instanceof Subscriber) conSubscribers.add(((Subscriber) user).getUserName());
        }*/
      for (User user : users.values())
            if (user instanceof Subscriber) conSubscribers.add(((Subscriber) user).getUserName());
        return conSubscribers;
    }

    public void login(String connectionId, String userName, String password) throws UserNotExistException, WrongPasswordException, ConnectionNotFoundException, UserNameNotExistException, AlreadyLoggedInException, LostConnectionException {
        userValidation.validateUser(userName, password);
        if (getAllLoggedInSubscribers().contains(userName))
            throw new AlreadyLoggedInException(userName);
        User user = getUser(connectionId);

        Subscriber subscriber = getSubscriber(userName);
        boolean managerAndOwner = false;
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        visitors.putIfAbsent(date, new HashMap<>());
        visitors_in_system.getGuests().putIfAbsent(date, 1);
        int managers = visitors_in_system.getManagers().computeIfAbsent(date, s -> 0);
        int owners = visitors_in_system.getOwners().computeIfAbsent(date, s -> 0);
        subscriber.moveCartFromUserToSubscriber(user);
        users.put(connectionId, subscriber);
        if(systemAdmins.containsKey(subscriber.getUserName())) {
            visitors.get(date).compute("admins", (k, v) -> v == null ? 1 : v + 1);
            visitors_in_system.getAdmins().compute(date, (k, v) -> v == null ? 1 : v + 1);
            VisitorsNotification n1 = new VisitorsNotification(getTotalVisitorsByAdminPerDay(date));
            Repo.persist(n1);
            ((Subscriber)systemAdmins.get(subscriber.getUserName())).notifyVisitors(n1);
            Repo.merge(visitors_in_system);
            Repo.merge(subscriber);
            return;
        }

        for (Store store : stores.values()) {
            if (subscriber.getRoles().get(store) != null && subscriber.getRoles().get(store).contains(Roles.Role.STORE_OWNER)) {
                visitors.get(date).compute("owners", (k, v) -> v == null ? 1 : v + 1);
                visitors_in_system.getOwners().compute(date, (k, v) -> v == null ? 1 : v + 1);
                if(managerAndOwner) {
                    visitors.get(date).compute("managers", (k, v) -> v == null ? 0 : v - 1);
                    visitors_in_system.getManagers().compute(date, (k, v) -> v == null ? 0 : v - 1);
                }
                VisitorsNotification n2 = new VisitorsNotification(getTotalVisitorsByAdminPerDay(date));
                Repo.persist(n2);
                ((Subscriber)systemAdmins.get("Admin")).notifyVisitors(n2);
                Repo.merge(visitors_in_system);
                Repo.merge(subscriber);
                return;
            }
            if (subscriber.getRoles().get(store) != null && subscriber.getRoles().get(store).contains(Roles.Role.STORE_MANAGER)) {
                visitors.get(date).compute("managers", (k, v) -> v == null ? 1 : v + 1);
                visitors_in_system.getManagers().compute(date, (k, v) -> v == null ? 1 : v + 1);
                managerAndOwner = true;
            }
        }
        if(managers == visitors_in_system.getManagers().get(date) && owners == visitors_in_system.getOwners().get(date)) {
            visitors.get(date).compute("subscribers", (k, v) -> v == null ? 1 : v + 1);
            visitors_in_system.getSubscribers().compute(date, (k, v) -> v == null ? 1 : v + 1);
        }
        VisitorsNotification n3 = new VisitorsNotification(getTotalVisitorsByAdminPerDay(date));
        Repo.persist(n3);
        ((Subscriber)systemAdmins.get("Admin")).notifyVisitors(n3);
        Repo.merge(subscriber);
        Repo.merge(visitors_in_system);
    }

    public void logout(String connectionId) throws ConnectionNotFoundException
    {
        User guest = getUser(connectionId);
        guest = new User();
        users.put(connectionId, guest);
    }



    public int openStore(String connectionId, String storeName) throws IllegalNameException, ConnectionNotFoundException, UserNotSubscriberException, IllegalDiscountException, LostConnectionException {
        Store store = new Store(storeName);
        Repo.persist(store);
        this.stores.put(store.getStoreId(), store);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.addStoreFounder(store);
        Repo.merge(subscriber);
        return store.getStoreId();
    }

    public Map<Store, Set<Product>> MarketInfo() throws StoreNotActiveException {
        Map<Store, Set<Product>> result = new HashMap<>();
        for(Map.Entry<Integer, Store> store : stores.entrySet())
        {
            if(store.getValue().isActivate())
                result.put(store.getValue(), store.getValue().getProducts());
        }
        return result;
    }

    public List<Product> searchProductByName(String productName) throws StoreNotActiveException {
        LinkedList<Product> result = new LinkedList<>();
        for(Map.Entry<Integer, Store> store : stores.entrySet())
        {
            if(store.getValue().isActivate())
                result.addAll(store.getValue().findProductByName(productName, spelling));
        }
        return result;
    }

    public List<Product> searchProductByCategory(String category) throws StoreNotActiveException {
        LinkedList<Product> result = new LinkedList<>();
        for(Map.Entry<Integer, Store> store : stores.entrySet())
        {
            if(store.getValue().isActivate())
                result.addAll(store.getValue().findProductsByCategory(category, spelling));
        }
        return result;
    }

    public List<Product> searchProductByKeyWord(String keyWord) throws StoreNotActiveException {
        LinkedList<Product> result = new LinkedList<>();
        for(Map.Entry<Integer, Store> store : stores.entrySet())
        {
            if(store.getValue().isActivate())
                result.addAll(store.getValue().findProductsByKeyWords(keyWord, spelling));
        }
        return result;
    }

    public Map<Store, ShoppingBag> getCart(String connectionId) throws ConnectionNotFoundException
    {
        User user = getUser(connectionId);
        return user.getCart();
    }

    public void updateShoppingBag(String connectionId, int storeID, int productID, int finalAmount) throws ProductNotFoundException, ConnectionNotFoundException, StoreNotFoundException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        Store store = getStore(storeID);
        Product product = store.getProductByID(productID);
        User user = getUser(connectionId);
        if(!store.isActivate())
            throw new StoreNotActiveException(storeID);
        user.updateShoppingBag(store, product, finalAmount);
    }

    //check if s2 is appointed to be owner by s1
    public boolean isOwnerByAtSomeStore(Subscriber s1,Subscriber s2){
        for(Store store: stores.values()){
            if(store.isOwnerBy(s1,s2))
                return true;
        }
        return false;
    }

    public void appointStoreOwner(String connectionId, int storeID, String userName) throws NotOwnerException, UserNameNotExistException, AlreadyOwnerException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, IllegalCircularityException, UserCantAppointHimself, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;

        if(subscriber.getUserName().equals(userName))
            throw new UserCantAppointHimself();
        subscriber.isStoreOwner(store);
        for (Map.Entry<String,Subscriber> entry : subscribers.entrySet())
        {
            Subscriber s = entry.getValue();
            if(s.getUserName().equals(userName))
            {
                s.isAlreadyStoreOwner(store);
//                if(isOwnerByAtSomeStore(s,subscriber))
//                    throw new IllegalCircularityException();
                s.addStoreOwner(store,subscriber);
                Repo.merge(s);
                store.addOwner(subscriber, entry.getValue());
                Repo.merge(store);
                return;
            }
        }
        throw new UserNameNotExistException(userName);
    }

    //check if s2 is appointed to be owner by s1
    public boolean isManagerByAtSomeStore(Subscriber s1,Subscriber s2){
        for(Store store: stores.values()){
            if(store.isManagerBy(s1,s2))
                return true;
        }
        return false;
    }

    public void appointStoreManager(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, AlreadyManagerException, StoreNotFoundException, UserNotSubscriberException, UserCantAppointHimself, IllegalCircularityException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        if(subscriber.getUserName().equals(userName))
            throw new UserCantAppointHimself();
        subscriber.isStoreOwner(store);
        for (Map.Entry<String,Subscriber> entry : subscribers.entrySet())
        {
            Subscriber s = entry.getValue();
            if(s.getUserName().equals(userName))
            {
                //s.isAlreadyStoreOwner(store);
                s.isAlreadyStoreManager(store);
//                if(isManagerByAtSomeStore(s,subscriber))
//                    throw new IllegalCircularityException();
                s.addStoreManager(store);
                Repo.merge(s);
                store.addManager(subscriber, entry.getValue());
                Repo.merge(store);
                return;
            }
        }
        throw new UserNameNotExistException(userName);
    }

    public void addInventoryManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, UserCantAppointHimself, LostConnectionException {
        addPermission(connectionId,storeID,userName, Roles.Role.INVENTORY_MANAGEMENT);
    }

    public void deleteInventoryManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotInventoryManagementException, NotManagerByException, CantRemovePolicyException, NoPermissionException, LostConnectionException {
        deletePermission(connectionId,storeID,userName,Role.INVENTORY_MANAGEMENT);
    }

    public void addStorePolicyManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, UserCantAppointHimself, LostConnectionException {
        addPermission(connectionId,storeID,userName, Roles.Role.STORE_POLICY);
    }

    public void deleteStorePolicyManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, CantRemovePolicyException, NoPermissionException, LostConnectionException {
        deletePermission(connectionId,storeID,userName,Role.STORE_POLICY);
    }

    public void closeStore(String connectionId, int storeID) throws StoreNotActiveException, NotFounderException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreFounder(store);
        store.closeStore();
        Repo.merge(store);
    }

    public List<String> purchasesHistory(String connectionId, int storeID) throws ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotManagerException {
        Store store = getStore(storeID);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreManager(store);
        return store.getHistory();
    }

    public double getCartPrice(String connectionId) throws EmptyCartException, InsufficientInventory, ProductNotFoundException, ConnectionNotFoundException, BagNotValidPolicyException, IllegalDiscountException {
        User user = getUser(connectionId);
        Map<Store, ShoppingBag> cart = user.getCart();
        if (user.getCart().isEmpty()) throw new EmptyCartException();
        double totalPrice = 0;
        boolean isEmptyCart=true;
        for (Map.Entry<Store, ShoppingBag> entry : cart.entrySet())
        {
            Store store= entry.getKey();
            ShoppingBag bag= entry.getValue();
            Map<Product, Integer> productsInBag=bag.getProducts();
            store.validateInventory(productsInBag);
            if(!productsInBag.isEmpty())
                isEmptyCart=false;
            IBuyingPolicy buyingPolicy=store.getPolicy();
            if(!buyingPolicy.isSatisfiesCondition(bag))
                throw new BagNotValidPolicyException(buyingPolicy.exceptionCause(bag));
            IDiscountPolicy DiscountPolicy= store.getStoreDiscount();
            totalPrice += DiscountPolicy.getCartPrice(bag);
        }
        if(isEmptyCart)
            throw new EmptyCartException();
        return totalPrice;
    }


    public void purchaseCart(String connectionId, String card_number, int month, int year, String holder, String ccv, String id, String name, String address, String city, String country, int zip) throws InsufficientInventory, ProductNotFoundException, ConnectionNotFoundException, BagNotValidPolicyException, IllegalDiscountException, PaymentException, DeliveryException, EmptyCartException, LostConnectionException {
        User user = getUser(connectionId);
        Map<Store, ShoppingBag> cart = user.getCart();
        if (user.getCart().isEmpty()) throw new EmptyCartException();
        double totalPrice = getCartPrice(connectionId);
        if(totalPrice == 0)
            return;
        PaymentDetails paymentDetails = new PaymentDetails(card_number, month, year, holder, ccv, id, totalPrice);
        DeliveryDetails deliveryDetails = new DeliveryDetails(name, address, city, country, zip);
        try {
            paymentAdapter.pay(paymentDetails);
            deliveryAdapter.deliver(deliveryDetails);
            updateInventory(cart);
            user.emptyCart();
        }
        catch (PaymentException ec)
        {
            cancelPaymentAndBackProducts(paymentDetails, cart);
            //throw new PaymentException();
        } catch (DeliveryException ex)
        {
            cancelPaymentAndBackProducts(paymentDetails, cart);
            deliveryAdapter.cancelDelivery(deliveryDetails);
        }

    }

    public Collection<String> eventLog(String connectionId) throws NotAdminException, IOException, ConnectionNotFoundException, UserNotSubscriberException {
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        isAdmin(subscriber.getUserName());
        Collection<String> eventLog = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new FileReader("Dec/src/logging.log"));
        String line = null;
        String ls = java.lang.System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            if(line.contains("INFO"))
                eventLog.add(line);
        }
        reader.close();
        return eventLog;
    }

    public String entrance() throws LostConnectionException {
        String uniqueID = UUID.randomUUID().toString();
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        visitors.putIfAbsent(date, new HashMap<>());
        visitors.get(date).compute("guests", (k, v) -> v == null ? 1 : v + 1);
        visitors_in_system.getGuests().putIfAbsent(date, 1);
        visitors_in_system.getSubscribers().putIfAbsent(date, 0);
        visitors_in_system.getManagers().putIfAbsent(date, 0);
        visitors_in_system.getOwners().putIfAbsent(date, 0);
        visitors_in_system.getAdmins().putIfAbsent(date, 0);
        visitors_in_system.getGuests().compute(date, (k, v) -> v == null ? 1 : v + 1);

        users.put(uniqueID, new User());
        try {
            VisitorsNotification n4 = new VisitorsNotification(getTotalVisitorsByAdminPerDay(date));
            Repo.persist(n4);
            ((Subscriber)systemAdmins.get("Admin")).notifyVisitors(n4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Repo.merge(visitors_in_system);
        return uniqueID;
    }

    public Map<String, Integer> getTotalVisitorsByAdminPerDay(String date) {
        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put("guests", visitors_in_system.getGuests().get(date));
        map.put("subscribers", visitors_in_system.getSubscribers().get(date));
        map.put("managers", visitors_in_system.getManagers().get(date));
        map.put("owners", visitors_in_system.getOwners().get(date));
        map.put("admins", visitors_in_system.getAdmins().get(date));
        return map;
    }

    public int addProductToStore(String connectionId, int storeId, String productName, String category, String description, int amount, double price) throws ConnectionNotFoundException, StoreNotFoundException, StoreNotActiveException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException {
        Store store = getStore(storeId);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreInventoryManager(store);
        Product product=null;
        if(store.containsProductWithDetails(storeId,productName,category,description,price))
            product = new Product(storeId, price,description,productName,category,true);
        else {
            product = new Product(storeId, price, description, productName, category);
            Repo.persist(product);
        }
        store.isStroeActivated();
        store.addProduct(product, amount);
        Repo.merge(store);
        return product.getProductId();
    }

    public void deleteProductFromStore(String connectionId, int storeId, int productID) throws ProductNotFoundException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException {
        Store store = getStore(storeId);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreInventoryManager(store);
        store.removeProduct(productID);
        Repo.merge(store);
    }

    public void updateProductDetails(String connectionId, int storeId, int productID, String newCategory, String description, Integer newAmount, Double newPrice) throws NotOwnerException, ProductNotFoundException, IllegalNameException, IllegalPriceException, InsufficientInventory, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException {
        Store store = getStore(storeId);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreInventoryManager(store);
        store.updateProduct(productID, newCategory, description, newAmount, newPrice);
        Repo.merge(store);
    }

    public Collection<String> storeRoles(String connectionId, int storeId) throws NotOwnerException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException {
        LinkedList<String> result = new LinkedList<>();
        Store store = getStore(storeId);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreOwner(store);
        for (Map.Entry<String, Subscriber> entry : subscribers.entrySet())
        {
            String temp= "";
            if(entry.getValue().getRoles().containsKey(store))
            {
                temp += entry.getValue().getUserName() + ": " + entry.getValue().getRoles().get(store).toString();
            }
            if(!temp.equals(""))
                result.add(temp);
        }
        return result;
    }


    public void isAdmin(String userName) throws NotAdminException {
        if(!this.systemAdmins.containsKey(userName)) {
            Logger.getLogger(MarketService.class).error("Not admin try to get hidden information");
            throw new NotAdminException();
        }
    }

    public Collection<String> adminPurchasesHistory(String connectionId, int storeId) throws NotAdminException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException {
        Store store = getStore(storeId);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        isAdmin(subscriber.getUserName());
        return store.getHistory();
    }


//    public void appointStoreOwner(String connectionId, int storeID, String userName) throws NotOwnerException, UserNameNotExistException, AlreadyOwnerException, ConnectionNotFoundException, StoreNotFoundException {
//        Store store = getStore(storeID);
//        User user = getUser(connectionId);
//        user.isStoreOwner(store);
//        for (Map.Entry<String,User> entry : users.entrySet())
//        {
//            if(entry.getValue().getUserName() != null &&entry.getValue().getUserName().equals(userName))
//            {
//                entry.getValue().isAlreadyStoreOwner(store);
//                entry.getValue().addStoreOwner(store);
//                store.addOwner(user, entry.getValue());
//                return;
//            }
//        }
//        throw new UserNameNotExistException(userName);
//    }

    public Collection<Subscriber> getSubscribers(){
        return subscribers.values();
    }

    public Subscriber getSubscriberByUserName(String userName) throws UserNameNotExistException {
        Collection<Subscriber> subscribers = getSubscribers();
        for(Subscriber s : subscribers)
            if(s.getUserName().equals(userName))
                return s;
        throw new UserNameNotExistException(userName);
    }

    public void removeOwner(String connectionId, int storeID, String userName) throws StoreNotFoundException, ConnectionNotFoundException, UserNameNotExistException, NotOwnerByException, IllegalPermissionsAccess, NotOwnerException, UserNotSubscriberException, ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        Subscriber needToRemoveFromOwnership = getSubscriberByUserName(userName);
        if(!subscriber.isStoreOwnerOrFounder(store))
            throw new IllegalPermissionsAccess();

        if(!store.isOwnerBy(subscriber,needToRemoveFromOwnership))
            throw new NotOwnerByException(store.getStoreName(),subscriber.getUserName(),userName);

        needToRemoveFromOwnership.isStoreOwner(store);
        needToRemoveFromOwnership.removeRole(store, Roles.Role.STORE_OWNER);
        Repo.merge(needToRemoveFromOwnership);
        store.removeOwner(subscriber, needToRemoveFromOwnership);
        store.updateBidListAfterRemoval();
        Repo.merge(store);

    }

    public void removeManager(String connectionId, int storeID, String userName) throws ConnectionNotFoundException, StoreNotFoundException, UserNameNotExistException, IllegalPermissionsAccess, NotManagerByException, NotManagerException, NoApointedUsersException, UserNotSubscriberException, ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        Subscriber needToRemoveFromManagement = getSubscriberByUserName(userName);
        if(!subscriber.isStoreOwnerOrFounder(store))
            throw new IllegalPermissionsAccess();

        if(!store.isManagerBy(subscriber,needToRemoveFromManagement))
            throw new NotManagerByException(store.getStoreName(),subscriber.getUserName(),userName);

        needToRemoveFromManagement.isStoreManager(store);
        needToRemoveFromManagement.removeRole(store, Roles.Role.STORE_MANAGER);
        Repo.merge(needToRemoveFromManagement);
        store.removeManager(subscriber,needToRemoveFromManagement);
        store.updateBidListAfterRemoval();
        Repo.merge(store);
    }

    private boolean isOwnerOrFounder (String userID,Store store) throws ConnectionNotFoundException, UserNotSubscriberException, StoreNotFoundException, NotOwnerException {
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        if(!subscriber.isStoreOwnerOrFounder(store))
            return false;
        return true;
    }

    //need to check who can apply this function
    public List<Integer> getStorePolicies(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, IllegalPermissionsAccess, UserNotSubscriberException, NotOwnerException {
        Store store = getStore(storeID);
        if(!isOwnerOrFounder(userID,store))
            throw new IllegalPermissionsAccess();
        return store.getPoliciesByID();

    }


    public IBuyingPolicy getPolicyByID(int policyID){
        for (Map.Entry<Integer,Store> s : stores.entrySet()){
            if(s.getValue().getPolicy().getPolicyID() == policyID)
                return s.getValue().getPolicy();
            List<IBuyingPolicy> search = s.getValue().getPolicies();
            for(IBuyingPolicy policy : search)
                if(policy.getPolicyID()==policyID)
                    return policy;
        }
        return null;

//        for (Map.Entry<Integer,Store> s : stores.entrySet()){
//            Store store = s.getValue();
//            IBuyingPolicy policy = store.getPolicy().getPolicyByID(policyID);
//            if(policy!=null)
//                return policy;
//        }
//        return null;
    }

    private void updateInventory(Map<Store, ShoppingBag> cart) throws ProductNotFoundException, InsufficientInventory, LostConnectionException {
        for (Map.Entry<Store, ShoppingBag> entry : cart.entrySet())
        {
            Store store = entry.getKey();
            ShoppingBag shoppingBag = entry.getValue();
            store.updateInventoryAfterPurchase(shoppingBag);
            Repo.merge(store);
        }
    }

    private void cancelPaymentAndBackProducts(PaymentDetails paymentDetails, Map<Store, ShoppingBag> cart) throws PaymentException {
        paymentAdapter.cancelPayment(paymentDetails);
        for (Map.Entry<Store, ShoppingBag> entry : cart.entrySet())
            entry.getKey().backProducts(entry.getValue().getProducts());
    }

    public void assignExistingBuyingPolicy(String userID,int policyID, int storeID) throws PolicyNotFoundException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException, IllegalPermissionsAccess, PolicyAlreadyExist, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        IBuyingPolicy policy = getPolicyByID(policyID);
        if(policy == null)
            throw new PolicyNotFoundException();

        if(store.getPolicy().getPolicyByID(policyID)!=null || (policy instanceof NoLimitationPolicy))
            throw new PolicyAlreadyExist();

        IBuyingPolicy storePolicy = store.getPolicy();
        if(storePolicy instanceof CompoundBuyingPolicy){
            CompoundBuyingPolicy compoundBuyingPolicy = ((CompoundBuyingPolicy) storePolicy);
            compoundBuyingPolicy.addPolicy(policy);
            Repo.merge(compoundBuyingPolicy);
            store.setStorePolicy(compoundBuyingPolicy);
            Repo.merge(store);
        }else{
            List<IBuyingPolicy> policies = new LinkedList<>();
            policies.add(policy);
            policies.add(storePolicy);
            AndCondition an = new AndCondition(policies);
            Repo.persist(an);
            store.setStorePolicy(an);
            Repo.merge(store);
        }

    }


    public void deletePolicyFromStore(String userID, int storeID, int policyID) throws PolicyNotFoundException, StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, NotStorePolicyException, LostConnectionException {

        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        IBuyingPolicy policyToRemove = getPolicyByID(policyID);
        if(policyToRemove==null)
            throw new PolicyNotFoundException(policyID);
        if(policyToRemove instanceof NoLimitationPolicy)
            return;
        IBuyingPolicy storeBuyingPolicy = store.getPolicy();
        if(storeBuyingPolicy==null || (storeBuyingPolicy instanceof NoLimitationPolicy))
            return ;

        //delete root policy
        if(!(storeBuyingPolicy instanceof CompoundBuyingPolicy) || policyToRemove.getPolicyID()==storeBuyingPolicy.getPolicyID()){
            NoLimitationPolicy noLimitationPolicy = new NoLimitationPolicy();
            Repo.persist(noLimitationPolicy);
            store.setStorePolicy(noLimitationPolicy);
            Repo.merge(store);
        }else{
            CompoundBuyingPolicy compoundBuyingPolicy = (CompoundBuyingPolicy) storeBuyingPolicy;
            List<IBuyingPolicy> children = compoundBuyingPolicy.getPolicies();
            if(children==null || children.size()==0){
                NoLimitationPolicy noLimitationPolicy = new NoLimitationPolicy();
                Repo.persist(noLimitationPolicy);
                store.setStorePolicy(noLimitationPolicy);
                Repo.merge(store);
            }else{
                IBuyingPolicy removed = compoundBuyingPolicy.removePolicy(policyToRemove);
                if(removed == null)
                    throw new PolicyNotFoundException(policyID);
                if(compoundBuyingPolicy.getPolicies().isEmpty()){
                    NoLimitationPolicy noLimitationPolicy = new NoLimitationPolicy();
                    Repo.persist(noLimitationPolicy);
                    store.setStorePolicy(noLimitationPolicy);
                    Repo.merge(store);
                }else{
                    store.setStorePolicy(compoundBuyingPolicy);
                    Repo.merge(store);
                }

            }

        }




//        Store store = getStore(storeID);
//        User user = getUser(userID);
//        if(!(user instanceof Subscriber))
//            throw new UserNotSubscriberException();
//        Subscriber subscriber = (Subscriber)user;
//        subscriber.isStorePolicyManager(store);
//
//        IBuyingPolicy policyToRemove = getPolicyByID(policyID);
//        if(policyToRemove==null||store.getPolicy()==null|| store.getPolicy().getPolicyByID(policyID)==null)
//            throw new PolicyNotFoundException();
//
//        if(policyToRemove instanceof NoLimitationPolicy)
//            return;
//
//        IBuyingPolicy storePolicy = store.getPolicy();
//        if(storePolicy instanceof CompoundBuyingPolicy){
//            CompoundBuyingPolicy afterRemove = (CompoundBuyingPolicy)storePolicy;
//            afterRemove.removePolicy(policyToRemove);
//            Repo.merge(afterRemove);
//            store.setStorePolicy(afterRemove);
//            Repo.merge(store);
//        }else{
//            if(storePolicy.getPolicyID()==policyID) {
//                NoLimitationPolicy nl = new NoLimitationPolicy();
//                Repo.persist(nl);
//                store.setStorePolicy(nl);
//                Repo.merge(store);
//            }
//            else
//                throw new PolicyNotFoundException();
//        }

    }


    private int addStorePolicy(Store store,IBuyingPolicy policyToAdd) throws LostConnectionException {

        IBuyingPolicy oldStorePolicy = store.getPolicy();
        if(oldStorePolicy==null|| (oldStorePolicy instanceof NoLimitationPolicy)) {
            store.setStorePolicy(policyToAdd);
            Repo.merge(store);
        }
        else{
            if(oldStorePolicy instanceof AndCondition){
                AndCondition newPolicy = ((AndCondition)oldStorePolicy);
                newPolicy.addPolicy(policyToAdd);
                Repo.merge(newPolicy);
                store.setStorePolicy(newPolicy);
                Repo.merge(store);
                return policyToAdd.getPolicyID();
            }
            List<IBuyingPolicy> newPolicies = new LinkedList<>();
            newPolicies.add(oldStorePolicy);
            newPolicies.add(policyToAdd);
            AndCondition newPolicy=new AndCondition(newPolicies);
            Repo.persist(newPolicy);
            store.setStorePolicy(newPolicy);
            Repo.merge(store);
        }
        return policyToAdd.getPolicyID();
    }

    public int addAmountLimitationPolicy(String userID, int storeID, Collection<Integer> products, int minQuantity, int maxQuantity) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, ProductNotFoundException, IllegalRangeException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
        if(minQuantity>maxQuantity)
            throw new IllegalRangeException(minQuantity,maxQuantity);
        Map<Product, PairInteger> productLimitation = new HashMap<>();
        PairInteger range = new PairInteger(minQuantity,maxQuantity);
//        Repo.persist(range);
        for(int productID : products){
            Product product = store.getProductByID(productID);
            productLimitation.put(product,range);
        }
        AmountLimitationPolicy amountLimitationPolicy = new AmountLimitationPolicy(productLimitation);
        Repo.persist(amountLimitationPolicy);
        return addStorePolicy(store,amountLimitationPolicy);
    }

    public int addMinimalCartPricePolicy(String userID, int storeID, int minCartPrice) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, IllegalPriceException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        if(minCartPrice<0)
            throw new IllegalPriceException(minCartPrice);

        TotalPriceLimitationPolicy newBuyingPolicy = new TotalPriceLimitationPolicy(minCartPrice,Double.MAX_VALUE);
        Repo.persist(newBuyingPolicy);
        return addStorePolicy(store,newBuyingPolicy);
    }


    public int addMaximalCartPricePolicy(String userID, int storeID, int maxCartPrice) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, IllegalPriceException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        if(maxCartPrice<0)
            throw new IllegalPriceException(maxCartPrice);

        TotalPriceLimitationPolicy newBuyingPolicy = new TotalPriceLimitationPolicy(0,maxCartPrice);
        Repo.persist(newBuyingPolicy);
        return addStorePolicy(store,newBuyingPolicy);
    }

    public int addCartRangePricePolicy(String userID, int storeID,int minCartPrice,int maxCartPrice) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, IllegalPriceException, IllegalRangeException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        if(maxCartPrice<0||minCartPrice<0)
            throw new IllegalPriceException(maxCartPrice);
        if(maxCartPrice<minCartPrice)
            throw new IllegalRangeException(minCartPrice,maxCartPrice);

        TotalPriceLimitationPolicy newBuyingPolicy = new TotalPriceLimitationPolicy(minCartPrice,maxCartPrice);
        Repo.persist(newBuyingPolicy);
        return addStorePolicy(store,newBuyingPolicy);
    }

    public int addTimePolicy(String userID, int storeID, Collection<Integer> productsID, String startTime, String endTime) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, ProductNotFoundException, IllegalTimeRangeException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        List<Product> products = new LinkedList<>();
        for(int id : productsID)
            products.add(store.getProductByID(id));

        LocalTime minTime = LocalTime.parse(startTime);
        LocalTime maxTime = LocalTime.parse(endTime);
        if(minTime.isAfter(maxTime))
            throw new IllegalTimeRangeException(minTime,maxTime);
        TimeLimitationPolicy newBuyingPolicy = new TimeLimitationPolicy(products,minTime,maxTime);
        Repo.persist(newBuyingPolicy);
        return addStorePolicy(store,newBuyingPolicy);
    }

    public int addForbiddenDatePolicy(String userID, int storeID, Collection<Integer> productsID, int year,int month,int day) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotStorePolicyException, ProductNotFoundException, DateAlreadyPassed, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        List<Product> products = new LinkedList<>();
        for(int id : productsID)
            products.add(store.getProductByID(id));

        LocalDate forbiddenDate= LocalDate.of(year,month,day);
        if(forbiddenDate.isBefore(LocalDate.now()))
            throw new DateAlreadyPassed(forbiddenDate);
        DateLimitationPolicy policy = new DateLimitationPolicy(forbiddenDate,products);
        Repo.persist(policy);
        return addStorePolicy(store,policy);
    }

    public Collection<Integer> getStoreDiscounts(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess {
        Store store = getStore(storeID);
        if(!systemAdmins.containsKey(userID)&&!isOwnerOrFounder(userID,store))
            throw new IllegalPermissionsAccess();

        return store.getDiscountsByID();
    }

    public IDiscountPolicy getDiscountsByID(int discountID){
        for (Map.Entry<Integer,Store> s : stores.entrySet()){
            if(s.getValue().getStoreDiscount().getDiscountID()==discountID)
                return s.getValue().getStoreDiscount();
            List<IDiscountPolicy> search = s.getValue().getDiscounts();
            for(IDiscountPolicy discount : search)
                if(discount.getDiscountID()==discountID)
                    return discount;
        }
        return null;
    }


    private int addStoreDiscount(Map<Store,ShoppingBag> map,Store store,IDiscountPolicy discountToAdd) throws DiscountAlreadyExistsException, IllegalDiscountException, LostConnectionException {
        IDiscountPolicy oldStoreDiscount = store.getStoreDiscount();
        if(oldStoreDiscount==null|| (oldStoreDiscount instanceof NoDiscountPolicy)) {
            store.setStoreDiscount(discountToAdd);
            Repo.merge(store);
        }
        else{
            if(oldStoreDiscount instanceof AddDiscountPolicy){
                AddDiscountPolicy newDiscount = ((AddDiscountPolicy)oldStoreDiscount);
                if(map==null ||!map.containsKey(store))
                    newDiscount.addDiscountPolicy(discountToAdd,null);
                else
                    newDiscount.addDiscountPolicy(discountToAdd,map.get(store));
                Repo.merge(newDiscount);
                store.setStoreDiscount(newDiscount);
                Repo.merge(store);
                return discountToAdd.getDiscountID();
            }
            List<IDiscountPolicy> newDiscounts = new LinkedList<>();
            newDiscounts.add(oldStoreDiscount);
            newDiscounts.add(discountToAdd);
            AddDiscountPolicy newPolicy= new AddDiscountPolicy(newDiscounts);
            Repo.persist(newPolicy);
            store.setStoreDiscount(newPolicy);
            Repo.merge(store);
        }
        //TODO: need to check if need to return AddDiscountPolicy id or the id of the discount we added
        return discountToAdd.getDiscountID();
    }


    public void deleteDiscountFromStore(String userID, int storeID, int discountID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, DiscountNotFoundException, CantRemoveDiscountException, IllegalDiscountException, CantRemoveProductException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        IDiscountPolicy discountToRemove = getDiscountsByID(discountID);
        if(discountToRemove==null)
            throw new DiscountNotFoundException(discountID);
        if(discountToRemove instanceof NoDiscountPolicy)
            return;
        IDiscountPolicy storeDiscountPolicy = store.getStoreDiscount();
        if(storeDiscountPolicy==null || (storeDiscountPolicy instanceof NoDiscountPolicy))
            return ;

        //delete root discount
        if(!(storeDiscountPolicy instanceof CompoundDiscountPolicy) || discountToRemove.getDiscountID()==storeDiscountPolicy.getDiscountID()){
            NoDiscountPolicy nd = new NoDiscountPolicy();
            Repo.persist(nd);
            store.setStoreDiscount(nd);
            Repo.merge(store);
        }else{
            CompoundDiscountPolicy compoundDiscountPolicy = (CompoundDiscountPolicy) storeDiscountPolicy;
            List<IDiscountPolicy> children = compoundDiscountPolicy.getDiscountPolicies();
            if(children==null || children.size()==0){
                NoDiscountPolicy nd = new NoDiscountPolicy();
                Repo.persist(nd);
                store.setStoreDiscount(nd);
                Repo.merge(store);
            }else{
                IDiscountPolicy removed = compoundDiscountPolicy.removeDiscountPolicy(discountToRemove);
                if(removed == null)
                    throw new DiscountNotFoundException(discountID);
                if(compoundDiscountPolicy.getDiscountPolicies().isEmpty()){
                    NoDiscountPolicy nd = new NoDiscountPolicy();
                    Repo.persist(nd);
                    store.setStoreDiscount(nd);
                    Repo.merge(store);
                }else{
                    store.setStoreDiscount(compoundDiscountPolicy);
                    Repo.merge(store);
                }

            }

        }

    }


    public int resetStoreDiscounts(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, IllegalDiscountException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
        NoDiscountPolicy discountPolicy = new NoDiscountPolicy();
        Repo.persist(discountPolicy);
        store.setStoreDiscount(discountPolicy);
        Repo.merge(store);
        return discountPolicy.getDiscountID();
    }

    public int addSimpleDiscount(String userID, int storeID, Collection<Integer> productsID, double discount) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, ProductNotFoundException, IllegalDiscountException, DiscountAlreadyExistsException, LostConnectionException {
        Store store = getStore(storeID);
        if(!isOwnerOrFounder(userID,store))
            throw new IllegalPermissionsAccess();
        List<Product> products= new LinkedList<>();
        for(int productID : productsID)
            products.add(store.getProductByID(productID));
        SimpleDiscountPolicy sd = new SimpleDiscountPolicy(discount,products);
        Repo.persist(sd);
        Map<Store,ShoppingBag> map = getCart(userID);
        return addStoreDiscount(map,store,sd);
    }


    public int resetStorePolicy(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
        NoLimitationPolicy buyingPolicy = new NoLimitationPolicy();
        Repo.persist(buyingPolicy);
        store.setStorePolicy(buyingPolicy);
        Repo.merge(store);
        return buyingPolicy.getPolicyID();
    }

    public int addMaxDiscount(String userID, int storeID, Collection<Integer> discountsID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, DiscountNotFoundException, CantRemoveProductException, NotCompoundException, CantRemoveDiscountException, DiscountAlreadyExistsException, IllegalDiscountException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        List<IDiscountPolicy> toMaxDiscounts = new ArrayList<>();
        for(int discountID : discountsID) {
            IDiscountPolicy discountPolicy = store.getStoreDiscount().getDiscountByID(discountID);
            if(discountPolicy==null)
                throw new DiscountNotFoundException(discountID);
            toMaxDiscounts.add(discountPolicy);
        }

        for(int discountIDToRemove : discountsID)
            deleteDiscountFromStore(userID,storeID,discountIDToRemove);
        MaxDiscountPolicy maxDiscountPolicy = new MaxDiscountPolicy(toMaxDiscounts);
        Repo.persist(maxDiscountPolicy);
        return addStoreDiscount(getCart(userID),store,maxDiscountPolicy);
    }

    public int getStorePolicy(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess {
        Store store = getStore(storeID);
        if(!isOwnerOrFounder(userID,store))
            throw new IllegalPermissionsAccess();
        return store.getPolicy().getPolicyID();
    }

    public int getStoreDiscount(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, NotStorePolicyException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
        return store.getStoreDiscount().getDiscountID();

    }

    public String showEmployeesDetails(String userID, int storeID) throws IllegalPermissionsAccess, StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException {
        Store store = getStore(storeID);
        if(!isOwnerOrFounder(userID,store))
            throw new IllegalPermissionsAccess();
        String details = "The owners of store " + storeID + " are:\n";
        Map<Subscriber , SubscriberList> ownersBy = store.getOwnersBy();
        Map<Subscriber , SubscriberList> managersBy = store.getManagersBy();
        for(Subscriber owner : ownersBy.keySet()){
            details += owner.getUserName() + "\n";
        }
        details += "The managers of store " + storeID + " are:\n";
        for(Subscriber manager : managersBy.keySet()){
            details += manager.getUserName() + "/n";
        }
        return  details;
    }

    public Collection<String> getStoreBuyingHistory(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, NotAdminException, StoreNotActiveException, NotManagerException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        if(!store.isStroeActivated())
        {
            if(!isOwnerOrFounder(userID,store))
                throw new IllegalPermissionsAccess();
        }
        else
        {
            subscriber.isStoreManager(store);
        }
        return store.getHistory();

    }

    public int addSimpleDiscountByCategory(String userID, int storeID, String category, double discount) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, DiscountAlreadyExistsException, IllegalDiscountException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
        SimpleDiscountByCategory categoryDiscountPolicy = new SimpleDiscountByCategory(store,category,discount);
        Repo.persist(categoryDiscountPolicy);
        return addStoreDiscount(getCart(userID),store,categoryDiscountPolicy);
    }

    public int addSimpleDiscountByProducts(String userID, int storeID, Collection<Integer> productsID, double discount) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, IllegalDiscountException, ProductNotFoundException, DiscountAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
        List<Product> products = new ArrayList<>();
        for(int productID : productsID)
            products.add(store.getProductByID(productID));
        SimpleDiscountByProducts productsDiscountPolicy = new SimpleDiscountByProducts(products,discount);
        Repo.persist(productsDiscountPolicy);
        return addStoreDiscount(getCart(userID),store,productsDiscountPolicy);
    }

    public int addSimpleDiscountByStore(String userID, int storeID, double discount) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, IllegalDiscountException, ProductNotFoundException, DiscountAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
        SimpleDiscountByStore storeDiscountPolicy = new SimpleDiscountByStore(store,discount);
        Repo.persist(storeDiscountPolicy);
        return addStoreDiscount(getCart(userID),store,storeDiscountPolicy);
    }

    public void assignExistingDiscountByStore(int discountID, String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, DiscountNotFoundException, DiscountAlreadyExistsException, NotStorePolicyException, IllegalDiscountException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        IDiscountPolicy discount = getDiscountsByID(discountID);
        if(discount==null)
            throw new DiscountNotFoundException(discountID);

        addStoreDiscount(getCart(userID),store,discount);
    }

    private int andXorOr(String userID, int storeID, Collection<Integer> policyIDs,int type) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, PolicyNotFoundException, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);

        List<IBuyingPolicy> toOperate = new ArrayList<>();
        for(int policyID : policyIDs) {
            IBuyingPolicy Policy = store.getPolicy().getPolicyByID(policyID);
            if(Policy==null)
                throw new PolicyNotFoundException(policyID);
            toOperate.add(Policy);
        }

        for(int policyIDToRemove : policyIDs)
            deletePolicyFromStore(userID,storeID,policyIDToRemove);

        IBuyingPolicy operator;
        switch(type){
            case 2:
                operator = new OrCondition(toOperate);
                break;
            case 3:
                operator = new XorCondition(toOperate);
                break;
            default:
                operator = new AndCondition(toOperate);
                break;
        }

        Repo.persist(operator);
        return addStorePolicy(store,operator);
    }

    public int andPolicy(String userID, int storeID, Collection<Integer> policyIDs) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, PolicyNotFoundException, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        return andXorOr(userID,storeID,policyIDs,1);
    }

    public int orPolicy(String userID, int storeID, Collection<Integer> policyIDs) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, PolicyNotFoundException, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        return andXorOr(userID,storeID,policyIDs,2);
    }

    public int xorPolicy(String userID, int storeID, Collection<Integer> policyIDs) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, PolicyNotFoundException, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        return andXorOr(userID,storeID,policyIDs,3);
    }

    public void membershipCancellation(String connectionId, String userName) throws NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, UserNameNotExistException, SubscriberHasRolesException, LostConnectionException {
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        isAdmin(subscriber.getUserName());
        Subscriber toRemove = getSubscriber(userName);
        if(toRemove.isRegularSubscriber() && systemAdmins.get(userName) == null)
        {
            userValidation.removeUser(userName);
            Repo.merge(userValidation);
            subscribers.remove(userName);
        }
        else
        {
            throw new SubscriberHasRolesException();
        }
    }

    public Collection<String> errorLog(String connectionId) throws NotAdminException, IOException, ConnectionNotFoundException, UserNotSubscriberException {
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        isAdmin(subscriber.getUserName());
        Collection<String> errorLog = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new FileReader("Dec/src/logging.log"));
        String line = null;
        String ls = java.lang.System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            if(line.contains("ERROR"))
                errorLog.add(line);
        }
        reader.close();
        return errorLog;
    }

    public Collection<String> infoAboutSubscribers(String connectionId) throws NotAdminException, ConnectionNotFoundException, UserNotSubscriberException {
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        isAdmin(subscriber.getUserName());
        Collection<String> result = new LinkedList<>();
        String temp = "";
        for (Map.Entry<String,Subscriber> entry : subscribers.entrySet())
        {
            Map<Store,Roles> rolesAtStore = entry.getValue().getRoles();
            temp = "User name: " + entry.getKey();
            if(rolesAtStore.isEmpty())
                temp+= ", no roles\n";
            for(Map.Entry<Store,Roles> rolesEntry : rolesAtStore.entrySet())
                temp = temp + ", roles at store "+rolesEntry.getKey().getStoreName()+":"+rolesEntry.getValue().toString()+"\n";

//            temp = "User name: " + entry.getKey() + ", roles: " + entry.getValue().getRoles().toString();
            result.add(temp);
        }
        return result;
    }

    public int addConditionalDiscount(String userID, Integer policyID, int storeID, double discount, Collection<Integer> productsID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, ProductNotFoundException, DiscountAlreadyExistsException, IllegalDiscountException, NotStorePolicyException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
        IBuyingPolicy condition = getPolicyByID(policyID);
        List<Product> products = new LinkedList<>();
        for(int productID : productsID)
            products.add(store.getProductByID(productID));
        SimpleDiscountByProducts discountPolicy = new SimpleDiscountByProducts(products,discount);
        Repo.persist(discountPolicy);
        ConditionalDiscountPolicy conditionalDiscountPolicy = new ConditionalDiscountPolicy(condition,discountPolicy);
        Repo.persist(conditionalDiscountPolicy);
        return addStoreDiscount(getCart(userID),store,conditionalDiscountPolicy);
    }

    public int assignConditionalDiscount(String userID, Integer policyID, int storeID, int discountID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, DiscountNotFoundException, DiscountAlreadyExistsException, NotStorePolicyException, IllegalDiscountException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStorePolicyManager(store);
        IBuyingPolicy condition = getPolicyByID(policyID);
        IDiscountPolicy discountPolicy = getDiscountsByID(discountID);
        if(discountPolicy==null)
            throw new DiscountNotFoundException(discountID);
        ConditionalDiscountPolicy conditionalDiscountPolicy = new ConditionalDiscountPolicy(condition,discountPolicy);
        Repo.persist(conditionalDiscountPolicy);
        return addStoreDiscount(getCart(userID),store,conditionalDiscountPolicy);
    }


    public boolean isBidPermission(String userConnectionID, Store store) throws ConnectionNotFoundException, StoreNotFoundException, NotOwnerException, UserNotSubscriberException {
        return isOwnerOrFounder(userConnectionID,store) || isInventoryManagement(userConnectionID,store);
    }

    public int addBid(String userConnectionID, int storeID, int productID, double price, int amount) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(userConnectionID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber sub = (Subscriber)user;
        Product product = store.getProductByID(productID);
        return store.addBid(sub,product,amount,price);
    }

    public void approveBid(String userConnectionID, int storeID, int bidID) throws StoreNotFoundException, IllegalPermissionsAccess, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, BidException, IllegalPriceException, ProductShouldBeInCartBeforeBidOnIt, LostConnectionException {
        Store store = getStore(storeID);
        if(!isBidPermission(userConnectionID,store))
            throw new IllegalPermissionsAccess();
        User user = getUser(userConnectionID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber sub =(Subscriber)user;
        Bid bid = store.getBidByID(bidID);
        if (bid == null)
            throw new BidException();
        bid.addOwnerApproval(sub);
        Repo.merge(bid);

    }



    public void rejectBid(String userConnectionID, int storeID, int bidID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, BidException, LostConnectionException {
        Store store = getStore(storeID);
        if(!isBidPermission(userConnectionID,store))
            throw new IllegalPermissionsAccess();
        Subscriber sub =(Subscriber)getUser(userConnectionID);
        Bid bid = store.getBidByID(bidID);
        if (bid == null)
            throw new BidException();
        bid.rejectBid(sub);
        Repo.merge(bid);
    }

    public int addCounteredProposal(String userConnectionID, int storeID, int bidID,int amount ,double price) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, BidException, ProductShouldBeInCartBeforeBidOnIt, ProductNotFoundException, IllegalRangeException, IllegalPriceException, LostConnectionException {
        Store store = getStore(storeID);
        if(!isBidPermission(userConnectionID,store))
            throw new IllegalPermissionsAccess();
        Subscriber sub =(Subscriber)getUser(userConnectionID);
        Bid bid = store.getBidByID(bidID);
        if (bid == null)
            throw new BidException();
        return bid.addCounteredProposal(sub,amount,price);

    }

    public Collection<String> getBidsByStore(String connectionId, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, IllegalPermissionsAccess, NotOwnerException, UserNotSubscriberException {
            Store store = getStore(storeId);
        if(!isBidPermission(connectionId,store))
            throw new IllegalPermissionsAccess("The current user doesn't have bid permissions for this store.");
            return store.getBidsString();
        }

    public Collection<String> getBidsForAddCounteredString(String connectionId, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, IllegalPermissionsAccess, NotOwnerException, UserNotSubscriberException {
        Store store = getStore(storeId);
        if(!isBidPermission(connectionId,store))
            throw new IllegalPermissionsAccess("The current user doesn't have bid permissions for this store.");
        return store.getBidsForAddCounteredString();
    }

    public Collection<String> getBidsForAddNormalBidString(String connectionId, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, IllegalPermissionsAccess, NotOwnerException, UserNotSubscriberException {
        Store store = getStore(storeId);
        if(!isBidPermission(connectionId,store))
            throw new IllegalPermissionsAccess("The current user doesn't have bid permissions for this store.");
        return store.getBidsForAddNormalBidString();
    }

    private boolean isInventoryManagement(String connectionId, Store store) throws ConnectionNotFoundException, UserNotSubscriberException {
            User user = getUser(connectionId);
            if(!(user instanceof Subscriber))
                throw new UserNotSubscriberException();
            Subscriber subscriber = (Subscriber)user;
            if(!subscriber.isManagerBidPermission(store))
                return false;
            return true;
        }



    public Collection<String> getNotifications(String userID) throws ConnectionNotFoundException {
            Subscriber user =(Subscriber) getUser(userID);
            Collection<Notification> pending = user.checkPendingNotifications();
            Collection<String> notifications = new LinkedList<>();
            for (Notification n: pending) {
                notifications.add(n.toString());
            }
            return notifications;
        }

    public Bid getBidByID(String userID, int storeId, int bidID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess {
        Store store = getStore(storeId);
        if(!isBidPermission(userID,store))
            throw new IllegalPermissionsAccess();
        return store.getBidByID(bidID);
    }

    public void addPermission(String ownerConnectionID, int storeID, String userName, Role role) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException, UserCantAppointHimself, NotManagerByException, UserNameNotExistException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(ownerConnectionID);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreOwner(store);
        for (Map.Entry<String,Subscriber> entry : subscribers.entrySet()) {
            Subscriber s = entry.getValue();
            if(s.getUserName().equals(userName)) {
                if(subscriber.getUserName().equals(userName))
                    throw new UserCantAppointHimself("The user:"+userName+" can't add himself permissions");
                if(store.isManagerBy(subscriber, s)) {
                    store.notifyNewRole(subscriber, s, role.name());
                    s.addPermissionToStore(store,role);
                    Repo.merge(s);
                    return;
                }
                throw new NotManagerByException(store.getStoreName(),subscriber.getUserName(),userName);
            }
        }
        throw new UserNameNotExistException(userName);
    }

    public void deletePermission(String connectionId, int storeID, String userName,Role role) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, CantRemovePolicyException, NoPermissionException, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;
        subscriber.isStoreOwner(store);
        for (Map.Entry<String,Subscriber> entry : subscribers.entrySet()) {
            Subscriber s = entry.getValue();
            if(s.getUserName().equals(userName)) {
                if(subscriber.getUserName().equals(userName))
                    throw new CantRemovePolicyException("The user:"+userName+" can't remove his own permission");
                s.hasPermissionAtStore(store,role);
                if(store.isManagerBy(subscriber, s)) {
                    store.notifyRemoveRole(subscriber, s, role.name());
                    s.removeRole(store, role);
                    Repo.merge(s);
                    return;
                }
                throw new NotManagerByException(store.getStoreName(),subscriber.getUserName(),userName);
            }
        }
        throw new UserNameNotExistException(userName);
    }

    public void addBidPermission(String ownerConnectionID, int storeID, String managerName) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, UserCantAppointHimself, NotManagerByException, LostConnectionException {
        addPermission(ownerConnectionID,storeID,managerName, Roles.Role.STORE_BID);
    }

    public void deleteBidPermission(String connectionId, int storeID, String userName) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, CantRemovePolicyException, NoPermissionException, LostConnectionException {
        deletePermission(connectionId,storeID,userName,Role.STORE_BID);
    }

    public void addOwnerApproval(String connectionId, int storeID, String userName) throws StoreNotFoundException, UserNotSubscriberException, NotOwnerException, ConnectionNotFoundException, UserCantAppointHimself, AlreadyOwnerException, UserNameNotExistException, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
        Store store = getStore(storeID);
        User user = getUser(connectionId);
        if(!(user instanceof Subscriber))
            throw new UserNotSubscriberException();
        Subscriber subscriber = (Subscriber)user;

        if(subscriber.getUserName().equals(userName))
            throw new UserCantAppointHimself();
        subscriber.isStoreOwner(store);
        for (Map.Entry<String,Subscriber> entry : subscribers.entrySet())
        {
            Subscriber s = entry.getValue();
            if(s.getUserName().equals(userName))
            {
                s.isAlreadyStoreOwner(store);
                s.addOwnerApproval(store,subscriber);
                Repo.merge(s);
//                if(s.canBeAppointedToOwner(store)){
//                    store.addOwner(subscriber, entry.getValue());
                   // s.addStoreOwner(store,subscriber);
               // }
                Repo.merge(store);
                return;
            }
        }
        throw new UserNameNotExistException(userName);
    }

    public String getOwnersString(int storeID) throws StoreNotFoundException {
        Store store = getStore(storeID);
        return store.getOwnersString();
    }

    public Collection<String> getPoliciesOfAllStoresExceptOne(int storeId) {
        Collection<String> res = new LinkedList<>();
        for(Map.Entry<Integer,Store> entry : stores.entrySet()){
            int id = entry.getKey();
            Store store = entry.getValue();
            if(id!=storeId){
                Collection<String> c = store.getPoliciesByIDLeafs();
                for(String s : c)
                    res.add(s);
            }
        }
        return res;
    }

    public Collection<String> getDiscountsOfAllStoresExceptOne(int storeId){
        Collection<String> res = new LinkedList<>();
        for(Map.Entry<Integer,Store> entry : stores.entrySet()){
            int id = entry.getKey();
            Store store = entry.getValue();
            if(id!=storeId){
                Collection<String> c = store.getDiscountsByIDLeafs();
                for(String s : c)
                    res.add(s);
            }
        }
        return res;
    }


    public Collection<String> getNotApprovedYetBids(String connectionId, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, IllegalPermissionsAccess, NotOwnerException, UserNotSubscriberException {
        Store store = getStore(storeId);
        if(!isBidPermission(connectionId,store))
            throw new IllegalPermissionsAccess("The current user doesn't have bid permissions for this store.");
        return store.getNotApprovedYetBids();
    }
}




