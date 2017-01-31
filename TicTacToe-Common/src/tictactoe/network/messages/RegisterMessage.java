
package tictactoe.network.messages;


public class RegisterMessage extends Message {
    
    private String email;
    private String displayName;
    private String password;  
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

     public RegisterMessage(){}
    
    public String getEmail() {
        
        String email = this.email;
        if (email != null){
            email = email.toLowerCase();
        }
        return email;
    }

    public void setEmail(String email) {
        if (email != null){
            email = email.toLowerCase();
        }
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
