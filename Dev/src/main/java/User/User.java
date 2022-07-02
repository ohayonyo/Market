package User;

import DAL.Repo;
import Exceptions.*;
import Store.Store;


import java.util.HashMap;
import java.util.Map;

import Store.Product;

import javax.persistence.CascadeType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.util.concurrent.ConcurrentHashMap;

@MappedSuperclass
public class User {

    @OneToMany(cascade = CascadeType.ALL)
    protected Map<Store, ShoppingBag> cart;

    public User() {
        this.cart = new ConcurrentHashMap<>();
    }

    public User(ConcurrentHashMap<Store, ShoppingBag> cart) {
        this.cart = cart;
    }

    public void updateShoppingBag(Store store, Product product, int finalAmount) throws OutOfInventoryException, LostConnectionException {
        int inventoryAmount = store.getProductsHashMap().get(product);
        if(finalAmount>inventoryAmount)
            throw new OutOfInventoryException(inventoryAmount,finalAmount);

        if(cart.get(store) != null) {
            if (finalAmount == 0) {
                cart.get(store).deleteProduct(product);
                if(this instanceof Subscriber) {
                Repo.merge(cart.get(store));
                Repo.merge(this);
                }
            } else {
                cart.get(store).updateProductAmount(product, finalAmount);
                if(this instanceof Subscriber) {
                    Repo.merge(cart.get(store));
                    Repo.merge(this);
                }
            }
        }
        else
        {
            Map<Product, Integer> map = new HashMap<>();
            map.put(product, finalAmount);
            ShoppingBag shoppingBag = new ShoppingBag(store, this,map);
            cart.put(store, shoppingBag);
            if(this instanceof Subscriber) {
                Repo.persist(shoppingBag);
                Repo.merge(this);
            }
        }
    }

    public void emptyCart() throws LostConnectionException {
        for(ShoppingBag sh: cart.values()){
            sh.getProducts().clear();
            if(this instanceof Subscriber)
                Repo.merge(sh);
        }
        if(this instanceof Subscriber)
            Repo.merge(this);
    }

    public Map<Store,ShoppingBag> getCart() {
        return cart;
    }
}
