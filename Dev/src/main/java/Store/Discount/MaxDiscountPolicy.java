package Store.Discount;

import Exceptions.IllegalDiscountException;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

@Entity
public class MaxDiscountPolicy extends CompoundDiscountPolicy{

    public MaxDiscountPolicy(List<IDiscountPolicy> discountPolicies) throws IllegalDiscountException {
        super(discountPolicies);
    }

    public MaxDiscountPolicy() {

    }

    public Map<Product, Double> getDiscountsToCart(ShoppingBag shoppingBag) throws IllegalDiscountException {
        IDiscountPolicy discounts;
        if(discountPolicies==null || discountPolicies.isEmpty() || (discountPolicies.size()==0&&(discountPolicies.get(0))instanceof NoDiscountPolicy))
            return null;

        IDiscountPolicy firstDiscountPolicy = discountPolicies.stream().toList().get(0);
        discounts = firstDiscountPolicy;
        double cartPrice = Double.MAX_VALUE;
        for(IDiscountPolicy discountPolicy : discountPolicies){
            double alternativePrice = discountPolicy.getCartPrice(shoppingBag);
            if(alternativePrice<cartPrice){
                discounts = discountPolicy;
                cartPrice = alternativePrice;
            }
        }
        return discounts.getDiscountsToCart(shoppingBag);
    }

    @Override
    public String getTypeName() {
        int counter=1;
        String res="best deal of the following discounts:\n";
        for(IDiscountPolicy policy:discountPolicies) {
            res = res + counter + ")" + policy.getTypeName() + "\n";
            counter++;
        }
        return res;
    }


}
