package UserTests;

import Exceptions.*;
import Store.Store;
import Store.Product;
import User.User;
import User.ShoppingBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserTest {
    private User user;
    @Mock private ConcurrentHashMap<Product, Integer> products;
    @Mock private ConcurrentHashMap<Store, ShoppingBag> cart;
    @Mock private Store store;
    @Mock private Product product;
    @Mock private ShoppingBag shoppingBag;
    @Mock private List<ShoppingBag> shoppingBagList;


    private String userName = "Ronen";
    private int finalAmount = 9;
    private int zeroAmount = 0;

    @BeforeEach
    void setUp()
    {
        user = new User(cart);
    }

    @Test
    void updateShoppingBagZeroAmountGuset() throws OutOfInventoryException, LostConnectionException {
        when(store.getProductsHashMap()).thenReturn(products);
        when(products.get(product)).thenReturn(zeroAmount);
        when(cart.get(store)).thenReturn(shoppingBag);
        user.updateShoppingBag(store, product, zeroAmount);
        verify(shoppingBag).deleteProduct(product);
    }


    @Test
    void updateShoppingBagGuest() throws OutOfInventoryException, LostConnectionException {
        when(store.getProductsHashMap()).thenReturn(products);
        when(products.get(product)).thenReturn(finalAmount);
        when(cart.get(store)).thenReturn(shoppingBag);
        user.updateShoppingBag(store, product, finalAmount);
        verify(shoppingBag).updateProductAmount(product, finalAmount);
    }

    @Test
    void purchaseCartGuest() throws LostConnectionException {
        Iterator<ShoppingBag> shoppingBagIterator = mock(Iterator.class);
        when(shoppingBagIterator.hasNext()).thenReturn(true,false);
        when(shoppingBagIterator.next()).thenReturn(shoppingBag);
        when(cart.values()).thenReturn(shoppingBagList);
        when(shoppingBagList.iterator()).thenReturn(shoppingBagIterator);
        when(shoppingBag.getProducts()).thenReturn(products);



        user.emptyCart();
        verify(products).clear();
    }

}