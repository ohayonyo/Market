package DAL;



public class Operation<T> {
    public enum Type {
        PERSISTENCE,
        MERGE
    }
    private T elem;
    private Type type;

    public Operation(T elem,Type type){
        this.elem=elem;
        this.type=type;
    }

    public Type getType(){
        return type;
    }

    public T getElem(){
        return elem;
    }
}
