package UserTests;

import Store.Store;
import Store.Product;
import User.User;
import User.ShoppingBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ShoppingBagTest {

    private ShoppingBag shoppingBag;
    @Mock private Store store;
    @Mock private User user;
    @Mock private Map<Product, Integer> products;
    @Mock private Product product;
    @Mock private Product product1;
    @Mock private Product product2;

    private int amount = 5;

    @BeforeEach
    void setUp() {
        shoppingBag = new ShoppingBag(store, user, products);
    }

    @Test
    void addNewSingleProduct()
    {
        when(products.containsKey(product)).thenReturn(false);
        when(products.get(product)).thenReturn(amount);
        shoppingBag.addSingleProduct(product,amount);
        verify(products).put(product, amount);
        assertEquals(amount, products.get(product));
    }
    @Test
    void addExistingSingleProduct()
    {
        when(products.containsKey(product)).thenReturn(true);
        when(products.get(product)).thenReturn(3);
        shoppingBag.addSingleProduct(product,amount);
        verify(products).put(product,amount + 3);
    }

    @Test
    void getAmountOfExistingProduct()
    {
        when(products.getOrDefault(product, 0)).thenReturn(5);
        int result = shoppingBag.getAmountOfProduct(product);
        verify(products).getOrDefault(product,0);
        assertEquals(result,5);
    }

    @Test
    void getNotExistingAmountOfProduct() {
        when(products.getOrDefault(product, 0)).thenReturn(0);
        int result = shoppingBag.getAmountOfProduct(product);
        verify(products).getOrDefault(product,0);
        assertEquals(result,0);
    }

    @Test
    void deleteExistingProduct()
    {
        when(products.containsKey(product)).thenReturn(true);
        shoppingBag.deleteProduct(product);
        verify(products).remove(product);
    }

    @Test
    void deleteNotExistingProduct()
    {
        when(products.containsKey(product)).thenReturn(false);
        shoppingBag.deleteProduct(product);
    }

    @Test
    void addProducts()
    {
        shoppingBag.addProducts(products);
    }

    @Test
    void updateNotExistingProductAmount()
    {
        when(products.containsKey(product)).thenReturn(false);
        shoppingBag.updateProductAmount(product, amount);
    }

    @Test
    void updateExistingProductAmount()
    {
        when(products.containsKey(product)).thenReturn(true);
        shoppingBag.updateProductAmount(product, amount);
        verify(products).put(product, amount);
    }

}