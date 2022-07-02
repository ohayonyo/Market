package Exceptions;

public class BidException extends Exception{
    public BidException()
    {super();
    }

    @Override
    public String toString() {
        return "BidException";
    }

}
