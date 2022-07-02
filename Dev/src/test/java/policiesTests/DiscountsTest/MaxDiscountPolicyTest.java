package policiesTests.DiscountsTest;

import Exceptions.IllegalDiscountException;
import Exceptions.IllegalNameException;
import Exceptions.LostConnectionException;
import Store.Discount.*;
import Store.Product;
import Store.Store;
import User.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import User.ShoppingBag;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class MaxDiscountPolicyTest {

    private String category= "Diary",category2="Vegan",category3="Vegetables";
    private double discount1 = 50,discount2=25;
    private double price1=7.0,price2=10.0,price3=5.0,price4=6.5;
    private int quantity1 =5, quantity2 =3,quantity3=4,quantity4=1;

    private String productName1="pizza",productName2="tomato",productName3="milk",productName4="egg";
    private String desc="fhnfsv";
    private Product p1 = new Product(1, price1,desc,productName1,category);
    private Product p2 = new Product(1, price2,desc,productName2,category3);
    private Product p3 = new Product(1, price3,desc,productName3,category);
    private Product p4 = new Product(1, price4,desc,productName4,category2);
    List<Product> realProducts1,realProducts2;
    ShoppingBag realShoppingBag;

    @BeforeEach
    void setUp() throws IllegalDiscountException, IllegalNameException, LostConnectionException {
        User realUser = new User();
        Store realStore = new Store("realStore");
        Map<Product,Integer> realProductsToBuy = new HashMap<>();
        realProductsToBuy.put(p1,quantity1);
        realProductsToBuy.put(p2,quantity2);
        realProductsToBuy.put(p3,quantity3);
        realProductsToBuy.put(p4,quantity4);

        realShoppingBag = new ShoppingBag(realStore,realUser,realProductsToBuy);
        realProducts1 = new LinkedList<>();
        realProducts2 = new LinkedList<>();

        realProducts1.add(p1);
        realProducts1.add(p2);
        realProducts2.add(p3);
        realProducts2.add(p4);

    }

    @AfterEach
    void tearDown(){

    }

    //best deal is the first deal (productsDiscountPolicy1)
    @Test
    void getDiscountsToCart_firstDealIsBetter() throws IllegalDiscountException{
        IDiscountPolicy productsDiscountPolicy1 = new SimpleDiscountByProducts(realProducts1,discount1);
        IDiscountPolicy productsDiscountPolicy2 = new SimpleDiscountByProducts(realProducts2,discount2);
        List<IDiscountPolicy> discountsToMax = new LinkedList<>();
        discountsToMax.add(productsDiscountPolicy1);
        discountsToMax.add(productsDiscountPolicy2);
        MaxDiscountPolicy maxDiscountPolicy = new MaxDiscountPolicy(discountsToMax);

        assertEquals(maxDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p1),discount1);
        assertEquals(maxDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p2),discount1);
        assertTrue(!maxDiscountPolicy.getDiscountsToCart(realShoppingBag).containsKey(p3));
        assertTrue(!maxDiscountPolicy.getDiscountsToCart(realShoppingBag).containsKey(p4));

    }

    //best deal is the second deal (productsDiscountPolicy2)
    @Test
    void getDiscountsToCart_secondDealIsBetter() throws IllegalDiscountException{
        IDiscountPolicy productsDiscountPolicy2 = new SimpleDiscountByProducts(realProducts1,discount1);
        IDiscountPolicy productsDiscountPolicy1 = new SimpleDiscountByProducts(realProducts2,discount2);
        List<IDiscountPolicy> discountsToMax = new LinkedList<>();
        discountsToMax.add(productsDiscountPolicy1);
        discountsToMax.add(productsDiscountPolicy2);
        MaxDiscountPolicy maxDiscountPolicy = new MaxDiscountPolicy(discountsToMax);

        assertEquals(maxDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p1),discount1);
        assertEquals(maxDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p2),discount1);
        assertTrue(!maxDiscountPolicy.getDiscountsToCart(realShoppingBag).containsKey(p3));
        assertTrue(!maxDiscountPolicy.getDiscountsToCart(realShoppingBag).containsKey(p4));

    }


    @Test
    void getDiscountsToCart_withCompoundDiscount() throws IllegalDiscountException{
        IDiscountPolicy productsDiscountPolicy2 = new SimpleDiscountByProducts(realProducts1,discount1);
        IDiscountPolicy productsDiscountPolicy1 = new SimpleDiscountByProducts(realProducts2,discount2);
        List<IDiscountPolicy> discountsToMax = new LinkedList<>();
        discountsToMax.add(productsDiscountPolicy1);
        discountsToMax.add(productsDiscountPolicy2);
        MaxDiscountPolicy maxDiscountPolicy = new MaxDiscountPolicy(discountsToMax);

        IDiscountPolicy productsDiscountPolicy3 = new SimpleDiscountByProducts(realProducts1,10);
        discountsToMax = new LinkedList<>();
        discountsToMax.add(maxDiscountPolicy);
        discountsToMax.add(productsDiscountPolicy3);
        maxDiscountPolicy = new MaxDiscountPolicy(discountsToMax);

        assertEquals(maxDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p1),discount1);
        assertEquals(maxDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p2),discount1);
        assertTrue(!maxDiscountPolicy.getDiscountsToCart(realShoppingBag).containsKey(p3));
        assertTrue(!maxDiscountPolicy.getDiscountsToCart(realShoppingBag).containsKey(p4));

    }



}

