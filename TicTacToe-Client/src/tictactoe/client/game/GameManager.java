
package tictactoe.client.game;

import org.codehaus.jackson.map.ObjectMapper;
import tictactoe.client.network.SessionManager;
import tictactoe.network.messages.EndGameMessage;
import tictactoe.network.messages.GameMoveMessage;

public class GameManager implements SessionManager.GameMessageListener{
   
    public interface GameListener{
        void onGameMove(int x, int y);
        void onGameEnd(String winner);
    }
    
    private static GameManager instance;
    
    private SessionManager sessionManager;
    
    private GameListener gameListener;
    
    private GameManager(){
        sessionManager = SessionManager.getInstance();
    }
    
    public static synchronized GameManager getInstance(){
        
        if(instance == null){
            instance = new GameManager();
        }
        
        return instance;
    }
    
    
    public void startGame(){
        
        sessionManager.setGameListener(this);
        
    }
    
    public void move(int x, int y){
        
        
        sessionManager.sendGameMove(x, y);
        
    }

    public void setGameListener(GameListener listener){
        this.gameListener = listener;
    }
    
    private void onGameMoveCallback(int x, int y){
        
        if(gameListener != null){
            gameListener.onGameMove(x, y);
        }
    }
    
    private void onGameEndCallback(String winner){
        
        if(gameListener != null){
            gameListener.onGameEnd(winner);
        }
        
    }
    
    @Override
    public void onGameMove(int x, int y) {
        onGameMoveCallback(x, y);
    }

    @Override
    public void onGameEnd(String winner) {
        onGameEndCallback(winner);
    }
    
}
