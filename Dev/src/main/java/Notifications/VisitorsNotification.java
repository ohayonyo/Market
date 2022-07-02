package Notifications;

import org.json.JSONObject;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.Map;

@Entity
public class VisitorsNotification extends Notification {

    @ElementCollection
    private Map<String, Integer> visitors;

    public VisitorsNotification(Map<String, Integer> visitors) {
        this.visitors = visitors;
    }

    public VisitorsNotification() {

    }

    @Override
    public void notifyNotification() {

    }

    @Override
    public String toString() {
        return new JSONObject(this.visitors).toString();
    }
}
