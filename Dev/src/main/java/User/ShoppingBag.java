package User;

import Store.Store;
import Store.Product;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
public class ShoppingBag implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private Store store;
    @Id
    private String userName;


//    @ManyToMany
@ElementCollection
@MapKeyJoinColumns({
        @MapKeyJoinColumn(name="productId"),
//        @MapKeyJoinColumn(name="storeId")
})
    private Map<Product, Integer> products; // <key = product, value = amount>

    public ShoppingBag(Store store, User user) {
        this.store = store;
        this.products = new HashMap<>();
        if(user instanceof Subscriber)
            this.userName = ((Subscriber)user).getUserName();
    }


    public ShoppingBag(Store store, User user, Map<Product, Integer> products) {
        this.store = store;
        this.products = products;
        if(user instanceof Subscriber)
            this.userName = ((Subscriber)user).getUserName();
    }

    public ShoppingBag() {

    }


    public Store getStore() {
        return store;
    }

    public String getUserName() {
        return userName;
    }

    public void addSingleProduct(Product product, int amount)  {
        if(!products.containsKey(product))
            products.put(product, amount);
        else
        {
            products.put(product, products.get(product) + amount);
        }
    }

    public int getAmountOfProduct(Product product)
    {
        return products.getOrDefault(product, 0);
    }

    public void setAmountOfProduct(Product product, int amount)
    {
        products.put(product, amount);
    }

    public void deleteProduct(Product product) {
        if(products.containsKey(product))
        {
            products.remove(product);
        }
    }

    public void addProducts(Map<Product, Integer> products)
    {
        for (Map.Entry<Product,Integer> entry : products.entrySet())
            addSingleProduct(entry.getKey(), entry.getValue());
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public void updateProductAmount(Product product, int finalAmount)
    {
        if(products.containsKey(product))
        {
            products.put(product, finalAmount);
        }
        else
        {
            products.put(product, finalAmount);
        }
    }
    public double getTotalPriceWithoutDiscount(){
        double sum = 0;
        for(Map.Entry<Product,Integer> entry : products.entrySet()){
           Product product = entry.getKey();
           int amount = entry.getValue();
           sum += product.getPrice()*amount;
        }
        return sum;
    }


    @Override
    public String toString() {
        return "ShoppingBag{" +
                "store=" + store.getStoreName() +
                ", user=" + getUserName()+
                ", products=" + products.toString() +
                '}';
    }
}
