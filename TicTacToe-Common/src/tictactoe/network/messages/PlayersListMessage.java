
package tictactoe.network.messages;

import java.util.ArrayList;
import java.util.List;
import tictactoe.models.Player;

public class PlayersListMessage extends Message {

    private List <Player> playerList = new ArrayList<>();
    
    public PlayersListMessage(){}
    
    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }
    
    public void addPlayer(Player player){
        this.playerList.add(player);
    }
    
    
    
    
}
