package Store.PolicyLimitation.LogicOperators;

import Store.PolicyLimitation.CompoundBuyingPolicy;
import Store.PolicyLimitation.IBuyingPolicy;
import User.ShoppingBag;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class OrCondition extends CompoundBuyingPolicy {
    public OrCondition(List<IBuyingPolicy> buyingPolicies){
        super(buyingPolicies);
    }

    public OrCondition() {

    }

    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        for (IBuyingPolicy buyingPolicy: buyingPolicies) {
            if(buyingPolicy.isSatisfiesCondition(shoppingBag))
                return true;
        }
        return false;
    }

    @Override
    public String exceptionCause(ShoppingBag shoppingBag) {
        if(isSatisfiesCondition(shoppingBag))
            return "";
        return "Or condition is not exists well";
    }

    @Override
    public String getTypeName() {
        String res="Or condition of:";
        if(buyingPolicies!=null)
            for(IBuyingPolicy policy : buyingPolicies)
                res = res + policy.getTypeName()+"\n";
        return res;
    }
}
