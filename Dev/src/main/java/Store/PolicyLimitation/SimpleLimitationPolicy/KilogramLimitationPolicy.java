
package Store.PolicyLimitation.SimpleLimitationPolicy;

import Store.PairDouble;
import Store.PolicyLimitation.SimpleBuyingPolicy;
import Store.Product;
import User.ShoppingBag;

import java.util.Map;

public class KilogramLimitationPolicy extends SimpleBuyingPolicy {

    private final Map<Product, PairDouble> productLimitation;

    public KilogramLimitationPolicy(Map<Product, PairDouble> productLimitation){
        this.productLimitation=productLimitation;
    }

    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        Map<Product,Integer> productsToBuy = shoppingBag.getProducts();
        for(Product p : productsToBuy.keySet()){
            if(productLimitation.containsKey(p)){
                double low = productLimitation.get(p).getKey();
                double high = productLimitation.get(p).getValue();
                int amount = productsToBuy.get(p);
                if(amount>high || amount<low)
                    return false;
            }
        }
        return true;
    }

    @Override
    public String exceptionCause(ShoppingBag shoppingBag) {
        return null;
    }

    @Override
    public String getTypeName() {
        return "";
    }
}
