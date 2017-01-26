
package tictactoe.helpers;

public class Utils {
    
    public static String getType(String rawType){
        
        String type = rawType;
        
        int index = type.indexOf("=");

        if(index >= 0){

            type = type.substring(index +1);

        }
        
        return type;
    }
    
}
