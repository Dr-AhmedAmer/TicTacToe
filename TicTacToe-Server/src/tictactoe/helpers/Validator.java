
package tictactoe.helpers;

public class Validator {

    public static boolean validateEmail(String email){
        return isStringEmpty(email);
    }
    
    public static boolean validateName(String name){
        return isStringEmpty(name);
    }
    
    public static boolean validatePassword(String pass){
        return isStringEmpty(pass);
    }
    
    public static boolean isStringEmpty (String s){
         return (s != null && !s.isEmpty());
    }
    
     
}
