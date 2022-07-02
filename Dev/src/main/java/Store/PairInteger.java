package Store;

import org.hibernate.annotations.Type;

import javax.persistence.*;

public class PairInteger {

        private int id;
        private static int counter = 0;
        private int key;
        private int value;

        public PairInteger(int key, int value){
            this.id = ++counter;
            this.key=key;
            this.value=value;
        }

    public PairInteger() {
        this.id=++counter;

    }

    public int getKey() {
            return key;
        }

        public int getValue(){
            return value;
        }

    public void setKey(int key) {
        this.key = key;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
