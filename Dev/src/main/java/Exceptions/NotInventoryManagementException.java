package Exceptions;

import User.Subscriber;

public class NotInventoryManagementException extends Exception {
    private Subscriber s;
    public NotInventoryManagementException(Subscriber s) {
        this.s=s;
    }

    @Override
    public String toString() {
        return "The user:"+s.getUserName()+" has no permission of INVENTORY_MANAGEMENT";
    }

}
