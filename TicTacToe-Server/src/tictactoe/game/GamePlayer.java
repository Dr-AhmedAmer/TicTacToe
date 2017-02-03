
package tictactoe.game;

import tictactoe.network.Session;
import tictactoe.network.messages.GameChatTextMessage;

public interface GamePlayer {
    void move(int x, int y);
    void end(String status);
    void sendChatTextMessage(GameChatTextMessage txtMsg);
    void addPoints(int points);
    void setGameMessageListener(Session.GameMessagesListener listener);
    void setStatus(String status);
    boolean isStarted();
    
}
