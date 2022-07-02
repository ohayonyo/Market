package Exceptions;

public class AlreadyAdminException extends Exception {
    private final String userName;
    public AlreadyAdminException(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "AlreadyAdminException: user name - " + userName;
    }

}