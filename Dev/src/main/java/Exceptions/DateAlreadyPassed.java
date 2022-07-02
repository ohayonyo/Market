package Exceptions;

import Store.PolicyLimitation.SimpleLimitationPolicy.DateLimitationPolicy;

import java.time.LocalDate;

public class DateAlreadyPassed extends Exception{
    private LocalDate localDate;

    public DateAlreadyPassed(LocalDate localDate){
        this.localDate=localDate;
    }

    public String toString(){
        return "The Date:"+localDate.toString()+" already passed";
    }
}
