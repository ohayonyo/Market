package Notifications;

import javax.persistence.Entity;

@Entity
public class SubscriberRemoveNotification extends Notification{
    private String userName;

    public SubscriberRemoveNotification(String userName) {
        this.userName = userName;
    }

    public SubscriberRemoveNotification() {

    }

    @Override
    public void notifyNotification() {
    }

    @Override
    public String toString() {
        return "Subscriber " + userName + " has removed from the market";
    }
}
