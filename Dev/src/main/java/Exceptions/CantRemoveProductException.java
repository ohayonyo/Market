package Exceptions;

import Store.Product;

public class CantRemoveProductException extends Exception {
    private Product product;

    public CantRemoveProductException(Product product){
        super();
        this.product = product;
    }

    public CantRemoveProductException(){
        super();
        this.product = null;
    }

    public String toString() {
        return product != null ? "CantRemoveProductException:"+product.toString() : "CantRemoveProductException";
    }
}
