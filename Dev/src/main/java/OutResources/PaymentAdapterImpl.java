package OutResources;
import Exceptions.PaymentException;
import java.io.IOException;
import java.time.LocalDate;

public class PaymentAdapterImpl implements PaymentAdapter {

    private RequestSender httpConnection;
    private final String url = "https://cs-bgu-wsep.herokuapp.com/";
    private final String method = "POST";
    private String params;
    private String msg;
    private String result;
    private int transactionId;

    public PaymentAdapterImpl() throws PaymentException {
        connect();
    }

    @Override
    public void connect() throws PaymentException {
        params = "action_type=handshake";
        msg = "";
        httpConnection = new RequestSender();

        try {
            result = httpConnection.send(url, method, params, msg);
            if(result == null || result.compareTo("OK") != 0)
                throw new PaymentException();
        }

        catch (IOException e) {
            throw new PaymentException();
        }



    }

    @Override
    public boolean pay(PaymentDetails data) throws PaymentException {
        params = "action_type=pay";
        LocalDate now = LocalDate.now();
        params += "&card_number=" + data.getCard_number();
        params += "&month=" + now.getMonthValue();
        params += "&year=" + now.getYear();
        params += "&holder=" + data.getHolder();
        params += "&ccv=" + data.getCcv();
        params += "&id=" + data.getId();
        msg = "";
        httpConnection = new RequestSender();

        try {
            result = httpConnection.send(url, method, params, msg);
            transactionId = Integer.parseInt(result);
            if(transactionId == -1)
                throw new PaymentException();
            data.setTransactionId(transactionId);
            data.setPaid();
            return true;
            //todo add socketexception
        } catch (IOException e) {
            throw new PaymentException();

        }
    }

    @Override
    public void cancelPayment(PaymentDetails data) throws PaymentException {
        params = "action_type=cancel_pay";
        params += "&transaction_id=" + data.getTransactionId();
        msg = "";
        httpConnection = new RequestSender();

        try {
            result = httpConnection.send(url,method,params,msg);
            transactionId = Integer.parseInt(result);
            if(transactionId == -1)
                throw new PaymentException();
            data.setNotPaid();
        }
        catch (IOException e){
            throw new PaymentException();
        }
    }
}
