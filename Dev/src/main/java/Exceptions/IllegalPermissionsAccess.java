package Exceptions;

public class IllegalPermissionsAccess extends Exception{
    private String message;
    public IllegalPermissionsAccess(){
        this.message="IllegalPermissionsAccess";
    }
    public IllegalPermissionsAccess(String msg){
        this.message=msg;
    }

    @Override
    public String toString() {
        return message;
    }
}
