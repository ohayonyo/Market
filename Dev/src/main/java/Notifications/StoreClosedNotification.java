package Notifications;

import javax.persistence.Entity;

@Entity
public class StoreClosedNotification extends Notification{

    private int storeId;

    public StoreClosedNotification(int storeId) {
        this.storeId = storeId;
    }

    public StoreClosedNotification() {

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
        return "Store with the ID: " + storeId + "is closed now";
    }

}
