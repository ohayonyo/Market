package Store.Discount;

import Exceptions.*;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public abstract class CompoundDiscountPolicy extends IDiscountPolicy{

    @ManyToMany
    protected List<IDiscountPolicy> discountPolicies;

    public CompoundDiscountPolicy(List<IDiscountPolicy> discountPolicies) throws IllegalDiscountException {
        this.discountPolicies=discountPolicies;
        isLegalDiscount(discountPolicies,null);
        //discountID = ++discountCounter;
    }

    public CompoundDiscountPolicy() {
        //++discountCounter;
    }


    @Override
    public double getCartPrice(ShoppingBag shoppingBag) throws IllegalDiscountException {
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
        if(discountPolicies==null || discountPolicies.isEmpty())
            return null;
        for(IDiscountPolicy child : discountPolicies){
            IDiscountPolicy found = child.getDiscountByID(discountID);
            if(found!=null)
                return found;
        }
        return null;
    }


    public void addDiscountPolicy(IDiscountPolicy discountPolicy,ShoppingBag shoppingBag) throws DiscountAlreadyExistsException, IllegalDiscountException {
        if(discountPolicies.contains(discountPolicy))
           throw new DiscountAlreadyExistsException();
        discountPolicies.add(discountPolicy);
        try {
            isLegalDiscount(discountPolicies,shoppingBag);
        }catch(Exception e){
            discountPolicies.remove(discountPolicy);
            throw e;
        }
    }

    private void isLegalDiscount(List<IDiscountPolicy> discountPolicies,ShoppingBag shoppingBag) throws IllegalDiscountException {
        Map<Product,Double> discounts = new HashMap<>();
        if(discountPolicies==null)
            return;
        for(IDiscountPolicy discountPolicy : discountPolicies){
            Map<Product,Double> currDiscount = discountPolicy.getDiscountsToCart(shoppingBag);
            if(currDiscount!=null)
                for(Map.Entry<Product,Double> entry : currDiscount.entrySet()){
                    Product p = entry.getKey();
                    double discount = entry.getValue();
                    if(discount<0||discount>100)
                        throw new IllegalDiscountException(discount);
                    if(discounts.containsKey(p)){
                        double oldDiscount = discounts.get(p);
                        if(discount+oldDiscount>100 || discount+oldDiscount<0)
                            throw new IllegalDiscountException(discount+oldDiscount);
                        discounts.replace(p,oldDiscount,oldDiscount+discount);
                    }else
                        discounts.put(p,discount);
                }
        }
    }

    public IDiscountPolicy removeDiscountPolicy(IDiscountPolicy discountPolicy){
        IDiscountPolicy res = null;
        if(discountPolicies!=null)
            for(IDiscountPolicy policy : discountPolicies)
                if(policy.getDiscountID()==discountPolicy.getDiscountID()){
                    res=discountPolicy;
                }
        if(res != null)
            discountPolicies.remove(res);
        return res;
    }



    @Override
    public List<IDiscountPolicy> getDiscountPolicies() {
        return discountPolicies;
    }

    @Override
    public int getDiscountID() {
        return discountID;
    }

    @Override
    public double moneySaved(ShoppingBag shoppingBag) throws IllegalDiscountException {
        return shoppingBag.getTotalPriceWithoutDiscount()-getCartPrice(shoppingBag);
    }

}
