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

public class AcceptanceTests3 {

    private static Service service;
    private int storeId1, storeId2, storeId3; //stores
    private int productIdFor100,productIdFor99,  productId1, productId2, productId3, productId4, productId5, productId6; //products
    private String admin1Id, founderStore1Id, founderStore2Id, founderStore3Id, store1Manager1Id,store1Manager2Id,store2ManagerInventoryId, subs1Id, subs2Id, subs3Id, guest1Id; //users Id's
    //user names:
    private String userad1="Admin1", store1FounderUser="store1FounderUser", store2FounderUser="store2FounderUser",
            store1Manager1User="Store1Manager1User",store2ManagerInventory="store2ManagerInventoryUser", subs1User = "subs1User",store1Manager2User="Store1Manager2User",subs2UserName = "subs2User",
            subs3User = "subs3User", guest1User = "guest1User";

    private String card_number = "1234", holder = "a", ccv = "001", id = "000000018", name = "name", address = "address", city = "city", country = "country";
    private int month = 1, year = 2022, zip = 12345;

//
    @BeforeEach
    public void setUp() throws Exception {

        service = Driver.getRealService("Admin", "1234"); //Gets details of system manager to register into user validator
        admin1Id = Driver.getAdminId();
        founderStore1Id = service.entrance();
        founderStore2Id = service.entrance();
        founderStore3Id = service.entrance();
        store1Manager1Id = service.entrance();
        store1Manager2Id = service.entrance();
        store2ManagerInventoryId = service.entrance();
        subs1Id = service.entrance();
        subs2Id = service.entrance();
        subs3Id = service.entrance();
        guest1Id = service.entrance();


        service.register("store1FounderUser", "1234");
        service.register("store2FounderUser", "1234");
        service.register("store3FounderUser", "1234");
        service.register("Store1Manager1User", "1234");
        service.register("Store1Manager2User", "1234");
        service.register("store2ManagerInventoryUser", "1234");
        service.register("subs1User", "1234");
        service.register("subs2User", "1234");
        service.register("subs3User", "1234");

        service.login(admin1Id, "Admin", "1234");
        service.login(founderStore1Id, "store1FounderUser", "1234"); //storeId1 founder
        service.login(founderStore2Id, "store2FounderUser", "1234"); //storeId2 founder
        service.login(founderStore3Id, "store3FounderUser", "1234"); //storeId3 founder
        service.login(store1Manager1Id, "Store1Manager1User", "1234");
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

        service.appointStoreManager(founderStore1Id, storeId1, store1Manager1User);
        service.appointStoreManager(founderStore2Id, storeId2, store2ManagerInventory);
        service.addBidPermission(founderStore2Id,storeId2,store2ManagerInventory);

        service.updateShoppingBag(subs1Id,storeId2,productId3,30);

    }


    @Test
    void addBidByGuest() throws InvalidActionException, ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, UserNotSubscriberException, IllegalRangeException, BidException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        service.updateShoppingBag(subs3Id,storeId2,productId3,15);
        assertThrows(UserNotSubscriberException.class,()-> service.addBid(guest1Id, storeId2, productId3, 20, 1));
    }

    @Test
    void addBidSuccess() throws InvalidActionException, ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, UserNotSubscriberException, IllegalRangeException, BidException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        service.updateShoppingBag(subs3Id,storeId2,productId3,15);
        assertDoesNotThrow(()-> service.addBid(subs1Id, storeId2, productId3, 20, 1));
    }

    @Test
    void addBidByProductNotInCart() throws InvalidActionException, ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, UserNotSubscriberException, IllegalRangeException, BidException {
        assertThrows(ProductNotFoundException.class,()-> service.addBid(subs1Id, storeId2, productId1, 20, 1));
    }


    @Test
    void getBidsByGuest()  {
        assertThrows(UserNotSubscriberException.class, () -> service.getBidsByStore(guest1Id, storeId1));
        assertThrows(UserNotSubscriberException.class, () -> service.getBidsByStore(guest1Id, storeId2));
    }

    @Test
    void getStoreBidWithoutPermissions() throws InvalidActionException {
        assertThrows(IllegalPermissionsAccess.class, () -> service.getBidsByStore(store1Manager1Id, storeId1));
        assertThrows(IllegalPermissionsAccess.class, () -> service.getBidsByStore(store1Manager1Id, storeId2));
        assertThrows(IllegalPermissionsAccess.class, () -> service.getBidsByStore(store2ManagerInventoryId, storeId1));
        assertThrows(IllegalPermissionsAccess.class, () -> service.getBidsByStore(founderStore1Id, storeId2));
        assertThrows(IllegalPermissionsAccess.class, () -> service.getBidsByStore(founderStore2Id, storeId1));
    }
    @Test
    void getStoreBidSuccess() throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, LostConnectionException {
        service.addBid(subs1Id, storeId2, productId3, 1.5, 5);
        service.addBid(subs1Id, storeId2, productId3, 4, 5);
        assertTrue(service.getBidsByStore(store2ManagerInventoryId, storeId2).toString().contains("amount of : 5 and price of : 1.5, id: 0, Subscriber : subs1User bid on product pizza with amount of : 5 and price of : 4.0"));
        System.out.println(service.getBidsByStore(store2ManagerInventoryId, storeId2).toString());

    }

    @Test
    void get_store_bids_by_owner_or_manager_with_permission() throws InvalidActionException, StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        service.addBid(subs1Id, storeId2, productId3, 2, 1);
        service.updateShoppingBag(subs1Id,storeId1,productId2,30);

        service.addBid(subs1Id, storeId1, productId2, 4, 5);
        service.addBid(subs1Id, storeId2, productId3, 1.5, 5);

        System.out.println(service.getBidsByStore(founderStore1Id, storeId1).toString());
        assertTrue(service.getBidsByStore(founderStore1Id, storeId1).toString().contains(" bid on product cheese with amount of : 5 and price of : 4.0" ));// && service.getBidsByStore(founderStore1Id, storeId1).toString().contains("and price of : 2"));
        assertTrue(service.getBidsByStore(founderStore2Id, storeId2).toString().contains(" bid on product pizza") && service.getBidsByStore(founderStore2Id, storeId2).toString().contains("and price of : 1.5"));
        assertTrue(service.getBidsByStore(founderStore1Id, storeId1).toString().contains(" bid on product cheese" ) && service.getBidsByStore(founderStore1Id, storeId1).toString().contains("and price of : 4"));
        System.out.println(service.getBidsByStore(store2ManagerInventoryId, storeId2).toString());
        assertTrue(service.getBidsByStore(store2ManagerInventoryId, storeId2).toString().contains("subs1User bid on product pizza with amount of : 5 and price of : 1.5"));

    }

    @Test
    void approveBidByGuest() throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, LostConnectionException {
        int id = service.addBid(subs1Id, storeId2, productId3, 1.5, 5);
        assertThrows(UserNotSubscriberException.class, () -> service.approveBid(guest1Id, storeId1,id));
    }

    @Test
    void approveBidWithoutPermission() throws InvalidActionException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, ProductNotFoundException, IllegalRangeException, BidException, NotOwnerException, ProductShouldBeInCartBeforeBidOnIt, IllegalPermissionsAccess, IllegalPriceException, LostConnectionException {
        service.addBid(subs1Id, storeId2, productId3, 1.5, 5);

        service.addBid(subs1Id, storeId2, productId3, 1.5, 5);
        assertThrows(IllegalPermissionsAccess.class, () -> service.approveBid(store1Manager1Id, storeId1,1));
        assertThrows(IllegalPermissionsAccess.class, () -> service.approveBid(founderStore1Id, storeId2,2));
        assertThrows(IllegalPermissionsAccess.class, () -> service.approveBid(store2ManagerInventoryId, storeId1,2));

    }

    @Test
    void notAllPermittedUsersApproveBid() throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, ProductNotFoundException, IllegalRangeException, BidException, NotOwnerException, IllegalPermissionsAccess, ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, LostConnectionException {
        int bidId = service.addBid(subs1Id, storeId2, productId3, 1.5, 5);
        service.approveBid(founderStore2Id,storeId2,bidId);
        Bid bid = service.getBidByID(founderStore2Id,storeId2,bidId);
        assertFalse(bid.isApproved());
    }

    @Test
    void approveBidByOwnerAndManagerWithPermission() throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, ProductNotFoundException, IllegalRangeException, BidException, NotOwnerException, IllegalPermissionsAccess, ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, LostConnectionException {
        int bidId = service.addBid(subs1Id, storeId2, productId3, 1.5, 5);
        service.approveBid(founderStore2Id,storeId2,bidId);
        service.approveBid(store2ManagerInventoryId,storeId2,bidId);
        Bid bid = service.getBidByID(founderStore2Id,storeId2,bidId);
        assertTrue(bid.isApproved());

        //bid ID doesn't exist
        assertThrows(BidException.class, () -> service.approveBid(founderStore1Id, storeId1, 210));

    }

    @Test
    void addCounteredBidOffer() throws ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, UserNotSubscriberException, IllegalRangeException, BidException, NotOwnerException, ProductShouldBeInCartBeforeBidOnIt, IllegalPermissionsAccess, IllegalPriceException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        service.updateShoppingBag(subs3Id,storeId2,productId3,15);
        int bidId = service.addBid(subs3Id, storeId2, productId3, 20, 1); // 20>10
        service.addCounteredBid(founderStore2Id,storeId2,bidId,2,4);
        Bid b = service.getBidByID(founderStore2Id,storeId2,bidId);
        Map<Subscriber,Bid > map = b.getOwnerCounteredProposal();
        assertTrue(map.toString().contains("bid on product pizza with amount of : 2 and price of : 4.0"));
    }



    @Test
    void purchaseCounteredBidOffer() throws ConnectionNotFoundException, StoreNotFoundException, ProductNotFoundException, UserNotSubscriberException, IllegalRangeException, BidException, NotOwnerException, ProductShouldBeInCartBeforeBidOnIt, IllegalPermissionsAccess, IllegalPriceException, PaymentException, DeliveryException, BagNotValidPolicyException, IllegalDiscountException, IllegalBuyerException, InsufficientInventory, NotManagerException, EmptyCartException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        service.updateShoppingBag(subs3Id,storeId2,productId3,15);
        int bidId = service.addBid(subs3Id, storeId2, productId3, 20, 1); // 20>10
        int countered = service.addCounteredBid(founderStore2Id,storeId2,bidId,2,4);
        Bid b = service.getBidByID(founderStore2Id,storeId2,countered);
    }



    @Test
    void purchaseBidLimitationNotValid() throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, ProductShouldBeInCartBeforeBidOnIt, BidException, IllegalPermissionsAccess, IllegalPriceException, ProductNotFoundException, IllegalRangeException, NotStorePolicyException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        service.addCartRangePricePolicy(founderStore2Id,storeId2,2,10);
        service.updateShoppingBag(subs3Id,storeId2,productId3,15);
        int bidId = service.addBid(subs3Id, storeId2, productId3, 20, 1); // 20>10
        service.approveBid(founderStore2Id,storeId2,bidId);
        assertThrows(BagNotValidPolicyException.class, () -> service.purchaseCart(subs3Id,"234",3,2022,"omer","123","434","omer","add","Asdf","fvdv",32));

    }

    @Test
    void purchaseBidUnderLimitationPolicy() throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalRangeException, IllegalPermissionsAccess, IllegalPriceException, ProductNotFoundException, BidException, ProductShouldBeInCartBeforeBidOnIt, PaymentException, DeliveryException, BagNotValidPolicyException, IllegalDiscountException, InsufficientInventory, NotManagerException, IllegalBuyerException, OutOfInventoryException, StoreNotActiveException, EmptyCartException, LostConnectionException {
        service.updateShoppingBag(subs2Id,storeId2,productId3,15);
        int bidId2 = service.addBid(subs2Id, storeId2, productId3, 3, 1);
        service.approveBid(founderStore2Id,storeId2,bidId2);
        service.approveBid(store2ManagerInventoryId,storeId2,bidId2);
        Bid b = service.getBidByID(founderStore2Id,storeId2,bidId2);
        service.purchaseCart(subs2Id,"234",3,2022,"omer","123","434","omer","add","Asdf","fvdv",32);
        assertTrue(service.purchaseHistory(founderStore2Id,storeId2).toString().contains("price=3.0, description='desc3', name='pizza'"));

    }


    @Test
    void addBidProductNotInCart() {
        Assertions.assertThrows(ProductShouldBeInCartBeforeBidOnIt.class, ()-> service.addBid(subs2Id, storeId2,productId3, 10, 4));
    }


    @Test
    void getBids() throws ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, OutOfInventoryException, IllegalDiscountException, IllegalNameException, ConnectionNotFoundException, StoreNotFoundException, StoreNotActiveException, UserNotSubscriberException, NotOwnerException, IllegalPermissionsAccess, LostConnectionException {
        service.updateShoppingBag(subs2Id,storeId2,productId3,15);
        service.addBid(subs2Id, storeId2, productId3, 1.5, 5);
        service.addBid(subs2Id, storeId2, productId3, 1, 5);
        assertTrue(service.getBidsByStore(founderStore2Id,storeId2).toString().contains("subs2User bid on product pizza with amount of : 5 and price of : 1.5, id: 0, Subscriber : subs2User bid on product pizza with amount of : 5"));




    }

}
