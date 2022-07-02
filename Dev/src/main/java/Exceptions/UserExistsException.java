package Exceptions;

public class UserExistsException extends Exception {
    final String name;

    public UserExistsException(String name)
    {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserExistsException: userName - " + name ;
    }

}
