package Exceptions;

public class UserCantAppointHimself extends Exception{
    private String s;

    public UserCantAppointHimself(String s){
        this.s=s;
    }

    public UserCantAppointHimself(){
        this.s="UserCantAppointHimself";
    }

    public String toString(){
        return s;
    }
}
