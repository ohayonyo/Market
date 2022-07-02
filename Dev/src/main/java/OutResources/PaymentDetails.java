package OutResources;

public class PaymentDetails {





    private final String card_number;
    private final int month;
    private final int year;
    private final String holder;
    private final String ccv;
    private final String id;
    private final double price;

    private int transactionId;
    private boolean paid;

    public int getTransactionId() {
        return transactionId;
    }

    public boolean isPaid() {
        return paid;
    }

    public double getPrice() {
        return price;
    }

    public String getCard_number() {
        return card_number;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getHolder() {
        return holder;
    }

    public String getCcv() {
        return ccv;
    }

    public String getId() {
        return id;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setPaid() {
        this.paid = true;
    }

    public void setNotPaid() {
        this.paid = false;
    }

    public PaymentDetails(String card_number, int month, int year, String holder, String ccv, String id, double toatalPrice) {
        this.card_number = card_number;
        this.month = month;
        this.year = year;
        this.holder = holder;
        this.ccv = ccv;
        this.id = id;
        this.price = toatalPrice;
    }


    public PaymentDetails() {
        this.card_number = "";
        this.month = 0;
        this.year = 0;
        this.holder = "";
        this.ccv = "";
        this.id = "";
        this.price = 0;
    }
}
