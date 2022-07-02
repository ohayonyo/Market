package Notifications;

import javax.persistence.Entity;

@Entity
public class PurchaseNotification extends Notification{

    private String basket = null;
    private int storeID;

    public PurchaseNotification(int storeID, String basket) {
        this.storeID = storeID;
        this.basket = basket;
    }

    public PurchaseNotification() {

    }

    @Override
    public void notifyNotification() {

    }

    @Override
    public String toString() {
        return "Some user purchased from you: " + basket.toString();
    }
}
