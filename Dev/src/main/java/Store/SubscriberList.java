package Store;

import User.Subscriber;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class SubscriberList {

    @Id
    @GeneratedValue
    @Column(name = "subscriber_appointments_id")
    private int id;

//    @Transient
//    private static int count = 0;

    @ManyToMany
    private List<Subscriber> appointments;

    public SubscriberList() {
        //this.id = ++count;
        this.appointments = new LinkedList<>();
    }

    public boolean contains(Subscriber subscriber)
    {
        return appointments.contains(subscriber);
    }

    public boolean remove(Subscriber subscriber)
    {
        return appointments.remove(subscriber);
    }

    public void add(Subscriber subscriber) {
        appointments.add(subscriber);
    }

    public List<Subscriber> getSubscribers()
    {
        return this.appointments;
    }

    public boolean isEmpty() {
        return this.appointments.isEmpty();
    }
}
