package AcceptanceTests;

import Exceptions.*;
import Service.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class AcceptanceTests {

    private static Service service;
    private int storeId1, storeId2, storeId3; //stores
    private int productIdFor100,productIdFor99,  productId1, productId2, productId3, productId4, productId5, productId6; //products
    private String admin1Id, founderStore1Id, founderStore2Id, founderStore3Id, store1Manager1Id,store1Manager2Id, subs1Id, subs2Id, subs3Id, guest1Id; //users Id's
    //user names:
    private String userad1="Admin1", store1FounderUser="store1FounderUser", store2FounderUser="store2FounderUser",
            store1Manager1User="Store1Manager1User", subs1User = "subs1User",store1Manager2User="Store1Manager2User",subs2UserName = "subs2User",
            subs3User = "subs3User", guest1User = "guest1User";

    private String card_number = "1234", holder = "a", ccv = "001", id = "000000018", name = "name", address = "address", city = "city", country = "country";
    private int month = 1, year = 2022, zip = 12345;


    @BeforeEach
    public void setUp() throws Exception {

        service = Driver.getRealService("Admin", "1234"); //Gets details of system manager to register into user validator
        admin1Id = Driver.getAdminId();
        founderStore1Id = service.entrance();
        founderStore2Id = service.entrance();
        founderStore3Id = service.entrance();
        store1Manager1Id = service.entrance();
        store1Manager2Id = service.entrance();
        subs1Id = service.entrance();
        subs2Id = service.entrance();
        subs3Id = service.entrance();
        guest1Id = service.entrance();


        service.register("store1FounderUser", "1234");
        service.register("store2FounderUser", "1234");
        service.register("store3FounderUser", "1234");
        service.register("Store1Manager1User", "1234");
        service.register("Store1Manager2User", "1234");
        service.register("subs1User", "1234");
        service.register("subs2User", "1234");
        service.register("subs3User", "1234");

        service.login(admin1Id, "Admin", "1234");
        service.login(founderStore1Id, "store1FounderUser", "1234"); //storeId1 founder
        service.login(founderStore2Id, "store2FounderUser", "1234"); //storeId2 founder
        service.login(founderStore3Id, "store3FounderUser", "1234"); //storeId3 founder
        service.login(store1Manager1Id, "Store1Manager1User", "1234");
        service.login(subs1Id, "subs1User", "1234");
        service.login(subs2Id, "subs2User", "1234");
        service.login(subs3Id, "subs3User", "1234");

        storeId1 = service.openStore(founderStore1Id, "store1");
        productId1 = service.addProductToStore(founderStore1Id, storeId1, "shoko", "DairyProducts", "desc1", 8, 4);
        productId2 = service.addProductToStore(founderStore1Id, storeId1, "cheese", "DairyProducts", "desc2", 30, 15);
        productIdFor100 = service.addProductToStore(founderStore1Id, storeId1, "milk", "DairyProducts", "desc100", 100, 10);
        productIdFor99 = service.addProductToStore(founderStore1Id, storeId1, "bread", "DairyProducts", "desc100", 99, 10);


        storeId2 = service.openStore(founderStore2Id, "store2");
        productId3 = service.addProductToStore(founderStore2Id, storeId2, "pizza", "DairyProducts", "desc3", 30, 6.5);
        productId4 = service.addProductToStore(founderStore2Id, storeId2, "cheese", "DairyProducts", "desc4", 20, 9);

        storeId3 = service.openStore(founderStore3Id, "store3");
        productId5 = service.addProductToStore(founderStore3Id, storeId3, "cheese", "DairyProducts", "desc5", 30, 6.5);
        productId6 = service.addProductToStore(founderStore3Id, storeId3, "shoko", "DairyProducts", "desc6", 20, 9);

        service.appointStoreManager(founderStore1Id, storeId1, store1Manager1User);





    }

    //requirement 1.1
    @Test
    void systemCreateSucceed() throws Exception{
        assertThrows(UserExistsException.class, () -> service.register("Admin", "1234"));
    }

    //todo check for better test
    @Test
    void systemCreateIncorrectly() throws Exception{
        assertThrows(UserExistsException.class, () -> service.register("Admin", "1234"));
    }


    //requirement 1.3
    @Test
    void payment() throws Exception{
        service.updateShoppingBag(guest1Id, storeId1, productId1, 1);
        assertDoesNotThrow(() -> service.purchaseCart(guest1Id, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }


    @Test
    void paymentForInsufficientInventory() throws Exception{
        assertThrows(OutOfInventoryException.class ,() -> service.updateShoppingBag(guest1Id, storeId1, productId1, 20));
        assertThrows(EmptyCartException.class ,() -> service.purchaseCart(guest1Id, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

    //requirement 1.4
    @Test
    void validPayment() throws Exception {
        service.updateShoppingBag(subs1Id, storeId1,productId1, 2 );
        assertDoesNotThrow(() -> service.purchaseCart(subs1Id, card_number, month, year, holder, ccv, id, name, address, city, country, zip));
    }

    //Currently irrelevant
//    void InValidPayment() throws Exception {
//        service.updateShoppingBag(subs1Id, storeId1,productId1, 2 );
//        assertDoesNotThrow(() -> service.purchaseCart(subs1Id));
//    }


    //requirement 2.1.1
    @Test
    void validGuestEntrance  () throws Exception{
        assertTrue(service.entrance()!= null);
    }
    //requirement 2.1.2 We do not have market leave method in service at the moment.

    //requirement 2.1.3
    @Test
    void registerGoodDetails() throws Exception{
        assertDoesNotThrow(() -> service.register("YoadOhayon", "100"));
        assertDoesNotThrow(() -> service.register("OmerShitrit", "100"));
    }

    @Test
    void registerExistingUsername() throws Exception{
        assertDoesNotThrow(() -> service.register("YoadOhayon", "100"));
        assertThrows( UserExistsException.class ,() -> service.register("YoadOhayon", "10111"));
    }

    //requirement 2.1.4
    @Test
    void validCredentialsLogin() throws Exception{
        service.register("YoadOhayon", "100");
        service.register("OmerShitrit", "100");
        assertDoesNotThrow(() -> service.login(subs1Id,"YoadOhayon", "100"));
        assertDoesNotThrow(() -> service.login(subs2Id,"OmerShitrit", "100"));
    }

    @Test
    void invalidCredentialsLogin() throws Exception{
        service.register("YoadOhayon", "100");
        service.register("OmerShitrit", "100");
        assertThrows(WrongPasswordException.class ,() -> service.login(subs1Id,"YoadOhayon", "30000"));
        assertThrows(WrongPasswordException.class ,() -> service.login(subs2Id,"OmerShitrit", "333300"));
    }

    //requirement 2.2.1
    @Test
    void getMarketInformation() throws Exception{
        assertTrue(service.MarketInfo(admin1Id).size()>0); //there are 3 stores opened
    }

    @Test
    void getMarketUserIsntAvailable() throws Exception{
        assertDoesNotThrow(() -> service.MarketInfo("3234")); //there are 3 stores opened
        String ans = service.MarketInfo(admin1Id).toString();
        assertTrue(ans.contains("store1") &&
                ans.contains("store2") &&
                ans.contains("store3")); //there are 3 stores opened
    }

    //requirement 2.2.2
    @Test
    void searchByProductExistsName() throws Exception{
        assertTrue(!service.searchProductByName("shoko").isEmpty()); //Prudoct Exists
    }

    @Test
    void searchByProductNotExistsName() throws Exception{
        assertTrue(service.searchProductByName("Cariot").isEmpty()); //Prudoct Not Exists
    }

    @Test
    void searchByPruductExistsCategory() throws Exception{
        assertTrue(!service.searchProductByCategory("DairyProducts").isEmpty()); //Category Exists
    }

    @Test
    void searchByCategoryNotExistsCategory() throws Exception{
        assertTrue(service.searchProductByCategory("Shoes").isEmpty()); //Category Not Exists
    }

    @Test
    void searchByProductExistsKeyWord() throws Exception{
        assertFalse(service.searchProductByKeyWord("shoko").isEmpty()); //Keyword Exists
    }

    @Test
    void searchByProductNotExistsKeyWord() throws Exception{
        assertTrue(service.searchProductByKeyWord("gnfgfgngdhdfbdfb").isEmpty()); //Keyword Not Exists
    }

    //requirement 2.2.3
    @Test
    void saveProduct() throws Exception{
        service.updateShoppingBag(guest1Id, storeId1, productId1, 1);
        assertTrue(service.getCart(guest1Id).toString().contains("store1"));
        assertTrue(service.getCart(guest1Id).toString().contains("shoko"));
    }

    //requirement 2.2.4.1
    @Test
    void validCart() throws Exception{
        service.updateShoppingBag(guest1Id, storeId1, productId1, 1);
        assertTrue(!service.getCart(guest1Id).isEmpty());
        assertTrue(service.getCart(guest1Id).toString().contains("store1"));
        assertTrue(service.getCart(guest1Id).toString().contains("shoko"));
    }

    //requirement 2.2.4.2
    @Test
    void changesApplied() throws Exception {
        service.updateShoppingBag(guest1Id, storeId1, productId1, 1);
        Collection<String> cart1=service.getCart(guest1Id);
        service.updateShoppingBag(guest1Id, storeId1, productId1, 2);
        Collection<String> cart2=service.getCart(guest1Id);
        assertTrue(cart1.toString().substring(0,10).equals(cart2.toString().substring(0,10)));
    }


    //requirement 2.3.1
    @Test
    void leaveMarket() throws Exception {
        Collection<String> cart1=service.getCart(guest1Id);
        assertDoesNotThrow(() -> service.logout(guest1Id));
        Collection<String> cart2=service.getCart(guest1Id);
        assertTrue(cart1.equals(cart2));
    }


    //requirement 2.3.2
    @Test
    void openStoreValidName() throws Exception {
        assertTrue(service.openStore(admin1Id, "King BBB")>=0);
    }

    @Test
    void openStoreInvalidName() throws Exception {
        assertThrows(IllegalNameException.class , ()-> service.openStore(admin1Id, ""));
    }

    //requirement 4.1
    @Test
    void addProductToStoreInventory() throws Exception {
        assertDoesNotThrow(()-> service.addProductToStore(founderStore1Id, storeId1, "tzah", "Soups", "desc", 10, 5));
    }

    @Test
    void addExistingProductToStoreInventory() throws Exception {
        assertDoesNotThrow(()-> service.addProductToStore(founderStore1Id, storeId1, "tzah", "Soups", "desc", 10, 5));
        assertDoesNotThrow(()-> service.addProductToStore(founderStore1Id, storeId1, "shoko", "Soups", "desc", 10, 5));
    }

    //requirement 4.4
    @Test
    void managerAddingSuccess() throws Exception {
        assertDoesNotThrow(()-> service.appointStoreManager(founderStore1Id, storeId1, "Admin"));
        assertTrue(service.storeRoles(founderStore1Id,storeId1).size()==3); //2 store managers
    }

    @Test
    void managerAddingExistingManager() throws Exception {
        service.appointStoreManager(founderStore1Id, storeId1, store2FounderUser);
        assertThrows(AlreadyManagerException.class , () ->service.appointStoreManager(founderStore1Id, storeId1, store2FounderUser)); //same manager
    }

    //requirement 4.6
    @Test
    void ownerAddingSuccess() throws Exception {
        assertDoesNotThrow(()-> service.appointStoreOwner(founderStore1Id, storeId1, store2FounderUser));
        assertTrue(service.storeRoles(founderStore2Id,storeId1).size()==3); //3 store owners
    }

    @Test
    void ownerAddingExistingOwner() throws Exception {
        service.appointStoreOwner(founderStore1Id, storeId1, store2FounderUser);
        assertThrows(AlreadyOwnerException.class , () ->service.appointStoreOwner(founderStore1Id, storeId1, store2FounderUser)); //same manager
    }

    //requirement 4.9
    @Test
    void closeStoreSuccess() throws Exception {
        assertDoesNotThrow(()-> service.closeStore(founderStore1Id, storeId1));
    }

    @Test
    void closeStoreNotManager() throws Exception {
        assertThrows(NotFounderException.class, ()-> service.closeStore(founderStore2Id, storeId1));
    }

    //requirement 4.11
    @Test
    void storeRolesSuccess() throws Exception {
        assertTrue(!service.storeRoles(founderStore1Id,storeId1).isEmpty());
    }

    @Test
    void storeRolesFailed() throws Exception {
        assertThrows( NotOwnerException.class, () -> service.storeRoles(founderStore2Id,storeId1));
    }

    //requirement 4.13
    @Test
    void gotPurchaseHistory() throws Exception {
        service.updateShoppingBag(store1Manager1Id, storeId1, productId1, 1);
        service.updateShoppingBag(store1Manager1Id, storeId1, productId2, 1);
        Collection<String> str = service.purchaseHistory(founderStore2Id,storeId2);
        service.purchaseCart(store1Manager1Id, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        assertTrue(str.size() == 0);
        assertTrue(service.purchaseHistory(founderStore1Id, storeId1).toString().contains("shoko"));
        //assertTrue(service.purchaseHistory(founderStore1Id, storeId1).toString().contains("cheese"));
        service.updateShoppingBag(store1Manager1Id, storeId1, productId2, 1);
        service.purchaseCart(store1Manager1Id, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        str = service.purchaseHistory(founderStore1Id, storeId1);
        assertTrue(str.size() == 2);
    }

    @Test
    void gotPurchaseHistoryNotOwner() throws Exception {
        assertThrows(NotManagerException.class, ()-> service.purchaseHistory(founderStore2Id,storeId1));
    }


    //requirement 5.1
    @Test
    void managementActionsSucceed() throws Exception {
        service.appointStoreOwner(founderStore1Id, storeId1, store2FounderUser);
        assertDoesNotThrow(()-> service.addProductToStore(founderStore2Id, storeId1, "pizza", "DairyProducts", "desc3", 30, 6.5));
    }

    @Test
    void managementActionsNotManager() throws Exception {
        assertThrows(NotInventoryManagementException.class, ()-> service.addProductToStore(founderStore2Id, storeId1, "pizza", "DairyProducts", "desc3", 30, 6.5));
    }

    //requirement 6.4
    @Test
    void gotPurchaseHistoryManager() throws Exception {
        service.updateShoppingBag(store1Manager1Id, storeId1, productId1, 1);
        service.updateShoppingBag(store1Manager1Id, storeId1, productId2, 1);
        Collection<String> str = service.purchaseHistory(founderStore2Id,storeId2);
        service.purchaseCart(store1Manager1Id, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        Collection<String> str1 = service.purchaseHistory(founderStore1Id,storeId1);
        assertTrue(str.size() == 0);
        assertTrue(service.purchaseHistory(founderStore1Id, storeId1).toString().contains("shoko"));
        //assertTrue(service.purchaseHistory(founderStore1Id, storeId1).toString().contains("cheese"));
        service.updateShoppingBag(store1Manager1Id, storeId1, productId2, 1);
        service.purchaseCart(store1Manager1Id, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        str = service.purchaseHistory(founderStore1Id, storeId1);
        assertTrue(str.size() == 2);
    }

    @Test
    void gotPurchaseHistoryNotManager() throws Exception {
        assertThrows(NotManagerException.class, ()-> service.purchaseHistory(founderStore1Id,storeId2));
    }

    @Test
    void eventLog() throws Exception{
        service.updateShoppingBag(subs1Id, storeId1, productId1, 1);
        service.updateShoppingBag(subs1Id, storeId1, productId2, 1);
        //events of opening a store
        service.openStore(subs1Id, "store3");
        assertTrue(service.eventLog(admin1Id).size() > 0);
       // assertTrue(service.eventLog(admin1Id).toString().contains("Register with userName: Admin"));
        assertTrue(service.eventLog(admin1Id).toString().contains("open store: store3"));
    }

    @Test
    void permissionsErrorEventLog() throws Exception{
        assertThrows(NotAdminException.class, () -> service.eventLog(founderStore1Id)); //founderStore1Id isn't admin
        assertThrows(UserNotSubscriberException.class, () -> service.eventLog(guest1Id)); //guest1Id isn't a subscriber

    }

    @Test
    void appointStoreManagerSuccess() throws Exception{
        assertThrows(NotManagerException.class, () -> service.purchaseHistory(founderStore2Id, storeId1));
        service.appointStoreManager(founderStore1Id, storeId1, store2FounderUser);
        service.purchaseHistory(founderStore2Id, storeId1);

        assertThrows(NotManagerException.class, () -> service.purchaseHistory(subs1Id, storeId2));
        service.appointStoreManager(founderStore2Id, storeId2, subs1User);
        service.purchaseHistory(subs1Id, storeId2);

        assertThrows(NotManagerException.class, () -> service.purchaseHistory(subs1Id, storeId1));
        service.appointStoreManager(founderStore1Id, storeId1, subs1User);
        service.purchaseHistory(subs1Id, storeId1);

    }

    @Test
    void appointAnAlreadyStoreManager() throws Exception{
        service.appointStoreManager(founderStore1Id, storeId1 ,"subs1User");
        assertThrows(AlreadyManagerException.class, () -> service.appointStoreManager(founderStore1Id, storeId1 ,"subs1User"));
    }

    @Test
    void appointGuestToStoreManager() throws Exception{
        assertThrows(UserNameNotExistException.class, () -> service.appointStoreManager(founderStore1Id, storeId1 ,"guest1User"));
    }

    @Test
    void appointStoreManagerFail() throws Exception{
        assertThrows(NotOwnerException.class, () -> service.appointStoreManager(founderStore2Id, storeId1, subs1User)); //founderStore2Id not owner
        assertThrows(NotOwnerException.class, () -> service.appointStoreManager(store1Manager1Id, storeId1, subs2UserName)); //store1Manager1Id not owner

    }

    @Test
    void appointStoreOwnerSuccess() throws Exception{
        assertThrows(NotOwnerException.class, () -> service.appointStoreManager(founderStore1Id, storeId2, "subs1User")); //founderStore1Id not owner
        service.appointStoreOwner(founderStore2Id, storeId2, "store1FounderUser");
        service.appointStoreManager(founderStore1Id, storeId2, "subs1User");

        assertThrows(NotOwnerException.class, () -> service.appointStoreManager(subs2Id, storeId2, "subs1User")); //founderStore2Id not owner
        service.appointStoreOwner(founderStore2Id, storeId2, subs2UserName);
        assertThrows(AlreadyManagerException.class, () -> service.appointStoreManager(founderStore1Id, storeId2, "subs1User"));


    }

    @Test
    void wrongAppointStoreOwnerFail() throws Exception{
        assertThrows(NotOwnerException.class, () -> service.appointStoreOwner(founderStore1Id, storeId2, "subs1User")); //founderStore1Id has no permissions at store2
        service.appointStoreOwner(founderStore1Id, storeId1, "subs1User");
        assertThrows(AlreadyOwnerException.class, () -> service.appointStoreOwner(subs1Id, storeId1, "store1FounderUser"));
        assertThrows(UserNameNotExistException.class, () -> service.appointStoreOwner(founderStore1Id, storeId1, "guest1User")); //guest1Id is a guest
        assertThrows(UserNameNotExistException.class, () -> service.appointStoreOwner(founderStore1Id, storeId1, "dani")); //connection id not exist


    }

    @Test
    void appointAnAlreadyStoreOwner() throws Exception{
        service.appointStoreOwner(founderStore1Id, storeId1 ,"subs1User");
        assertThrows(AlreadyOwnerException.class, () -> service.appointStoreOwner(founderStore1Id, storeId1 ,"subs1User"));
        assertThrows(AlreadyOwnerException.class, () -> service.appointStoreOwner(subs1Id, storeId1 ,"store1FounderUser"));

    }

    @Test
    void appointHimselfAsStoreOwner() throws Exception{
        assertThrows(UserCantAppointHimself.class,()->service.appointStoreOwner(founderStore1Id,storeId1,"store1FounderUser"));
    }


    @Test
    void appointGuestToStoreOwner() throws Exception{
        assertThrows(UserNameNotExistException.class, () -> service.appointStoreOwner(founderStore1Id, storeId1 ,"guest1User"));
    }

    @Test
    void appointHimselfAsStoreManager() throws Exception{
        assertThrows(UserCantAppointHimself.class,()->service.appointStoreManager(founderStore1Id,storeId1,"store1FounderUser"));
    }



    @Test
    void purchaseCart() throws ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        service.updateShoppingBag(subs1Id, storeId1, productId1, 8);
        service.updateShoppingBag(subs2Id, storeId1, productId1, 1);

        AtomicInteger count = new AtomicInteger();
        Thread t1 = new Thread(()-> {
            try {
                service.purchaseCart(subs1Id,"1",1,2022,"sub1","123","1","Sub1","Metudela","Beer-Sheva","Israel",1);
            } catch (Exception e) {
                System.out.println(e.toString());
                count.getAndIncrement();
            }
        },"Thread1");

        Thread t2 = new Thread(()-> {
            try {
                service.purchaseCart(subs2Id,"1",1,2022,"sub2","123","1","Sub2","Metudela","Beer-Sheva","Israel",1);
            } catch (Exception e) {
                System.out.println(e.toString());
                count.getAndIncrement();
            }
        },"Thread2");

        t1.run();
        t2.run();

        assertEquals(1,count.get());

    }

    @Test
    void purchaseCorrectlyCartTenBuyers() throws Exception {
        String usersName [] = new String[100];
        String usersConnID [] = new String[100];

        for(int i = 0; i < 100; i++){
          usersName[i] = "userNum" + i;
          usersConnID[i] = service.entrance();
          service.register(usersName[i], "123");
          service.login(usersConnID[i],usersName[i],"123");
          service.updateShoppingBag(usersConnID[i], storeId1, productIdFor100, 1);
        }

        AtomicInteger count = new AtomicInteger();
        Thread hundredThread [] = new Thread[100];
        for(int i = 0; i < 100; i++){
            int finalI = i;
            hundredThread[i] = new Thread(()-> {
                try {
                    service.purchaseCart(usersConnID[finalI],"1",1,2022,"sub1","123","1","Sub1","Metudela","Beer-Sheva","Israel",1);
                } catch (Exception e) {
                    System.out.println(e.toString());
                    count.getAndIncrement();
                }
            },"Thread"+i);
        }
        for(int i = 0; i < 100; i++)
            hundredThread[i].run();

        assertEquals(0,count.get());

    }

    @Test
    void purchaseCartHundredBuyersOneCantBuy() throws Exception {
        String usersName [] = new String[100];
        String usersConnID [] = new String[100];

        for(int i = 0; i < 100; i++){
            usersName[i] = "userNum" + i;
            usersConnID[i] = service.entrance();
            service.register(usersName[i], "123");
            service.login(usersConnID[i],usersName[i],"123");
            service.updateShoppingBag(usersConnID[i], storeId1, productIdFor99, 1);
        }

        AtomicInteger count = new AtomicInteger();
        Thread hundredThread [] = new Thread[100];
        for(int i = 0; i < 100; i++){
            int finalI = i;
            hundredThread[i] = new Thread(()-> {
                try {
                    service.purchaseCart(usersConnID[finalI],"1",1,2022,"sub1","123","1","Sub1","Metudela","Beer-Sheva","Israel",1);
                } catch (Exception e) {
                    System.out.println(e.toString());
                    count.getAndIncrement();
                }
            },"Thread"+i);
        }
        for(int i = 0; i < 100; i++)
            hundredThread[i].run();

        assertEquals(1,count.get());

    }

    @Test
    void scenario1Circularity() throws Exception {
        String conId1= service.entrance();
        service.register("u1", "123");
        service.register("u2", "123");
        service.register("u3", "123");
        service.login(conId1, "u1", "123");
        int newstore1= service.openStore(conId1, "s1");
        service.appointStoreOwner(conId1, newstore1, "u2");
        service.logout(conId1);
        service.login(conId1, "u2", "123");
        service.appointStoreOwner(conId1, newstore1, "u3");
        service.logout(conId1);
        service.login(conId1, "u1", "123");
        service.removeOwner(conId1, newstore1, "u2");
        service.logout(conId1);
        service.login(conId1, "u3", "123");
        assertThrows(NotOwnerException.class, () -> service.appointStoreOwner(conId1, newstore1, "u2"));

    }


    @Test
    void getBidsByGuest()  {

        assertThrows(UserNotSubscriberException.class, () -> service.getBidsByStore(guest1Id, storeId1));
        assertThrows(UserNotSubscriberException.class, () -> service.getBidsByStore(guest1Id, storeId2));
    }

    @Test
    void get_store_offers_by_owner_or_manager_without_permission() throws InvalidActionException {
//        assertThrows(NoPermissionException.class, () -> service.getBidsByStore(store1Manager1Id, storeId1));
//        assertThrows(NoPermissionException.class, () -> service.getBidsByStore(store1Manager1Id, storeId2));
//        assertThrows(NoPermissionException.class, () -> service.getBidsByStore(store2Manager1Id, storeId1));
//        assertThrows(NoPermissionException.class, () -> service.getBidsByStore(store2Manager1Id, storeId2));
        assertThrows(IllegalPermissionsAccess.class, () -> service.getBidsByStore(founderStore1Id, storeId2));
        assertThrows(IllegalPermissionsAccess.class, () -> service.getBidsByStore(founderStore2Id, storeId1));
    }




}
