package Notifications;

import javax.persistence.Entity;

    @Entity
    public class FinalAppointOwnerNotification extends Notification{
        private int storeId;
        private String subscriber;


        public FinalAppointOwnerNotification(int storeId,String subscriber ){
            this.storeId = storeId;
            this.subscriber = subscriber;
        }

        public FinalAppointOwnerNotification( ){

        }

        @Override
        public void notifyNotification() {

        }

        @Override
        public String toString() {
            return  subscriber + " had appointed to an owner in store Id: " + storeId ;
        }
}
