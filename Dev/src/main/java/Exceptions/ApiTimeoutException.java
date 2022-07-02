package Exceptions;

public class ApiTimeoutException extends Exception {
    public ApiTimeoutException()
    {
    }

    @Override
    public String toString() {
        return "Timeout error- purchase has been failed ";
    }

}