package Exceptions;

public class IllegalPriceException extends Exception {
    private final double price;
    public IllegalPriceException(double price)
    {
        this.price = price;
    }

    @Override
    public String toString() {
        return "IllegalPriceException: price is - " + price;
    }

}
