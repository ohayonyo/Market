package Exceptions;

public class DiscountNotFoundException extends Exception {
    private double discount;
    public DiscountNotFoundException(double discount){
        this.discount = discount;
    }

    public String toString() {
        return "DiscountNotFoundException";
    }

}
