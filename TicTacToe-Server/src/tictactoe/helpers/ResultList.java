
package tictactoe.helpers;

import java.util.ArrayList;
import java.util.List;

public class ResultList<T> extends Result {
    
    private List<T> results;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
    
    public void addResult(T result){
        
        if(this.results == null)
            this.results = new ArrayList<T>();
        
        this.results.add(result);
    }
    
}
