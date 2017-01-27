
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
    
    private BufferedReader reader;
    private PrintStream printer;
       
    public Client(Socket socket){
        this.socket = socket;
        
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printer = new PrintStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public boolean send(String msg)
    {
        boolean result;
        
        this.printer.print(msg);
        
        return true;
    }
    
    
    public String recieve(){
        
        String msg = null;
        
        try {
            
            msg = this.reader.readLine();
          
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
