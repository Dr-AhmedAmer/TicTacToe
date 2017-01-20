
package tictactoe.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable{
    
    private Socket socket;
    private Thread th;
    
    public Client(Socket socket){
        this.socket = socket;
        th = new Thread(this);
        th.start();
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
    
    public void run(){
        
    }
    
    
    
}
