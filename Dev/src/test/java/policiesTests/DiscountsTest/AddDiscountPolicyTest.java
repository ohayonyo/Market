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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import User.ShoppingBag;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddDiscountPolicyTest {


    @Mock private Product product1, product2,product3,product4;
    @Mock private ShoppingBag shoppingBag;
    @Mock private Map<Product,Integer> productsToBuy;
    @Mock private Set<Map.Entry<Product,Integer>> productsEntry;
    @Mock private Map.Entry<Product,Integer> productEntry1,productEntry2,productEntry3,productEntry4;
    @Mock private List<Product> productList1,productList2;


    private String category= "Diary",category2="Vegan",category3="Vegetables";
    private double totalCartPrice=52.375;
    private double discount1 = 50,discount2=25,noDiscount=0,negativeDiscount=-10,exceededDiscount=110;
    private double price1=7.0,price2=10.0,price3=5.0,price4=6.5;
    private int quantity1 =5, quantity2 =3,quantity3=4,quantity4=1;
    private String productName1="pizza",productName2="tomato",productName3="milk",productName4="egg";
    private String desc="fhnfsv";

    private Product p1 = new Product(1,price1,desc,productName1,category);
    private Product p2 = new Product(1, price2,desc,productName2,category3);
    private Product p3 = new Product(1, price3,desc,productName3,category);
    private Product p4 = new Product(1, price4,desc,productName4,category2);

    @BeforeEach
    void setUp(){

    }

    @AfterEach
    void tearDown(){

    }

    void setShoppingBag(){
        Iterator<Map.Entry<Product,Integer>> iterator = mock(Iterator.class);
        when(shoppingBag.getProducts()).thenReturn(productsToBuy);
        when(productsToBuy.entrySet()).thenReturn(productsEntry);
        when(productsEntry.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true,true,true,true,false);
        when(iterator.next()).thenReturn(productEntry1).thenReturn(productEntry2).thenReturn(productEntry3).thenReturn(productEntry4);
        when(productEntry1.getKey()).thenReturn(product1);
        when(productEntry1.getValue()).thenReturn(quantity1);
        when(productEntry2.getKey()).thenReturn(product2);
        when(productEntry2.getValue()).thenReturn(quantity2);
        when(productEntry3.getKey()).thenReturn(product3);
        when(productEntry3.getValue()).thenReturn(quantity3);
        when(productEntry4.getKey()).thenReturn(product4);
        when(productEntry4.getValue()).thenReturn(quantity4);
        when(product1.getPrice()).thenReturn(price1);
        when(product2.getPrice()).thenReturn(price2);
        when(product3.getPrice()).thenReturn(price3);
        when(product4.getPrice()).thenReturn(price4);
    }


    void setProductsAtStore(){
        Iterator<Product> productsIterator1 = mock(Iterator.class);
        when(productList1.iterator()).thenReturn(productsIterator1);
        when(productsIterator1.hasNext()).thenReturn(true,true,false);
        when(productsIterator1.next()).thenReturn(product1).thenReturn(product2);

        Iterator<Product> productsIterator2 = mock(Iterator.class);
        when(productList2.iterator()).thenReturn(productsIterator2);
        when(productsIterator2.hasNext()).thenReturn(true,true,false);
        when(productsIterator2.next()).thenReturn(product3).thenReturn(product4);
    }

    void setProductsAtStore2(){
        Iterator<Product> productsIterator1 = mock(Iterator.class);
        when(productList1.iterator()).thenReturn(productsIterator1);
        when(productsIterator1.hasNext()).thenReturn(true,true,false);
        when(productsIterator1.next()).thenReturn(product1).thenReturn(product2);

        Iterator<Product> productsIterator2 = mock(Iterator.class);
        when(productList2.iterator()).thenReturn(productsIterator2);
        when(productsIterator2.hasNext()).thenReturn(true,true,true,false);
        when(productsIterator2.next()).thenReturn(product1).thenReturn(product3).thenReturn(product4);

    }


//    @Test
//    void getDiscountsToCart_addTwoSimpleDiscounts() throws IllegalDiscountException {
//        Map<Product,Double> discounts = new HashMap<>();
//        discounts.put(product1,discount1);
//        discounts.put(product2,discount1);
//        discounts.put(product3,discount2);
//        discounts.put(product4,discount2);
//
//        setProductsAtStore();
//        IDiscountPolicy productsDiscountPolicy1 = new SimpleDiscountByProducts(productList1,discount1);
//        IDiscountPolicy productsDiscountPolicy2 = new SimpleDiscountByProducts(productList2,discount2);
//        List<IDiscountPolicy> discountsToAdd = new LinkedList<>();
//        discountsToAdd.add(productsDiscountPolicy1);
//        discountsToAdd.add(productsDiscountPolicy2);
//        AddDiscountPolicy addDiscountPolicy = new AddDiscountPolicy(discountsToAdd);
//        assertEquals(addDiscountPolicy.getDiscountsToCart(shoppingBag),discounts);
//    }

    @Test
    void getDiscountsToCart_addCompoundDiscounts() throws IllegalDiscountException, IllegalNameException, LostConnectionException {

        User realUser = new User();
        Store realStore = new Store("realStore");
        Map<Product,Integer> realProductsToBuy = new HashMap<>();
        realProductsToBuy.put(p1,quantity1);
        realProductsToBuy.put(p2,quantity2);
        realProductsToBuy.put(p3,quantity3);
        realProductsToBuy.put(p4,quantity4);

        ShoppingBag realShoppingBag = new ShoppingBag(realStore,realUser,realProductsToBuy);
        List<Product> realProducts1 = new LinkedList<>();
        List<Product> realProducts2 = new LinkedList<>();

        realProducts1.add(p1);
        realProducts1.add(p2);
        realProducts2.add(p3);
        realProducts2.add(p4);

        IDiscountPolicy productsDiscountPolicy1 = new SimpleDiscountByProducts(realProducts1,discount1);
        IDiscountPolicy productsDiscountPolicy2 = new SimpleDiscountByProducts(realProducts2,discount2);
        List<IDiscountPolicy> discountsToAdd = new LinkedList<>();
        discountsToAdd.add(productsDiscountPolicy1);
        discountsToAdd.add(productsDiscountPolicy2);
        AddDiscountPolicy addDiscountPolicy = new AddDiscountPolicy(discountsToAdd);
        IDiscountPolicy productsDiscountPolicy3 = new SimpleDiscountByProducts(realProducts1,10);
        discountsToAdd = new LinkedList<>();
        discountsToAdd.add(addDiscountPolicy);
        discountsToAdd.add(productsDiscountPolicy3);
        addDiscountPolicy = new AddDiscountPolicy(discountsToAdd);

        assertEquals(addDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p1),discount1+10);
        assertEquals(addDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p2),discount1+10);
        assertEquals(addDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p3),discount2);
        assertEquals(addDiscountPolicy.getDiscountsToCart(realShoppingBag).get(p4),discount2);
    }

    @Test
    void exceededAddDiscount() throws IllegalDiscountException {
        Map<Product,Double> discounts = new HashMap<>();
        discounts.put(product1,discount1);
        discounts.put(product2,discount1);
        discounts.put(product3,discount2);
        discounts.put(product4,discount2);

        setProductsAtStore2();
        IDiscountPolicy productsDiscountPolicy1 = new SimpleDiscountByProducts(productList1,70);
        IDiscountPolicy productsDiscountPolicy2 = new SimpleDiscountByProducts(productList2,40);
        List<IDiscountPolicy> discountsToAdd = new LinkedList<>();
        discountsToAdd.add(productsDiscountPolicy1);
        discountsToAdd.add(productsDiscountPolicy2);
        assertThrows(IllegalDiscountException.class, ()->new AddDiscountPolicy(discountsToAdd));
    }
//    @Test
//    void getCartPrice() throws IllegalDiscountException {
//        setProductsAtStore();
//        IDiscountPolicy productsDiscountPolicy1 = new SimpleDiscountByProducts(productList1,discount1);
//        IDiscountPolicy productsDiscountPolicy2 = new SimpleDiscountByProducts(productList2,discount2);
//        List<IDiscountPolicy> discountsToAdd = new LinkedList<>();
//        discountsToAdd.add(productsDiscountPolicy1);
//        discountsToAdd.add(productsDiscountPolicy2);
//        AddDiscountPolicy addDiscountPolicy = new AddDiscountPolicy(discountsToAdd);
//        setShoppingBag();
//        System.out.println(addDiscountPolicy.getCartPrice(shoppingBag));
//        assertTrue(addDiscountPolicy.getCartPrice(shoppingBag)==totalCartPrice);
//    }


}

