package Store.Discount;

import Exceptions.IllegalDiscountException;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.Entity;
import java.util.*;

@Entity
public class AddDiscountPolicy extends CompoundDiscountPolicy{

    public AddDiscountPolicy(List<IDiscountPolicy> discountPolicies) throws IllegalDiscountException {
        super(discountPolicies);
    }

    public AddDiscountPolicy() throws IllegalDiscountException {
        super(null);
    }


    public Map<Product,Double> getDiscountsToCart(ShoppingBag shoppingBag) throws IllegalDiscountException {
        if(discountPolicies==null || discountPolicies.isEmpty() || (discountPolicies.size()==0&&(discountPolicies.get(0))instanceof NoDiscountPolicy))
            return null;
        Map<Product,Double> discounts = new HashMap<>();
        for(IDiscountPolicy discountPolicy : discountPolicies){
            Map<Product,Double> currentDiscount = discountPolicy.getDiscountsToCart(shoppingBag);
            if(currentDiscount!=null && !currentDiscount.isEmpty())
                for(Map.Entry<Product,Double> entry : currentDiscount.entrySet()){
                    Product p = entry.getKey();
                    double discount = entry.getValue();
                    if(discount<0||discount>100)
                        throw new IllegalDiscountException(discount);
                    if(discounts.containsKey(p)){
                        double oldDiscount = discounts.get(p);
                        if(oldDiscount+discount>100)
                            throw new IllegalDiscountException(oldDiscount+discount);
                        discounts.replace(p,oldDiscount,oldDiscount+discount);
                    }else
                        discounts.put(p,discount);
                }
        }
        return discounts;
    }

    public String getTypeName() {
        int counter=1;
        String res="All of the following discounts:\n";
        for(IDiscountPolicy policy:discountPolicies) {
            res = res + counter + ")" + policy.getTypeName() + "\n";
            counter++;
        }
        return res;
    }

}
