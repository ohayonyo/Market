package Exceptions;

public class IllegalDiscountException extends Exception {
    private final double discount;

    public IllegalDiscountException(double discount){
        super();
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "IllegalDiscountException, discount can't be:"+discount+"%";
    }
}
