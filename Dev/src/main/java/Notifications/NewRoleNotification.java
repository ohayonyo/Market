package Notifications;

import User.Subscriber;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class NewRoleNotification extends Notification{

    @ManyToOne
    private Subscriber source;
    private String role;
    private int storeId;

    public NewRoleNotification() {

    }

    @Override
    public String toString() {
        return source.getUserName() + " appointed you to be " + role + " of storeId: " + storeId;
    }

    public NewRoleNotification(Subscriber dest, String role, int storeId) {
        this.source = dest;
        this.role = role;
        this.storeId = storeId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    @Override
    public void notifyNotification() {

    }
}
