package Exceptions;

public class StoreIsAlreadyActiveException extends Exception{
    private int storeId;

    public StoreIsAlreadyActiveException(int storeId)
    {
        this.storeId = storeId;
    }
    public String toString() {
        return "StoreIsAlreadyActiveException";
    }
}
