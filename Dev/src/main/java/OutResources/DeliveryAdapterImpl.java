package OutResources;

import Exceptions.DeliveryException;

import java.io.IOException;

public class DeliveryAdapterImpl implements DeliveryAdapter {

    private RequestSender httpConnection;
    private final String url = "https://cs-bgu-wsep.herokuapp.com/";
    private final String method = "POST";
    private String params;
    private String msg;
    private String result;
    private int transactionId;

    public DeliveryAdapterImpl() throws DeliveryException {
        connect();
    }

    @Override
    public void connect() throws DeliveryException {
        params = "action_type=handshake";
        msg = "";
        httpConnection = new RequestSender();
        try {
            result = httpConnection.send(url, method, params, msg);
            if(result == null || result.compareTo("OK") != 0)
                throw new DeliveryException();
        }


        catch (IOException e) {
            throw new DeliveryException();
        }
    }

    @Override
    public boolean deliver(DeliveryDetails data) throws DeliveryException {
        params = "action_type=supply";
        params += "&name=" + data.getName();
        params += "&address=" + data.getAddress();
        params += "&city=" + data.getCity();
        params += "&country=" + data.getCountry();
        params += "&zip=" + data.getZip();
        msg = "";
        httpConnection = new RequestSender();

        try {
            result = httpConnection.send(url,method,params,msg);
            transactionId = Integer.parseInt(result);
            if(transactionId == -1)
                throw new DeliveryException();
            data.setTransactionId(transactionId);
            data.setDelivered();
            return true;
        }
        catch (IOException e){
            throw new DeliveryException();
        }
    }

    @Override
    public void cancelDelivery(DeliveryDetails data) throws DeliveryException {
        params = "action_type=cancel_supply";
        params += "&transaction_id=" + data.getTransactionId();
        msg = "";
        httpConnection = new RequestSender();

        try {
            result = httpConnection.send(url,method,params,msg);
            transactionId = Integer.parseInt(result);
            if(transactionId == -1)
                throw new DeliveryException();
            data.setNotDelivered();
        }
        catch (IOException e){
            throw new DeliveryException();
        }
    }
}
