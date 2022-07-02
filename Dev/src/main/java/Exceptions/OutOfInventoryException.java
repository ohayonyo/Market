package Exceptions;

public class OutOfInventoryException extends Exception{
    private int validAmountAtStock;
    private int neededAmount;

    public OutOfInventoryException(int validAmountAtStock,int neededAmount){
        this.neededAmount=neededAmount;
        this.validAmountAtStock=validAmountAtStock;
    }

    public String toString(){
        return "OutOfInventoryException: try to buy "+neededAmount+" products but only "+validAmountAtStock+" are at stock";
    }
}
