package Exceptions;

public class PaymentException extends Exception {
    String message="";

    public PaymentException()
    {
    }

    public PaymentException(String msg)
    {
        this.message=msg;
    }

    @Override
    public String toString() {
        return "PaymentException" + this.message;
    }

}
