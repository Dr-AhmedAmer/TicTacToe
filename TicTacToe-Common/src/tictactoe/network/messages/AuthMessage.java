
package tictactoe.network.messages;

public class AuthMessage extends Message {
    
    private String userName;
    private String password;
    
    public AuthMessage(){}

    public String getUserName() {
         String userName = this.userName;
        if (userName != null){
            userName = userName.toLowerCase();
        }
        return userName;
    }

    public void setUserName(String userName) {
        
        if (userName != null){
            userName = userName.toLowerCase();
        }
        
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
