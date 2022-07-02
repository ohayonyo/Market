package StoreTests;

import Exceptions.*;
import Store.Bid.Bid;
import Store.Product;
import Store.Store;
import Store.SubscriberList;
import User.ShoppingBag;
import User.Subscriber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BidUnitTests {
    private Product product;
    private Subscriber noPermissions;
    private Subscriber subscriber;
    private Store store;
    private Bid bid;
    private Set<Product> products;
    private Set<Subscriber> owners;
    private HashMap<Product,Integer> map;
    private HashMap<Subscriber, SubscriberList> ownersBy;
    private Subscriber owner1;
    private Subscriber owner2;
    private HashMap<Subscriber,SubscriberList> managersBy;
    private Subscriber manager1;
    private Subscriber manager2;
    private Set<Subscriber> managers;
    private HashMap<Store,ShoppingBag> cart;
    private ShoppingBag shoppingBag;



    @BeforeEach
    void setUp() throws ProductNotFoundException, IllegalRangeException, BidException {
        store = mock(Store.class);
        products = mock(Set.class);
        owners = mock(Set.class);
        map = mock(HashMap.class);
        ownersBy = mock(HashMap.class);
        subscriber = mock(Subscriber.class);
        noPermissions = mock(Subscriber.class);
        product = mock(Product.class);
        owner1 = mock(Subscriber.class);
        owner2 = mock(Subscriber.class);
        managersBy = mock(HashMap.class);
        manager1 = mock(Subscriber.class);
        manager2 = mock(Subscriber.class);
        managers = mock(Set.class);
        cart = mock(HashMap.class);
        shoppingBag = mock(ShoppingBag.class);


      ///  MockitoAnnotations.openMocks(this);
        when(store.getProducts()).thenReturn(products);
        when(products.contains(product)).thenReturn(true);
        when(store.getProductsAndAmount()).thenReturn(map);
        when(map.get(product)).thenReturn(5);

        bid = new Bid(subscriber,product,4,10.5, store);
    }

    @AfterEach
    void tearDown(){

    }

    public void beforeAddOwnerApproval(){
        when(store.getOwnersBy()).thenReturn(ownersBy);
        when(ownersBy.keySet()).thenReturn(owners);
        Iterator<Subscriber> iteratorOwners = mock(Iterator.class);
        when(owners.iterator()).thenReturn(iteratorOwners);
        when(iteratorOwners.hasNext()).thenReturn(true,true,false);
        when(iteratorOwners.next()).thenReturn(owner1).thenReturn(owner2);
        when(store.getManagersBy()).thenReturn(managersBy);
        when(managersBy.keySet()).thenReturn(managers);
        Iterator<Subscriber> iteratorManagers = mock(Iterator.class);
        when(managers.iterator()).thenReturn(iteratorManagers);
        when(iteratorManagers.hasNext()).thenReturn(true,true,false);
        when(iteratorManagers.next()).thenReturn(manager1).thenReturn(manager2);
        when(manager1.isManagerBidPermission(store)).thenReturn(true);
        when(manager2.isManagerBidPermission(store)).thenReturn(false);

    }

    @Test
    public void addApprovalWithoutPermissions(){
        beforeAddOwnerApproval();
        assertThrows(IllegalPermissionsAccess.class,()->bid.addOwnerApproval(noPermissions)); //Subscriber Doesn't have permissions to approve bid
        beforeAddOwnerApproval();
        assertThrows(IllegalPermissionsAccess.class,()->bid.addOwnerApproval(manager2)); //Manager Doesn't have permissions to approve bid
    }

    @Test
    public void addOwnerApproval() throws ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, IllegalPermissionsAccess, LostConnectionException {
        //All permitted subscribers approve the bid
        beforeAddOwnerApproval();
        bid.addOwnerApproval(owner1);
        beforeAddOwnerApproval();
        bid.addOwnerApproval(owner2);
        beforeAddOwnerApproval();
        when(subscriber.getCart()).thenReturn(cart);
        when(subscriber.getCart()).thenReturn(cart);
        when(cart.get(store)).thenReturn(shoppingBag);
        bid.addOwnerApproval(manager1);
        assertTrue(bid.isApproved());

    }

    public void beforeAddCounteredProposal(){
        when(store.getOwnersBy()).thenReturn(ownersBy);
        when(ownersBy.keySet()).thenReturn(owners);
        Iterator<Subscriber> iteratorOwners = mock(Iterator.class);
        when(owners.iterator()).thenReturn(iteratorOwners);
        when(iteratorOwners.hasNext()).thenReturn(true,true,false);
        when(iteratorOwners.next()).thenReturn(owner1).thenReturn(owner2);
        when(store.getManagersBy()).thenReturn(managersBy);
        when(managersBy.keySet()).thenReturn(managers);
        Iterator<Subscriber> iteratorManagers = mock(Iterator.class);
        when(managers.iterator()).thenReturn(iteratorManagers);
        when(iteratorManagers.hasNext()).thenReturn(true,true,false);
        when(iteratorManagers.next()).thenReturn(manager1).thenReturn(manager2);
        when(manager1.isManagerBidPermission(store)).thenReturn(true);
        when(manager2.isManagerBidPermission(store)).thenReturn(false);
    }

    @Test
    public void addCounteredProposal() throws NotOwnerException, ProductShouldBeInCartBeforeBidOnIt, ProductNotFoundException, IllegalRangeException, BidException, IllegalPriceException, IllegalPermissionsAccess, LostConnectionException {
        when(store.getOwnersBy()).thenReturn(ownersBy).thenReturn(ownersBy);
        when(ownersBy.keySet()).thenReturn(owners).thenReturn(owners);
        Iterator<Subscriber> iteratorOwners = mock(Iterator.class);
        Iterator<Subscriber> iteratorOwners2 = mock(Iterator.class);
        when(owners.iterator()).thenReturn(iteratorOwners).thenReturn(iteratorOwners2);
        when(iteratorOwners.hasNext()).thenReturn(true,true,false);
        when(iteratorOwners.next()).thenReturn(owner1).thenReturn(owner2);
        when(iteratorOwners2.hasNext()).thenReturn(true,true,false);
        when(iteratorOwners2.next()).thenReturn(owner1).thenReturn(owner2);
        when(store.getManagersBy()).thenReturn(managersBy).thenReturn(managersBy);
        when(managersBy.keySet()).thenReturn(managers).thenReturn(managers);
        Iterator<Subscriber> iteratorManagers = mock(Iterator.class);
        Iterator<Subscriber> iteratorManagers2 = mock(Iterator.class);
        when(managers.iterator()).thenReturn(iteratorManagers).thenReturn(iteratorManagers2);
        when(iteratorManagers.hasNext()).thenReturn(true,true,false);
        when(iteratorManagers.next()).thenReturn(manager1).thenReturn(manager2);
        when(iteratorManagers2.hasNext()).thenReturn(true,true,false);
        when(iteratorManagers2.next()).thenReturn(manager1).thenReturn(manager2);
        when(manager1.isManagerBidPermission(store)).thenReturn(true).thenReturn(true);
        when(manager2.isManagerBidPermission(store)).thenReturn(false).thenReturn(false);


        bid.addCounteredProposal(owner1,3,2.0);
        assertTrue(bid.getOwnerCounteredProposal(owner1).toString().contains("bid on product")&&bid.getOwnerCounteredProposal(owner1).toString().contains("with amount of : 3"));

    }

    @Test
    public void allOwnersAndManagersApproveCounteredProposal() throws NotOwnerException, ProductShouldBeInCartBeforeBidOnIt, ProductNotFoundException, IllegalRangeException, BidException, IllegalPriceException, IllegalPermissionsAccess, LostConnectionException {
        addCounteredProposal();
        Bid b = bid.getOwnerCounteredProposal(owner1);
        beforeAddOwnerApproval();
        b.addOwnerApproval(owner2);
        beforeAddOwnerApproval();
        when(subscriber.getCart()).thenReturn(cart).thenReturn(cart);
        when(cart.get(store)).thenReturn(shoppingBag);

        b.addOwnerApproval(manager1);
        assertTrue(b.isApproved());
    }

    @Test
    public void NotAllOwnersAndManagersApproveCounteredProposal() throws NotOwnerException, ProductShouldBeInCartBeforeBidOnIt, ProductNotFoundException, IllegalRangeException, BidException, IllegalPriceException, IllegalPermissionsAccess, LostConnectionException {
        addCounteredProposal();
        Bid b = bid.getOwnerCounteredProposal(owner1);
        beforeAddOwnerApproval();
        b.addOwnerApproval(owner2);
        beforeAddOwnerApproval();

        assertFalse(b.isApproved());
    }


    @Test
    public void rejectBid() throws IllegalPermissionsAccess, LostConnectionException {
        when(store.getOwnersBy()).thenReturn(ownersBy);
        when(ownersBy.keySet()).thenReturn(owners);
        when(owners.contains(owner1)).thenReturn(true);

        bid.rejectBid(owner1);
        assertFalse(bid.isApproved());
    }

    @Test
    public void rejectBidWithoutPermissions(){
        assertThrows(IllegalPermissionsAccess.class,()->bid.rejectBid(noPermissions)); //Subscriber Doesn't have permissions to reject bid
        assertThrows(IllegalPermissionsAccess.class,()->bid.rejectBid(manager2)); //Manager Doesn't have permissions to reject bid

    }


}
