package OutResourcesTests;

import Exceptions.DeliveryException;
import OutResources.DeliveryMockAdapter;
import OutResources.DeliveryDetails;
import OutResources.DeliveryMockMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryMockTest {
    private DeliveryMockAdapter deliveryMockAdapter;

    @BeforeEach
    void setUp() {
        deliveryMockAdapter = new DeliveryMockMock();
    }

    @Test
    void deliver() throws DeliveryException {
        deliveryMockAdapter.deliver(new DeliveryDetails());
    }

    @Test
    void deliverResult() throws DeliveryException {
        assertTrue(deliveryMockAdapter.deliver(new DeliveryDetails()));
    }
}