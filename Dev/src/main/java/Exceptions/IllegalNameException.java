package Exceptions;

public class IllegalNameException extends Exception{
    private String name;

    public IllegalNameException(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        if(name==null)
            return "IllegalNameException: name can't be null";
        else if(name.equals(""))
            return "IllegalNameException: name can't be empty string";
        else
            return "IllegalNameException";
    }
}
