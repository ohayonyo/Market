package Exceptions;

public class LostConnectionException extends Exception {
    public LostConnectionException()
    {
    }

    @Override
    public String toString() {
        return "The connection with the data base lost \nplease make sure that your future actions will not save.";
    }

}
