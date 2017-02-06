
package tictactoe.server;
import tictactoe.network.Server;

public class TicTacToeServer {

    public static void main(String[] args) {
        
        new Server(8000).start();
          
        
    }
    
}
