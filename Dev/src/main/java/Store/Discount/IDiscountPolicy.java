package Store.Discount;

import Exceptions.IllegalDiscountException;
import Store.Product;
import User.ShoppingBag;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class IDiscountPolicy {

    @Transient
    protected static int discountCounter = 0;

    @Id
    @GeneratedValue
    protected int discountID;

    public void setDiscountCounter() {
        IDiscountPolicy.discountCounter++;
    }

    public abstract Map<Product,Double> getDiscountsToCart(ShoppingBag shoppingBag) throws IllegalDiscountException;

    public abstract double getCartPrice(ShoppingBag shoppingBag) throws IllegalDiscountException;

    public abstract IDiscountPolicy getDiscountByID(int discountID);

    public abstract List<IDiscountPolicy> getDiscountPolicies();

    public abstract int getDiscountID();

    public abstract double moneySaved(ShoppingBag shoppingBag) throws IllegalDiscountException;

    public abstract String getTypeName();


}
