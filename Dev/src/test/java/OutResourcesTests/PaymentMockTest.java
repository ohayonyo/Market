package OutResourcesTests;

import Exceptions.PaymentException;
import OutResources.PaymentMockAdapter;
import OutResources.PaymentDetails;
import OutResources.PaymentMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentMockTest {
    private PaymentMockAdapter paymentMockAdapter;

    @BeforeEach
    void setUp() {
        paymentMockAdapter = new PaymentMock();
    }

    @Test
    void pay() throws PaymentException {
        paymentMockAdapter.pay(new PaymentDetails());
    }

    @Test
    void payResult() throws PaymentException {
        assertTrue(paymentMockAdapter.pay(new PaymentDetails()));
    }

    @Test
    void cancelPayment() {
        paymentMockAdapter.cancelPayment(new PaymentDetails());
    }

}