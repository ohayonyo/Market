package Store;


import Exceptions.*;
import User.Subscriber;

import javax.persistence.*;

@Entity
//@IdClass(ProductId.class)
public class Product{


    public void setIdCounter() {
        idCounter++;
    }

    @Transient
    private static int idCounter = 0;

    @Id
    private int productId;

    @Transient
    private int storeId;

    private double price;

    private String description;

    private String name;

    private String category;

    @Transient
    private boolean isLocked = false;

    @Transient
    private Subscriber subscriberForBid;//null if all the users can buy it

    public Product(int storeId, double price, String description, String name, String category) {
        this.productId = ++idCounter;
        this.price = price;
        this.description = description;
        this.name = name;
        this.category = category;
        this.storeId = storeId;
        this.subscriberForBid = null;
    }

    public Product(double price, String description, String name, String category, Subscriber subscriberForBid) {
        this.productId = ++idCounter;
        this.price = price;
        this.description = description;
        this.name = name;
        this.category = category;
        isLocked = false;
        this.subscriberForBid = subscriberForBid;
    }

    public Subscriber getSubscriberForBid() {
        return subscriberForBid;
    }

    public Product(int storeId, double price, String description, String name, String category,boolean ignore) {
        this.productId = ++idCounter;
        this.price = price;
        this.description = description;
        this.name = name;
        this.category = category;
        this.storeId = storeId;
        isLocked = false;
        this.subscriberForBid = null;
    }


    public Product() {
       // ++idCounter;
        int x;
    }

    public Product(Product oldProduct) {
        this.productId = ++idCounter;
        this.price = oldProduct.price;
        this.description = oldProduct.description;
        this.name = oldProduct.name;
        this.category = oldProduct.category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public void setSubscriberForBid(Subscriber subscriberForBid) {
        this.subscriberForBid = subscriberForBid;
    }

    public int getProductId() {
        return productId;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws IllegalPriceException {
        if (price <= 0){
            throw new IllegalPriceException(price);
        }
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws IllegalNameException {
        if (description == null || description.equals("")){
            throw new IllegalNameException(description);
        }
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws IllegalNameException{
        if (name == null || name.equals(""))
            throw new IllegalNameException(category);
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) throws IllegalNameException {
        if (category == null || category.equals("")){
            throw new IllegalNameException(category);
        }
        this.category = category;
    }

    public void lock() { isLocked = true; }

    public void unlock() {isLocked = false; }

    public boolean isLocked() {return isLocked; }

    public boolean areSameProducts(Product other){
        return this.getName().equals(other.name)&&this.storeId==other.storeId&&
                this.price==other.price&&this.description.equals(other.description)&&
                this.category.equals(other.category);
    }

}
