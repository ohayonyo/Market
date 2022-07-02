package Store.PolicyLimitation;


import User.ShoppingBag;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class IBuyingPolicy{

    @Id
    @GeneratedValue
    protected int policyID;

    public IBuyingPolicy(int policyID) {
        this.policyID = policyID;
    }

    public IBuyingPolicy() {

    }

    public abstract boolean isSatisfiesCondition(ShoppingBag shoppingBag);

    public abstract List<IBuyingPolicy> getPolicies();

    public abstract IBuyingPolicy getPolicyByID(int policyID);

    public int getPolicyID(){
        return policyID;
    }

    public abstract String exceptionCause(ShoppingBag shoppingBag);

    public abstract String getTypeName();

}
