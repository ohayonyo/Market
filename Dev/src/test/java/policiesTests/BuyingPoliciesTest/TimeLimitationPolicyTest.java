package policiesTests.BuyingPoliciesTest;

import Store.PolicyLimitation.IBuyingPolicy;
import Store.PolicyLimitation.SimpleLimitationPolicy.TimeLimitationPolicy;
import Store.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.*;

import User.ShoppingBag;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TimeLimitationPolicyTest {

    @Mock private Product product1,product2;
    @Mock private ShoppingBag shoppingBag;
    @Mock private List<Product> products;
    @Mock private Map<Product,Integer> productsToBuy;
    @Mock private Set<Product> productsSet;
    Iterator<Product> productsIterator = mock(Iterator.class);

    @BeforeEach
    void setUp(){
        when(shoppingBag.getProducts()).thenReturn(productsToBuy);
        when(productsToBuy.keySet()).thenReturn(productsSet);
        when(productsSet.iterator()).thenReturn(productsIterator);
        when(productsIterator.hasNext()).thenReturn(true,true,false);
        when(productsIterator.next()).thenReturn(product1).thenReturn(product2);
        when(products.contains(product1)).thenReturn(true);
    }

    @AfterEach
    void tearDown(){

    }


    @Test
    void isSatisfiesCondition_timeInRange(){
        LocalTime minTime,maxTime;
        LocalTime now = LocalTime.now();
        minTime = LocalTime.of((now.getHour()-1)%24,0);
        maxTime = LocalTime.of((now.getHour()+1)%24,0);
        IBuyingPolicy timePolicy = new TimeLimitationPolicy(products,minTime,maxTime);
        assertTrue(timePolicy.isSatisfiesCondition(shoppingBag));
    }

    @Test
    void isSatisfiesCondition_timeOutOfRange(){
        LocalTime minTime,maxTime;
        LocalTime now = LocalTime.now();
        minTime = LocalTime.of((now.getHour()+1)%24,0);
        maxTime = LocalTime.of((now.getHour()+2)%24,0);
        IBuyingPolicy timePolicy = new TimeLimitationPolicy(products,minTime,maxTime);
        assertFalse(timePolicy.isSatisfiesCondition(shoppingBag));
    }

}

