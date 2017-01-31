
package tictactoe.network.messages;

import tictactoe.models.Player;


public class GameChatTextMessage extends Message {
    
    private String content;
    private Player sender;
   

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    
    
}
