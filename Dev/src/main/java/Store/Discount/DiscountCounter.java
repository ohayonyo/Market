package Store.Discount;

public class DiscountCounter {
    private static int discountCounter = 0;

    public int getInstance(){
        return discountCounter++;
    }
}
