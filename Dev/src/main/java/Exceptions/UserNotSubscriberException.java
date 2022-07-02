package Exceptions;

public class UserNotSubscriberException extends Exception {
    public UserNotSubscriberException()
    {
    }

    @Override
    public String toString() {
        return "UserNotSubscriberException";
    }

}
