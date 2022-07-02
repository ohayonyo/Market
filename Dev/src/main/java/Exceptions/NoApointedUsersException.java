package Exceptions;

public class NoApointedUsersException extends Exception{
    private String storeName;
    private String userName;

    public NoApointedUsersException(String userName, String storeName)
    {
        this.storeName = storeName;
        this.userName = userName;
    }

    @Override
    public String toString(){
        return "NoApointedUsersException: store - " + storeName + ", user name: " + userName;
    }
}
