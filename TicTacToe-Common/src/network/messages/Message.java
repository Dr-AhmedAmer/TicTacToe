
package tictactoe.network.messages;

import java.util.ArrayList;
import java.util.List;

public class Message {
    
    List<Error> errors; 

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
    
    public void addError(Error error){
        if(this.errors == null){
            this.errors = new ArrayList<Error>();
        }
        this.errors.add(error);
    }
    
}
