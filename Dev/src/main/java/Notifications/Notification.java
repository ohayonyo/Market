package Notifications;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Notification {

    @Id
    @GeneratedValue
    private Integer id;

    @Transient
    private boolean shown = false;

    public abstract void notifyNotification();

    @Override
    public String toString() {
        return "Notification: shown - " + shown;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
