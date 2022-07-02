package OutResources;

import Exceptions.DeliveryException;

public interface DeliveryAdapter {
    public boolean deliver(DeliveryDetails deliveryDetails) throws DeliveryException;
    public void cancelDelivery(DeliveryDetails deliveryDetails) throws DeliveryException ;
    void connect() throws DeliveryException;



}
