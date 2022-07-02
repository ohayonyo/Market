package MarketServiceTests;

import Exceptions.*;
import Service.MarketService;
import Service.Service;
import Store.Product;
import Store.Store;
import System.System;
import User.ShoppingBag;
import User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MarketServiceTest {

    private Service marketService;
    @Mock private System system;
    @Mock private List<Product> products;
    @Mock private Collection<String> roles;
    @Mock private Product product1;
    @Mock private Product product2;
    @Mock private User user;
    @Mock private Store store;
    @Mock private ShoppingBag shoppingBag;

    private String entrance1 = "999";
    private String userName1 = "Admin";
    private String password1 = "admin123";
    private String prouductName1 = "Ball";
    private String category1 = "Fun";
    private String keyWord1 = "Swimming";
    private String storeName = "Ebay";
    private String description = "blah blah";
    private double startRange = 1.0;
    private double endRange = 120.5;
    private int storeId = 55;
    private int productId = 187;
    private int finalAmount = 10;

    private String card_number = "1234", holder = "a", ccv = "001", id = "000000018", name = "name", address = "address", city = "city", country = "country";
    private int month = 1, year = 2022, zip = 12345;

    @BeforeEach
    void setUp()
    {
        marketService = new MarketService(system);
    }


    @Test
    void entrance() throws Exception {
        when(system.entrance()).thenReturn("unique");
        marketService.entrance();
        verify(system).entrance();
        assertEquals("unique", marketService.entrance());
    }

    @Test
    void register() throws UserExistsException, ConnectionNotFoundException, LostConnectionException {
        marketService.register(userName1, password1);
        verify(system).register(userName1, password1);
    }

    @Test
    void login() throws UserNotExistException, WrongPasswordException, ConnectionNotFoundException, UserNameNotExistException, AlreadyLoggedInException, LostConnectionException {
        marketService.login(entrance1, userName1, password1);
        verify(system).login(entrance1, userName1, password1);
    }

//    @Test
//    void marketInfo() throws IllegalNameException, StoreNotActiveException {
//        Store store = new Store("Amazon");
//        Set<Product> products = new HashSet<Product>();
//        Map<Store, Set<Product>> marketInfo = new HashMap<>();
//        marketInfo.put(store,products);
//        when(system.MarketInfo()).thenReturn(marketInfo);
//        Collection<String> result = marketService.MarketInfo();
//        verify(system).MarketInfo();
//        assertEquals(1, result.size());
//        assertTrue( result.contains("Amazon: []"));
//    }

    @Test
    void searchProductByName() throws StoreNotActiveException, LostConnectionException {
        when(system.searchProductByName(prouductName1)).thenReturn(products);
        Collection<Product> result = marketService.searchProductByName(prouductName1);
        verify(system).searchProductByName(prouductName1);
        assertEquals(products, result);
    }

    @Test
    void searchProductByCategory() throws StoreNotActiveException, LostConnectionException {
        when(system.searchProductByCategory(category1)).thenReturn(products);
        Collection<Product> result = marketService.searchProductByCategory(category1);
        verify(system).searchProductByCategory(category1);
        assertEquals(products, result);
    }

    @Test
    void searchProductByKeyWord() throws StoreNotActiveException, LostConnectionException {
        when(system.searchProductByKeyWord(keyWord1)).thenReturn(products);
        Collection<Product> result = marketService.searchProductByKeyWord(keyWord1);
        verify(system).searchProductByKeyWord(keyWord1);
        assertEquals(products, result);
    }

    @Test
    void filterByPrices() throws LostConnectionException {
        List<Product> prods = new LinkedList<Product>();
        prods.add(product1);
        prods.add(product2);
        when(product1.getPrice()).thenReturn(111.0);
        when(product2.getPrice()).thenReturn(444.5);
        Collection<Product> result = marketService.filterByPrices(prods, startRange, endRange);
        assertTrue(result.size() == 1);
        assertTrue(result.contains(product1));

    }

    @Test
    void filterByCategory() throws LostConnectionException {
        List<Product> prods = new LinkedList<Product>();
        prods.add(product1);
        prods.add(product2);
        when(product1.getCategory()).thenReturn("Fun");
        when(product2.getCategory()).thenReturn("illness");
        Collection<Product> result = marketService.filterByCategory(prods, category1);
        assertTrue(result.size() == 1);
        assertTrue(result.contains(product1));
    }

    @Test
    void updateShoppingBag() throws ProductNotFoundException, ConnectionNotFoundException, StoreNotFoundException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        marketService.updateShoppingBag(entrance1, storeId, productId, finalAmount);
        verify(system).updateShoppingBag(entrance1, storeId, productId, finalAmount);
    }

    @Test
    void getCart() throws IllegalNameException, ConnectionNotFoundException, LostConnectionException {
        ShoppingBag shoppingBag = new ShoppingBag(store, user);
        shoppingBag.addSingleProduct(product1, 10);
        Map<Store, ShoppingBag> cart = new HashMap<>();
        cart.put(store,shoppingBag);
        when(system.getCart(entrance1)).thenReturn(cart);
        when(product1.getName()).thenReturn("Milk");
        Collection<String> result = marketService.getCart(entrance1);
        verify(system).getCart(entrance1);
        assertEquals(1, result.size());
        assertTrue( result.toString().contains("Milk"));
    }

    @Test
    void purchaseCart() throws InsufficientInventory, ProductNotFoundException, ConnectionNotFoundException, BagNotValidPolicyException, IllegalDiscountException, PaymentException, DeliveryException, EmptyCartException, IllegalBuyerException, LostConnectionException {
        marketService.purchaseCart(entrance1, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        verify(system).purchaseCart(entrance1, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
    }

    @Test
    void logout() throws ConnectionNotFoundException, LostConnectionException {
        marketService.logout(entrance1);
        verify(system).logout(entrance1);
    }

    @Test
    void openStore() throws IllegalNameException, ConnectionNotFoundException, UserNotSubscriberException, IllegalDiscountException, LostConnectionException {
        marketService.openStore(entrance1, storeName);
        verify(system).openStore(entrance1, storeName);
    }

    @Test
    void addProductToStore() throws Exception {
        when(system.addProductToStore(entrance1, storeId, prouductName1, category1, description , finalAmount, endRange)).thenReturn(4);
        marketService.addProductToStore(entrance1, storeId, prouductName1, category1, description , finalAmount, endRange);
        verify(system).addProductToStore(entrance1, storeId, prouductName1, category1, description , finalAmount, endRange);
        assertEquals(4, marketService.addProductToStore(entrance1, storeId, prouductName1, category1, description , finalAmount, endRange));
    }

    @Test
    void deleteProductFromStore() throws Exception {
        marketService.deleteProductFromStore(entrance1, storeId, productId);
        verify(system).deleteProductFromStore(entrance1, storeId, productId);
    }

    @Test
    void updateProductDetails() throws Exception {
        marketService.updateProductDetails(entrance1, storeId, productId, category1, description, finalAmount, endRange);
        verify(system).updateProductDetails(entrance1, storeId, productId, category1, description, finalAmount, endRange);
    }

//    @Test
//    void appointStoreOwner() throws AlreadyOwnerException, NotOwnerException, UserNameNotExistException, IllegalPermissionsAccess, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, IllegalCircularityException, UserCantAppointHimself, AllTheOwnersNeedToApproveAppointement {
//        marketService.appointStoreOwner(entrance1, storeId, userName1);
//        verify(system).appointStoreOwner(entrance1, storeId, userName1);
//    }

    @Test
    void appointStoreManager() throws AlreadyOwnerException, NotOwnerException, AlreadyManagerException, UserNameNotExistException, IllegalPermissionsAccess, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, UserCantAppointHimself, IllegalCircularityException, LostConnectionException {
        marketService.appointStoreManager(entrance1, storeId, userName1);
        verify(system).appointStoreManager(entrance1, storeId, userName1);
    }

    @Test
    void closeStore() throws NotFounderException, StoreNotActiveException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, LostConnectionException {
        marketService.closeStore(entrance1, storeId);
        verify(system).closeStore(entrance1, storeId);
    }

    @Test
    void storeRoles() throws NotOwnerException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException {
        when(system.storeRoles(entrance1, storeId)).thenReturn(roles);
        marketService.storeRoles(entrance1, storeId);
        verify(system).storeRoles(entrance1, storeId);
        assertEquals(roles, marketService.storeRoles(entrance1, storeId));
    }

    @Test
    void purchaseHistory() throws NotOwnerException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotManagerException, LostConnectionException {
        List<String> history = new LinkedList<>();
        when(shoppingBag.toString()).thenReturn("HIHI");
        history.add(shoppingBag.toString());
        when(system.purchasesHistory(entrance1, storeId)).thenReturn(history);
        Collection<String> result = marketService.purchaseHistory(entrance1, storeId);
        verify(system).purchasesHistory(entrance1, storeId);
        assertEquals(1, result.size());
        assertTrue( result.contains("HIHI"));
    }

    @Test
    void adminPurchaseHistory() throws NotAdminException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException {
        List<String> history = new LinkedList<>();
        when(shoppingBag.toString()).thenReturn("HIHI");
        history.add(shoppingBag.toString());
        when(system.adminPurchasesHistory(entrance1, storeId)).thenReturn(history);
        Collection<String> result = marketService.adminPurchaseHistory(entrance1, storeId);
        verify(system).adminPurchasesHistory(entrance1, storeId);
        assertEquals(1, result.size());
        assertTrue( result.contains("HIHI"));
    }

    @Test
    void eventLog() throws IOException, NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException {
        List<String> log = new LinkedList<>();
        log.add("register:123");
        log.add("register:444");
        log.add("register:876");
        when(system.eventLog(entrance1)).thenReturn(log);
        Collection<String> result = marketService.eventLog(entrance1);
        verify(system).eventLog(entrance1);
        assertEquals(3, result.size());
        assertTrue( result.contains("register:123"));
        assertFalse( result.remove("register:321"));
    }
}
