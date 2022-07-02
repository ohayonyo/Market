package Store.PolicyLimitation.LogicOperators;

import Store.PolicyLimitation.IBuyingPolicy;
import Store.PolicyLimitation.SimpleLimitationPolicy.PolicyCounter;
import User.ShoppingBag;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
public class ConditioningLimitation extends IBuyingPolicy {
    @ManyToOne
    private IBuyingPolicy condition;
    @ManyToOne
    private IBuyingPolicy conditionEnables;
    protected int policyID;

    public ConditioningLimitation(IBuyingPolicy condition,IBuyingPolicy conditionEnables){
        this.condition=condition;
        this.conditionEnables=conditionEnables;
        //policyID = new PolicyCounter().getInstance();
    }

    public ConditioningLimitation() {

    }

    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        if(!condition.isSatisfiesCondition(shoppingBag) && conditionEnables.isSatisfiesCondition(shoppingBag))
            return false;
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
        if(condition.getPolicyByID(policyID)!=null)
            return condition.getPolicyByID(policyID);
        if(conditionEnables.getPolicyByID(policyID)!=null)
            return conditionEnables.getPolicyByID(policyID);
        return null;
    }

    @Override
    public int getPolicyID() {
        return policyID;
    }

    @Override
    public String exceptionCause(ShoppingBag shoppingBag) {
        return condition.exceptionCause(shoppingBag);
    }

    @Override
    public String getTypeName() {
        return "conditioning limitation, condition:"+condition.getTypeName()+" condition enables:"+conditionEnables.getTypeName();
    }


}
