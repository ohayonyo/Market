package policiesTests.DiscountsTest;

import Exceptions.IllegalDiscountException;
import Store.Discount.*;
import Store.Product;
import Store.Store;
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
public class SimpleDiscountByStoreTest {

    @Mock private Store store;
    @Mock private Product product1, product2;
    @Mock private ShoppingBag shoppingBag;
    @Mock private Set<Product> products;
    @Mock private Map<Product,Integer> productsToBuy;
    @Mock private Set<Map.Entry<Product,Integer>> productsEntry;
    @Mock private Map.Entry<Product,Integer> productEntry1,productEntry2;


    private String category= "Diary",category2="Vegan";
    private double totalCartPrice1=47.5,totalCartPrice2=65.0,totalCartPrice3=32.5;
    private double totalCartPrice4=48.75;
    private double discount1 = 50,discount2=25,noDiscount=0,negativeDiscount=-10,exceededDiscount=110;
    private double price1=7.0,price2=10.0;
    private int quantity1 =5, quantity2 =3;
    @BeforeEach
    void setUp(){

    }

    @AfterEach
    void tearDown(){

    }

    //wants to buy 5 of products of product1(price 7.0) and 3 products of product2 (price 10.0)
    void setShoppingBag(){
        Iterator<Map.Entry<Product,Integer>> iterator = mock(Iterator.class);
        when(shoppingBag.getProducts()).thenReturn(productsToBuy);
        when(productsToBuy.entrySet()).thenReturn(productsEntry);
        when(productsEntry.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true,true,false);
        when(iterator.next()).thenReturn(productEntry1).thenReturn(productEntry2);
        when(productEntry1.getKey()).thenReturn(product1);
        when(productEntry1.getValue()).thenReturn(quantity1);
        when(productEntry2.getKey()).thenReturn(product2);
        when(productEntry2.getValue()).thenReturn(quantity2);
        when(product1.getPrice()).thenReturn(price1);
        when(product2.getPrice()).thenReturn(price2);
    }

    void setProductsAtStore(){
        Iterator<Product> productsIterator = mock(Iterator.class);
        when(store.getProducts()).thenReturn(products);
        when(products.iterator()).thenReturn(productsIterator);
        when(productsIterator.hasNext()).thenReturn(true,true,false);
        when(productsIterator.next()).thenReturn(product1).thenReturn(product2);
    }


    @Test
    void illegalDiscount(){
        assertThrows(IllegalDiscountException.class,()->new SimpleDiscountByStore(store,negativeDiscount));
        assertThrows(IllegalDiscountException.class,()->new SimpleDiscountByStore(store,exceededDiscount));
    }

//    @Test // 50% discount on store1
//    void getDiscountsToCart_halfPrice() throws IllegalDiscountException {
//        Map<Product,Double> discounts = new HashMap<>();
//        discounts.put(product1,discount1);
//        discounts.put(product2,discount1);
//        setProductsAtStore();
//        SimpleDiscountByStore storeDiscountPolicy = new SimpleDiscountByStore(store,discount1);
//        assertEquals(discounts, storeDiscountPolicy.getDiscountsToCart(shoppingBag));
//    }

    //with discount
//    @Test
//    void getCartPrice_withHalfPriceDiscount() throws IllegalDiscountException {
//        setShoppingBag();
//        setProductsAtStore();
//        SimpleDiscountByStore storeDiscountPolicy = new SimpleDiscountByStore(store,discount1);
//        assertTrue(storeDiscountPolicy.getCartPrice(shoppingBag)==totalCartPrice3);
//    }

//    @Test
//    void getCartPrice_withQuarterPriceDiscount() throws IllegalDiscountException {
//        setShoppingBag();
//        setProductsAtStore();
//        SimpleDiscountByStore storeDiscountPolicy = new SimpleDiscountByStore(store,discount2);
//        assertTrue(storeDiscountPolicy.getCartPrice(shoppingBag)==totalCartPrice4);
//    }

//    @Test
//    void getCartPrice_fullPrice() throws IllegalDiscountException {
//        setShoppingBag();
//        setProductsAtStore();
//        SimpleDiscountByStore storeDiscountPolicy = new SimpleDiscountByStore(store,noDiscount);
//        assertTrue(storeDiscountPolicy.getCartPrice(shoppingBag)==totalCartPrice2);
//    }

}

