package Exceptions;

public class UserNotExistException extends Exception {
    final String name;

    public UserNotExistException(String name)
    {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserNotExistException: userName - " + name ;
    }

}
