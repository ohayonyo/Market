package AcceptanceTests;

import AcceptanceTests.Driver;
import Exceptions.*;
import Service.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AcceptanceTests2 {
    private static Service service;



    private String IdOfAdmin;
    private String IdOfStore1Founder;
    private String IdOfStore2Founder;
    private String IdOfStore1Manager;
    private String IdOfStore2Manager;
    private String IdOfSubscriber;
    private String IdOfGuest;

    // list of store Id's
    private int storeId1, storeId2;

    // list of product Id's
    private int computer, tv, streamer, phone, chair, table;

    //list of subscribers user names
    private String manager1UserName = "manager1UserName";
    private String manager2UserName = "manager2UserName";
    private String founder1UserName = "founder1UserName";
    private String founder2UserName = "founder2UserName";
    private String subscriberUserName = "subscriberUserName";
    private String adminUserName = "Admin";
    private String adminPassword = "555sm";

    private int amountPolicy, minBasketPolicy, timePolicy, andPolicy, amountDiscount1, amountDiscount2, plusDiscount, maxDiscount;
    private String card_number = "1234", holder = "a", ccv = "001", id = "000000018", name = "name", address = "address", city = "city", country = "country";
    private int month = 1, year = 2022, zip = 12345;

    @BeforeEach
    void setUp() throws UserExistsException, ConnectionNotFoundException, UserNameNotExistException, UserNotExistException, WrongPasswordException, AlreadyLoggedInException, PaymentException, DeliveryException, AdminNotFoundException, LostConnectionException {
        service = Driver.getRealService(adminUserName, adminPassword);
        IdOfAdmin = Driver.getAdminId();
//        service.login(IdOfAdmin, adminUserName, adminPassword);
    }

    void setUpGuest() throws Exception {
        IdOfGuest = service.entrance();
    }

    void setUpSubscriber() throws Exception {
        IdOfSubscriber = service.entrance();
        service.register(subscriberUserName, "10203040");
        service.login(IdOfSubscriber, subscriberUserName, "10203040");
    }

    void setUpAdmin() throws Exception {
        IdOfAdmin = service.entrance();
        service.login(IdOfAdmin, adminUserName, adminPassword);
    }

    void setUpFounder1() throws Exception {
        IdOfStore1Founder = service.entrance();
        service.register(founder1UserName, "506070");
        service.login(IdOfStore1Founder, founder1UserName, "506070");
    }

    void setUpManager1() throws Exception {
        IdOfStore1Manager = service.entrance();
        service.register(manager1UserName, "1234");
        service.login(IdOfStore1Manager, manager1UserName, "1234");
        service.appointStoreManager(IdOfStore1Founder, storeId1, manager1UserName);
    }

    void setUpStore1() throws Exception {
        setUpFounder1();
        storeId1 = service.openStore(IdOfStore1Founder, "store1");
        computer = service.addProductToStore(IdOfStore1Founder, storeId1, "computer", "gadgets", "new product of store", 15, 3500);
        tv = service.addProductToStore(IdOfStore1Founder, storeId1, "tv", "gadgets", "Sony", 10, 1500);
        streamer = service.addProductToStore(IdOfStore1Founder, storeId1, "streamer", "gadgets", "new product of store", 20, 131.7);
        phone = service.addProductToStore(IdOfStore1Founder, storeId1, "phone", "gadgets", "galaxy", 50, 2200);
        setUpManager1();
    }

    void setUpFounder2() throws Exception {
        IdOfStore2Founder = service.entrance();
        service.register(founder2UserName, "4444");
        service.login(IdOfStore2Founder, founder2UserName, "4444");
    }

    void setUpManager2() throws Exception {
        IdOfStore2Manager = service.entrance();
        service.register(manager2UserName, "6666");
        service.login(IdOfStore2Manager, manager2UserName, "6666");
        service.appointStoreManager(IdOfStore2Founder, storeId2, manager2UserName);
    }

    void setUpStore2() throws Exception {
        setUpFounder2();
        storeId2 = service.openStore(IdOfStore2Founder, "store2");
        chair = service.addProductToStore(IdOfStore2Founder, storeId2, "chair", "Furniture", "nice", 30, 151.9);
        table = service.addProductToStore(IdOfStore2Founder, storeId2, "table", "Furniture", "", 20, 299.9);
        setUpManager2();
    }

    Collection<Integer> store1Items() {
        Collection<Integer> items = new ArrayList<>();
        items.add(computer);
        items.add(tv);
        items.add(streamer);
        items.add(phone);
        return items;
    }

    Collection<Integer> store2Items() {
        Collection<Integer> items = new ArrayList<>();
        items.add(chair);
        items.add(table);
        return items;
    }

    @Test
    void purchaseEmptyCart() throws Exception {
        setUpStore1();
        assertEquals(0, service.getCart(IdOfStore1Founder).size());
        assertThrows(EmptyCartException.class, ()-> service.purchaseCart(IdOfStore1Founder, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

    @Test
    void purchaseItemJustOneStore() throws Exception {
        setUpStore1();
        service.updateShoppingBag(IdOfStore1Founder, storeId1, computer, 2);
        assertEquals(1, service.getCart(IdOfStore1Founder).size());
        service.purchaseCart(IdOfStore1Founder, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        assertEquals(0, service.getCart(IdOfStore1Founder).size());
        assertEquals(1, service.purchaseHistory(IdOfStore1Founder, storeId1).size());
        assertTrue(service.purchaseHistory(IdOfStore1Founder, storeId1).toString().contains("3500.0"));
    }

    @Test
    void purchaseTwoItemsFromDifferentTwoStores() throws Exception {
        setUpStore1();
        setUpStore2();
        service.updateShoppingBag(IdOfStore1Founder, storeId1, computer, 2);
        service.updateShoppingBag(IdOfStore1Founder, storeId2, table, 3);
        assertEquals(2, service.getCart(IdOfStore1Founder).size());
        service.purchaseCart(IdOfStore1Founder, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        assertEquals(0, service.getCart(IdOfStore1Founder).size());
        assertEquals(1, service.purchaseHistory(IdOfStore1Founder, storeId1).size());
        assertTrue(service.purchaseHistory(IdOfStore1Founder, storeId1).toString().contains("3500.0") &&
                service.purchaseHistory(IdOfStore2Founder, storeId2).toString().contains("299.9"));
    }

    @Test
    void validManagerGetHistory() throws Exception {
        setUpStore1();
        service.updateShoppingBag(IdOfStore1Founder, storeId1, computer, 2);
        service.updateShoppingBag(IdOfStore1Founder, storeId1, tv, 3);
        service.purchaseCart(IdOfStore1Founder, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        assertDoesNotThrow(() -> service.purchaseHistory(IdOfStore1Manager, storeId1));
        assertEquals(1, service.purchaseHistory(IdOfStore1Manager, storeId1).size());
        assertTrue(service.purchaseHistory(IdOfStore1Manager, storeId1).toString().contains("computer"));
        assertTrue(service.purchaseHistory(IdOfStore1Manager, storeId1).toString().contains("tv"));
    }

    @Test
    void notValidManagerGetHistory() throws Exception {
        setUpStore1();
        setUpStore2();
        service.updateShoppingBag(IdOfStore1Founder, storeId1, computer, 2);
        service.updateShoppingBag(IdOfStore1Founder, storeId1, tv, 3);
        service.purchaseCart(IdOfStore1Founder, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        assertDoesNotThrow(() -> service.purchaseHistory(IdOfStore1Manager, storeId1));
        assertThrows(NotManagerException.class, () -> service.purchaseHistory(IdOfStore2Manager, storeId1));
    }

    @Test
    void validOwnerGetHistory() throws Exception {
        setUpStore1();
        service.updateShoppingBag(IdOfStore1Founder, storeId1, computer, 2);
        service.updateShoppingBag(IdOfStore1Founder, storeId1, tv, 3);
        service.purchaseCart(IdOfStore1Founder, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        assertDoesNotThrow(() -> service.purchaseHistory(IdOfStore1Founder, storeId1));
        assertEquals(1, service.purchaseHistory(IdOfStore1Founder, storeId1).size());
        assertTrue(service.purchaseHistory(IdOfStore1Founder, storeId1).toString().contains("computer"));
        assertTrue(service.purchaseHistory(IdOfStore1Founder, storeId1).toString().contains("tv"));
    }

    @Test
    void notValidOwnerGetHistory() throws Exception {
        setUpStore1();
        setUpStore2();
        service.updateShoppingBag(IdOfStore1Founder, storeId1, computer, 2);
        service.updateShoppingBag(IdOfStore1Founder, storeId1, tv, 3);
        service.purchaseCart(IdOfStore1Founder, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        assertDoesNotThrow(() -> service.purchaseHistory(IdOfStore1Founder, storeId1));
        assertThrows(NotManagerException.class, () -> service.purchaseHistory(IdOfStore2Founder, storeId1));
    }

    @Test
    void validAllowToEditPurchasePoliciesByStoreOwner() throws Exception {
        setUpStore1();
        service.getStorePolicy(IdOfStore1Founder, storeId1);
        Collection<Integer> store1Items = store1Items();

        amountPolicy = service.addAmountLimitationPolicy(IdOfStore1Founder, storeId1, store1Items, 1, 10);
        minBasketPolicy = service.addMinimalCartPricePolicy(IdOfStore1Founder, storeId1, 50);
        timePolicy = service.addTimePolicy(IdOfStore1Founder, storeId1, store1Items, "20:00", "23:59");
    }

    @Test
    void validAllowToEditDiscountPoliciesByStoreOwner() throws Exception {
        setUpStore1();
        service.getStoreDiscount(IdOfStore1Founder, storeId1);

        Collection<Integer> store1Items = store1Items();
        Collection<Integer> discounts = new LinkedList<>();

        amountDiscount1 = service.addAmountLimitationPolicy(IdOfStore1Founder, storeId1, store1Items, 10, 40);
        amountDiscount2 = service.addAmountLimitationPolicy(IdOfStore1Founder, storeId1, store1Items, 20, 50);
        discounts.add(amountDiscount1);
        discounts.add(amountDiscount2);
    }

    @Test
    void NotValidGetEventLog() throws Exception {
        setUpStore1();
        setUpStore2();
        assertThrows(NotAdminException.class, () -> service.eventLog(IdOfStore1Founder));
        assertThrows(NotAdminException.class, () -> service.eventLog(IdOfStore2Founder));
        assertThrows(NotAdminException.class, () -> service.eventLog(IdOfStore1Manager));
        assertThrows(NotAdminException.class, () -> service.eventLog(IdOfStore2Manager));
    }

    @Test
    void ValidGetEventLog() throws Exception {
        setUpStore1();
        setUpStore2();
        setUpSubscriber();
        setUpAdmin();
        service.updateShoppingBag(IdOfSubscriber, storeId1, computer, 1);
        service.updateShoppingBag(IdOfSubscriber, storeId1, tv, 1);
        //events of opening a store
        service.openStore(IdOfSubscriber, "nice");
        assertTrue(service.eventLog(IdOfAdmin).size() > 0);
        //System.out.println(service.eventLog(IdOfAdmin).toString());
        assertTrue(service.eventLog(IdOfAdmin).toString().contains("Register with userName: Admin"));
        assertTrue(service.eventLog(IdOfAdmin).toString().contains("open store: nice"));
    }

    @Test
    void NotValidGetErrorLog() throws Exception {
        setUpStore1();
        setUpStore2();
        assertThrows(NotAdminException.class, () -> service.errorLog(IdOfStore1Founder));
        assertThrows(NotAdminException.class, () -> service.errorLog(IdOfStore2Founder));
        assertThrows(NotAdminException.class, () -> service.errorLog(IdOfStore1Manager));
        assertThrows(NotAdminException.class, () -> service.errorLog(IdOfStore2Manager));
    }

    @Test
    void ValidGetErrorLog() throws Exception {
        NotValidGetErrorLog();
        setUpAdmin();
        assertTrue(service.errorLog(IdOfAdmin).size() > 0);
        assertTrue(service.errorLog(IdOfAdmin).toString().contains("Not admin try to get hidden information"));
    }

    @Test
    void validGetSubscribersInfo() throws Exception {
        setUpAdmin();
        setUpStore1();
        setUpStore2();
        assertTrue(service.infoAboutSubscribers(IdOfAdmin).size() > 0);
        assertTrue(service.infoAboutSubscribers(IdOfAdmin).toString().contains("founder1UserName"));
    }

    @Test
    void NotValidGetSubscribersInfo() throws Exception {
        setUpStore1();
        setUpStore2();
        setUpSubscriber();
        assertThrows(NotAdminException.class, () -> service.infoAboutSubscribers(IdOfSubscriber));
    }

    @Test
    void validMembershipCancellation() throws Exception {
        setUpAdmin();
        setUpStore1();
        setUpStore2();
        setUpSubscriber();
        assertDoesNotThrow(() -> service.membershipCancellation(IdOfAdmin, subscriberUserName));
    }

    @Test
    void NotValidMembershipCancellation() throws Exception {
        setUpAdmin();
        setUpStore1();
        setUpStore2();
        setUpSubscriber();
        assertThrows(SubscriberHasRolesException.class, () -> service.membershipCancellation(IdOfAdmin, founder1UserName));
    }

    @Test
    void validAddInventoryManagementPermission() throws Exception {
        setUpStore1();
        service.addInventoryManagementPermission(IdOfStore1Founder, storeId1, manager1UserName);
        service.addProductToStore(IdOfStore1Manager, storeId1, "screen", "gadgets", "good", 10, 100);
    }

    @Test
    void NotValidAddInventoryManagementPermission() throws Exception {
        setUpStore1();
        assertThrows(Exception.class, () -> service.addProductToStore(IdOfStore1Manager, storeId1, "screen", "gadgets", "good", 10, 100));
    }

    @Test
    void validDeleteInventoryManagementPermission() throws Exception {
        setUpStore1();
        service.addInventoryManagementPermission(IdOfStore1Founder, storeId1, manager1UserName);
        service.addProductToStore(IdOfStore1Manager, storeId1, "screen", "gadgets", "good", 10, 100);
        service.deleteInventoryManagementPermission(IdOfStore1Founder, storeId1, manager1UserName);
    }

    @Test
    void NotValidDeleteInventoryManagementPermission() throws Exception {
        setUpStore1();
        setUpStore2();
        assertThrows(NotOwnerException.class, () -> service.deleteInventoryManagementPermission(IdOfStore2Founder, storeId1, manager1UserName));
    }

    @Test
    void validAddStorePolicyManagementPermission() throws Exception {
        setUpStore1();
        service.addStorePolicyManagementPermission(IdOfStore1Founder, storeId1, manager1UserName);
        service.addMinimalCartPricePolicy(IdOfStore1Founder, storeId1, 100);
    }

    @Test
    void NotValidAddStorePolicyManagementPermission() throws Exception {
        setUpStore1();
        assertThrows(NotStorePolicyException.class, () -> service.addMinimalCartPricePolicy(IdOfStore1Manager, storeId1, 100));
    }

    @Test
    void validDeleteStorePolicyManagementPermission() throws Exception {
        setUpStore1();
        service.addStorePolicyManagementPermission(IdOfStore1Founder, storeId1, manager1UserName);
        service.deleteStorePolicyManagementPermission(IdOfStore1Founder, storeId1, manager1UserName);
    }

    @Test
    void NotValidDeleteStorePolicyManagementPermission() throws Exception {
        setUpStore1();
        setUpStore2();
        service.addStorePolicyManagementPermission(IdOfStore1Founder, storeId1, manager1UserName);
        assertThrows(NotOwnerException.class, () -> service.deleteStorePolicyManagementPermission(IdOfStore2Founder, storeId1, manager1UserName));
    }

    @Test
    void ValidRemoveOwner() throws Exception {
        setUpStore1();
        service.appointStoreOwner(IdOfStore1Founder, storeId1, manager1UserName);
        service.removeOwner(IdOfStore1Founder, storeId1, manager1UserName);
    }

    @Test
    void NotValidRemoveOwner() throws Exception {
        setUpStore1();
        assertThrows(NotOwnerByException.class, () -> service.removeOwner(IdOfStore1Founder, storeId1, manager1UserName));
    }

    @Test
    void updateShoppingBag() throws Exception {
        setUpStore1();
        service.updateShoppingBag(IdOfStore1Manager, storeId1, streamer, 4);
        service.updateShoppingBag(IdOfStore1Manager, storeId1, phone, 1);
        service.updateShoppingBag(IdOfStore1Manager, storeId1, computer, 5);
        service.purchaseCart(IdOfStore1Manager, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
    }

    @Test
    void validRemoveStoreOwnerByTheOwnerAssignor() throws Exception {
        setUpStore1();
        setUpFounder2();

        service.appointStoreOwner(IdOfStore1Founder, storeId1, founder2UserName);
        service.purchaseHistory(IdOfStore2Founder, storeId1);
        service.removeOwner(IdOfStore1Founder,storeId1, founder2UserName);
        assertThrows(NotManagerException.class , () -> service.purchaseHistory(IdOfStore2Founder, storeId1));
    }

    @Test
    void removeStoreOwnerWithStoreOwnerWhoDidntAssignTheOwner() throws Exception {
        setUpStore1();
        setUpFounder2();

        service.appointStoreOwner(IdOfStore1Founder, storeId1, founder2UserName);
        //owners in store1: IdOfStore1Founder,founder2UserName

        service.appointStoreOwner(IdOfStore2Founder, storeId1,  manager1UserName);//the first appoint is the one who appointed him when all approve
        //service.appointStoreOwner(IdOfStore1Founder, storeId1, founder2UserName);
        service.appointStoreOwner(IdOfStore1Founder, storeId1, manager1UserName);

        assertThrows(NotOwnerByException.class, () -> service.removeOwner(IdOfStore1Founder,storeId1, manager1UserName));
        assertThrows(NotOwnerByException.class, () -> service.removeOwner(IdOfStore1Founder,storeId1, manager1UserName));
        assertDoesNotThrow(()-> service.removeOwner(IdOfStore2Founder,storeId1, manager1UserName));

    }

    @Test
    void removeStoreOwnerByOwnerOfAnotherStore() throws Exception {
        setUpStore1();
        setUpFounder2();

        service.appointStoreOwner(IdOfStore1Founder, storeId1, manager1UserName);
        assertThrows(IllegalPermissionsAccess.class, () -> service.removeOwner(IdOfStore2Founder, storeId1, manager1UserName));
    }

    @Test
    void removeStoreOwnerByManagerOfTheStore() throws Exception {
        setUpStore1();

        assertThrows(IllegalPermissionsAccess.class, () -> service.removeOwner(IdOfStore1Manager, storeId1, founder1UserName));
    }

    @Test
    void removeStoreOwnerRemovesAllTheManagersAndOwnersWithTheRemovedAssignee() throws Exception {
        setUpStore1();
        setUpStore2();

        service.appointStoreOwner(IdOfStore1Founder, storeId1, founder2UserName);
        service.appointStoreOwner(IdOfStore2Founder, storeId1, manager2UserName);
        service.appointStoreManager(IdOfStore2Founder, storeId2,  manager1UserName);
        service.removeOwner(IdOfStore1Founder, storeId1, founder2UserName);
    }

    void updateShoppingBagUseCase() throws Exception {
        setUpStore1();
        setUpStore2();
        setUpSubscriber();
        service.updateShoppingBag(IdOfSubscriber, storeId1, computer, 15);
        service.updateShoppingBag(IdOfSubscriber, storeId1, tv, 4);
        service.updateShoppingBag(IdOfSubscriber, storeId1, phone, 2);
    }

    @Test
    void validPurchaseCart() throws Exception {
        updateShoppingBagUseCase();
        service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, subscriberUserName, subscriberUserName, address, city, country, zip);
    }

    @Test
    void purchaseCartWrongAmount() throws Exception {
        updateShoppingBagUseCase();
        service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        assertThrows(OutOfInventoryException.class, () -> service.updateShoppingBag(IdOfSubscriber, storeId1, computer, 1));
        assertThrows(EmptyCartException.class, () -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip)); //amount in store is currently 0
    }

    @Test
    void purchaseCartWithNoMinimalPrice() throws Exception {
        updateShoppingBagUseCase();
        service.addMinimalCartPricePolicy(IdOfStore1Founder, storeId1, 100000);
        assertThrows(BagNotValidPolicyException.class, () -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
        service.updateShoppingBag(IdOfSubscriber, storeId1, phone, 40);
        assertDoesNotThrow(() -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

    @Test
    void purchaseCartWithNoMinimalPrice1() throws Exception {
        updateShoppingBagUseCase();
        service.addMinimalCartPricePolicy(IdOfStore1Founder, storeId1, 100000);
        assertThrows(BagNotValidPolicyException.class, () -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
        service.resetStorePolicy(IdOfStore1Founder, storeId1);
        assertDoesNotThrow(() -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

    @Test
    void purchaseCartWithNotEnoughAmount() throws Exception {
        updateShoppingBagUseCase();
        List<Integer> lst = new LinkedList<>();
        lst.add(phone);
        service.addAmountLimitationPolicy(IdOfStore1Founder, storeId1, lst, 5, 10);
        assertThrows(BagNotValidPolicyException.class, () -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
        service.updateShoppingBag(IdOfSubscriber, storeId1, phone, 7);
        assertDoesNotThrow(() -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

    @Test
    void purchaseCartWithOverAmount() throws Exception {
        updateShoppingBagUseCase();
        List<Integer> lst = new LinkedList<>();
        lst.add(phone);
        service.addAmountLimitationPolicy(IdOfStore1Founder, storeId1, lst, 5, 10);
        service.updateShoppingBag(IdOfSubscriber, storeId1, phone, 20);
        assertThrows(BagNotValidPolicyException.class, () -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

    @Test
    void purchaseCartWithNoMaximalPrice() throws Exception {
        updateShoppingBagUseCase();
        service.addMaximalCartPricePolicy(IdOfStore1Founder, storeId1, 100);
        assertThrows(BagNotValidPolicyException.class, () -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
        service.resetStorePolicy(IdOfStore1Founder, storeId1);
        assertDoesNotThrow(() -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

    @Test
    void purchaseCartWrongTime() throws Exception {
        updateShoppingBagUseCase();
        List<Integer> lst = new LinkedList<>();
        lst.add(phone);
        service.addTimePolicy(IdOfStore1Founder, storeId1,lst, "12:00", "13:59");
        assertThrows(BagNotValidPolicyException.class, () -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
        service.resetStorePolicy(IdOfStore1Founder, storeId1);
        assertDoesNotThrow(() -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

    @Test
    void purchaseCartGoodTime() throws Exception {
        updateShoppingBagUseCase();
        List<Integer> lst = new LinkedList<>();
        lst.add(phone);
        service.addTimePolicy(IdOfStore1Founder, storeId1,lst, "11:00", "22:00");
        assertDoesNotThrow(() -> service.purchaseCart(IdOfSubscriber, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

}