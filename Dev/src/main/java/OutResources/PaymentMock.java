package OutResources;

import Exceptions.PaymentException;

public class PaymentMock implements PaymentMockAdapter {

    @Override
    public boolean pay(PaymentDetails paymentDetails) throws PaymentException
    {
        return true;
    }


    public void cancelPayment(PaymentDetails paymentDetails)
    {

    }
}
