
package tictactoe.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import tictactoe.models.Player;
import tictactoe.network.Server;

public class TicTacToeServer {

    public static void main(String[] args) {
        
        new Server(8080).start();
          
        
    }
    
}
