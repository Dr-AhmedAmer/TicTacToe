
package tictactoe.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    
    private Socket socket;
       
    public Client(Socket socket){
        this.socket = socket;
    }
    
    public boolean send(String msg)
    {
        boolean result;
        
        try {
            PrintStream ps=new PrintStream(socket.getOutputStream());
            ps.print(msg);
            result = true;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        }
        return result;
    }
    
    
    public String recieve(){
        
        String msg = null;
        
        try {
            
            BufferedReader d = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            msg = d.readLine();
          
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return msg;
        
    }
    
    public void close(){
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
