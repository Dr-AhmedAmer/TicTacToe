
package tictactoe.helpers;

public class ResultObject<T> extends Result {
    
    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
    
}
