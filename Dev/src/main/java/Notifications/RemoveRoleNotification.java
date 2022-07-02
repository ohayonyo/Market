package Notifications;

import User.Subscriber;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class RemoveRoleNotification extends Notification{

    @ManyToOne
    private Subscriber source;
    private String role;
    private int storeId;

    public RemoveRoleNotification(){

    }

    public void setRemover(Subscriber source) {
        this.source = source;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    @Override
    public String toString() {
        return source.getUserName() + " has removed you as a " + role + " from storeId: " + storeId;
    }
}
