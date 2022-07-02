package Exceptions;

public class NotOwnerByException extends Exception {
    private final String storeName;
    private final String ownerUserName;
    private final String userName;
    public NotOwnerByException(String storeName,String ownerUserName,String userName)
    {
        this.storeName=storeName;
        this.ownerUserName=ownerUserName;
        this.userName=userName;
    }

    @Override
    public String toString() {
        return "NotOwnerByException: The user:"+ownerUserName+" did not appointed "+userName+" at store:"+storeName;
    }
}
