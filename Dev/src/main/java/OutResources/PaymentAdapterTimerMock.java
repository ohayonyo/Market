package OutResources;

import Exceptions.PaymentException;

public class PaymentAdapterTimerMock implements PaymentAdapter {

    private PaymentAdapterImpl paymentSystem;
    private long startTime;
    private long endTime;
    private boolean fake;

    public PaymentAdapterTimerMock() throws PaymentException {
        paymentSystem = new PaymentAdapterImpl();
        fake = false;
    }

    @Override
    public void connect() throws PaymentException {
        startTime = System.nanoTime();
    }

    @Override
    public boolean pay(PaymentDetails data) throws PaymentException {
        if(fake)
            throw new PaymentException();
        paymentSystem.pay(data);
        endTime = System.nanoTime();
        return true;
    }

    @Override
    public void cancelPayment(PaymentDetails data) throws PaymentException {
        if(!fake) {
            paymentSystem.cancelPayment(data);
            endTime = System.nanoTime();
        }
    }

    public long getTime() { return (this.endTime - this.startTime) / 1000000; }

    public void setFake(boolean fake) {this.fake = fake;}

}
