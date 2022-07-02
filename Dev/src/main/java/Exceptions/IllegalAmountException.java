package Exceptions;

public class IllegalAmountException extends Exception {
    private final int amount;
    public IllegalAmountException(int amount)
    {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "IllegalAmountException: amount is - " + amount;
    }

}
