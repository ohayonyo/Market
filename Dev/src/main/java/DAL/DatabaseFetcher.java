package DAL;

import Exceptions.IllegalDiscountException;
import Security.UserValidation;
import Store.Store;
import Store.Product;
import User.Subscriber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseFetcher {

    private ConcurrentHashMap<String, Subscriber> subscribers;
    private ConcurrentHashMap<Integer, Store> stores;
    private AtomicInteger subscriberIdCounter;
    private Map<String, String> userValidation;


    public DatabaseFetcher() {
        Repo.getEm(); //initializing Entity Manager
        subscribers = new ConcurrentHashMap<>();
        stores = new ConcurrentHashMap<>();
        subscriberIdCounter = new AtomicInteger();
        this.userValidation = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, Subscriber> getSubscribers() {
        List<Subscriber> list = Repo.getSubscribers();
        for (Subscriber s:list ) {
            subscribers.put(s.getUserName(), s);
        }
        return subscribers;
    }

    public UserValidation getValidation() {
        UserValidation uv = Repo.getValidation();
        for (Map.Entry<String,String> entry : uv.getUsers().entrySet()) {
            userValidation.put(entry.getKey(), entry.getValue());
        }
        return uv;
    }

    public ConcurrentHashMap<Integer, Store> getStores() throws IllegalDiscountException {
        List<Store> list = Repo.getStores();
        for (Store s:list ) {
            stores.put(s.getStoreId(), s);
            s.setIdCounter();
            for (Product p : s.getProducts())
                p.setIdCounter();
            //s.getStoreDiscount().setDiscountCounter();
        }
        return stores;
    }

    public AtomicInteger getSubscriberIdCounter() {
        List<Subscriber> list = Repo.getSubscribers();
        subscriberIdCounter.set(list.size());
        return subscriberIdCounter;
    }

}
