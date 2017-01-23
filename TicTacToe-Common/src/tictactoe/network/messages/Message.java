
package tictactoe.network.messages;

import java.util.ArrayList;
import java.util.List;

public class Message {
    
    private List<Error> errors = new ArrayList<>(); 

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
    
    public void addError(Error error){
        this.errors.add(error);
    }
    
}
