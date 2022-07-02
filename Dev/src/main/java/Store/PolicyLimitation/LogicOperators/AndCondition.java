package Store.PolicyLimitation.LogicOperators;

import Store.PolicyLimitation.CompoundBuyingPolicy;
import Store.PolicyLimitation.IBuyingPolicy;
import User.ShoppingBag;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;

@Entity
public class AndCondition extends CompoundBuyingPolicy {

    @Transient
    private String cause;

    public AndCondition(List<IBuyingPolicy> buyingPolicies) {
        super(buyingPolicies);
    }

    public AndCondition() {

    }

    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        for (IBuyingPolicy buyingPolicy: buyingPolicies) {
            if(!buyingPolicy.isSatisfiesCondition(shoppingBag)){
                if(buyingPolicy instanceof AndCondition)
                    cause = "Policy with id:"+buyingPolicy.getPolicyID()+" violated the \"And\" condition";
                else if(buyingPolicy instanceof OrCondition)
                    cause = "Policy with id:"+buyingPolicy.getPolicyID()+" violated the \"Or\" condition";
                else if(buyingPolicy instanceof XorCondition)
                    cause = "Policy with id:"+buyingPolicy.getPolicyID()+" violated the \"Xor\" condition";
                else
                    cause=buyingPolicy.exceptionCause(shoppingBag);
                return false;
            }
        }
        return true;
    }

    @Override
    public String exceptionCause(ShoppingBag shoppingBag) {
        if(isSatisfiesCondition(shoppingBag))
            return "";
        return cause;
    }

    @Override
    public String getTypeName() {
        int counter=1;
        String res="And condition of:\n";
        if(buyingPolicies!=null)
            for(IBuyingPolicy policy : buyingPolicies) {
                res = res + counter + ")" + policy.getTypeName() + "\n";
                counter++;
            }
        return res;
    }


}
