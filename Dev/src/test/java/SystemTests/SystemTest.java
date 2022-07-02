package SystemTests;

import Exceptions.*;
import OutResources.*;

import Security.UserValidation;
import Store.Discount.IDiscountPolicy;
import Store.PolicyLimitation.IBuyingPolicy;
import Store.Store;
import Store.Product;
import User.User;
import User.ShoppingBag;
import User.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import System.System;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemTest {

    private System system;
    @Mock private DeliveryAdapter deliveryAdapter;
    @Mock private PaymentAdapter paymentAdapter;
    @Mock private UserValidation userValidation;
    @Mock private Map<String, User> users;
    @Mock private Map<Integer, Store> stores;
    private Map<String, User> systemAdmins;
    @Mock private Map<String, Subscriber> subscribers;
    @Mock private User user;
    @Mock private Subscriber subscriber;
    @Mock private Subscriber subscriber2;
    @Mock private Subscriber owner,user1;
    @Mock private Store store;
    @Mock private Store store2;
    @Mock private Product product;
    @Mock private Map<String, String> usersOfValidation;
    @Mock private Map<Store, ShoppingBag> cart1, cart2;
    @Mock private Set<Map.Entry<Store, ShoppingBag>> shoppingBagEntrySet;
    @Mock private Map.Entry<Store, ShoppingBag> entry1, entry2;
    @Mock private Map<Product, Integer> bagProduct;
    @Mock private ShoppingBag shoppingBag1;
    @Mock private IBuyingPolicy buyingPolicy1;
    @Mock private IDiscountPolicy discountPolicy1;
    @Mock private PaymentDetails paymentDetails1;
    @Mock private DeliveryDetails deliveryDetails1;

    private String entrance = "hbh38b";
    private String entrance2 = "fagads224";
    private String userName = "Lin";
    private String userName1 = "fdfd";
    private String userName2 = "Yoad";
    private String password = "hbds732b";
    private String storeName = "Amazon";
    private String productName = "Ball";
    private String category = "water";
    private String keyWord = "fun";
    private String description = "jsdbkldms";

    private int storeId = 1;
    private int storeId2 = 2;
    private int productId = 97;
    private int finalAmount = 10;
    private double price = 65.8;
    private int policyID=1;

    private String card_number = "1234", holder = "a", ccv = "001", id = "000000018", name = "name", address = "address", city = "city", country = "country";
    private int month = 1, year = 2022, zip = 12345;

    @BeforeEach
    void setUp() throws DeliveryException, PaymentException, AdminNotFoundException {
        systemAdmins = mock(Map.class);
        when(systemAdmins.get("Admin")).thenReturn(subscriber);
        userValidation.setUsers(usersOfValidation);
//        deliveryAdapter = new DeliveryAdapterImpl();
//        paymentAdapter= new PaymentAdapterImpl();
        system = new System(deliveryAdapter, paymentAdapter, userValidation, users, subscribers, stores, systemAdmins);
    }


    @Test
    void register() throws UserExistsException, ConnectionNotFoundException, LostConnectionException {
        system.register(userName, password);
        verify(subscribers).put(eq(userName), any(Subscriber.class));
    }

//    @Test
//    void login() throws ConnectionNotFoundException, UserNotExistException, WrongPasswordException, UserNameNotExistException, AlreadyLoggedInException {
//        when(users.containsKey(entrance)).thenReturn(true);
//        when(users.get(entrance)).thenReturn(user);
//        when(subscribers.containsKey(userName)).thenReturn(true);
//        when(subscribers.get(userName)).thenReturn(subscriber);
//        system.login(entrance, userName, password);
//        verify(users).put(entrance, subscriber);
//    }

    @Test
    void logout() throws ConnectionNotFoundException {
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(user);
        system.logout(entrance);
        verify(users).put(eq(entrance), any(User.class));
    }

    @Test
    void openStore() throws ConnectionNotFoundException, IllegalNameException, UserNotSubscriberException, IllegalDiscountException, LostConnectionException {
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        system.openStore(entrance, storeName);
        verify(stores).put(any(Integer.TYPE), any(Store.class));
        verify(subscriber).addStoreFounder(any(Store.class));
    }

    @Test
    void openStoreNotUser() {
        when(users.containsKey(entrance)).thenReturn(false);
        assertThrows(ConnectionNotFoundException.class, () -> system.openStore(entrance, storeName));
    }

    @Test
    void openStoreNotLegal() throws ConnectionNotFoundException, IllegalNameException {
        assertThrows(IllegalNameException.class, () -> system.openStore(entrance, null));
    }

    @Test
    void getCart() throws ConnectionNotFoundException {
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(user);
        system.getCart(entrance);
        verify(user).getCart();
    }

    @Test
    void getCartNotUser() throws ConnectionNotFoundException {
        when(users.containsKey(entrance)).thenReturn(false);
        assertThrows(ConnectionNotFoundException.class, () -> system.getCart(entrance));
    }

    @Test
    void updateShoppingBag() throws ProductNotFoundException, ConnectionNotFoundException, StoreNotFoundException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(store.getProductByID(productId)).thenReturn(product);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(user);
        when(store.isActivate()).thenReturn(true);
        system.updateShoppingBag(entrance, storeId, productId, finalAmount);
        verify(user).updateShoppingBag(store, product, finalAmount);
    }

    @Test
    void appointStoreOwner() throws NotOwnerException, UserNameNotExistException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        when(subscriber.getUserName()).thenReturn(userName1);
        assertThrows(UserNameNotExistException.class, () -> system.appointStoreOwner(entrance, storeId, userName));
        verify(subscriber).isStoreOwner(store);
    }

    @Test
    void appointHimselfAsStoreOwner() {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        when(subscriber.getUserName()).thenReturn(userName1);
        assertThrows(UserCantAppointHimself.class, () -> system.appointStoreOwner(entrance, storeId, userName1));
    }
    
    @Test
    void appointStoreManager() throws NotOwnerException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        when(subscriber.getUserName()).thenReturn(userName1);
        assertThrows(UserNameNotExistException.class, () -> system.appointStoreManager(entrance, storeId, userName));
        verify(subscriber).isStoreOwner(store);
    }

    @Test
    void appointHimselfAsStoreManager() {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        when(subscriber.getUserName()).thenReturn(userName1);
        assertThrows(UserCantAppointHimself.class, () -> system.appointStoreManager(entrance, storeId, userName1));
    }

//    @Test
//    void appointCircularityStoreManager() throws AlreadyOwnerException, ConnectionNotFoundException, StoreNotFoundException, NotOwnerException, AlreadyManagerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, IllegalCircularityException {
//        when(stores.containsKey(storeId)).thenReturn(true);
//        when(stores.containsKey(storeId2)).thenReturn(true);
//        when(stores.get(storeId)).thenReturn(store);
//        when(stores.get(storeId2)).thenReturn(store2);
//        when(users.containsKey(entrance)).thenReturn(true);
//        when(users.containsKey(entrance2)).thenReturn(true);
//        when(users.get(entrance)).thenReturn(subscriber);
//        when(users.get(entrance2)).thenReturn(subscriber2);
//        when(subscriber.getUserName()).thenReturn(userName1);
//        when(subscriber2.getUserName()).thenReturn(userName2);
////        when(subscribers.containsKey(userName1)).thenReturn(true);
////        when(subscribers.containsKey(userName2)).thenReturn(true);
////        when(subscribers.get(userName1)).thenReturn(subscriber);
////        when(subscribers.get(userName2)).thenReturn(subscriber2);
//        when(system.getSubscribers().contains(userName1)).thenReturn(true);
//        when(system.getSubscribers().contains(userName2)).thenReturn(true);
//        when(system.getSubscriber(userName1)).thenReturn(subscriber);
//        when(system.getSubscriber(userName2)).thenReturn(subscriber2);
//        system.appointStoreManager(entrance,storeId,userName2);
//        assertThrows(IllegalCircularityException.class, () -> system.appointStoreManager(entrance2,storeId2,userName1));
//        verify(store).addManager(subscriber,subscriber2);
//    }

    @Test
    void closeStore() throws NotFounderException, StoreNotActiveException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, LostConnectionException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        system.closeStore(entrance, storeId);
        verify(subscriber).isStoreFounder(store);
        verify(store).closeStore();
    }

    @Test
    void purchasesHistory() throws ConnectionNotFoundException, StoreNotFoundException, NotOwnerException, UserNotSubscriberException, NotManagerException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        system.purchasesHistory(entrance, storeId);
        verify(subscriber).isStoreManager(store);
        verify(store).getHistory();
    }

    @Test
    void purchaseEmptyCart() throws ConnectionNotFoundException, InsufficientInventory, ProductNotFoundException, PaymentException, DeliveryException, BagNotValidPolicyException, IllegalDiscountException, EmptyCartException, IllegalBuyerException {
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(user);
        assertThrows(EmptyCartException.class, ()-> system.purchaseCart(entrance, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }



    @Test
    void eventLogAdmin() throws IOException, NotAdminException, ConnectionNotFoundException, UserNotSubscriberException {
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        when(subscriber.getUserName()).thenReturn("Barak");
        when(systemAdmins.containsKey("Barak")).thenReturn(true);
        system.eventLog(entrance);
    }

    @Test
    void eventLogNotAdmin()
    {
        assertThrows(ConnectionNotFoundException.class, () -> system.eventLog(entrance));
    }

    @Test
    void entrance() throws LostConnectionException {
        String connectionId = system.entrance();
        verify(users).put(anyString(), any(User.class));
        String uuid = java.util.UUID.randomUUID().toString();
        assertNotSame(uuid, connectionId);
        assertEquals(uuid.length(), connectionId.length());
    }

    @Test
    void addProductToStore() throws ConnectionNotFoundException, StoreNotFoundException, NotOwnerException, StoreNotActiveException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        system.addProductToStore(entrance, storeId, productName, category, description, finalAmount, price);
        verify(subscriber).isStoreInventoryManager(store);
        verify(store).isStroeActivated();
        verify(store).addProduct(any(Product.class), eq(finalAmount));
    }

    @Test
    void deleteProductFromStore() throws ConnectionNotFoundException, StoreNotFoundException, NotOwnerException, ProductNotFoundException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        system.deleteProductFromStore(entrance, storeId, productId);
        verify(subscriber).isStoreInventoryManager(store);
        verify(store).removeProduct(productId);
    }

    @Test
    void updateProductDetails() throws ConnectionNotFoundException, StoreNotFoundException, NotOwnerException, InsufficientInventory, ProductNotFoundException, IllegalNameException, IllegalPriceException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        system.updateProductDetails(entrance, storeId, productId, category, description, finalAmount, price);
        verify(subscriber).isStoreInventoryManager(store);
        verify(store).updateProduct(productId, category, description, finalAmount, price);
    }

    @Test
    void storeRoles() throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        system.storeRoles(entrance, storeId);
        verify(subscriber).isStoreOwner(store);
    }

    @Test
    void isAdminForAdmin() throws NotAdminException {
        when(systemAdmins.containsKey(entrance)).thenReturn(true);
        system.isAdmin(entrance);
    }

    @Test
    void isAdminNotAdmin() {
        when(systemAdmins.containsKey(entrance)).thenReturn(false);
        assertThrows(NotAdminException.class, () -> system.isAdmin(entrance));
    }

    @Test
    void adminPurchasesHistory() throws StoreNotFoundException, NotAdminException, ConnectionNotFoundException, UserNotSubscriberException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        when(subscriber.getUserName()).thenReturn(userName1);
        when(systemAdmins.containsKey(userName1)).thenReturn(true);
        system.adminPurchasesHistory(entrance, storeId);
        verify(store).getHistory();
    }

    @Test
    void adminPurchasesHistoryNotAdmin() throws StoreNotFoundException, NotAdminException {
        when(stores.containsKey(storeId)).thenReturn(true);
        when(stores.get(storeId)).thenReturn(store);
        when(users.containsKey(entrance)).thenReturn(true);
        when(users.get(entrance)).thenReturn(subscriber);
        when(subscriber.getUserName()).thenReturn(userName1);
        when(systemAdmins.containsKey(userName1)).thenReturn(false);
        assertThrows(NotAdminException.class, () -> system.adminPurchasesHistory(entrance, storeId));
    }


//    @Test
//    void assignExistingBuyingPolicy() throws StoreNotFoundException, ConnectionNotFoundException, PolicyAlreadyExist, NotOwnerException, UserNotSubscriberException, PolicyNotFoundExceptyion, IllegalPermissionsAccess {
//        when(stores.containsKey(storeId)).thenReturn(true);
//        when(stores.get(storeId)).thenReturn(store);
////        system.assignExistingBuyingPolicy(entrance,policyID,storeId);
//        verify(system.getPolicyByID(policyID));
//    }


//    @Test
//    void removeManager() throws AlreadyManagerException, NoApointedUsersException {
//        when(stores.containsKey(storeId)).thenReturn(true);
//        when(stores.get(storeId)).thenReturn(store);
//        when(store.getOwnersBy().containsKey(owner)).thenReturn(true);
//        store.addManager(owner,user1);
//        assertTrue(store.getOwnersBy().containsKey(owner));
//        assertTrue(store.getOwnersBy().get(owner).contains(user1));
//        store.removeManager(owner,user1);
//        assertTrue(!store.getOwnersBy().containsKey(owner));
//        assertTrue(!store.getOwnersBy().get(owner).contains(user1));
//    }
}