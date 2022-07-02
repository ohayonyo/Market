package Exceptions;

public class NotOwnerException extends Exception {
    public NotOwnerException()
    {
    }

    @Override
    public String toString() {
        return "NotOwnerException";
    }

}
