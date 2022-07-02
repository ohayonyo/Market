package Notifications;

import Store.Bid.Bid;
import User.Subscriber;

import javax.persistence.Entity;

@Entity
public class BidCounteredNotification extends BidNotification{

    public BidCounteredNotification(Bid bid) {
        super(bid);

    }

    public BidCounteredNotification() {


    }


    @Override
    public String toString() {
        return "The bid of product: " + getBid().getProduct().getName() + " was countered with new prise of: " + getBid().getPrice();
    }
}
