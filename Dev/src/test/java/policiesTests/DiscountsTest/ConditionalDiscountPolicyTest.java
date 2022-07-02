package policiesTests.DiscountsTest;

import Exceptions.IllegalDiscountException;
import Exceptions.IllegalNameException;
import Exceptions.LostConnectionException;
import Store.Discount.*;
import Store.PolicyLimitation.IBuyingPolicy;
import Store.PolicyLimitation.SimpleLimitationPolicy.NoLimitationPolicy;
import Store.PolicyLimitation.SimpleLimitationPolicy.TotalPriceLimitationPolicy;
import Store.Product;
import Store.Store;
import User.User;
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
public class ConditionalDiscountPolicyTest {


    @Mock private Product product1, product2;
    @Mock private ShoppingBag shoppingBag;
    @Mock private List<Product> productList1;


    private String category= "Diary",category3="Vegetables";;
    private double discount1 = 50;
    private double price1=7.0,price2=10.0,price3=5.0,price4=6.5;
    private int quantity1 =5, quantity2 =3;
    private String productName1="pizza",productName2="tomato";
    private String desc="fhnfsv";

    private Product p1 = new Product(1, price1,desc,productName1,category);
    private Product p2 = new Product(1, price2,desc,productName2,category3);
    Iterator<Product> productsIterator = mock(Iterator.class);
    IDiscountPolicy productsDiscountPolicy;
    Map<Product,Double> discounts;


    @BeforeEach
    void setUp(){

    }

    @AfterEach
    void tearDown(){

    }

    void setUpProducts(){
        when(productList1.iterator()).thenReturn(productsIterator);
        when(productsIterator.hasNext()).thenReturn(true,true,false);
        when(productsIterator.next()).thenReturn(product1).thenReturn(product2);
    }

    void setUpDiscounts() throws IllegalDiscountException {
        productsDiscountPolicy = new SimpleDiscountByProducts(productList1,discount1);
        discounts = new HashMap<>();
        discounts.put(product1,discount1);
        discounts.put(product2,discount1);
    }


    @Test
    void getDiscountsToCart_noLimitationPolicy() throws IllegalDiscountException {
        setUpProducts();
        setUpDiscounts();
        IBuyingPolicy buyingPolicy = new NoLimitationPolicy();
        ConditionalDiscountPolicy conditionalDiscountPolicy = new ConditionalDiscountPolicy(buyingPolicy,productsDiscountPolicy);
        assertEquals(conditionalDiscountPolicy.getDiscountsToCart(shoppingBag),discounts);
    }

    @Test
    void getDiscountsToCart_BuyingPolicyIsNull() throws IllegalDiscountException {
        setUpProducts();
        setUpDiscounts();
        ConditionalDiscountPolicy conditionalDiscountPolicy = new ConditionalDiscountPolicy(null,productsDiscountPolicy);
        assertEquals(conditionalDiscountPolicy.getDiscountsToCart(shoppingBag),discounts);
    }

    @Test
    void getDiscountsToCart_incorrectCondition() throws IllegalDiscountException, IllegalNameException, LostConnectionException {
        IBuyingPolicy buyingPolicy = new TotalPriceLimitationPolicy(0,5.0);

        User realUser = new User();
        Store realStore = new Store("Store1");
        Map<Product,Integer> realProductsToBuy = new HashMap<>();
        realProductsToBuy.put(p1,quantity1);
        realProductsToBuy.put(p2,quantity2);

        ShoppingBag realShoppingBag = new ShoppingBag(realStore,realUser,realProductsToBuy);
        List<Product> realProducts1 = new LinkedList<>();

        realProducts1.add(p1);
        realProducts1.add(p2);

        SimpleDiscountByProducts productsDiscountPolicy1 = new SimpleDiscountByProducts(realProducts1,discount1);
        ConditionalDiscountPolicy conditionalDiscountPolicy = new ConditionalDiscountPolicy(buyingPolicy,productsDiscountPolicy1);
        conditionalDiscountPolicy.getDiscountsToCart(realShoppingBag);
        assertNull(conditionalDiscountPolicy.getDiscountsToCart(realShoppingBag));
    }

}

