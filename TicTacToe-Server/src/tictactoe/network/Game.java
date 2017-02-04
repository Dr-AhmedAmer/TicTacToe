
package tictactoe.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import tictactoe.game.GamePlayer;
import tictactoe.helpers.DBManager;
import tictactoe.models.Player;
import tictactoe.network.messages.EndGameMessage;
import tictactoe.network.messages.GameChatTextMessage;
import tictactoe.network.messages.GameMoveMessage;
import tictactoe.network.messages.MessageTypes;


public class Game implements Runnable, Session.GameMessagesListener{

    private class GameMove{
        
        public GamePlayer player;
        public GameMoveMessage mvMsg;
    }
    
    private int moveCount;
    private int gridSize;
    private SessionManager sMan = SessionManager.getInstance();
    private enum cellState{Blank, X, O};
    private enum winState{X, O, Draw,NoWin};
    private  cellState [][] gameBoard;
    private GamePlayer p1;
    private GamePlayer p2;
    
    private ObjectMapper objectMapper;
    
    private DBManager dbManager;
    
    private Map<GamePlayer, String> playersSymbols = new HashMap<>();
    
    private Thread th;
    
    private boolean isStarted = false;
    
    private BlockingQueue<GameMove> queue = new LinkedBlockingQueue<GameMove>();
//    private BlockingQueue<GameTextMsg> chatQueue = new LinkedBlockingQueue<>();
    
   public Game(int gridSize, GamePlayer p1, GamePlayer p2){
        
         this.moveCount = 0;
         this.gridSize = gridSize;
         
         this.p1 = p1;
         this.p2 = p2;
         
         this.p1.setGameMessageListener(this);
         this.p2.setGameMessageListener(this);
   
         
         
         this.objectMapper = new ObjectMapper();
         this.dbManager = DBManager.getInstance();
         
         this.p1.setStatus(Player.STATUS_PLAYING);
         this.p2.setStatus(Player.STATUS_PLAYING);
         sMan.refreshPlayerList();
         
         this.playersSymbols.put(p1, cellState.X.toString());
         this.playersSymbols.put(p2, cellState.O.toString());
        
        gameBoard = new cellState [gridSize][gridSize];
         
        for(int row =0 ; row <gridSize ; row++){
            for(int col = 0 ; col<gridSize ; col++){
                this.gameBoard[row][col] = cellState.Blank;
            }
        }
    }
   
   public void start(){
       
       if(!isStarted){
           
           isStarted = true;
           
           this.th = new Thread(this);
           this.th.start();
           
       }
       
   }
   
   public void stop(){
       
       isStarted = false;
       
       this.th.interrupt();
       
       if(this.p1 != null){
           this.p1.setGameMessageListener(null);
           this.p1.setStatus(Player.STATUS_IDLE);
       }
       
       if(this.p2 != null){
           this.p2.setGameMessageListener(null);
           this.p2.setStatus(Player.STATUS_IDLE);
       }
        
        
         sMan.refreshPlayerList();
   }
   
   public void run(){
       
       while(this.isStarted){
           
           try {
               GameMove gameMv = this.queue.take();
               
               
               String symblStr = this.playersSymbols.get(gameMv.player);
               cellState symbl = cellState.valueOf(symblStr);
               
               if(updateCell(symbl, gameMv.mvMsg.getX(), gameMv.mvMsg.getY())){
                   
                   GamePlayer opponent = getOpponent(gameMv.player);
                   
                   opponent.move(gameMv.mvMsg.getX(), gameMv.mvMsg.getY());
                   
                   winState wState = checkWin(symbl, gameMv.mvMsg.getX(), gameMv.mvMsg.getY());
                   
                   if(wState == winState.O || wState == winState.X){
                       
                       gameMv.player.end("Winner");
                       
                       gameMv.player.addPoints(5);
                       
                       opponent.end("Looser");
                    
                       stop();
                       
                   }else if(wState == winState.Draw){
                       
                        this.p1.end("Draw");
                        this.p2.end("Draw");
                       
                        stop();
                   }
                   
               }
               
           } catch (InterruptedException ex) {
               Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
           }
           
           
           
       }
       
   }
   
   
    @Override
    public void onGameMoveMessage(GamePlayer p, GameMoveMessage mvMsg) {
        
        GameMove gameMv = new GameMove();
        gameMv.player = p;
        gameMv.mvMsg = mvMsg;
        
        this.queue.add(gameMv);
    }
    
     @Override
    public void onGameChatTextMessage(GamePlayer p, GameChatTextMessage textMsg) {
        
        GamePlayer opponent = this.getOpponent(p);
        opponent.sendChatTextMessage(textMsg);
    }
    
     @Override
    public void onGameEnd(GamePlayer p) {
       GamePlayer opponent = getOpponent(p);
       
       if(opponent.isStarted()){
           opponent.setStatus(Player.STATUS_IDLE);
           
       }
       sMan.refreshPlayerList();
       stop();
    }
    
    private boolean validateMove(int x , int y){
        return this.gameBoard[x][y] == cellState.Blank && x < gridSize && x >= 0 && y< gridSize && y >= 0;
    }

    public boolean updateCell(cellState state , int x ,int y){
        
        if( validateMove(x, y)){
            this.gameBoard[x][y] = state;
            this.moveCount ++;
            return true;
        }else{
            return false;
        }
    }
    
//    private void  updatePointsOfWinner(){
//        Session winner = this.ga
//    }
	
    
    public winState checkWin(cellState state , int x , int y){
        
        
        for(int i =0 ; i <this.gridSize ; i++){
            
            if(this.gameBoard[x][i] != state)
                break;
            if(i == gridSize-1)
                return winState.valueOf(state.toString());
        }
        
        for(int i =0 ; i<this.gridSize ; i++){
            
            if(this.gameBoard[i][y] != state)
                break;
            if(i == gridSize-1)
                return winState.valueOf(state.toString());
        }
        
         if(x == y){
    
            for(int i = 0; i < this.gridSize; i++){
                if(this.gameBoard[i][i] != state)
                    break;
                if(i == gridSize-1){
                    return winState.valueOf(state.toString());
                }
            }
        }
         
         if(x + y == this.gridSize-1){
             
            for(int i = 0;i<this.gridSize;i++){
                
                if(this.gameBoard[i][(this.gridSize-1)-i] != state)
                    break;
                if(i == this.gridSize-1){
                    return winState.valueOf(state.toString());
                }
            }
        }
         
        if(this.moveCount == Math.pow(this.gridSize, 2) -1)
             return winState.Draw;
        
        return winState.NoWin;
    }
    
    private GamePlayer getOpponent(GamePlayer player){
        
        GamePlayer opponent = null;
        
        if(player != this.p1){
            opponent = this.p1;
        }else if(player != this.p2){
            opponent = this.p2;
        }
        
        return opponent;
    }
}
