package Exceptions;

public class EmptyCartException extends Exception {


    public EmptyCartException()
    {

    }

    @Override
    public String toString() {
        return "EmptyCart";
    }
}
