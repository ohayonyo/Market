package Exceptions;

public class BagNotValidPolicyException extends Exception {
    private String details;

    public BagNotValidPolicyException(String details)
    {
        this.details=details;
    }

    @Override
    public String toString() {
        return details;
    }

}
