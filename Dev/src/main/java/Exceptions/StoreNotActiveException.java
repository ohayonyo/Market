package Exceptions;

public class StoreNotActiveException extends Exception {
    private final int storeId;
    public StoreNotActiveException(int storeId)
    {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "StoreNotActiveException";
    }

}
