package Exceptions;

public class IllegalRangeException extends Exception {
    private final int min;
    private final int max;

    public IllegalRangeException(int min,int max){
        this.min=min;
        this.max=max;
    }

    public String toString() {
        return "IllegalRangeException range is:"+min+"->"+max;
    }
}
