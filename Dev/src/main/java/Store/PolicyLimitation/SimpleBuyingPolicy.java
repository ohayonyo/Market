package Store.PolicyLimitation;

import Store.PolicyLimitation.SimpleLimitationPolicy.PolicyCounter;
import User.ShoppingBag;

import javax.persistence.Entity;
import java.util.List;
@Entity
public abstract class SimpleBuyingPolicy extends IBuyingPolicy {

    public SimpleBuyingPolicy(){
        //super(new PolicyCounter().getInstance());
    }
    abstract public boolean isSatisfiesCondition(ShoppingBag shoppingBag);

    public List<IBuyingPolicy> getPolicies(){
        return null;
    }

    @Override
    public IBuyingPolicy getPolicyByID(int policyID) {
        if(this.policyID==policyID)
            return this;
        return null;
    }

}
