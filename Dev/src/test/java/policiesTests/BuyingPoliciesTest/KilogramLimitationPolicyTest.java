package policiesTests.BuyingPoliciesTest;

import Store.PairDouble;
import Store.PolicyLimitation.IBuyingPolicy;
import Store.PolicyLimitation.SimpleLimitationPolicy.KilogramLimitationPolicy;
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

@ExtendWith(MockitoExtension.class)
public class KilogramLimitationPolicyTest {

    @Mock private Product product1;
    @Mock private ShoppingBag shoppingBag;
    @Mock private Set<Product> products;
    @Mock private Map<Product,Integer> productsToBuy;
    @Mock private PairDouble pair1;
    @Mock private Map<Product, PairDouble> productsLimitation;
    private Iterator<Product> productsIterator1 = mock(Iterator.class);
    private IBuyingPolicy buyingPolicy;

    @BeforeEach
    void setUp(){
        buyingPolicy = new KilogramLimitationPolicy(productsLimitation);
        when(shoppingBag.getProducts()).thenReturn(productsToBuy);
        when(productsToBuy.keySet()).thenReturn(products);
        when(productsLimitation.containsKey(product1)).thenReturn(true);
        when(products.iterator()).thenReturn(productsIterator1);
        when(productsIterator1.hasNext()).thenReturn(true,false);
        when(productsIterator1.next()).thenReturn(product1);
        when(productsLimitation.containsKey(product1)).thenReturn(true);
        when(productsLimitation.get(product1)).thenReturn(pair1);
        when(pair1.getKey()).thenReturn(1.0);
        when(pair1.getValue()).thenReturn(3.0);
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

