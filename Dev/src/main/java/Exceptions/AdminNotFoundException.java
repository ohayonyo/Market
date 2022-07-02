package Exceptions;

public class AdminNotFoundException extends Exception {
    public AdminNotFoundException()
    {
    }

    @Override
    public String toString() {
        return "AdminNotFoundException";
    }

}
