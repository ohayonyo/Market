package OutResourcesTests;

import Exceptions.PaymentException;
import OutResources.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

class PaymentTest {
    //    private double amount; //last price purchased
//    private boolean isSucceed;
//    private Collection<String> payments = new LinkedList<>(); //list of usernames
    private HashMap<String, LinkedList<Double>> payments = new HashMap<>();
    private PaymentAdapter paymentAdapter;

/*    @BeforeClass
    public void beforeClass() {
        RepoMock.enable();
    }*/

    @BeforeEach
    void setUp() throws PaymentException {
        MockitoAnnotations.openMocks(this);
        paymentAdapter = new PaymentAdapterImpl();
    }

    @Test
    void connectPaymentSystem() throws PaymentException {
        paymentAdapter.connect();
    }
    @Test
    void pay() throws PaymentException {
        int tId;
        PaymentDetails paymentData = new PaymentDetails("1234", 1, 2022, "a", "323", "000000018", 20);
        tId = paymentData.getTransactionId();
        assertEquals( 0, tId);
        assertFalse(paymentData.isPaid());
        paymentAdapter.pay(paymentData);
        tId = paymentData.getTransactionId();
        Assertions.assertTrue( tId >= 10000 && tId <= 100000);
        Assertions.assertTrue(paymentData.isPaid());
    }

@Test
    void payTimeoutCVV() throws PaymentException {
        int tId;
        PaymentDetails paymentData = new PaymentDetails("1234", 1, 2022, "a", "984", "000000018", 20);
        tId = paymentData.getTransactionId();
        assertEquals( 0, tId);
        assertFalse(paymentData.isPaid());
        assertThrows(PaymentException.class, () -> paymentAdapter.pay(paymentData));
    }

    @Test
    void payInvalidCVVResponse() throws PaymentException {
        int tId;
        PaymentDetails paymentData = new PaymentDetails("1234", 1, 2022, "a", "986", "000000018", 20);
        tId = paymentData.getTransactionId();
        assertEquals( 0, tId);
        assertFalse(paymentData.isPaid());
        assertThrows(PaymentException.class, () -> paymentAdapter.pay(paymentData));
    }

    @Test
    void cancel() throws PaymentException {
        int tId;
        PaymentDetails paymentData = new PaymentDetails("1234", 1, 2022, "a", "001", "000000018", 40);
        tId = paymentData.getTransactionId();
        assertEquals( 0, tId);
        paymentAdapter.cancelPayment(paymentData);
        assertFalse(paymentData.isPaid());
    }

    @Test
    void payAndCancel() throws PaymentException {
        int tId;
        PaymentDetails paymentData = new PaymentDetails("1234", 1, 2022, "a", "001", "000000018", 30);
        assertFalse(paymentData.isPaid());
        paymentAdapter.pay(paymentData);
        tId = paymentData.getTransactionId();
        Assertions.assertTrue(paymentData.isPaid());
        paymentAdapter.cancelPayment(paymentData);
        Assertions.assertTrue( tId >= 10000 && tId <= 100000);
        assertFalse(paymentData.isPaid());
    }

}