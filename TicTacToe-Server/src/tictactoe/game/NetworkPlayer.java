/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.game;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import tictactoe.helpers.DBManager;
import tictactoe.network.Game;
import tictactoe.network.Session;
import tictactoe.network.messages.EndGameMessage;
import tictactoe.network.messages.GameChatTextMessage;
import tictactoe.network.messages.GameMoveMessage;
import tictactoe.network.messages.MessageTypes;

/**
 *
 * @author ahmed
 */
public class NetworkPlayer implements GamePlayer {
    
    private Session session;
    private DBManager dbManager;
    private ObjectMapper objectMapper;
    
    public NetworkPlayer(Session session){
        this.session = session;
        this.dbManager = DBManager.getInstance();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public void move(int x, int y) {
        
        GameMoveMessage msg = new GameMoveMessage();
        msg.setX(x);
        msg.setY(y);
        
        try {
            this.session.send(MessageTypes.MSG_TYPE_GAME_MOVE, this.objectMapper.writeValueAsString(msg));
        } catch (IOException ex) {
            Logger.getLogger(NetworkPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setGameMessageListener(Session.GameMessagesListener listener) {
        this.session.setGameMessageListener(listener);
    }

    @Override
    public void setStatus(String status) {
        
        tictactoe.models.Player player = this.session.getPlayer();
        player.setStatus(status);
        
        this.dbManager.update(player);
        
    }

    @Override
    public void end(String status) {
        EndGameMessage endMessage = new EndGameMessage();
        endMessage.setStatus(status);
                       
        try {
            this.session.send(MessageTypes.MSG_TYPE_GAME_END, this.objectMapper.writeValueAsString(endMessage));
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addPoints(int points) {
        tictactoe.models.Player player = this.session.getPlayer();
        player.addPoints(points);
        
        this.dbManager.update(player);
    }

    @Override
    public void sendChatTextMessage(GameChatTextMessage txtMsg) {
        try {
            
            this.session.send(MessageTypes.MSG_TYPE_CHAT_TEXT, this.objectMapper.writeValueAsString(txtMsg));
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isStarted() {
        return this.session.isStarted();
    }
    
}
