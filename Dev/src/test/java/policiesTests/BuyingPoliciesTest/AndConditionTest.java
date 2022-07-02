package policiesTests.BuyingPoliciesTest;

import Store.PolicyLimitation.IBuyingPolicy;
import Store.PolicyLimitation.LogicOperators.AndCondition;
import Store.PolicyLimitation.SimpleLimitationPolicy.AmountLimitationPolicy;
import Store.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import User.ShoppingBag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import Store.PairInteger;

@ExtendWith(MockitoExtension.class)
public class AndConditionTest {

    @Mock private Product product1;
    @Mock private ShoppingBag shoppingBag;
    @Mock private Set<Product> products;
    @Mock private Map<Product,Integer> productsToBuy;
    @Mock private PairInteger pair1,pair2;
    @Mock private Map<Product, PairInteger> productsLimitation1,productsLimitation2;
    private Iterator<Product> productsIterator1 = mock(Iterator.class);
    private IBuyingPolicy buyingPolicy1,buyingPolicy2;
    private AndCondition andCondition;

    @Mock private Set<Map.Entry <Product, PairInteger>> productsLimitationSet1;
    @Mock private Map.Entry <Product, PairInteger> productsLimitationS1;

    void setUpBuyingPolicy1(){
        when(productsLimitation1.entrySet()).thenReturn(productsLimitationSet1);
        Iterator<Map.Entry<Product,PairInteger>> productsLimIterator = mock(Iterator.class);
        when(productsLimitationSet1.iterator()).thenReturn(productsLimIterator);
        when(productsLimIterator.hasNext()).thenReturn(true,false);
        when(productsLimIterator.next()).thenReturn(productsLimitationS1);
        when(productsLimitationS1.getKey()).thenReturn(product1);
        when(productsLimitationS1.getValue()).thenReturn(pair1);
        when(pair1.getKey()).thenReturn(1);
        when(pair1.getValue()).thenReturn(3);


        buyingPolicy1 = new AmountLimitationPolicy(productsLimitation1);
        when(shoppingBag.getProducts()).thenReturn(productsToBuy).thenReturn(productsToBuy);
        when(productsToBuy.keySet()).thenReturn(products).thenReturn(products);
//        when(productsLimitation1.containsKey(product1)).thenReturn(true);
        when(products.iterator()).thenReturn(productsIterator1).thenReturn(productsIterator1);
        when(productsIterator1.hasNext()).thenReturn(true,false).thenReturn(true,false);
        when(productsIterator1.next()).thenReturn(product1).thenReturn(product1);
//        when(productsLimitation1.containsKey(product1)).thenReturn(true);
//        when(productsLimitation1.get(product1)).thenReturn(pair1);
//        when(pair1.getKey()).thenReturn(1);
//        when(pair1.getValue()).thenReturn(3);
    }

    void setUpBuyingPolicy2(){
        when(productsLimitation2.entrySet()).thenReturn(productsLimitationSet1);
        Iterator<Map.Entry<Product,PairInteger>> productsLimIterator = mock(Iterator.class);
        when(productsLimitationSet1.iterator()).thenReturn(productsLimIterator);
        when(productsLimIterator.hasNext()).thenReturn(true,false);
        when(productsLimIterator.next()).thenReturn(productsLimitationS1);
        when(productsLimitationS1.getKey()).thenReturn(product1);
        when(productsLimitationS1.getValue()).thenReturn(pair2);
        when(pair2.getKey()).thenReturn(1);
        when(pair2.getValue()).thenReturn(3);


        buyingPolicy2 = new AmountLimitationPolicy(productsLimitation2);
//        when(productsLimitation2.containsKey(product1)).thenReturn(true);
//        when(productsLimitation2.get(product1)).thenReturn(pair2);
//        when(pair2.getKey()).thenReturn(1);
//        when(pair2.getValue()).thenReturn(2);
    }

    void setUpAndCondition(){
        List<IBuyingPolicy> buyingPolicies =new LinkedList<>();
        buyingPolicies.add(buyingPolicy1);
        buyingPolicies.add(buyingPolicy2);
        andCondition = new AndCondition(buyingPolicies);
    }

    @BeforeEach
    void setUp(){
        setUpBuyingPolicy1();
    }


    @AfterEach
    void tearDown(){

    }

    @Test
    void isSatisfiesCondition_bothTrue(){
        setUpBuyingPolicy2();
        when(productsToBuy.get(product1)).thenReturn(2).thenReturn(2);
        setUpAndCondition();
        assertTrue(andCondition.isSatisfiesCondition(shoppingBag));
    }

    @Test
    void isSatisfiesCondition_firstTrueSecondFalse(){
        setUpBuyingPolicy2();
        when(productsToBuy.get(product1)).thenReturn(2).thenReturn(4);
        setUpAndCondition();
        assertFalse(andCondition.isSatisfiesCondition(shoppingBag));
    }

    @Test
    void isSatisfiesCondition_firstFalseSecondTrue(){
        when(productsToBuy.get(product1)).thenReturn(4).thenReturn(2);
        setUpAndCondition();
        assertFalse(andCondition.isSatisfiesCondition(shoppingBag));
    }

    @Test
    void isSatisfiesCondition_bothFalse(){
        when(productsToBuy.get(product1)).thenReturn(14).thenReturn(12);
        setUpAndCondition();
        assertFalse(andCondition.isSatisfiesCondition(shoppingBag));
    }

}

