package Exceptions;

import User.Subscriber;

public class NotBidManagementException extends Exception {

    private Subscriber s;
    public NotBidManagementException(Subscriber s) {
        this.s=s;
    }

    @Override
    public String toString() {
        return "The user:"+s.getUserName()+" has no permission of STORE_BID";
    }
}
