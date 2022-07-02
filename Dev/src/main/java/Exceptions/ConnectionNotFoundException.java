package Exceptions;

public class ConnectionNotFoundException extends Exception {
    public ConnectionNotFoundException()
    {
    }

    @Override
    public String toString() {
        return "ConnectionNotFoundException";
    }

}
