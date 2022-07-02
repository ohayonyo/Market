package User;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Entity
public class Visitors {
    @Id
    private int id = 1;
    @ElementCollection
    private Map<String, Integer> guests;
    @ElementCollection
    private Map<String, Integer> subscribers;
    @ElementCollection
    private Map<String, Integer> managers;
    @ElementCollection
    private Map<String, Integer> owners;
    @ElementCollection
    private Map<String, Integer> admins;

    public Visitors() {
        guests = new ConcurrentHashMap<>();
        subscribers = new ConcurrentHashMap<>();
        managers = new ConcurrentHashMap<>();
        owners = new ConcurrentHashMap<>();
        admins = new ConcurrentHashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, Integer> getGuests() {
        return guests;
    }

    public void setGuests(Map<String, Integer> guests) {
        this.guests = guests;
    }

    public Map<String, Integer> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Map<String, Integer> subscribers) {
        this.subscribers = subscribers;
    }

    public Map<String, Integer> getManagers() {
        return managers;
    }

    public void setManagers(Map<String, Integer> managers) {
        this.managers = managers;
    }

    public Map<String, Integer> getOwners() {
        return owners;
    }

    public void setOwners(Map<String, Integer> owners) {
        this.owners = owners;
    }

    public Map<String, Integer> getAdmins() {
        return admins;
    }

    public void setAdmins(Map<String, Integer> admins) {
        this.admins = admins;
    }
}
