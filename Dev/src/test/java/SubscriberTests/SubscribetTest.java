package SubscriberTests;

import Exceptions.*;
import Store.Store;
import Store.Product;
import User.Roles;
import User.Subscriber;
import User.ShoppingBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriberTest {
    private Subscriber subscriber;
    @Mock private ConcurrentHashMap<Store, ShoppingBag> cart;
    @Mock private ConcurrentHashMap<Product, Integer> products;
    @Mock private ConcurrentHashMap<Store, Roles> roles;
    @Mock private Roles rolesList;
    @Mock private Store store;
    @Mock private Product product;
    @Mock private ShoppingBag shoppingBag;

    @Mock private List<Subscriber> ownerlist;
    @Mock private Subscriber owner;
    @Mock private Subscriber owner1;
    @Mock private Subscriber owner2;

    private String userName = "Ronen";
    private int finalAmount = 9;
    private int zeroAmount = 0;

    @BeforeEach
    void setUp()
    {
        subscriber = new Subscriber(cart, userName,roles);
    }

    @Test
    void updateShoppingBagZeroAmountSubscriber() throws OutOfInventoryException, LostConnectionException {
        when(store.getProductsHashMap()).thenReturn(products);
        when(products.get(product)).thenReturn(zeroAmount);
        when(cart.get(store)).thenReturn(shoppingBag);
        subscriber.updateShoppingBag(store, product, zeroAmount);
        verify(shoppingBag).deleteProduct(product);
    }

    @Test
    void updateShoppingBagSubscriber() throws OutOfInventoryException, LostConnectionException {
        when(store.getProductsHashMap()).thenReturn(products);
        when(products.get(product)).thenReturn(finalAmount);
        when(cart.get(store)).thenReturn(shoppingBag);
        subscriber.updateShoppingBag(store, product, finalAmount);
        verify(shoppingBag).updateProductAmount(product, finalAmount);
    }

    @Test
    void purchaseCartSubscriber()
    {
        subscriber.emptyCartAfterPurchase();
        verify(cart).clear();
    }

    @Test
    void addStoreFounder() throws LostConnectionException {
        when(roles.size()).thenReturn(3);
        when(roles.containsKey(Roles.Role.STORE_OWNER)).thenReturn(true);
        when(roles.containsKey(Roles.Role.STORE_MANAGER)).thenReturn(true);
        when(roles.containsKey(Roles.Role.STORE_FOUNDER)).thenReturn(true);
        subscriber.addStoreFounder(store);
        verify(roles).put(eq(store), any(Roles.class));
        assertEquals(3, roles.size());
        assertTrue(roles.containsKey(Roles.Role.STORE_OWNER));
        assertTrue(roles.containsKey(Roles.Role.STORE_FOUNDER));
        assertTrue(roles.containsKey(Roles.Role.STORE_MANAGER));


    }

    @Test
    void addStoreOwnerExistingStore() throws NotOwnerException, AllTheOwnersNeedToApproveAppointement, AlreadyOwnerException, LostConnectionException {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);

        when(store.getOwnersList()).thenReturn(ownerlist);
        when(ownerlist.contains(owner)).thenReturn(true);
        Iterator<Subscriber> iteratorManagers = mock(Iterator.class);
        when(ownerlist.iterator()).thenReturn(iteratorManagers);
        when(iteratorManagers.hasNext()).thenReturn(false);


        subscriber.addOwnerApproval(store,owner);
        verify(roles, times(6)).get(store);
        verify(rolesList).add(Roles.Role.STORE_OWNER);
    }

    @Test
    void addStoreOwnerNonExistingStore() throws NotOwnerException, AllTheOwnersNeedToApproveAppointement, AlreadyOwnerException, LostConnectionException {
        when(roles.containsKey(store)).thenReturn(false);
        when(store.getOwnersList()).thenReturn(ownerlist);

        when(ownerlist.contains(owner)).thenReturn(true);
        Iterator<Subscriber> iteratorManagers = mock(Iterator.class);
        when(ownerlist.iterator()).thenReturn(iteratorManagers);
        when(iteratorManagers.hasNext()).thenReturn(false);

        subscriber.addOwnerApproval(store,owner);
        verify(roles).put(eq(store), any(Roles.class));
    }

    @Test
    void addStoreManagerExistingStore() throws LostConnectionException {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        subscriber.addStoreManager(store);
        verify(roles, times(2)).get(store);
        verify(rolesList).add(Roles.Role.STORE_MANAGER);
    }

    @Test
    void addStoreManagerNonExistingStore() throws LostConnectionException {
        when(roles.containsKey(store)).thenReturn(false);
        subscriber.addStoreManager(store);
        verify(roles).put(eq(store), any(Roles.class));
    }

    @Test
    void isStoreFounderForFounder() {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_FOUNDER)).thenReturn(true);
        assertDoesNotThrow(() -> subscriber.isStoreFounder(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_FOUNDER);
    }

    @Test
    void isStoreFounderNotFounder() {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_FOUNDER)).thenReturn(false);
        assertThrows(NotFounderException.class, () -> subscriber.isStoreFounder(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_FOUNDER);
    }

    @Test
    void isStoreOwnerForOwner()
    {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_OWNER)).thenReturn(true);
        assertDoesNotThrow(() -> subscriber.isStoreOwner(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_OWNER);
    }

    @Test
    void isStoreOwnerNotOwner()
    {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_OWNER)).thenReturn(false);
        assertThrows(NotOwnerException.class, () -> subscriber.isStoreOwner(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_OWNER);
    }

    @Test
    void isAlreadyStoreOwnerForOwner() {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_OWNER)).thenReturn(true);
        assertThrows(AlreadyOwnerException.class, () -> subscriber.isAlreadyStoreOwner(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_OWNER);
    }

    @Test
    void isAlreadyStoreOwnerNotOwner() {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_OWNER)).thenReturn(false);
        assertDoesNotThrow(() -> subscriber.isAlreadyStoreOwner(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_OWNER);
    }

    @Test
    void isStoreManagerForManager()
    {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_MANAGER)).thenReturn(true);
        assertDoesNotThrow(() -> subscriber.isStoreManager(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_MANAGER);
    }

    @Test
    void isStoreManagerNotManager()
    {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_MANAGER)).thenReturn(false);
        assertThrows(NotManagerException.class, () -> subscriber.isStoreManager(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_MANAGER);
    }

    @Test
    void isAlreadyStoreManagerForManager() {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_MANAGER)).thenReturn(true);
        assertThrows(AlreadyManagerException.class, () -> subscriber.isAlreadyStoreManager(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_MANAGER);
    }

    @Test
    void isAlreadyStoreManagerNotManager() {
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
        when(rolesList.contains(Roles.Role.STORE_MANAGER)).thenReturn(false);
        assertDoesNotThrow(() -> subscriber.isAlreadyStoreManager(store));
        verify(roles).get(store);
        verify(rolesList).contains(Roles.Role.STORE_MANAGER);
    }

    void beforeAddOwnersApproval(){
        when(roles.containsKey(store)).thenReturn(true);
        when(roles.get(store)).thenReturn(rolesList);
    }

    @Test
    void add_three_owners_appointment_approval_existing_store() throws NotOwnerException, AllTheOwnersNeedToApproveAppointement, AlreadyOwnerException, LostConnectionException {
        beforeAddOwnersApproval();
        Iterator<Subscriber> iteratorOwners = mock(Iterator.class);
        when(iteratorOwners.hasNext()).thenReturn(true,true,false);
        when(iteratorOwners.next()).thenReturn(owner1).thenReturn(owner2);
        Iterator<Subscriber> iteratorOwners2 = mock(Iterator.class);
        Iterator<Subscriber> iteratorOwners3 = mock(Iterator.class);
        when(ownerlist.iterator()).thenReturn(iteratorOwners).thenReturn(iteratorOwners2).thenReturn(iteratorOwners3);
        when(store.getOwnersList()).thenReturn(ownerlist);
        when(ownerlist.contains(owner)).thenReturn(true);

        subscriber.addOwnerApproval(store,owner);
        verify(roles, times(0)).get(store);

        beforeAddOwnersApproval();
        when(ownerlist.contains(owner1)).thenReturn(true);
        subscriber.addOwnerApproval(store,owner1);
        verify(roles, times(0)).get(store);

        beforeAddOwnersApproval();
        when(ownerlist.contains(owner2)).thenReturn(true);
        subscriber.addOwnerApproval(store,owner2);
        verify(roles, times(6)).get(store);
        verify(rolesList).add(Roles.Role.STORE_OWNER);
    }


    void beforeAddOwnersApprovalNotExistingStore(){
        when(roles.containsKey(store)).thenReturn(false);
        when(roles.get(store)).thenReturn(rolesList);
    }

    @Test
    void add_three_owners_appointment_approval_not_existing_store() throws NotOwnerException, AllTheOwnersNeedToApproveAppointement, AlreadyOwnerException, LostConnectionException {
        beforeAddOwnersApprovalNotExistingStore();
        Iterator<Subscriber> iteratorOwners = mock(Iterator.class);
        when(iteratorOwners.hasNext()).thenReturn(true,true,false);
        when(iteratorOwners.next()).thenReturn(owner1).thenReturn(owner2);
        Iterator<Subscriber> iteratorOwners2 = mock(Iterator.class);
        Iterator<Subscriber> iteratorOwners3 = mock(Iterator.class);
        when(ownerlist.iterator()).thenReturn(iteratorOwners).thenReturn(iteratorOwners2).thenReturn(iteratorOwners3);
        when(store.getOwnersList()).thenReturn(ownerlist);
        when(ownerlist.contains(owner)).thenReturn(true);

        subscriber.addOwnerApproval(store,owner);
        verify(roles, times(0)).get(store);

        beforeAddOwnersApproval();
        when(ownerlist.contains(owner1)).thenReturn(true);
        subscriber.addOwnerApproval(store,owner1);
        verify(roles, times(0)).get(store);

        beforeAddOwnersApproval();
        when(ownerlist.contains(owner2)).thenReturn(true);
        subscriber.addOwnerApproval(store,owner2);
        verify(roles, times(6)).get(store);
        verify(rolesList).add(Roles.Role.STORE_OWNER);
    }

    @Test
    void add_appoint_owner_approval_no_permission(){
        when(store.getOwnersList()).thenReturn(ownerlist);
        when(ownerlist.contains(owner)).thenReturn(false);
        assertThrows(NotOwnerException.class, ()->subscriber.addOwnerApproval(store,owner));
    }

}