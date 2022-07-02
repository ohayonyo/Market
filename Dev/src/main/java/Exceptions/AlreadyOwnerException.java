package Exceptions;

public class AlreadyOwnerException extends Exception {
    private final String userName;
    public AlreadyOwnerException(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "AlreadyOwnerException: user name - " + userName;
    }

}
