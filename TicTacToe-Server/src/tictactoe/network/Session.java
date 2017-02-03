
package tictactoe.network;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import tictactoe.game.GamePlayer;
import tictactoe.game.NetworkPlayer;
import tictactoe.helpers.AuthHelper;
import tictactoe.helpers.DBManager;
import tictactoe.helpers.PlayerHelper;
import tictactoe.helpers.ResultList;
import tictactoe.helpers.ResultObject;
import tictactoe.helpers.Utils;
import tictactoe.models.Player;
import tictactoe.network.messages.AuthMessage;
import tictactoe.network.messages.AuthResultMessage;
import tictactoe.network.messages.GameChatTextMessage;
import tictactoe.network.messages.GameMoveMessage;
import tictactoe.network.messages.GameRequestMessage;
import tictactoe.network.messages.GameResponseMessage;
import tictactoe.network.messages.MessageTypes;
import tictactoe.network.messages.PlayersListMessage;
import tictactoe.network.messages.RegisterMessage;

public class Session implements Runnable{
    
    public interface GameMessagesListener{
        void onGameMoveMessage(GamePlayer p, GameMoveMessage mvMsg);
        void onGameChatTextMessage(GamePlayer p ,GameChatTextMessage textMsg);
        void onGameEnd(GamePlayer p);
    }
    
    private BlockingQueue<String> sendQueue = new LinkedBlockingQueue<String>();
    
    private Client client;
    private boolean isStarted;
    private ObjectMapper objectMapper;
    private Thread sendThread;
    private SessionManager sessionManager;
    private DBManager dbManager;
    private Player player;
    private GamePlayer gamePlayer;
    
    public Player getPlayer() {
        return player;
    }
    
    private GameMessagesListener gameMessageListener;
    
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
        this.gamePlayer = new NetworkPlayer(this);
        
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
        onGameEnd();
            
    }
    
    public boolean isStarted(){
        return isStarted;
    }
    
    public GamePlayer getGamePlayer(){
        return this.gamePlayer;
    }
    
    public void send(String type,String msg){
        
        type ="type=" + type + "\n";
        this.sendQueue.add(type);
        
        msg+="\n";
        this.sendQueue.add(msg);
    }
    
    public void setGameMessageListener(GameMessagesListener listener){
        
        this.gameMessageListener = listener;
    
    }
    
    public void onGameMoveMessage(GameMoveMessage mvMsg){
        
        if(this.gameMessageListener != null){
            
            this.gameMessageListener.onGameMoveMessage(this.gamePlayer, mvMsg);
            
        }
    }
    
     public void onGameChatTextMessage(GameChatTextMessage textMsg){
        
        if(this.gameMessageListener != null){
            
            this.gameMessageListener.onGameChatTextMessage(this.gamePlayer, textMsg);
            
        }
    }
     public void onGameEnd(){
        
        if(this.gameMessageListener != null){
            
            this.gameMessageListener.onGameEnd(this.gamePlayer);
            
        }
    }
      
    
    public void run(){
        
        while(isStarted){
            
            String type = this.client.recieve();
            String msg = this.client.recieve();
              
                
            if(type != null && msg != null){

                type = Utils.getType(type);
                
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
                                
                                AuthResultMessage authResultMsg = new AuthResultMessage();
                                authResultMsg.setErrors(authResult.getErrors());
                                authResultMsg.setPlayer(authResult.getResult());

                                this.send(MessageTypes.MSG_TYPE_AUTH,this.objectMapper.writeValueAsString(authResultMsg));

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

                                
                                AuthResultMessage regResultMsg = new AuthResultMessage();
                                regResultMsg.setErrors(regResult.getErrors());
                                regResultMsg.setPlayer(regResult.getResult());
                                
                                this.send(MessageTypes.MSG_TYPE_AUTH,this.objectMapper.writeValueAsString(regResultMsg));

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
                                
                            case MessageTypes.MSG_TYPE_GAME_REQUEST:
                                
                                GameRequestMessage gameRequest = this.objectMapper.readValue(msg, GameRequestMessage.class);
                                
                                if(PlayerHelper.checkAvaliablePlayer(gameRequest.getSenderId(), gameRequest.getReciverId())){
                                    
                                 sessionManager.getSessionByPlayerId(gameRequest.getReciverId())
                                    .send(MessageTypes.MSG_TYPE_GAME_REQUEST, this.objectMapper.writeValueAsString(gameRequest));
                                }
                                 break;
                                 
                            case MessageTypes.MSG_TYPE_GAME_RESPONSE:
                                
                                GameResponseMessage gameResponse = this.objectMapper.readValue(msg, GameResponseMessage.class);
                                
                                 gameResponse.setSymbol("O");
                                 sessionManager.getSessionByPlayerId(gameResponse.getReciverId())
                                    .send(MessageTypes.MSG_TYPE_GAME_RESPONSE, this.objectMapper.writeValueAsString(gameResponse));
                                 
                                 if(gameResponse.getResponse() == 0){
                                     
                                     gameResponse.setSymbol("X");
                                     this.send(MessageTypes.MSG_TYPE_GAME_RESPONSE,
                                            this.objectMapper.writeValueAsString(gameResponse));
                                     
                                    int tempId = gameResponse.getReciverId();
                                    gameResponse.setReciverId(gameResponse.getSenderId());
                                    gameResponse.setSenderId(tempId);
                                    
        
                                    sessionManager.getSessionByPlayerId(gameResponse.getReciverId())
                                    .send(MessageTypes.MSG_TYPE_GAME_RESPONSE, this.objectMapper.writeValueAsString(gameResponse));
                                    
                                     Session receiver = sessionManager.getSessionByPlayerId(gameResponse.getSenderId());
                                     
                                     new Game(3, receiver.getGamePlayer() ,this.gamePlayer).start();
                                     
                                 }else{
                                     sessionManager.getSessionByPlayerId(gameResponse.getReciverId()).send(MessageTypes.MSG_TYPE_GAME_RESPONSE,
                                            this.objectMapper.writeValueAsString(gameResponse));
                                 }
                                     
                                 
                                 break;
                            
                            case MessageTypes.MSG_TYPE_GAME_MOVE:
                                
                                GameMoveMessage gameMvMessage = this.objectMapper.readValue(msg, GameMoveMessage.class);
                                System.out.println("tictactoe.network.Session.run()");
                                this.onGameMoveMessage(gameMvMessage);
                                
                                break;
                                
                            case MessageTypes.MSG_TYPE_CHAT_TEXT:
            
                                GameChatTextMessage gameChatTextMessage = this.objectMapper.readValue(msg, GameChatTextMessage.class);
                               
                                this.onGameChatTextMessage(gameChatTextMessage);
                                
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
