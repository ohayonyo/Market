package Store.PolicyLimitation.SimpleLimitationPolicy;

import Store.PolicyLimitation.IBuyingPolicy;
import User.ShoppingBag;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class NoLimitationPolicy extends IBuyingPolicy {

    public NoLimitationPolicy(){
        //policyID = new PolicyCounter().getInstance();
    }
    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        return true;
    }

    @Override
    public List<IBuyingPolicy> getPolicies() {
        return null;
    }

    @Override
    public IBuyingPolicy getPolicyByID(int policyID) {
        if(this.policyID==policyID)
            return this;
        return null;
    }

    @Override
    public int getPolicyID() {
        return policyID;
    }

    @Override
    public String exceptionCause(ShoppingBag shoppingBag) {
        return "";
    }

    @Override
    public String getTypeName() {
        return "No limitation policy";
    }

}
