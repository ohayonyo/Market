package Notifications;

import Store.Bid.Bid;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class BidNotification extends Notification{
    @OneToOne
    private Bid bid;

    public BidNotification(Bid bid) {
        this.bid = bid;
    }

    public BidNotification() {

    }

    @Override
    public void notifyNotification() {

    }

    public Bid getBid() { return bid; }

    @Override
    public String toString() {
        return "New bid offered by: " + bid.getSubscriber().getUserName() + ", about product " + bid.getProduct().getName() +
                ", with amount of: " + bid.getAmount() + ", and price: " + bid.getPrice();
    }


}
