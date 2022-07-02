package Store.PolicyLimitation.SimpleLimitationPolicy;

import Store.PairInteger;
import Store.PolicyLimitation.SimpleBuyingPolicy;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "amount_limitation_policy")
public class AmountLimitationPolicy extends SimpleBuyingPolicy {

//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "amount_limitation_policy_mapping",
//            joinColumns = {@JoinColumn(name = "amount_policy_id", referencedColumnName = "policyID")},
//            inverseJoinColumns = {@JoinColumn(name = "Pair_Integer_id", referencedColumnName = "id")})
//    @MapKeyJoinColumn(name = "productID")


//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "product_amount_limitation_mapping",
//            joinColumns = {@JoinColumn(name = "policy_id", referencedColumnName = "policyID")},
//            inverseJoinColumns = {@JoinColumn(name = "limit_id", referencedColumnName = "pairID")})
//    @MapKeyJoinColumn(name = "seller_id")
//    private Map<Product, PairInteger> productLimitation;

    @ElementCollection
    private Map<Product,Integer> productMinLimitation;
    @ElementCollection
    private Map<Product,Integer> productMaxLimitation;

    @Transient
    private String cause="";
    //good
    public AmountLimitationPolicy(Map<Product, PairInteger> productLimitation){
        productMinLimitation= new HashMap<>();
        productMaxLimitation = new HashMap<>();
        for(Map.Entry<Product,PairInteger> entry : productLimitation.entrySet()){
            Product p = entry.getKey();
            PairInteger pairInteger = entry.getValue();
            productMinLimitation.put(p,pairInteger.getKey());
            productMaxLimitation.put(p,pairInteger.getValue());
        }
        //this.policyID = new PolicyCounter().getInstance();
    }

    public AmountLimitationPolicy() {

    }

    @Override
    public boolean isSatisfiesCondition(ShoppingBag shoppingBag) {
        if(shoppingBag==null)
            return true;
        Map<Product,Integer> productsToBuy = shoppingBag.getProducts();
        for(Product p : productsToBuy.keySet()){
            if(productMinLimitation.containsKey(p)){
               int low = productMinLimitation.get(p);
               int high = productMaxLimitation.get(p);
               int amount = productsToBuy.get(p);
               if(amount>high || amount<low) {
                   cause="The product "+p.getName()+" is not in the limited range "+low+"-"+high;
                   return false;
               }
            }
        }
        return true;
    }

    @Override
    public String exceptionCause(ShoppingBag shoppingBag) {
        isSatisfiesCondition(shoppingBag);
        return cause;
    }

    public List<String> getProductsNames(){
        List<String> res = new LinkedList<>();
        if(productMaxLimitation!=null)
            for(Product p : productMaxLimitation.keySet())
                res.add(p.getName());
        return res;
    }

    public String getProductLimitationString(Product p){
        if(productMinLimitation==null||productMaxLimitation==null)
            return "";
        if(!productMinLimitation.containsKey(p)||!productMaxLimitation.containsKey(p))
            return "";
        return "product:"+p.getName()+ " has limitation of "+productMinLimitation.get(p)+"-"+productMaxLimitation.get(p);
    }

    @Override
    public String getTypeName() {
        List<String> productsName = getProductsNames();
        String res="";
        Set<Product> productSet = productMaxLimitation.keySet();
        if(productSet==null || productSet.isEmpty())
            return "";
        int minLimitation=0,maxLimitation=0;
        if(productSet!=null)
            for(Product p : productSet) {
                minLimitation = productMinLimitation.get(p);
                maxLimitation = productMaxLimitation.get(p);
                break;
            }
        if(productsName.size()>1) {
            res = "Products:" + productsName.toString() + " have limitation of " + minLimitation + "-" + maxLimitation;
        }else
            res = "Product:" + productsName.toString() + " has limitation of " +minLimitation+"-"+maxLimitation;

        return res;

//        String res="";
//        if(productMaxLimitation!=null)
//        for(Product p : productMaxLimitation.keySet())
//            res=res+getProductLimitationString(p)+"\n";
//        return res;
    }
}
