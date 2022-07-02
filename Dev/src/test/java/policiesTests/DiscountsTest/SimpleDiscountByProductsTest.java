package policiesTests.DiscountsTest;

import Exceptions.IllegalDiscountException;
import Store.Discount.*;
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
public class SimpleDiscountByProductsTest {

    @Mock private Product product1, product2;
    @Mock private ShoppingBag shoppingBag;
    @Mock private Map<Product,Integer> productsToBuy;
    @Mock private Set<Map.Entry<Product,Integer>> productsEntry;
    @Mock private Map.Entry<Product,Integer> productEntry1,productEntry2;
    @Mock private List<Product> productList;

    private double totalCartPrice2=65.0, totalCartPrice1 =32.5;
    private double discount1 = 50,discount2=25,negativeDiscount=-10,exceededDiscount=110;
    private double price1=7.0,price2=10.0;
    private int quantity1 =5, quantity2 =3;


    @BeforeEach
    void setUp(){

    }

    @AfterEach
    void tearDown(){

    }

    void setProductsAtStore(){
        Iterator<Product> productsIterator = mock(Iterator.class);
        when(productList.iterator()).thenReturn(productsIterator);
        when(productsIterator.hasNext()).thenReturn(true,true,false);
        when(productsIterator.next()).thenReturn(product1).thenReturn(product2);
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

    @Test
    void getDiscountsToCart_halfPrice() throws IllegalDiscountException {
        Map<Product,Double> discounts = new HashMap<>();
        discounts.put(product1,discount1);
        discounts.put(product2,discount1);
        setProductsAtStore();
        SimpleDiscountByProducts productsDiscountPolicy = new SimpleDiscountByProducts(productList,discount1);
        assertEquals(discounts, productsDiscountPolicy.getDiscountsToCart(shoppingBag));
    }


    @Test
    void getCartPrice_withHalfPriceDiscount() throws IllegalDiscountException {
        setShoppingBag();
        setProductsAtStore();
        SimpleDiscountByProducts productsDiscountPolicy = new SimpleDiscountByProducts(productList,discount1);
        assertTrue(productsDiscountPolicy.getCartPrice(shoppingBag)== totalCartPrice1);
    }

    @Test
    void getCartPrice_noProductsWithDiscount() throws IllegalDiscountException {
        Iterator<Product> productsIterator = mock(Iterator.class);
        when(productList.iterator()).thenReturn(productsIterator);
        when(productsIterator.hasNext()).thenReturn(false);
        when(shoppingBag.getTotalPriceWithoutDiscount()).thenReturn(totalCartPrice2);
        SimpleDiscountByProducts productsDiscountPolicy = new SimpleDiscountByProducts(productList,discount1);
        assertTrue(productsDiscountPolicy.getCartPrice(shoppingBag)==totalCartPrice2);
    }

    @Test
    void illegalDiscount(){
        List<Product> productsWithDiscount = mock(List.class);
        assertThrows(IllegalDiscountException.class,()->new SimpleDiscountByProducts(productsWithDiscount,negativeDiscount));
        assertThrows(IllegalDiscountException.class,()->new SimpleDiscountByProducts(productsWithDiscount,exceededDiscount));
    }

}

