package policiesTests.BuyingPoliciesTest;

import Store.PolicyLimitation.IBuyingPolicy;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import Store.PairInteger;

@ExtendWith(MockitoExtension.class)
public class AmountLimitationPolicyTest {

    @Mock private Product product1;
    @Mock private ShoppingBag shoppingBag;
    @Mock private Set<Product> products;
    @Mock private Map<Product,Integer> productsToBuy;
    @Mock private PairInteger pair1;
    @Mock private Map<Product, PairInteger> productsLimitation;

    @Mock private Set<Map.Entry <Product, PairInteger>> productsLimitationSet;
    @Mock private Map.Entry <Product, PairInteger> productsLimitationS;
    @Mock private Map<Product,Integer> productMinLimitation;
    @Mock private Map<Product,Integer> productMaxLimitation;
    private Iterator<Product> productsIterator1 = mock(Iterator.class);
    private IBuyingPolicy buyingPolicy;

    @BeforeEach
    void setUp(){

        when(productsLimitation.entrySet()).thenReturn(productsLimitationSet);
        Iterator<Map.Entry<Product,PairInteger>> productsLimIterator = mock(Iterator.class);
        when(productsLimitationSet.iterator()).thenReturn(productsLimIterator);
        when(productsLimIterator.hasNext()).thenReturn(true,false);
        when(productsLimIterator.next()).thenReturn(productsLimitationS);
        when(productsLimitationS.getKey()).thenReturn(product1);
        when(productsLimitationS.getValue()).thenReturn(pair1);
        when(pair1.getKey()).thenReturn(1);
        when(pair1.getValue()).thenReturn(3);

        buyingPolicy = new AmountLimitationPolicy(productsLimitation);

        when(shoppingBag.getProducts()).thenReturn(productsToBuy);
        when(productsToBuy.keySet()).thenReturn(products);
        when(products.iterator()).thenReturn(productsIterator1);
        when(productsIterator1.hasNext()).thenReturn(true,false);
        when(productsIterator1.next()).thenReturn(product1);
//        when(productsLimitation.containsKey(product1)).thenReturn(true);
//        when(productsLimitation.containsKey(product1)).thenReturn(true);
//        when(productsLimitation.get(product1)).thenReturn(pair1);
//        when(pair1.getKey()).thenReturn(1);
//        when(pair1.getValue()).thenReturn(3);
    }

    @AfterEach
    void tearDown(){

    }

    @Test
    void isSatisfiesCondition_amountInRange(){
        when(productsToBuy.get(product1)).thenReturn(2);
        assertTrue(buyingPolicy.isSatisfiesCondition(shoppingBag));
    }

    @Test
    void isSatisfiesCondition_amountOutOfRange(){
        when(productsToBuy.get(product1)).thenReturn(4);
        assertFalse(buyingPolicy.isSatisfiesCondition(shoppingBag));
    }

}

