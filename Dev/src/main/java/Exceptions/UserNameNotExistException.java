package Exceptions;

public class UserNameNotExistException extends Exception {
    private final String userName;
    public UserNameNotExistException(String userName)
    {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserNameNotExistException: user name - " + userName;
    }

}
