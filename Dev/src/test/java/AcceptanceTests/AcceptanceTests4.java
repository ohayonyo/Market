package AcceptanceTests;

        import Exceptions.*;
        import Store.Bid.Bid;
        import User.Subscriber;
        import Store.Product;
        import Store.Store;
        import User.User;
        import org.junit.jupiter.api.Assertions;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import Service.Service;
        import org.junit.jupiter.api.function.Executable;

        import javax.naming.NameNotFoundException;
        import java.util.*;
        import java.util.concurrent.atomic.AtomicInteger;
        import static org.junit.jupiter.api.Assertions.*;
        import static org.testng.Assert.assertThrows;

public class AcceptanceTests4 {

    private static Service service;
    private int storeId1, storeId2, storeId3; //stores
    private int productIdFor100,productIdFor99,  productId1, productId2, productId3, productId4, productId5, productId6; //products
    private String admin1Id, founderStore1Id, founderStore2Id, founderStore3Id, store1Owner1Id,store1Owner2Id,store2ManagerInventoryId, subs1Id, subs2Id, subs3Id, guest1Id; //users Id's
    //user names:
    private String userad1="Admin1", store1FounderUser="store1FounderUser", store2FounderUser="store2FounderUser",
            store1Owner1User="store1Owner1User",store2ManagerInventory="store2ManagerInventoryUser", subs1User = "subs1User",store1Owner2User="store1Owner2User",subs2UserName = "subs2User",
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
        store1Owner1Id = service.entrance();
        store1Owner2Id = service.entrance();
        store2ManagerInventoryId = service.entrance();
        subs1Id = service.entrance();
        subs2Id = service.entrance();
        subs3Id = service.entrance();
        guest1Id = service.entrance();


        service.register("store1FounderUser", "1234");
        service.register("store2FounderUser", "1234");
        service.register("store3FounderUser", "1234");
        service.register("store1Owner1User", "1234");
        service.register("store1Owner2User", "1234");
        service.register("store2ManagerInventoryUser", "1234");
        service.register("subs1User", "1234");
        service.register("subs2User", "1234");
        service.register("subs3User", "1234");

        service.login(admin1Id, "Admin", "1234");
        service.login(founderStore1Id, "store1FounderUser", "1234"); //storeId1 founder
        service.login(founderStore2Id, "store2FounderUser", "1234"); //storeId2 founder
        service.login(founderStore3Id, "store3FounderUser", "1234"); //storeId3 founder
        service.login(store1Owner1Id, "store1Owner1User", "1234");
        service.login(store2ManagerInventoryId, "store2ManagerInventoryUser", "1234");

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

        service.appointStoreManager(founderStore1Id, storeId1, store1Owner1User);
        service.appointStoreManager(founderStore2Id, storeId2, store2ManagerInventory);
        service.addBidPermission(founderStore2Id,storeId2,store2ManagerInventory);

        service.updateShoppingBag(subs1Id,storeId2,productId3,30);

    }

    @Test
    void appoint_Store_Owner_Only_Founder_Exist() throws AlreadyOwnerException, ConnectionNotFoundException, StoreNotFoundException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, IllegalCircularityException, IllegalPermissionsAccess, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
        service.appointStoreOwner(founderStore1Id,storeId1,store1Owner1User);
        assertTrue(service.getOwnersString(storeId1).contains("store1FounderUser") &&
                service.getOwnersString(storeId1).contains("store1Owner1User"));

        //assertThrows(AllTheOwnersNeedToApproveAppointement.class, ()->service.appointStoreOwner(store1Owner1Id,storeId1,store1Manager2User));
    }

    @Test
    void appoint_Store_Owner_Two_Owners_Approved() throws AlreadyOwnerException, ConnectionNotFoundException, StoreNotFoundException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, IllegalCircularityException, IllegalPermissionsAccess, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
        service.appointStoreOwner(founderStore1Id,storeId1,store1Owner1User);
        service.appointStoreOwner(founderStore1Id,storeId1,store1Owner2User);
        service.appointStoreOwner(store1Owner1Id,storeId1,store1Owner2User);
        assertTrue(service.getOwnersString(storeId1).contains("store1FounderUser") &&
                service.getOwnersString(storeId1).contains("store1Owner2User") &&
                service.getOwnersString(storeId1).contains("store1Owner1User") ) ;
    }

    @Test
    void add_approval_without_permissions()  {
        assertThrows(NotOwnerException.class,()->service.appointStoreOwner(founderStore1Id,storeId2,store1Owner1User));
        assertThrows(NotOwnerException.class,()->service.appointStoreOwner(store1Owner1Id,storeId1,store1Owner2User));
        assertThrows(NotOwnerException.class,()->service.appointStoreOwner(store1Owner1Id,storeId2,store1Owner2User));
        assertThrows(NotOwnerException.class,()->service.appointStoreOwner(store1Owner1Id,storeId1,store1FounderUser));
        assertThrows(NotOwnerException.class,()->service.appointStoreOwner(store1Owner1Id,storeId2,store1FounderUser));
        assertThrows(NotOwnerException.class,()->service.appointStoreOwner(store2ManagerInventoryId,storeId1,store1Owner2User));
        assertThrows(UserNotSubscriberException.class,()->service.appointStoreOwner(guest1Id,storeId1,store1Owner2User));
        assertThrows(NotOwnerException.class,()->service.appointStoreOwner(subs1Id,storeId1,store1Owner2User));
        assertThrows(NotOwnerException.class,()->service.appointStoreOwner(subs1Id,storeId1,store1Owner1User));

    }

    @Test
    void add_approval_to_an_already_owner() throws StoreNotFoundException, ConnectionNotFoundException, AlreadyOwnerException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, AllTheOwnersNeedToApproveAppointement, IllegalCircularityException, LostConnectionException {
        service.appointStoreOwner(founderStore1Id,storeId1,store1Owner1User);
        service.appointStoreOwner(founderStore1Id,storeId1,store1Owner2User);
        service.appointStoreOwner(store1Owner1Id,storeId1,store1Owner2User);
        assertThrows(AlreadyOwnerException.class,()->service.appointStoreOwner(founderStore1Id,storeId1,store1Owner1User));
        assertThrows(AlreadyOwnerException.class,()->service.appointStoreOwner(founderStore1Id,storeId1,store1Owner2User));
        assertThrows(AlreadyOwnerException.class,()->service.appointStoreOwner(store1Owner1Id,storeId1,store1FounderUser));

    }

    @Test
    void user_appoint_himself() throws StoreNotFoundException, ConnectionNotFoundException, AlreadyOwnerException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, AllTheOwnersNeedToApproveAppointement, IllegalCircularityException, LostConnectionException {
        assertThrows(UserCantAppointHimself.class,()->service.appointStoreOwner(founderStore1Id,storeId1,store1FounderUser));
        service.appointStoreOwner(founderStore1Id,storeId1,store1Owner1User);
        //service.addOwnerApproval(founderStore1Id,storeId1,store1Owner2User);
        assertThrows(UserCantAppointHimself.class,()->service.appointStoreOwner(store1Owner1Id,storeId1,store1Owner1User));
        service.appointStoreOwner(store1Owner1Id,storeId1,store2ManagerInventory);
        service.appointStoreOwner(founderStore1Id,storeId1,store2ManagerInventory);
        assertThrows(UserCantAppointHimself.class,()->service.appointStoreOwner(store2ManagerInventoryId,storeId1,store2ManagerInventory));

    }
    @Test
    void guest_appoint_owner(){
        assertThrows(UserNotSubscriberException.class,()->service.appointStoreOwner(guest1Id,storeId2,store1Owner1User));
    }



}

