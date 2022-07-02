package Store.Discount;

import Exceptions.IllegalDiscountException;

import javax.persistence.Entity;

@Entity
public class NoDiscountPolicy extends SimpleDiscountPolicy{
    public NoDiscountPolicy() throws IllegalDiscountException {
        super(0, null);
    }

    @Override
    public String getTypeName() {
        return "No discounts";
    }
}
