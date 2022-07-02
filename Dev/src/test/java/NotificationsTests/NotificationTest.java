package NotificationsTests;

import Exceptions.*;
import Store.Store;
import Store.Product;
import User.Subscriber;
import User.User;
import User.ShoppingBag;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class NotificationTest {

    private Store store1;
    private Product product1, product2;
    private Subscriber subscriber1, subscriber2;
    private User guest;
    private ShoppingBag shoppingBag1, shoppingBag2;


    void createBid(){

    }

    void createUsers(){
        guest = new User();
        subscriber1 = new Subscriber("subscriber1");
        subscriber2 = new Subscriber("subscriber2");
    }

    void openStoreAndAddItems() throws IllegalDiscountException, IllegalNameException, OutOfInventoryException, LostConnectionException {
        createUsers();
        store1 = new Store("TestStore");
        store1.subscribe(subscriber1);
        product1 = new Product(store1.getStoreId(),10, "product1", "milk", "category1");
        product2 = new Product(store1.getStoreId(),20, "product2", "honey", "category2");
        store1.addProduct(product1, 50);
        store1.addProduct(product2, 100);
        addItemsToBasket();
    }

    void addItemsToBasket() throws OutOfInventoryException, LostConnectionException {
        guest.updateShoppingBag(store1, product1, 5);
        subscriber1.updateShoppingBag(store1, product2, 10);
    }

    @Test
    void subscribeOpenStore() throws IllegalDiscountException, IllegalNameException, OutOfInventoryException, LostConnectionException {
        openStoreAndAddItems();
        assertTrue(store1.getObservable().getObservers().contains(subscriber1));
    }

    @Test
    void subscribeAppointOwner() throws IllegalDiscountException, IllegalNameException, AlreadyOwnerException, OutOfInventoryException, NotOwnerException, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
        openStoreAndAddItems();
        store1.addOwner(subscriber1, subscriber2);
        assertTrue(store1.getObservable().getObservers().contains(subscriber2));
    }

    @Test
    void subscribeAppointManager() throws IllegalDiscountException, IllegalNameException, AlreadyManagerException, OutOfInventoryException, LostConnectionException {
        openStoreAndAddItems();
        store1.addManager(subscriber1, subscriber2);
        assertTrue(store1.getObservable().getObservers().contains(subscriber2));
    }

    @Test
    void subscribeAppointOwnerAndGetNotification() throws IllegalDiscountException, IllegalNameException, AlreadyOwnerException, OutOfInventoryException, NotOwnerException, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
        openStoreAndAddItems();
        store1.addOwner(subscriber1, subscriber2);
        assertTrue(subscriber2.checkPendingNotifications().toString().contains("Owner"));
    }

    @Test
    void subscribeAppointManagerAndGetNotification() throws IllegalDiscountException, IllegalNameException, AlreadyManagerException, OutOfInventoryException, LostConnectionException {
        openStoreAndAddItems();
        store1.addManager(subscriber1, subscriber2);
        assertTrue(subscriber2.checkPendingNotifications().toString().contains("Manager"));
    }

/*    @Test
    void subscribeAppointOwnerAndRemoveHim() throws IllegalDiscountException, IllegalNameException, NoApointedUsersException, AlreadyOwnerException {
        openStoreAndAddItems();
        store1.addOwner(subscriber1, subscriber2);
        assertTrue(store1.getObservable().getObservers().contains(subscriber2));
        store1.removeOwner(subscriber1,subscriber2);
        assertTrue(subscriber2.checkPendingNotifications().toString().contains("has removed"));
    }*/

    @Test
    void subscribeAppointManagerAndRemoveHim() throws IllegalDiscountException, IllegalNameException, AlreadyManagerException, NoApointedUsersException, OutOfInventoryException, LostConnectionException {
        openStoreAndAddItems();
        store1.addManager(subscriber1, subscriber2);
        assertTrue(store1.getObservable().getObservers().contains(subscriber2));
        store1.removeManager(subscriber1, subscriber2);
        assertTrue(subscriber2.checkPendingNotifications().toString().contains("has removed"));
    }

    @Test
    void closeStore() throws IllegalDiscountException, IllegalNameException, StoreNotActiveException, OutOfInventoryException, LostConnectionException {
        openStoreAndAddItems();
        assertTrue(store1.isStroeActivated() == true);
        assertTrue(store1.getObservable().getObservers().contains(subscriber1));
        store1.closeStore();
        assertTrue(subscriber1.checkPendingNotifications().toString().contains("is closed now"));
    }

    @Test
    void purchaseNotification() throws IllegalDiscountException, IllegalNameException, InsufficientInventory, ProductNotFoundException, OutOfInventoryException, LostConnectionException {
        openStoreAndAddItems();
        store1.updateInventoryAfterPurchase(subscriber1.getCart().get(store1));
        assertTrue(subscriber1.checkPendingNotifications().toString().contains("Some user purchased from"));
        assertTrue(subscriber1.checkPendingNotifications().size() == 0);
    }
}
