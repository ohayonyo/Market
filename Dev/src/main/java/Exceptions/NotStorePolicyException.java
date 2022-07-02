package Exceptions;

import User.Roles;
import User.Subscriber;

public class NotStorePolicyException extends Exception {
    private Subscriber s;
    public NotStorePolicyException(Subscriber s) {
        this.s=s;
    }

    @Override
    public String toString() {
        return "The user:"+s.getUserName()+" has no permission of STORE_POLICY";
    }

}
