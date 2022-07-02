package OutResources;

public class DeliveryDetails {

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getZip() {
        return zip;
    }

    private final String name;
    private final String address;
    private final String city;
    private final String country;
    private final int zip;

    private int transactionId;
    private boolean delivered;

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setDelivered() {
        this.delivered = true;
    }

    public void setNotDelivered() {
        this.delivered = false;
    }
    public int getTransactionId() {
        return transactionId;
    }

    public boolean isDelivered() {
        return delivered;
    }



    public DeliveryDetails(String name, String address, String city, String country, int zip) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.country = country;
        this.zip = zip;
        this.delivered=false;
    }

    public DeliveryDetails() {
        this.name = "";
        this.address = "";
        this.city = "";
        this.country = "";
        this.zip = 0;
    }
}
