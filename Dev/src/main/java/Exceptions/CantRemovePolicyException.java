package Exceptions;

public class CantRemovePolicyException extends Exception{
    private String s;
    public CantRemovePolicyException(String s){
        this.s=s;
    }
    @Override
    public String toString() {
        return s;
    }
}
