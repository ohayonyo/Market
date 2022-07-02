package Exceptions;

public class WrongPasswordException extends Exception {
    final String name;

    public WrongPasswordException(String name)
    {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WrongPasswordException: userName - " + name + ", password - ********";
    }

}
