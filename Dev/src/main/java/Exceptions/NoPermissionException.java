package Exceptions;

import User.Roles.Role;
import User.Subscriber;

public class NoPermissionException extends Exception{
    private Subscriber subscriber;
    private Role role;

    public NoPermissionException(Subscriber subscriber, Role role){
        this.subscriber=subscriber;
        this.role=role;
    }

    public String toString(){
        String res="";
        switch (role.name()){
            case "INVENTORY_MANAGEMENT":
                res = new NotInventoryManagementException(subscriber).toString();
                break;
            case "STORE_POLICY":
                res = new NotStorePolicyException(subscriber).toString();
                break;
            case "STORE_BID":
                res = new NotBidManagementException(subscriber).toString();
                break;
            default:
                res="NoPermissionException";
                break;
        }
        return res;
    }
}
