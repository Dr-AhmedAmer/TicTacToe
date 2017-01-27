
package tictactoe.client.network;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import tictactoe.models.Player;
import tictactoe.network.messages.AuthMessage;
import tictactoe.network.messages.AuthResultMessage;
import tictactoe.network.messages.EndGameMessage;
import tictactoe.network.messages.GameMoveMessage;
import tictactoe.network.messages.GameRequestMessage;
import tictactoe.network.messages.GameResponseMessage;
import tictactoe.network.messages.MessageTypes;
import tictactoe.network.messages.PlayersListMessage;

public class SessionManager implements NetworkManager.ConnectionListener, NetworkManager.MessageListener {
    
    public interface AuthListener{
        void onSuccess(Player p);
        void onFailure();
    } 
    
    public interface GameMessageListener{
        
        void onGameMove(int x, int y);
        void onGameEnd(String winner);
        
    }
    
    public interface GameControlListener{
        void onGameRequest(int senderId);
        void onGameResponse(int senderId, int response);
        void onPlayerList(List<Player> players);
    }
    
    private static SessionManager instance;
    
    private NetworkManager netMan;
    
    private ObjectMapper objectMapper;
    
    private AuthListener authListner;
    private GameMessageListener gameListener;
    private GameControlListener gameControlListener;
    
    private boolean isLogging = false;
    
    private String username;
    private String password;
    
    private Player player;
    
    private SessionManager(){
        netMan = NetworkManager.getInstance();
        objectMapper = new ObjectMapper();
        
        netMan.setConnectionListener(this);
        netMan.addListener(this);
        
    }
    
    public synchronized static SessionManager getInstance(){
        if(instance == null){
            instance = new SessionManager();
        }
        
        return instance;
    }
    
    public void sendGameMove(int x, int y){
        
        GameMoveMessage msg = new GameMoveMessage();
        
        msg.setX(x);
        msg.setY(y);
        
        try {
            netMan.send(MessageTypes.MSG_TYPE_GAME_MOVE, objectMapper.writeValueAsString(msg));
        } catch (IOException ex) {
            Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void sendListPlayers(){
        
        netMan.send(MessageTypes.MSG_TYPE_LIST, "");
        
    }
    
    public void sendInvite(int userId){
        
        GameRequestMessage msg = new GameRequestMessage();
        
        msg.setSenderId(this.player.getId());
        msg.setReciverId(userId);
        
        try {
            netMan.send(MessageTypes.MSG_TYPE_GAME_REQUEST, objectMapper.writeValueAsString(msg));
        } catch (IOException ex) {
            Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendResponse(int receiverId, int response){
        
        GameResponseMessage msg = new GameResponseMessage();
        
        msg.setSenderId(this.player.getId());
        msg.setReciverId(receiverId);
        msg.setResponse(response);
        
        try {
            netMan.send(MessageTypes.MSG_TYPE_GAME_RESPONSE, objectMapper.writeValueAsString(msg));
        } catch (IOException ex) {
            Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void login(String username, String password){
        
        if(isLogging)
            return;
        
        isLogging = true;
        
        this.username = username;
        this.password = password;
        
        netMan.connect();
        
    }
    
    public void setAuthListener(AuthListener listener){
        this.authListner = listener;
    }

    private void onAuthSuccess(Player p){
        
        if(authListner != null){
            authListner.onSuccess(p);
        }
        
    }
    
    private void onAuthFailure(){
        
        isLogging = false;
        
        if(authListner != null){
            authListner.onFailure();
        }
        
    }
    
    public void setGameListener(GameMessageListener listener){
        
        this.gameListener = listener;
        
    }
    
    private void onGameMove(GameMoveMessage msg){
        
        if(gameListener != null){
            gameListener.onGameMove(msg.getX(), msg.getY());
        }
    }
    
    private void onGameEnd(EndGameMessage msg){
        
        if(gameListener != null){
            gameListener.onGameEnd(msg.getStatus());
        }
        
    }
    
    
    public void setGameControlListener(GameControlListener listener){
        this.gameControlListener = listener;
    }
    
    public void onGameRequest(GameRequestMessage msg){
        
        if(gameControlListener != null){
            
            gameControlListener.onGameRequest(msg.getSenderId());
            
        }
        
    }
    
    public void onGameResponse(GameResponseMessage msg){
        
        if(gameControlListener != null){
            
            gameControlListener.onGameResponse(msg.getSenderId(), msg.getResponse());
            
        }
        
    }
    
    public void onPlayerList(PlayersListMessage msg){
        
        if(gameControlListener != null){
            
            gameControlListener.onPlayerList(msg.getPlayerList());
            
        }
        
    }
    
    @Override
    public void onConnected() {
        
        AuthMessage msg = new AuthMessage();
        
        msg.setUserName(this.username);
        msg.setPassword(this.password);
        
        try {
            netMan.send(MessageTypes.MSG_TYPE_AUTH, objectMapper.writeValueAsString(msg));
        } catch (IOException ex) {
            Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void onAuthResultMessage(AuthResultMessage msg) {
        
        if(msg.getErrors().size() > 0){
            onAuthFailure();
        }else{
            
            Player p = msg.getPlayer();
            
            onAuthSuccess(p);
            
            this.player = p;
            
        }
        
    }

    @Override
    public void onPlayerListMessage(PlayersListMessage msg) {
        onPlayerList(msg);
    }

    @Override
    public void onGameRequestMessage(GameRequestMessage msg) {
        onGameRequest(msg);
    }

    @Override
    public void onGameResponseMessage(GameResponseMessage msg) {
        onGameResponse(msg);
    }

    @Override
    public void onGameMoveMessage(GameMoveMessage msg) {
        onGameMove(msg);
    }

    @Override
    public void onGameEndMessage(EndGameMessage msg) {
        onGameEnd(msg);
    }
    
}
