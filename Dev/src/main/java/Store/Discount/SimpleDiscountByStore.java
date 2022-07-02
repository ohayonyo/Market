package Store.Discount;

import Exceptions.IllegalDiscountException;
import Store.Store;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.*;
import java.util.*;

@Entity
public class SimpleDiscountByStore extends IDiscountPolicy{
    @ManyToOne
    private Store store;
    @Transient
    private Map<Product,Double> discounts;
    private double discount;

    public SimpleDiscountByStore(Store store,double discount) throws IllegalDiscountException {
        if(discount>100 || discount<0)
            throw new IllegalDiscountException(discount);
        this.store=store;
        //this.discountID = ++discountCounter;
        this.discount=discount;
    }

    public SimpleDiscountByStore() {
        //++discountCounter;
    }


    @Override
    public Map<Product, Double> getDiscountsToCart(ShoppingBag shoppingBag){
        if(store.getProducts()==null || store.getProducts().isEmpty())
            return null;
        discounts = new HashMap<>();
        Set<Product> productsToAddDiscount = shoppingBag==null ? store.getProducts() : shoppingBag.getProducts().keySet();
        for(Product p : productsToAddDiscount)
            discounts.put(p,discount);
        return discounts;
    }

    public double getCartPrice(ShoppingBag shoppingBag){
        if(shoppingBag==null)
            return 0;
        Map<Product,Double> discounts = getDiscountsToCart(shoppingBag);
        if(discounts == null || discounts.isEmpty())
            return shoppingBag.getTotalPriceWithoutDiscount();
        double cartPrice = 0;
        for(Map.Entry<Product,Integer> products : shoppingBag.getProducts().entrySet()){
            Product p = products.getKey();
            int amount = products.getValue();
            double discount = 0;
            if(discounts.containsKey(p))
                discount = discounts.get(p);
            cartPrice+=((100-discount)/100.0)*p.getPrice()*amount;
        }
        return cartPrice;
    }

    @Override
    public IDiscountPolicy getDiscountByID(int discountID) {
        if(this.discountID==discountID)
            return this;
        return null;
    }

    @Override
    public List<IDiscountPolicy> getDiscountPolicies() {
        return null;
    }

    @Override
    public int getDiscountID() {
        return discountID;
    }

    @Override
    public double moneySaved(ShoppingBag shoppingBag){
        return shoppingBag.getTotalPriceWithoutDiscount()-getCartPrice(shoppingBag);
    }

    @Override
    public String getTypeName() {
        return discount+"% discount off store:"+store.getStoreId();
    }
}
