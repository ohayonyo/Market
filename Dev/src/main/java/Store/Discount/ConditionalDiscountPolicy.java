package Store.Discount;

import Exceptions.IllegalDiscountException;
import Store.PolicyLimitation.IBuyingPolicy;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.Map;

@Entity
public class ConditionalDiscountPolicy extends IDiscountPolicy{

    @ManyToOne
    private IBuyingPolicy buyingPolicy;
    @ManyToOne
    private IDiscountPolicy discountPolicy;

    public ConditionalDiscountPolicy(IBuyingPolicy buyingPolicy,IDiscountPolicy discountPolicy){
        this.discountPolicy=discountPolicy;
        this.buyingPolicy=buyingPolicy;
        //this.discountID = ++discountCounter;
    }

    public ConditionalDiscountPolicy() {
        //++discountCounter;
    }

    @Override
    public Map<Product, Double> getDiscountsToCart(ShoppingBag shoppingBag) throws IllegalDiscountException {
        if(buyingPolicy==null||buyingPolicy.isSatisfiesCondition(shoppingBag))
            return discountPolicy.getDiscountsToCart(shoppingBag);
        return null;
    }

    @Override
    public double getCartPrice(ShoppingBag shoppingBagToBuy) throws IllegalDiscountException {
        if(shoppingBagToBuy==null)
            return 0;
        if(buyingPolicy==null||buyingPolicy.isSatisfiesCondition(shoppingBagToBuy))
            return discountPolicy.getCartPrice(shoppingBagToBuy);
        return shoppingBagToBuy.getTotalPriceWithoutDiscount();
    }

    @Override
    public IDiscountPolicy getDiscountByID(int discountID) {
        return discountPolicy.getDiscountByID(discountID);
    }


    @Override
    public List<IDiscountPolicy> getDiscountPolicies() {
        return discountPolicy.getDiscountPolicies();
    }

    public IBuyingPolicy getCondition(){
        return buyingPolicy;
    }

    @Override
    public int getDiscountID() {
        return discountID;
    }

    @Override
    public double moneySaved(ShoppingBag shoppingBag) throws IllegalDiscountException {
        if(buyingPolicy==null || buyingPolicy.isSatisfiesCondition(shoppingBag))
            return discountPolicy.moneySaved(shoppingBag);
        return 0;
    }

    public String getTypeName() {
        String res="If this buying policy take place:\n"+buyingPolicy.getTypeName();
        res = res + " than you will get the following discount:"+discountPolicy.getTypeName()+"\n";
        return res;
    }
}
