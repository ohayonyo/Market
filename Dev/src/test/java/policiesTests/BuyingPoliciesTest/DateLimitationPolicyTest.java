package policiesTests.BuyingPoliciesTest;

import Store.PolicyLimitation.SimpleLimitationPolicy.DateLimitationPolicy;
import Store.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import User.ShoppingBag;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DateLimitationPolicyTest {

    @Mock private Product product1,product2;
    @Mock private ShoppingBag shoppingBag;
    @Mock private List<Product> products;
    @Mock private Map<Product,Integer> productsToBuy;
    @Mock private Set<Product> productsSet;
    @Mock private LocalDate forbiddenDate;
    @Mock private LocalDate today;
    Iterator<Product> productsIterator = mock(Iterator.class);
    private DateLimitationPolicy dateLimitationPolicy;

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
    void isSatisfiesCondition_dateInRange(){
        dateLimitationPolicy=new DateLimitationPolicy(LocalDate.of(2030,10,5),products);
        assertTrue(dateLimitationPolicy.isSatisfiesCondition(shoppingBag));
    }

    @Test
    void isSatisfiesCondition_dateOutOfRange(){
        dateLimitationPolicy=new DateLimitationPolicy(LocalDate.now(),products);
        assertFalse(dateLimitationPolicy.isSatisfiesCondition(shoppingBag));
    }

}

