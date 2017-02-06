
package tictactoe.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tictactoe.helpers.PlayerHelper;
import tictactoe.helpers.ResultList;
import tictactoe.models.Player;
import tictactoe.network.messages.MessageTypes;
import tictactoe.network.messages.PlayersListMessage;
import org.codehaus.jackson.map.ObjectMapper;

public class SessionManager{
    
    public interface PlayerStatusListener{
        
        public void onPlayerStatusChange(List<Player> list);
    } 
    
    private static SessionManager instance;
    
    private PlayerStatusListener playerStatusListener;
    
    private HashMap<Integer, Session> sessions;
    private ObjectMapper objectMapper;
    
    private SessionManager(){
        
        this.sessions = new HashMap<Integer, Session>();
        this.objectMapper = new ObjectMapper();
        
    }
    
    public static synchronized SessionManager getInstance(){
        
        if(instance == null){
            instance = new SessionManager();
        }
        
        return instance;
        
    }
    
    public synchronized void addSession(int playerId, Session session){
        
        this.sessions.put(playerId, session);
        
    }
    
    public synchronized Session getSessionByPlayerId(int playerId){
        
        return this.sessions.get(playerId);
        
    }
    
    public synchronized void removeSession(int playerId){
        
        this.sessions.remove(playerId);
        
    }

    
    public void refreshPlayerList() {
        for (Map.Entry pair : sessions.entrySet()) {
            Session s =(Session) pair.getValue();
            System.out.println("tictactoe.network.SessionManager.onPlayerLog()");
             ResultList <Player> resultList = PlayerHelper.getIdlePlayersExceptMe(s.getPlayer().getId());
             PlayersListMessage playerListMessage = new PlayersListMessage();
             playerListMessage.setPlayerList(resultList.getResults());
             
            try {
                s.send(MessageTypes.MSG_TYPE_LIST,this.objectMapper.writeValueAsString(playerListMessage));
            } catch (IOException ex) {
                Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        onPlayerStatusChange();
    }
    
    public void onPlayerStatusChange(){
        List <Player> resultList = null;
        
        for (Map.Entry pair : sessions.entrySet()) {
            Session s =(Session) pair.getValue();
             resultList = PlayerHelper.getAllPlayers().getResults();
        }
        
        if(this.playerStatusListener != null && resultList != null){
            
            this.playerStatusListener.onPlayerStatusChange(resultList);
        }
    }
    
    public void setPlayerStatusListener(PlayerStatusListener listener)
    {
        this.playerStatusListener = listener;
    }
    
}
