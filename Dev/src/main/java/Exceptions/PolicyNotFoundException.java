package Exceptions;

public class PolicyNotFoundException extends Exception{

    private int policyID;

    public PolicyNotFoundException(int policyID){
        this.policyID=policyID;
    }

    public PolicyNotFoundException(){
        this.policyID=-1;
    }
    @Override
    public String toString() {
        if(policyID>0)
            return "Policy with id "+policyID +" not found";
        return "PolicyNotFoundException";
    }
}
