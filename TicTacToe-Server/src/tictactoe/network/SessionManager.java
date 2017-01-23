
package tictactoe.network;

import java.util.HashMap;

public class SessionManager {
    
    private static SessionManager instance;
    
    private HashMap<Integer, Session> sessions;
    
    private SessionManager(){
        
        this.sessions = new HashMap<Integer, Session>();
        
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
    
}
