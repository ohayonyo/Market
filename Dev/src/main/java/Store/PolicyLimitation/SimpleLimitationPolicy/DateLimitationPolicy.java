package Store.PolicyLimitation.SimpleLimitationPolicy;

import Store.PolicyLimitation.SimpleBuyingPolicy;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
public class DateLimitationPolicy extends SimpleBuyingPolicy {
    private LocalDate forbiddenDate;
    @ManyToMany
    private List<Product> products;

    @Transient
    private String cause;

    public DateLimitationPolicy(LocalDate forbiddenDate, List<Product> products) {
        this.forbiddenDate = forbiddenDate;
        this.products = products;
        //policyID = new PolicyCounter().getInstance();
    }

    public DateLimitationPolicy() {

    }

    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        if(shoppingBag==null)
            return true;
        Map<Product,Integer> productsToBuy = shoppingBag.getProducts();
        for(Product p : productsToBuy.keySet()){
            if(products.contains(p) && LocalDate.now().equals(forbiddenDate)) {
                cause = "Can't purchase the product:"+p.getName()+" at date:"+forbiddenDate.toString();
                return false;
            }
        }
        return true;
    }

    @Override
    public String exceptionCause(ShoppingBag shoppingBag) {
        isSatisfiesCondition(shoppingBag);
        return cause;
    }

    private List<String> getProductsNames(){
        List<String> names = new LinkedList<>();
        if(products!=null)
            for(Product p : products)
                names.add(p.getName());
        return names;
    }
    @Override
    public String getTypeName() {
        return "Forbidden date limitation on products :"+getProductsNames().toString()+ " at date:"+forbiddenDate.toString();
    }
}
