package Store.Discount;

import Exceptions.IllegalDiscountException;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class SimpleDiscountPolicy extends IDiscountPolicy{

    protected double discount;
    @ManyToMany
    protected List<Product> products;
    @Transient
    protected Map<Product,Double> discounts;

    public SimpleDiscountPolicy(double discount, List<Product> products) throws IllegalDiscountException {
        if(discount<0|| discount>100)
            throw new IllegalDiscountException(discount);
        this.discount = discount;
        this.products = products;
        //this.discountID = ++discountCounter;
        if(products==null||products.isEmpty())
            discounts = null;
        discounts = new HashMap<>();
        if(products!=null)
            for(Product p : products)
                 discounts.put(p,discount);

    }

    public SimpleDiscountPolicy() {
        //++discountCounter;
    }

    @Override
    public Map<Product, Double> getDiscountsToCart(ShoppingBag shoppingBag) {
        return discounts;
    }

    @Override
    public double getCartPrice(ShoppingBag shoppingBag) {
        if(shoppingBag==null)
            return 0;
        Map<Product,Integer> cartProducts = shoppingBag.getProducts();
        if(cartProducts==null||cartProducts.isEmpty())
            return 0;
        double res = 0;
        for(Product p : cartProducts.keySet()){
            if(products!=null&&products.contains(p))
                res+=((100-discount)/100.0)*p.getPrice()*cartProducts.get(p);
            else
                res+=p.getPrice()*cartProducts.get(p);
        }
        return res;
    }

    @Override
    public IDiscountPolicy getDiscountByID(int discountID) {
        if(discountID == this.discountID)
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
    public double moneySaved(ShoppingBag shoppingBag) {
        return shoppingBag.getTotalPriceWithoutDiscount()-getCartPrice(shoppingBag);
    }

    public String getTypeName(){
        return "simpleDiscountPolicy";
    }

}
