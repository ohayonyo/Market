package Exceptions;

public class StoreNotFoundException extends Exception {
    public StoreNotFoundException()
    {
    }

    @Override
    public String toString() {
        return "StoreNotFoundException";
    }

}
