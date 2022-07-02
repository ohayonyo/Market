package Notifications;

import Store.Bid.Bid;

import javax.persistence.Entity;

@Entity
public class BidApprovedNotification extends BidNotification {

    public BidApprovedNotification(Bid bid) {
        super(bid);
    }

    public BidApprovedNotification() {

    }

    @Override
    public void notifyNotification() {

    }

    @Override
    public String toString() {
        return " The bid suggested by " + this.getBid().getSubscriber().getUserName()+ " of product: " + this.getBid().getProduct().getName() + " was approved";
    }
}
