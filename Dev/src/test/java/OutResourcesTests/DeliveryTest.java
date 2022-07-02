package OutResourcesTests;

import Exceptions.DeliveryException;
import OutResources.DeliveryAdapter;
import OutResources.DeliveryAdapterImpl;
import OutResources.DeliveryDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class DeliveryTest {

    private DeliveryAdapter deliveryAdapter;

    @BeforeEach
    void setUp() throws DeliveryException {
        deliveryAdapter = new DeliveryAdapterImpl();
    }

    @Test
    void connectDeliverySystem() throws DeliveryException {
        deliveryAdapter.connect();
    }

    @Test
    void deliver() throws DeliveryException {
        DeliveryDetails deliveryData = new DeliveryDetails("Yarin", "Klauzner", "BeerSheva" , "Israel", 12345);
        int tId = deliveryData.getTransactionId();
        assertEquals( 0, tId);
        assertFalse(deliveryData.isDelivered());
        deliveryAdapter.deliver(deliveryData);
        tId = deliveryData.getTransactionId();
        assertTrue( tId !=-1 );
        assertTrue(deliveryData.isDelivered());
    }

    @Test
    void cancel() throws DeliveryException {
        int tId;
        DeliveryDetails deliveryData = new DeliveryDetails("Yarin", "Klauzner", "BeerSheva" , "Israel", 12345);
        tId = deliveryData.getTransactionId();
        assertEquals( 0, tId);
        deliveryAdapter.cancelDelivery(deliveryData);
        assertFalse(deliveryData.isDelivered());
    }

    @Test
    void deliverAndCancel() throws DeliveryException {
        int tId;
        DeliveryDetails deliveryData = new DeliveryDetails("Yarin", "Klauzner", "BeerSheva" , "Israel", 12345);
        assertFalse(deliveryData.isDelivered());
        deliveryAdapter.deliver(deliveryData);
        tId = deliveryData.getTransactionId();
        assertTrue(deliveryData.isDelivered());
        deliveryAdapter.cancelDelivery(deliveryData);
        assertTrue( tId >= 10000 && tId <= 100000);
        assertFalse(deliveryData.isDelivered());
    }
}
