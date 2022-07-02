package Exceptions;

public class AlreadyManagerException extends Exception {
    private final String userName;
    public AlreadyManagerException(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "AlreadyManagerException: user name - " + userName;
    }

}
