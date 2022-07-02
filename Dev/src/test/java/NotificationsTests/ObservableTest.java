package NotificationsTests;

import Exceptions.LostConnectionException;
import Notifications.*;
import Store.Product;
import Store.Store;
import User.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservableTest {
    private Observable observable;
    @Mock private Subscriber observer;
    @Mock private Subscriber observer1;
    @Mock private Collection<Subscriber> observers;
    @Mock private Map<Product, Integer> basket;
    @Mock private Store store;

    private String userName = "Dor";
    private int storeID = 1;
    private String role = "owner";

    @BeforeEach
    void setUp() {
        observable = new Observable();
        observable.setObservers(observers);
    }

    @Test
    void subscribeExistingObserver() throws LostConnectionException {
        when(observers.contains(observer)).thenReturn(true);
        observable.subscribe(observer);
        assertTrue(observers.size()==0);
    }

    @Test
    void subscribeNonExistingObserver() throws LostConnectionException {
        when(observers.contains(observer)).thenReturn(false);
        observable.subscribe(observer);
        verify(observers).add(observer);
    }

    @Test
    void unsubscribeExistingObserver() throws LostConnectionException {
        when(observers.contains(observer)).thenReturn(true);
        observable.unsubscribe(observer);
        verify(observers).remove(observer);
    }

    @Test
    void unsubscribeNonExistingObserver() throws LostConnectionException {
        when(observers.contains(observer)).thenReturn(false);
        observable.unsubscribe(observer);
        assertTrue(observers.size()==0);
    }

    @Test
    void notifySubscriberRemove() throws LostConnectionException {
        Iterator<Subscriber> observersIterator = mock(Iterator.class);
        when(observersIterator.hasNext()).thenReturn(true,true,false);
        when(observersIterator.next()).thenReturn(observer).thenReturn(observer1);
        when(observers.iterator()).thenReturn(observersIterator);
        observable.notifySubscriberRemove(userName);
        verify(observer).notifyNotification(any(SubscriberRemoveNotification.class));
        verify(observer1).notifyNotification(any(SubscriberRemoveNotification.class));
    }

    @Test
    void notifyPurchase() throws LostConnectionException {
        Iterator<Subscriber> observersIterator = mock(Iterator.class);
        when(observersIterator.hasNext()).thenReturn(true,true,false);
        when(observersIterator.next()).thenReturn(observer).thenReturn(observer1);
        when(observers.iterator()).thenReturn(observersIterator);
        observable.notifyPurchase(store, basket);
        verify(observer).notifyNotification(any(PurchaseNotification.class));
        verify(observer1).notifyNotification(any(PurchaseNotification.class));
    }

    @Test
    void notifyRemoveRole() throws LostConnectionException {
        when(observers.contains(observer1)).thenReturn(true);
        observable.notifyRemoveRole(observer, observer1, storeID, role);
        verify(observer1).notifyNotification(any(RemoveRoleNotification.class));
        verify(observers).remove(observer1);
    }

    @Test
    void notifyNewRole() throws LostConnectionException {
        when(observers.contains(observer1)).thenReturn(false);
        observable.notifyNewRole(observer, observer1, storeID, role);
        verify(observers).add(observer1);
        verify(observer1).notifyNotification(any(NewRoleNotification.class));
    }

    @Test
    void notifyStoreClosed() throws LostConnectionException {
        Iterator<Subscriber> observersIterator = mock(Iterator.class);
        when(observersIterator.hasNext()).thenReturn(true,true,false);
        when(observersIterator.next()).thenReturn(observer).thenReturn(observer1);
        when(observers.iterator()).thenReturn(observersIterator);
        observable.notifyStoreClosed(storeID);
        verify(observer).notifyNotification(any(StoreClosedNotification.class));
        verify(observer1).notifyNotification(any(StoreClosedNotification.class));
    }
}