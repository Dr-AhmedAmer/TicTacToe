/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.client;

import tictactoe.client.network.SessionManager;
import tictactoe.models.Player;

/**
 *
 * @author ahmed
 */
public class TicTacToeClient {

    private static SessionManager.AuthListener listener = new SessionManager.AuthListener() {
        @Override
        public void onSuccess(Player p) {
            
            System.out.println("Login Success");
            System.out.println("Player email is: " + p.getEmail());
            
        }

        @Override
        public void onFailure() {
            System.out.println("Login Failed");
        }
    };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        SessionManager sMan = SessionManager.getInstance();
        
        sMan.setAuthListener(listener);
        
        sMan.login("Amer", "123");
        
        System.out.println("Sent login");
        
    }
    
}
