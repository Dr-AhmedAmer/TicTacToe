
package tictactoe.network.messages;


public class RegisterMessage {
    
    private String email;
    private String displayName;
    private String password;  

     public RegisterMessage(){}
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }  
    
    
}
