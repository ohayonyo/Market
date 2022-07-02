package Exceptions;

public class CantRemoveDiscountException extends Exception{

    public CantRemoveDiscountException(){
        super();
    }

    public String toString() {
        return "CantRemoveDiscountException";
    }

}
