
package tictactoe.client.network;

import java.io.IOException;
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
    
    private static SessionManager instance;
    
    private NetworkManager netMan;
    
    private ObjectMapper objectMapper;
    
    private AuthListener authListner;
    
    private boolean isLogging = false;
    
    private String username;
    private String password;
    
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
            onAuthSuccess(msg.getPlayer());
        }
        
    }

    @Override
    public void onPlayerListMessage(PlayersListMessage msg) {
        
    }

    @Override
    public void onGameRequestMessage(GameRequestMessage msg) {
        
    }

    @Override
    public void onGameResponseMessage(GameResponseMessage msg) {
        
    }

    @Override
    public void onGameMoveMessage(GameMoveMessage msg) {
        
    }

    @Override
    public void onGameEndMessage(EndGameMessage msg) {
        
    }
    
}
