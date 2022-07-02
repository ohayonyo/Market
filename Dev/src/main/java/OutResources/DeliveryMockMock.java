package OutResources;

import Exceptions.DeliveryException;

public class DeliveryMockMock implements DeliveryMockAdapter {

    @Override
    public boolean deliver(DeliveryDetails deliveryDetails) throws DeliveryException
    {
        return true;
    }

    public void cancelDelivery(DeliveryDetails deliveryDetails)
    {

    }
}
