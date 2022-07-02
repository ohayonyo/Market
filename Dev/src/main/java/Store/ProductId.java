package Store;

import java.io.Serializable;
import java.util.Objects;

public class ProductId implements Serializable {
    private int productId;
    private int storeId;


    public ProductId() {

    }

    public ProductId(int productId, int storeId) {
        this.productId = productId;
        this.storeId = storeId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int id) {
        this.productId = id;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId itemId = (ProductId) o;
        return productId == itemId.productId && storeId == itemId.storeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, storeId);
    }
}