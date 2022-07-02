package Store.Discount;

import Exceptions.IllegalDiscountException;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
public class SimpleDiscountByProducts extends IDiscountPolicy {
    @ManyToMany
    private List<Product> products;
    private double discount;
    @Transient
    private Map<Product,Double> discounts;;

    public SimpleDiscountByProducts(List<Product> products ,double discount) throws IllegalDiscountException{
        if(discount>100 || discount<0)
            throw new IllegalDiscountException(discount);
        this.products=products;
        this.discount=discount;
        //this.discountID = ++discountCounter;
    }

    public SimpleDiscountByProducts() {
        //++discountCounter;
    }

    @Override
    public Map<Product, Double> getDiscountsToCart(ShoppingBag shoppingBag){
        discounts = new HashMap<>();
        for(Product product : products)
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

    private List<String> getProductsNames(){
        List<String> res = new LinkedList<>();
        if(products!=null)
            for(Product p : products)
                res.add(p.getName());
        return res;
    }

    @Override
    public String getTypeName() {
        return discount+"% discount off the products:"+getProductsNames().toString();
    }

}
