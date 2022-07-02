package AcceptanceTests;
import Exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Service.Service;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CurrcaranctAcceptanceTest {

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

        storeId3 = service.openStore(founderStore2Id, "store3");
        productId5 = service.addProductToStore(founderStore2Id, storeId2, "cheese", "DairyProducts", "desc5", 30, 6.5);
        productId6 = service.addProductToStore(founderStore2Id, storeId2, "shoko", "DairyProducts", "desc6", 20, 9);

        service.appointStoreManager(founderStore1Id, storeId1, store1Manager1User);


    }




    @Test
    void purchaseCartTwoTryOneSucc() throws ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        service.updateShoppingBag(subs1Id, storeId1, productId1, 8);
        service.updateShoppingBag(subs2Id, storeId1, productId1, 1);

        AtomicInteger count = new AtomicInteger();
        Thread t1 = new Thread(()-> {
            try {
                service.purchaseCart(subs1Id,"1",1,2022,"sub1","123","1","Sub1","Metudela","Beer-Sheva","Israel",1);
            } catch (Exception e) {
                count.getAndIncrement();
            }
        },"Thread1");

        Thread t2 = new Thread(()-> {
            try {
                service.purchaseCart(subs2Id,"1",1,2022,"sub2","123","1","Sub2","Metudela","Beer-Sheva","Israel",1);
            } catch (Exception e) {
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
        Thread tenThread [] = new Thread[10];
        for(int i = 0; i < 10; i++){
            int finalI = i;
            tenThread[i] = new Thread(()-> {
                try {
                    service.purchaseCart(usersConnID[finalI],"1",1,2022,"sub1","123","1","Sub1","Metudela","Beer-Sheva","Israel",1);
                } catch (Exception e) {
                    count.getAndIncrement();
                }
            },"Thread"+i);
        }
        for(int i = 0; i < 10; i++)
            tenThread[i].run();

        assertEquals(0,count.get());

    }

    @Test
    void purchaseCartTenBuyersOneCantBuy() throws Exception {
        String usersName [] = new String[10];
        String usersConnID [] = new String[10];

        for(int i = 0; i < 10; i++){
            usersName[i] = "userNum" + i;
            usersConnID[i] = service.entrance();
            service.register(usersName[i], "123");
            service.login(usersConnID[i],usersName[i],"123");
            service.updateShoppingBag(usersConnID[i], storeId1, productIdFor99, 11);
        }

        AtomicInteger count = new AtomicInteger();
        Thread hundredThread [] = new Thread[10];
        for(int i = 0; i < 10; i++){
            int finalI = i;
            hundredThread[i] = new Thread(()-> {
                try {
                    service.purchaseCart(usersConnID[finalI],"1",1,2022,"sub1","123","1","Sub1","Metudela","Beer-Sheva","Israel",1);
                } catch (Exception e) {
                    count.getAndIncrement();
                }
            },"Thread"+i);
        }
        for(int i = 0; i < 10; i++)
            hundredThread[i].run();

        assertEquals(1,count.get());

    }

    @Test
    void purchaseCartHundredBuyersElevenCantBuy() throws Exception {
        String usersName [] = new String[100];
        String usersConnID [] = new String[100];

        for(int i = 0; i < 100; i++){
            usersName[i] = "userNum" + i;
            usersConnID[i] = service.entrance();
            service.register(usersName[i], "123");
            service.login(usersConnID[i],usersName[i],"123");
            service.updateShoppingBag(usersConnID[i], storeId1, productIdFor99, 9);
        }

        AtomicInteger count = new AtomicInteger();
        Thread hundredThread [] = new Thread[100];
        for(int i = 0; i < 100; i++){
            int finalI = i;
            hundredThread[i] = new Thread(()-> {
                try {
                    service.purchaseCart(usersConnID[finalI],"1",1,2022,"sub1","123","1","Sub1","Metudela","Beer-Sheva","Israel",1);
                } catch (Exception e) {
                    count.getAndIncrement();
                }
            },"Thread"+i);
        }
        for(int i = 0; i < 100; i++)
            hundredThread[i].run();

        assertEquals(89,count.get());

    }

    @Test
    void add2ProductsToStore() throws Exception {


        final Integer[] p1 = {null};
        Thread t1 = new Thread(()-> {
            try {
                p1[0] = (service.addProductToStore(founderStore2Id, storeId2, "Milkey", "DairyProducts", "desc5", 30, 6.5));
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        },"Thread1");

        final Integer[] p2 = {null};
        Thread t2 = new Thread(()-> {
            try {
                p2[0] = (service.addProductToStore(founderStore2Id, storeId2, "Danone", "DairyProducts", "desc6", 20, 9));
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        },"Thread2");

        t1.run();
        t2.run();

        assertNotNull(p1);
        assertNotNull(p2);

    }
    @Test
    void addFiftyProductsToStore() throws Exception {

        final Integer[][] p = new Integer[50][1];
        for(int i=0;i<p.length;i++)
            p[0] = new Integer[]{null};
        Thread fiftyThread [] = new Thread[50];
        for(int i=0;i<p.length;i++) {
            int finalI = i;
            fiftyThread[i] = new Thread(()-> {
                try {
                    p[finalI][0] = (service.addProductToStore(founderStore2Id, storeId2, "Milk"+finalI, "DairyProducts", "desc5", 30, 6.5));
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            },"Thread"+i);
        }
        for(int i = 0; i < 50; i++)
            fiftyThread[i].run();

        for(int i = 0; i < 50; i++)
            assertNotNull(p[i][0]);

    }

    @Test
    void deleteProductFromStore() throws Exception {
        int toDelete = service.addProductToStore(founderStore2Id, storeId2, "Danone", "DairyProducts", "desc6", 20, 9);


        AtomicInteger count = new AtomicInteger();
        Thread hundredThread [] = new Thread[100];
        for(int i = 0; i < 100; i++){
            int finalI = i;
            hundredThread[i] = new Thread(()-> {
                try {
                    service.deleteProductFromStore(founderStore2Id,storeId2,toDelete);
                } catch (Exception e) {
                    count.getAndIncrement();
                }
            },"Thread"+i);
        }
        for(int i = 0; i < 100; i++)
            hundredThread[i].run();

        assertEquals(99,count.get());


    }



    @Test
    void appointOwner() throws ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, AlreadyOwnerException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, IllegalCircularityException, IllegalPermissionsAccess {

        AtomicInteger count = new AtomicInteger();
        Thread t1 = new Thread(()-> {
            try {
                service.appointStoreOwner(founderStore1Id, storeId1, subs1User);            }
            catch (Exception e) {
                count.getAndIncrement();
            }
        },"Thread1");

        Thread t2 = new Thread(()-> {
            try {
                service.appointStoreOwner(founderStore1Id, storeId1, subs1User);            }
            catch (Exception e) {
                count.getAndIncrement();
            }
        },"Thread2");

        t1.run();
        t2.run();

        assertEquals(1,count.get());

    }


    @Test
    void appointManager() throws ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, AlreadyOwnerException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, IllegalCircularityException, IllegalPermissionsAccess {

        AtomicInteger count = new AtomicInteger();
        Thread t1 = new Thread(()-> {
            try {
                service.appointStoreManager(founderStore1Id, storeId1, subs1User);            }
            catch (Exception e) {
                count.getAndIncrement();
            }
        },"Thread1");

        Thread t2 = new Thread(()-> {
            try {
                service.appointStoreManager(founderStore1Id, storeId1, subs1User);            }
            catch (Exception e) {
                count.getAndIncrement();
            }
        },"Thread2");

        t1.run();
        t2.run();

        assertEquals(1,count.get());

    }

    @Test
    void appointManagerHundredTimes() throws ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, AlreadyOwnerException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, IllegalCircularityException, IllegalPermissionsAccess {

        AtomicInteger count = new AtomicInteger();
        Thread hundredThread [] = new Thread[100];
        for(int i = 0; i < 100; i++){
            int finalI = i;
            hundredThread[i] = new Thread(()-> {
                try {
                    service.appointStoreManager(founderStore1Id, storeId1, subs1User);
                } catch (Exception e) {
                    count.getAndIncrement();
                }
            },"Thread"+i);
        }
        for(int i = 0; i < 100; i++)
            hundredThread[i].run();

        assertEquals(99,count.get());


    }


    @Test
    void appointOwnerHundredTimes() throws ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, AlreadyOwnerException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, IllegalCircularityException, IllegalPermissionsAccess {

        AtomicInteger count = new AtomicInteger();
        Thread hundredThread [] = new Thread[100];
        for(int i = 0; i < 100; i++){
            int finalI = i;
            hundredThread[i] = new Thread(()-> {
                try {
                    service.appointStoreOwner(founderStore1Id, storeId1, subs1User);
                } catch (Exception e) {
                    count.getAndIncrement();
                }
            },"Thread"+i);
        }
        for(int i = 0; i < 100; i++)
            hundredThread[i].run();

        assertEquals(99,count.get());


    }

}