package ProductTests;

import Exceptions.IllegalNameException;
import Exceptions.IllegalPriceException;
import Store.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    private Product product;
    @BeforeEach
    void setUp() {
        product = new Product(1,5.8,"Tnuva milk 3%","Tnuva milk","Milk");
    }

    @Test
    void setDescription() throws IllegalNameException {
        assertThrows(IllegalNameException.class,()-> product.setDescription(null));
        assertThrows(IllegalNameException.class,()-> product.setDescription(""));
        product.setDescription("Tnuva milk 5%");
        assertTrue(product.getDescription().equals("Tnuva milk 5%"));
    }

    @Test
    void setName() throws IllegalNameException {
        assertThrows(IllegalNameException.class,()-> product.setName(null));
        assertThrows(IllegalNameException.class,()-> product.setName(""));
        product.setName("YOLO");
        assertTrue(product.getName().equals("YOLO"));
    }

    @Test
    void setCategory() throws IllegalNameException {
        assertThrows(IllegalNameException.class,()-> product.setCategory(null));
        assertThrows(IllegalNameException.class,()-> product.setCategory(""));
        product.setCategory("Vegan");
        assertTrue(product.getCategory().equals("Vegan"));
    }

    @Test
    void setPrice() throws IllegalPriceException{
        assertThrows(IllegalPriceException.class,()-> product.setPrice(-2.8));
        assertThrows(IllegalPriceException.class,()-> product.setPrice(0));
        product.setPrice(10.0);
        assertTrue(product.getPrice()==10.0);
    }
}