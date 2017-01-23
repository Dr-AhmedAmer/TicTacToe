
package tictactoe.network;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import tictactoe.helpers.AuthHelper;
import tictactoe.helpers.DBManager;
import tictactoe.helpers.PlayerHelper;
import tictactoe.helpers.ResultList;
import tictactoe.helpers.ResultObject;
import tictactoe.models.Player;
import tictactoe.network.messages.AuthMessage;
import tictactoe.network.messages.MessageTypes;
import tictactoe.network.messages.PlayersListMessage;
import tictactoe.network.messages.RegisterMessage;

public class Session implements Runnable{
    
    private BlockingQueue<String> sendQueue = new LinkedBlockingQueue<String>();
    
    private Client client;
    private boolean isStarted;
    private ObjectMapper objectMapper;
    private Thread sendThread;
    private SessionManager sessionManager;
    private DBManager dbManager;
    private Player player;
    
    private Runnable sendRunnable = new Runnable() {
        @Override
        public void run() {
            
            while(isStarted){
                
                try {
                    
                    String msg = Session.this.sendQueue.take();
                    boolean succcess = Session.this.client.send(msg);
                    
                    if(!succcess){
                        Session.this.stop();
                    }
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
            
        }
    };
    
    public Session(Client client){
        this.client = client;
        this.objectMapper = new ObjectMapper();
        this.sessionManager = SessionManager.getInstance();
        this.dbManager = DBManager.getInstance();
    }
    
    public void start(){
        if(!isStarted){
            
            isStarted = true;
            
            Thread th = new Thread(this);
            th.start();
            
            this.sendThread = new Thread(this.sendRunnable);
            this.sendThread.start();
            
        }
    }
    
    public void stop(){
        isStarted = false;
        this.client.close();
        this.sendThread.interrupt();
        
        if(this.player != null){
            this.sessionManager.removeSession(this.player.getId());
            
            this.player.setStatus(Player.STATUS_OFFLINE);
            this.dbManager.update(this.player);
        }
            
    }
    
    public void send(String type,String msg){
        
        type ="type=" + type + "\n";
        this.sendQueue.add(type);
        
        msg+="\n";
        this.sendQueue.add(msg);
    }
    
    public void run(){
        
        while(isStarted){
            
            String type = this.client.recieve();
            String msg = this.client.recieve();
              
                
            if(type != null && msg != null){

                int index = type.indexOf("=");

                if(index >= 0){

                    type = type.substring(index +1);

                }
                
                try {

                    if(this.player == null){
                        
                        switch(type){

                            case MessageTypes.MSG_TYPE_AUTH:

                                AuthMessage authMessage = this.objectMapper.readValue(msg, AuthMessage.class);

                                ResultObject<Player> authResult = AuthHelper.logIn(authMessage);

                                if(authResult.getErrors().isEmpty()){

                                    this.player = authResult.getResult();
                                    this.sessionManager.addSession(this.player.getId(), this);

                                    this.player.setStatus(Player.STATUS_IDLE);
                                    this.dbManager.update(this.player);


                                }

                                this.send(MessageTypes.MSG_TYPE_AUTH,this.objectMapper.writeValueAsString(authResult));

                                break;

                            case MessageTypes.MSG_TYPE_REG:

                                RegisterMessage registerMessage = this.objectMapper.readValue(msg, RegisterMessage.class);

                                ResultObject<Player> regResult = AuthHelper.register(registerMessage);

                                if(regResult.getErrors().isEmpty()){

                                    this.player = regResult.getResult();
                                    this.sessionManager.addSession(this.player.getId(), this);

                                    this.player.setStatus(Player.STATUS_IDLE);
                                    this.dbManager.update(this.player);

                                }

                                this.send(MessageTypes.MSG_TYPE_REG,this.objectMapper.writeValueAsString(regResult));

                                break;

                        }
                        
                    }else{
                        
                        switch(type){
                            
                            case MessageTypes.MSG_TYPE_LIST:

                                ResultList<Player> resultList = PlayerHelper.getIdlePlayersExceptMe(this.player.getId());
                                
                                PlayersListMessage playerListMessage = new PlayersListMessage();
                                playerListMessage.setPlayerList(resultList.getResults());
                                
                                this.send(MessageTypes.MSG_TYPE_LIST,this.objectMapper.writeValueAsString(playerListMessage));
                                
                                break;
                        }
                    }
                    

                } catch (IOException ex) {

                    Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
                }
                   
            }else{
                
                this.stop();
                System.out.println("client dropped");
                
            }
        }
        
        System.out.println("Session closed");
    }
    
}
