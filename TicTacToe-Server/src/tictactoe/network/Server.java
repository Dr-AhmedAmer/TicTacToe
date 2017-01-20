
package tictactoe.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable{
    
    private int port;
    private ServerSocket serverSocket;
    private boolean isConnected;
    private Thread th;
    private boolean isStarted;
            
    public Server(int port){
        this.port = port;     
    }
    
    public void stop(){
       isStarted = false;
        try {
            this.serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void start(){
        if (!isStarted){
            this.th = new Thread(this);
            this.th.start();
            isStarted = true;
        }
    }
    
    public void run(){
        connectSocket();
        
        while (isStarted){
            if(this.isConnected)
            {
                try {
                    Socket socket = serverSocket.accept();
                    
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    this.isConnected = false;
                }
            }else {
                try {
                    Thread.sleep(10*1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                connectSocket();
            }
        }
        
    }
    private void connectSocket(){
        
        try{
            this.serverSocket = new ServerSocket(port);
            this.isConnected = true;
        }catch(IOException ex){
            ex.printStackTrace();
            this.isConnected = false;
        }
    }
}
