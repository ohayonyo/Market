package Store.Discount;

import Exceptions.IllegalDiscountException;
import Store.Product;
import Store.Store;
import User.ShoppingBag;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class SimpleDiscountByCategory extends IDiscountPolicy{
    private String category;
    @ManyToOne
    private Store store;
    private double discount;
    @Transient
    private Map<Product,Double> discounts;

    public SimpleDiscountByCategory(Store store,String category, double discount) throws IllegalDiscountException {
        if(discount>100 || discount<0)
            throw new IllegalDiscountException(discount);
        //this.discountID = ++discountCounter;
        this.category=category;
        this.store=store;
        this.discount=discount;
    }

    public SimpleDiscountByCategory() {
        //++discountCounter;
    }

    @Override
    public Map<Product, Double> getDiscountsToCart(ShoppingBag shoppingBag){
        discounts = new HashMap<>();
        Set<Product> productsToAddDiscount = shoppingBag==null ? store.getProducts() : shoppingBag.getProducts().keySet();
        if(productsToAddDiscount!=null && !productsToAddDiscount.isEmpty())
            for(Product product : productsToAddDiscount)
                if(product.getCategory().equals(category))
                    discounts.put(product,discount);
        return discounts;
    }


    //TODO: need to add lock
    @Override
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
        return discount+"% discount off category:"+category+" at store:"+store.getStoreId();
    }
}
