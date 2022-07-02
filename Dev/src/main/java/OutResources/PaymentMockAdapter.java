package OutResources;

import Exceptions.PaymentException;

public interface PaymentMockAdapter {

    public boolean pay(PaymentDetails paymentDetails) throws PaymentException;

    public void cancelPayment(PaymentDetails paymentDetails);

}
