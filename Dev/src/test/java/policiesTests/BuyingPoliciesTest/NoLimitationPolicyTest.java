package policiesTests.BuyingPoliciesTest;

import Store.PolicyLimitation.IBuyingPolicy;
import Store.PolicyLimitation.SimpleLimitationPolicy.NoLimitationPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import User.ShoppingBag;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class NoLimitationPolicyTest {

    @Mock private ShoppingBag shoppingBag;
    @BeforeEach
    void setUp(){

    }

    @AfterEach
    void tearDown(){

    }

    @Test
    void isSatisfiesCondition_alwaysTrue(){
        IBuyingPolicy buyingPolicy = new NoLimitationPolicy();
        assertTrue(buyingPolicy.isSatisfiesCondition(shoppingBag));
    }

}

