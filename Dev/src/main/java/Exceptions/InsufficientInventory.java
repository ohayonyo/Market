package Exceptions;

public class InsufficientInventory extends Exception {
    final String storeName;
    final String productName;

    public InsufficientInventory(String storeName, String productName)
    {
        this.productName = productName;
        this.storeName = storeName;
    }

    @Override
    public String toString() {
        return "InsufficientInventory: store - " + storeName + ", product name - " + productName;
    }

}
