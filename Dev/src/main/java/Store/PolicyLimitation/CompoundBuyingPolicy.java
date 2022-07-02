package Store.PolicyLimitation;


import Store.Discount.IDiscountPolicy;
import Store.PolicyLimitation.SimpleLimitationPolicy.PolicyCounter;
import User.ShoppingBag;

import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;
@Entity
public abstract class CompoundBuyingPolicy extends IBuyingPolicy {

    @ManyToMany
    @CollectionTable(name = "compound_purchase_policy_purchase_policies")
    protected List<IBuyingPolicy> buyingPolicies;
    public CompoundBuyingPolicy(List<IBuyingPolicy> buyingPolicies){
        //super(new PolicyCounter().getInstance());
        this.buyingPolicies=buyingPolicies;
    }

    public CompoundBuyingPolicy() {

    }

    public abstract boolean isSatisfiesCondition(ShoppingBag shoppingBag);

    public void addPolicy(IBuyingPolicy buyingPolicy) { this.buyingPolicies.add(buyingPolicy); }

    public IBuyingPolicy removePolicy(IBuyingPolicy buyingPolicy) {
        IBuyingPolicy res = null;
        if(buyingPolicies!=null)
            for(IBuyingPolicy policy : buyingPolicies)
                if(policy.getPolicyID()==buyingPolicy.getPolicyID()){
                    res=buyingPolicy;
                }
        if(res != null)
            buyingPolicies.remove(res);
        return res;
    }


    @Override
    public List<IBuyingPolicy> getPolicies() {
        return buyingPolicies;
    }

    @Override
    public IBuyingPolicy getPolicyByID(int policyID) {
        if(this.policyID==policyID)
            return this;
        if(buyingPolicies==null || buyingPolicies.isEmpty())
            return null;
        for(IBuyingPolicy child : buyingPolicies){
            IBuyingPolicy found = child.getPolicyByID(policyID);
            if(found!=null)
                return found;
        }
        return null;
    }

}
