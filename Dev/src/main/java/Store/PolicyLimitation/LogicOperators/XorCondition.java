package Store.PolicyLimitation.LogicOperators;

import Store.PolicyLimitation.CompoundBuyingPolicy;
import Store.PolicyLimitation.IBuyingPolicy;
import User.ShoppingBag;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class XorCondition extends CompoundBuyingPolicy {

    public XorCondition(List<IBuyingPolicy> buyingPolicies){
        super(buyingPolicies);
    }

    public XorCondition() {

    }

    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        boolean isExist=false;
        for (IBuyingPolicy buyingPolicy: buyingPolicies) {
            if(isExist && buyingPolicy.isSatisfiesCondition(shoppingBag))
                return false;
            else if(buyingPolicy.isSatisfiesCondition(shoppingBag)&&!isExist)
                isExist=true;
        }
        return isExist;
    }

    @Override
    public String exceptionCause(ShoppingBag shoppingBag) {
        if(isSatisfiesCondition(shoppingBag))
            return "";
        return "Xor condition is not exists well";
    }

    @Override
    public String getTypeName() {
        String res="Xor condition of:";
        if(buyingPolicies!=null)
            for(IBuyingPolicy policy : buyingPolicies)
                res = res + policy.getTypeName()+"\n";
        return res;
    }
}
