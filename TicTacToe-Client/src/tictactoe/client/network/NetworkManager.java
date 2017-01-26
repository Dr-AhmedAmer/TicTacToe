/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.client.network;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import tictactoe.helpers.Utils;
import tictactoe.network.Client;
import tictactoe.network.messages.AuthMessage;
import tictactoe.network.messages.AuthResultMessage;
import tictactoe.network.messages.EndGameMessage;
import tictactoe.network.messages.GameMoveMessage;
import tictactoe.network.messages.GameRequestMessage;
import tictactoe.network.messages.GameResponseMessage;
import tictactoe.network.messages.MessageTypes;
import tictactoe.network.messages.PlayersListMessage;

/**
 *
 * @author ahmed
 */
public class NetworkManager implements Runnable{

    public interface MessageListener{
        
        void onAuthResultMessage(AuthResultMessage msg);
        void onPlayerListMessage(PlayersListMessage msg);
        void onGameRequestMessage(GameRequestMessage msg);
        void onGameResponseMessage(GameResponseMessage msg);
        void onGameMoveMessage(GameMoveMessage msg);
        void onGameEndMessage(EndGameMessage msg);
    
    }
    
    public interface ConnectionListener{
        void onConnected();
    }
    
    private static NetworkManager instance;
    
    private Thread th; 
    private BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();
    
    private Thread sendThread;
    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            
            while(isStarted){
                
                try {
                    
                    String msg = sendQueue.take();
                    boolean succcess = client.send(msg);
                    
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(NetworkManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
            
        }
    };
    
    private boolean isStarted = false;
    private boolean isConnected = false;
    
    private Client client;
    
    private ObjectMapper objectMapper;
    
    private final List<MessageListener> msgListenersList = Collections.synchronizedList(new ArrayList<MessageListener>());
    private ConnectionListener connListener;
    
    private NetworkManager(){
        
        this.objectMapper = new ObjectMapper();
    }
    
    public static synchronized NetworkManager getInstance(){
        
        if(instance == null){
            instance = new NetworkManager();
        }
        
        return instance;
        
    }
    
    public void connect(){
        
        if(isStarted)
           return; 
        
        isStarted = true;
        
        this.th = new Thread(this);
        this.th.start();
        
        this.sendThread = new Thread(this.sendRunnable);
        this.sendThread.start();
    }
    
    public void send(String type, String msg){
        
        if(this.isConnected){
            
            type ="type=" + type + "\n";
            this.sendQueue.add(type);

            msg+="\n";
            this.sendQueue.add(msg);
            
        }
        
    }
    
    public void addListener(MessageListener listener){
        this.msgListenersList.add(listener);
    }
    
    public void removeListener(MessageListener listener){
        this.msgListenersList.remove(listener);
    }
    
    public void setConnectionListener(ConnectionListener listener){
        this.connListener = listener;
    }
    
    public void onConnected(){
        
        if(connListener != null){
            connListener.onConnected();
        }
    }
    
    public void onAuthResultMsg(AuthResultMessage msg){
        
        synchronized(this.msgListenersList){
            
          for(MessageListener listener : this.msgListenersList){
              
              listener.onAuthResultMessage(msg);
              
          }  
            
        }
        
    }
    
    public void onPlayerListMsg(PlayersListMessage msg){
        
        synchronized(this.msgListenersList){
            
          for(MessageListener listener : this.msgListenersList){
              
              listener.onPlayerListMessage(msg);
              
          }  
            
        }
        
    }
    
    public void onGameRequestMsg(GameRequestMessage msg){
        
        synchronized(this.msgListenersList){
            
          for(MessageListener listener : this.msgListenersList){
              
              listener.onGameRequestMessage(msg);
              
          }  
            
        }
        
    }
    
    public void onGameResponseMsg(GameResponseMessage msg){
        
        synchronized(this.msgListenersList){
            
          for(MessageListener listener : this.msgListenersList){
              
              listener.onGameResponseMessage(msg);
              
          }  
            
        }
        
    }
    
    public void onGameMoveMsg(GameMoveMessage msg){
        
        synchronized(this.msgListenersList){
            
          for(MessageListener listener : this.msgListenersList){
              
              listener.onGameMoveMessage(msg);
              
          }  
            
        }
        
    }

    public void onGameEndMsg(EndGameMessage msg){
        
        synchronized(this.msgListenersList){
            
          for(MessageListener listener : this.msgListenersList){
              
              listener.onGameEndMessage(msg);
              
          }  
            
        }
        
    }
    @Override
    public void run() {
        
        while(this.isStarted){
            
            tryConnect();   
            
            if(this.isConnected){
                
                String type = this.client.recieve();
                String msg = this.client.recieve();
                
                if(type != null && msg != null){
                    
                    type = Utils.getType(type);
                    
                    try {
                        switch(type){

                            case MessageTypes.MSG_TYPE_AUTH:

                                AuthResultMessage authResultMsg = this.objectMapper.readValue(msg, AuthResultMessage.class);
                                
                                onAuthResultMsg(authResultMsg);
                                
                                break;
                                
                            case MessageTypes.MSG_TYPE_LIST:

                                PlayersListMessage listMsg = this.objectMapper.readValue(msg, PlayersListMessage.class);
                                
                                onPlayerListMsg(listMsg);
                                
                                break;
                                
                            case MessageTypes.MSG_TYPE_GAME_REQUEST:

                                GameRequestMessage gameReqMsg = this.objectMapper.readValue(msg, GameRequestMessage.class);
                                
                                onGameRequestMsg(gameReqMsg);
                                
                                break;
                                
                            case MessageTypes.MSG_TYPE_GAME_RESPONSE:

                                GameResponseMessage gameRespMsg = this.objectMapper.readValue(msg, GameResponseMessage.class);
                                
                                onGameResponseMsg(gameRespMsg);
                                
                                break;
                                
                            case MessageTypes.MSG_TYPE_GAME_MOVE:

                                GameMoveMessage mvMsg = this.objectMapper.readValue(msg, GameMoveMessage.class);
                                
                                onGameMoveMsg(mvMsg);
                                
                                break;
                                
                            case MessageTypes.MSG_TYPE_GAME_END:

                                EndGameMessage endMsg = this.objectMapper.readValue(msg, EndGameMessage.class);
                                
                                onGameEndMsg(endMsg);
                                
                                break;
                        }
                        
                    } catch (IOException ex) {
                            
                        Logger.getLogger(NetworkManager.class.getName()).log(Level.SEVERE, null, ex);

                    }
                }
                    
                
                
            }else{
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(NetworkManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
    }
    
    private void tryConnect(){
        
        if(this.isConnected)
            return;
        
        try {
            Socket sock = new Socket("localhost", 8080);

            this.isConnected = true;

            this.client = new Client(sock);
            
            onConnected();

        } catch (IOException ex) {
            this.isConnected = false;
            Logger.getLogger(NetworkManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
