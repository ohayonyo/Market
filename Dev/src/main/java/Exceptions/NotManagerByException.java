package Exceptions;

public class NotManagerByException extends Exception{

    private final String storeName;
    private final String ownerUserName;
    private final String userName;
    public NotManagerByException(String storeName,String ownerUserName,String userName)
    {
        this.storeName=storeName;
        this.ownerUserName=ownerUserName;
        this.userName=userName;
    }

    @Override
    public String toString() {
        return "The user:"+ownerUserName+" did not appointed "+userName+" at store:"+storeName+" to be a manager";
    }

}
