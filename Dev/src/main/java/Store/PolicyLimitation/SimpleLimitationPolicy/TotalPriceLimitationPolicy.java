package Store.PolicyLimitation.SimpleLimitationPolicy;

import Store.PolicyLimitation.SimpleBuyingPolicy;
import User.ShoppingBag;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class TotalPriceLimitationPolicy extends SimpleBuyingPolicy {
   private double minSum;
   private double maxSum;

   @Transient
   private String cause;

    public TotalPriceLimitationPolicy(double minSum, double maxSum) {
        this.minSum = minSum;
        this.maxSum = maxSum;
        //policyID = new PolicyCounter().getInstance();
    }

    public TotalPriceLimitationPolicy() {

    }

    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        if(shoppingBag==null)
            return true;
        double cartPrice = shoppingBag.getTotalPriceWithoutDiscount();
        if(cartPrice<minSum || cartPrice>maxSum){
            cause="The cart price must be in the range:["+minSum+","+maxSum+"]"+" but its price is:"+cartPrice;
            return false;
        }
        return true;
    }

    //
    @Override
    public String exceptionCause(ShoppingBag shoppingBag) {
        isSatisfiesCondition(shoppingBag);
        return cause;
    }

    @Override
    public String getTypeName() {
        if(maxSum==Double.MAX_VALUE)
            return "Total price of cart must be at least:"+minSum;
        else if(minSum==0)
            return "Total price of cart must be maximum:"+maxSum;
        return "Total price of cart must be between "+minSum+"-"+maxSum;
    }
}
