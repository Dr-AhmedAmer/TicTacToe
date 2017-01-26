
package tictactoe.network.messages;

import tictactoe.models.Player;

public class AuthResultMessage extends Message {
    
    private Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
}
