package OutResources;

import Exceptions.DeliveryException;

public interface DeliveryMockAdapter {
    public boolean deliver(DeliveryDetails deliveryDetails) throws DeliveryException;
    public void cancelDelivery(DeliveryDetails deliveryDetails) ;


}
