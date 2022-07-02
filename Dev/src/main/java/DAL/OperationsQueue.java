package DAL;

import java.util.LinkedList;
import java.util.Queue;

public class OperationsQueue {
    private Queue<Operation> operations;

    public OperationsQueue(){
        this.operations = new LinkedList();
    }

    public void addOperation(Object object, Operation.Type type){
        operations.add(new Operation(object,type));
    }

    public Queue<Operation> getOperations(){
        return  operations;
    }
}
