package Store;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class PairDouble {

    private int id;
    private double key;
    private double value;

    public PairDouble(double key, double value){
        this.key=key;
        this.value=value;
    }

    public PairDouble() {

    }

    public double getKey() {
            return key;
        }

        public double getValue(){
            return value;
        }

    public void setKey(double key) {
        this.key = key;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
