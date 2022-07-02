package StoreTests;

import Exceptions.*;
import Notifications.Observable;
import SpellChecker.Spelling;
import Store.Discount.IDiscountPolicy;
import Store.PolicyLimitation.IBuyingPolicy;
import Store.Store;
import Store.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import User.Subscriber;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StoreTest2 {

    private Store store;
    private String storeName1="firstStore";
    private static int idCounter = 0;
    private int storeId1=1;
    private boolean isActive1=true;
    private int policyId1=1,policyId2=2,policyId3=3;
    private int discountId1=1,discountId2=2,discountId3=3;
    @Mock private Map<Subscriber,List<Subscriber>> managersBy;
    @Mock private Map<Subscriber,List<Subscriber>> ownersBy;
    @Mock private Map<Product,Integer> products;
    @Mock private List<String> history;
    @Mock private Observable observable;
    @Mock private IBuyingPolicy storePolicy;
    @Mock private IDiscountPolicy storeDiscountPolicy;

    @Mock private List<Product> productsFiltered;
    @Mock private Spelling spelling;
    @Mock private List<IBuyingPolicy> buyingPolicies;
    @Mock private List<IDiscountPolicy> discountPolicies;
    @Mock private IBuyingPolicy buyingPolicy2,buyingPolicy3;
    @Mock private IDiscountPolicy discountPolicy2,discountPolicy3;
    @Mock private Map<Product, Integer> bag;
    @Mock private Product product;

    private String productName1="Milk";
    @BeforeEach
    void setUp() throws IllegalDiscountException, IllegalNameException, LostConnectionException {
        store = new Store(storeName1);
        store.setStorePolicy(storePolicy);
        store.setStoreDiscount(storeDiscountPolicy);
    }

//    @Test
//    void getPoliciesID(){
//        List<Integer>ids=new LinkedList<>();
//        ids.add(1);
//        ids.add(2);
//        ids.add(3);
//        Iterator<IBuyingPolicy> policiesIterator = mock(Iterator.class);
//
//        when(policiesIterator.hasNext()).thenReturn(true,true,true,false);
//        when(policiesIterator.next()).thenReturn(storePolicy).thenReturn(buyingPolicy2).thenReturn(buyingPolicy3);
//        when(buyingPolicies.iterator()).thenReturn(policiesIterator);
//        when(storePolicy.getPolicies()).thenReturn(buyingPolicies);
//
//        when((store.getPolicy() instanceof CompoundBuyingPolicy)).thenReturn(true);
//
//        when(storePolicy.getPolicyID()).thenReturn(policyId1);
//        when(buyingPolicy2.getPolicyID()).thenReturn(policyId2);
//        when(buyingPolicy3.getPolicyID()).thenReturn(policyId3);
//
//
//        List<Integer> policiesIds = store.getPoliciesByID();
//        assertTrue(policiesIds.equals(ids));
//    }
//
//    @Test
//    void getDiscountsID(){
//        List<Integer>ids=new LinkedList<>();
//        ids.add(1);
//        ids.add(2);
//        ids.add(3);
//        Iterator<IDiscountPolicy> discountsIterator = mock(Iterator.class);
//
//        when(discountsIterator.hasNext()).thenReturn(true,true,true,false);
//        when(discountsIterator.next()).thenReturn(storeDiscountPolicy).thenReturn(discountPolicy2).thenReturn(discountPolicy3);
//        when(discountPolicies.iterator()).thenReturn(discountsIterator);
//        when(storeDiscountPolicy.getDiscountPolicies()).thenReturn(discountPolicies);
//
//        when(storeDiscountPolicy instanceof CompoundDiscountPolicy).thenReturn(true);
//
//        when(storeDiscountPolicy.getDiscountID()).thenReturn(discountId1);
//        when(discountPolicy2.getDiscountID()).thenReturn(discountId2);
//        when(discountPolicy3.getDiscountID()).thenReturn(discountId3);
//
//        Collection<Integer> discountsIds = store.getDiscountsByID();
//        assertTrue(discountsIds.equals(ids));
//    }

    @Test
    void validateInventory() throws InsufficientInventory, ProductNotFoundException {
//        store.setProducts(products);
////        when(products.containsKey(product)).thenReturn(true);
////        when(products.get(product)).thenReturn(10);
//        Iterator<Map<Product,Integer>> bagIterator = mock(Iterator.class);
//        when(bagIterator.hasNext()).thenReturn(true,false);
//        when(bagIterator.next()).thenReturn(bag);
//        when(bag.entrySet().iterator()).thenReturn(bagIterator);
//
//
////        when(bag.containsKey(product)).thenReturn(true);
////        when(bag.get(product)).thenReturn(5);
//        store.validateInventory(bag);
//        assertDoesNotThrow(() -> store.validateInventory(bag));
    }



//    @Test
//    void getPoliciesByID(){
////        List<Integer> ids = new LinkedList<>();
////        ids.add(1);
////        ids.add(2);
////        ids.add(3);
//
////        when(store.getPolicy()).thenReturn(storePolicy);
//        when(storePolicy.getPolicyID()).thenReturn(policyId1);
//        when(storePolicy.isCompound()).thenReturn(true);
//        when(storePolicy.getPolicies()).thenReturn(buyingPolicies);
//        when(buyingPolicies.contains(buyingPolicy2)).thenReturn(true);
//        when(buyingPolicies.contains(buyingPolicy3)).thenReturn(true);
//        when(buyingPolicy2.getPolicyID()).thenReturn(policyId2);
//        when(buyingPolicy3.getPolicyID()).thenReturn(policyId3);
////        for(IBuyingPolicy policy :storePolicy.getPolicies())
////            assertTrue(ids.contains(policy.getPolicyID()));
//
//
//    }


//    @Mock private System system;
//    @Mock private List<Product> products;
//    @Mock private Collection<String> roles;
//    @Mock private Product product1;
//    @Mock private Product product2;
//    @Mock private User user;
//    @Mock private Store store;
//    @Mock private ShoppingBag shoppingBag;
//
//    private String entrance1 = "999";
//    private String userName1 = "Admin";
//    private String password1 = "admin123";
//    private String prouductName1 = "Ball";
//    private String category1 = "Fun";
//    private String keyWord1 = "Swimming";
//    private String storeName = "Ebay";
//    private String description = "blah blah";
//    private double startRange = 1.0;
//    private double endRange = 120.5;
//    private int storeId = 55;
//    private int productId = 187;
//    private int finalAmount = 10;
//
//    private String card_number = "1234", holder = "a", ccv = "001", id = "000000018", name = "name", address = "address", city = "city", country = "country";
//    private int month = 1, year = 2022, zip = 12345;


}
