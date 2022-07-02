package Exceptions;

import java.time.LocalTime;

public class IllegalTimeRangeException extends Exception{
    private final LocalTime start;
    private final LocalTime end;

    public IllegalTimeRangeException(LocalTime start,LocalTime end){
        this.start=start;
        this.end=end;
    }

    public String toString() {
        return "IllegalTimeRangeException start:" + start.toString() + " end:" + end.toString();
    }

}
