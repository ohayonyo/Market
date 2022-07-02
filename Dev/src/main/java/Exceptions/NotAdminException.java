package Exceptions;

public class NotAdminException extends Exception {
    public NotAdminException()
    {
    }

    @Override
    public String toString() {
        return "NotAdminException";
    }

}
