package Notifications;

import Store.Bid.Bid;

import javax.persistence.Entity;

@Entity
public class BidRejectionNotification extends BidNotification{
    public BidRejectionNotification(Bid bid) {
        super(bid);
    }

    public BidRejectionNotification() {

    }


    @Override
    public String toString() {
        return "The bid of product: " + this.getBid().getProduct().getName() + " was rejected ";
    }

}
