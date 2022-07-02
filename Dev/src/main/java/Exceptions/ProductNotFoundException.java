package Exceptions;

public class ProductNotFoundException extends Exception {
    private final int productId;
    private final int storeId;
    public ProductNotFoundException(int productId, int storeId)
    {
        this.productId = productId;
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "ProductNotFoundException: store ID - " + storeId + ", product ID - " + productId;
    }

}
