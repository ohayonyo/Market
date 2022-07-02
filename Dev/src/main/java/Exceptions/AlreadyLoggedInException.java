package Exceptions;

public class AlreadyLoggedInException extends Exception {
    private final String userName;
    public AlreadyLoggedInException(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "AlreadyLoggedInException: user name - " + userName;
    }

}