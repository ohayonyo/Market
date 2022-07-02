package Notifications;

import Exceptions.LostConnectionException;
import Store.Bid.Bid;
import DAL.Repo;
import Store.Store;
import Store.Product;
import User.Subscriber;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@Entity
public class Observable implements Serializable {

    @ManyToMany
    private Collection<Subscriber> observers = new HashSet<>();
    @Id
    @GeneratedValue
    private Long id;

    public Observable() {
    }

    public void setObservers(Collection<Subscriber> observers) {
        this.observers = observers;
    }

    public void subscribe(Subscriber observer) throws LostConnectionException {
        if(!observers.contains(observer))
        {
            observers.add(observer);
            Repo.merge(this);
        }
    }

    public void unsubscribe(Subscriber observer) throws LostConnectionException {
        if(observers.contains(observer))
        {
            observers.remove(observer);
            Repo.merge(this);
        }
    }

    //notify to all the system managers of removing a subscriber
    public void notifySubscriberRemove(String userName) throws LostConnectionException {
        SubscriberRemoveNotification notification = new SubscriberRemoveNotification(userName);
        Repo.persist(notification);
        for (Subscriber s: observers) {
            s.notifyNotification(notification);
        }
    }

    //notifies store owner upon purchase
    public void notifyPurchase(Store store, Map<Product, Integer> basket) throws LostConnectionException {
        PurchaseNotification notification = new PurchaseNotification(store.getStoreId(), basket.toString());
        Repo.persist(notification);
        for (Subscriber s: observers) {
            s.notifyNotification(notification);
        }
    }

    public Collection<Subscriber> getObservers() {
        return observers;
    }

    public void notifyRemoveRole(Subscriber source, Subscriber dest, int storeId, String role) throws LostConnectionException {
        RemoveRoleNotification notification = new RemoveRoleNotification();
        notification.setRemover(source);
        notification.setStoreId(storeId);
        notification.setRole(role);
        Repo.persist(notification);
        dest.notifyNotification(notification);
        unsubscribe(dest);
    }

    public void notifyNewRole(Subscriber source, Subscriber toAssign, int storeId, String role) throws LostConnectionException {
        NewRoleNotification notification = new NewRoleNotification(source, role, storeId);
        Repo.persist(notification);
        subscribe(toAssign);
        toAssign.notifyNotification(notification);
    }

    public void notifyStoreClosed(int storeId) throws LostConnectionException {
        StoreClosedNotification notification = new StoreClosedNotification(storeId);
        Repo.persist(notification);
        for(Subscriber subscriber : observers)
            subscriber.notifyNotification(notification);
    }

    public void notifyNewBid(Bid bid) throws LostConnectionException {
        BidNotification notification = new BidNotification(bid);
        Repo.persist(notification);
        for (Subscriber s: observers) {
            s.notifyNotification(notification);
        }
    }

    public void notifyApprovedBid(Bid bid) throws LostConnectionException {
        BidApprovedNotification notification = new BidApprovedNotification(bid);
        Repo.persist(notification);
        bid.getSubscriber().notifyNotification(notification);
        for (Subscriber s: observers)
            s.notifyNotification(notification);
    }

    public void notifyDeclinedBid(Bid bid) throws LostConnectionException {
        BidRejectionNotification notification = new BidRejectionNotification(bid);
        Repo.persist(notification);
        bid.getSubscriber().notifyNotification(notification);
        for (Subscriber s: observers)
            s.notifyNotification(notification);
    }

    public void notifyCounterBid(Bid bid) throws LostConnectionException {
        BidCounteredNotification notification = new BidCounteredNotification(bid);
        Repo.persist(notification);
        bid.getSubscriber().notifyNotification(notification);
        for (Subscriber s: observers)
            s.notifyNotification(notification);
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void notifyApproveOwnerNotification(ApproveOwnerAppointmentNotification notification) throws LostConnectionException {
        for (Subscriber s: observers)
            s.notifyNotification(notification);
    }

    public void notifyFinalOwnerApproval(FinalAppointOwnerNotification finalAppointOwnerNotification) throws LostConnectionException {
        for (Subscriber s: observers)
            s.notifyNotification(finalAppointOwnerNotification);
    }
}
