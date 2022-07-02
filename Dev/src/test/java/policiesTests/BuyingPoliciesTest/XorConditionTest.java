package policiesTests.BuyingPoliciesTest;

import Store.PolicyLimitation.IBuyingPolicy;
import Store.PolicyLimitation.LogicOperators.XorCondition;
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
public class XorConditionTest {

    @Mock private Product product1;
    @Mock private ShoppingBag shoppingBag;
    @Mock private Set<Product> products1,products2;
    @Mock private Map<Product,Integer> productsToBuy;
    @Mock private PairInteger pair1,pair2;
    @Mock private Map<Product, PairInteger> productsLimitation1,productsLimitation2;
    private Iterator<Product> productsIterator1 = mock(Iterator.class);
    private Iterator<Product> productsIterator2 = mock(Iterator.class);
    private IBuyingPolicy buyingPolicy1,buyingPolicy2;
    private XorCondition xorCondition;

    @Mock private Set<Map.Entry <Product, PairInteger>> productsLimitationSet;
    @Mock private Map.Entry <Product, PairInteger> productsLimitationS;

    void setUpBuyingPolicy1(){

        when(productsLimitation1.entrySet()).thenReturn(productsLimitationSet);
        Iterator<Map.Entry<Product,PairInteger>> productsLimIterator = mock(Iterator.class);
        when(productsLimitationSet.iterator()).thenReturn(productsLimIterator);
        when(productsLimIterator.hasNext()).thenReturn(true,false);
        when(productsLimIterator.next()).thenReturn(productsLimitationS);
        when(productsLimitationS.getKey()).thenReturn(product1);
        when(productsLimitationS.getValue()).thenReturn(pair1);
        when(pair1.getKey()).thenReturn(1);
        when(pair1.getValue()).thenReturn(3);




        buyingPolicy1 = new AmountLimitationPolicy(productsLimitation1);
        when(shoppingBag.getProducts()).thenReturn(productsToBuy).thenReturn(productsToBuy);
        when(productsToBuy.keySet()).thenReturn(products1).thenReturn(products2);
        when(products1.iterator()).thenReturn(productsIterator1);
        when(productsIterator1.hasNext()).thenReturn(true,false);
        when(productsIterator1.next()).thenReturn(product1);

    }

    void setUpBuyingPolicy2(){
        when(products2.iterator()).thenReturn(productsIterator2);
        when(productsIterator2.hasNext()).thenReturn(true,false);
        when(productsIterator2.next()).thenReturn(product1);

    }

    void setUpOrCondition(){
        List<IBuyingPolicy> buyingPolicies =new LinkedList<>();
        buyingPolicies.add(buyingPolicy1);
        buyingPolicies.add(buyingPolicy2);
        xorCondition = new XorCondition(buyingPolicies);
    }

    @BeforeEach
    void setUp(){
        setUpBuyingPolicy1();


        when(productsLimitation2.entrySet()).thenReturn(productsLimitationSet);
        Iterator<Map.Entry<Product,PairInteger>> productsLimIterator = mock(Iterator.class);
        when(productsLimitationSet.iterator()).thenReturn(productsLimIterator);
        when(productsLimIterator.hasNext()).thenReturn(true,false);
        when(productsLimIterator.next()).thenReturn(productsLimitationS);
        when(productsLimitationS.getKey()).thenReturn(product1);
        when(productsLimitationS.getValue()).thenReturn(pair1);
        when(pair1.getKey()).thenReturn(1);
        when(pair1.getValue()).thenReturn(4);


        buyingPolicy2 = new AmountLimitationPolicy(productsLimitation2);
        setUpBuyingPolicy2();
    }


    @AfterEach
    void tearDown(){

    }

    @Test
    void isSatisfiesCondition_bothTrue(){
        when(productsToBuy.get(product1)).thenReturn(2).thenReturn(2);
        setUpOrCondition();
        assertFalse(xorCondition.isSatisfiesCondition(shoppingBag));
    }

    @Test
    void isSatisfiesCondition_firstTrueSecondFalse(){
        when(productsToBuy.get(product1)).thenReturn(2).thenReturn(5);
        setUpOrCondition();
        assertTrue(xorCondition.isSatisfiesCondition(shoppingBag));
    }


    @Test
    void isSatisfiesCondition_firstFalseSecondTrue(){
        when(productsToBuy.get(product1)).thenReturn(5).thenReturn(2);
        setUpOrCondition();
        assertTrue(xorCondition.isSatisfiesCondition(shoppingBag));
    }

    @Test
    void isSatisfiesCondition_bothFalse(){
        when(productsToBuy.get(product1)).thenReturn(14).thenReturn(12);
        setUpOrCondition();
        assertFalse(xorCondition.isSatisfiesCondition(shoppingBag));
    }

}

