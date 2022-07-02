package OutResources;
import Exceptions.PaymentException;

public interface PaymentAdapter {

    void connect() throws PaymentException;

    boolean pay(PaymentDetails data) throws PaymentException;

    void cancelPayment(PaymentDetails data) throws PaymentException;
}
