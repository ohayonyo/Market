package Notifications;

import javax.persistence.Entity;

@Entity
public class ApproveOwnerAppointmentNotification extends Notification{
    private int storeId;
    private String ownerName;
    private String subscriber;


   public ApproveOwnerAppointmentNotification(int storeId, String ownerName,String subscriber ){
      this.storeId = storeId;
      this.ownerName = ownerName;
      this.subscriber = subscriber;
   }

   public ApproveOwnerAppointmentNotification(){

   }
    @Override
    public void notifyNotification() {

    }

    @Override
    public String toString() {
        return ownerName + " approved the appointment of " + subscriber + " to an owner in store " + storeId ;
    }
}
