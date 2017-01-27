
package tictactoe.helpers;


public class Error {

    private String description;
    
    public Error(){
        
    }
    
    public Error(String description){
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
