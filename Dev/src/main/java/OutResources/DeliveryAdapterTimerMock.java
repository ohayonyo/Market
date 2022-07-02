package OutResources;
import Exceptions.DeliveryException;

public class DeliveryAdapterTimerMock implements DeliveryAdapter {

    private DeliveryAdapter deliverySystem;
    private long startTime;
    private long endTime;
    private boolean fake;

    public DeliveryAdapterTimerMock() throws DeliveryException {
        deliverySystem = new DeliveryAdapterImpl();
        fake = false;
    }

    @Override
    public void connect() throws DeliveryException {
        startTime = System.nanoTime();
    }

    @Override
    public boolean deliver(DeliveryDetails data) throws DeliveryException {
        if(fake)
            throw new DeliveryException();
        deliverySystem.deliver(data);
        endTime = System.nanoTime();
        return true;
    }

    @Override
    public void cancelDelivery(DeliveryDetails date) throws DeliveryException {
        if(!fake) {
            deliverySystem.cancelDelivery(date);
            endTime = System.nanoTime();
        }
    }

    public long getTime() { return (this.endTime - this.startTime) / 1000000; }

    public void setFake(boolean fake) {this.fake = fake;}

}
