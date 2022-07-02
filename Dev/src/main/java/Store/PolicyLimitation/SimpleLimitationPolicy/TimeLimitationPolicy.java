package Store.PolicyLimitation.SimpleLimitationPolicy;

import Store.PolicyLimitation.SimpleBuyingPolicy;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
public class TimeLimitationPolicy extends SimpleBuyingPolicy{

    @ManyToMany
    private List<Product> products;
    private LocalTime minTime;
    private LocalTime maxTime;

    @Transient
    private String cause;


    public TimeLimitationPolicy(List<Product> products, LocalTime minTime, LocalTime maxTime) {
        this.products = products;
        this.minTime = minTime;
        this.maxTime=maxTime;
        //policyID = new PolicyCounter().getInstance();
    }

    public TimeLimitationPolicy() {

    }


    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        if(shoppingBag==null)
            return true;
        LocalTime nowTime = LocalTime.now();
        String systemTime = nowTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        String policyMinTime = minTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        String policyMaxTime = maxTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        boolean isTimeInRange=systemTime.compareTo(policyMinTime)>=0 && systemTime.compareTo(policyMaxTime)<=0;
        Map<Product,Integer> productsToBuy = shoppingBag.getProducts();
        for(Product p : productsToBuy.keySet()){
            if(products.contains(p) && !isTimeInRange){
                cause = "The product:"+p.getName()+" can be purchase only between:"+minTime.toString()+" to "+maxTime.toString();
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
        return "Time must be between "+minTime.toString()+"-"+maxTime.toString() + " for the products:"+getProductsNames().toString();
    }

}
