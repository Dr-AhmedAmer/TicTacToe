///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package tictactoe.client;
//
//import java.util.List;
//import java.util.Scanner;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import tictactoe.client.game.GameManager;
//import tictactoe.client.network.SessionManager;
//import tictactoe.models.Player;
//
///**
// *
// * @author ahmed
// */
//public class TicTacToeClient {
//
//    private static boolean isMyTurn = false;
//    private static boolean isFirst = false;
//    
//    private static SessionManager sMan;
//    
//    private static GameManager.GameListener gameListener = new GameManager.GameListener() {
//        @Override
//        public void onGameMove(int x, int y) {
//            System.out.println("Opponent moved to x: "+x+" y: "+y);
//            
//            isMyTurn = true;
//            
//             System.out.println("Enter x: ");
//            Scanner s = new Scanner(System.in);
//
//            int new_x = s.nextInt();
//
//            System.out.println("Enter y: ");
//
//            int new_y = s.nextInt();
//
//            GameManager.getInstance().move(new_x, new_y);
//            
//        }
//
//        @Override
//        public void onGameEnd(String winner) {
//            System.out.println(winner);
//        }
//    };
//    
//    private static SessionManager.GameControlListener gameControlListener = new SessionManager.GameControlListener() {
//        @Override
//        public void onGameRequest(int senderId) {
//            
//            System.out.println("Game request from user id: "+senderId);
//            System.out.println("Enter response: ");
//            
//            Scanner s = new Scanner(System.in);
//            
//            int response = s.nextInt();
//            
//            sMan.sendResponse(senderId, response);
//        }
//
//        @Override
//        public void onGameResponse(int senderId, int response) {
//            
//            if(response == 0){
//                
//               GameManager gMan = GameManager.getInstance();
//               gMan.setGameListener(gameListener);
//               
//               gMan.startGame();
//                
//               if(isFirst){
//                   isMyTurn = true;
//                    
//                    
//                    System.out.println("Enter x: ");
//                    Scanner s = new Scanner(System.in);
//
//                    int x = s.nextInt();
//
//                    System.out.println("Enter y: ");
//
//                    int y = s.nextInt();
//
//                    GameManager.getInstance().move(x, y);
//               }
//               
//            }
//            
//        }
//
//        @Override
//        public void onPlayerList(List<Player> players) {
//            
//            for(Player player : players){
//                
//                System.out.println("Player Id: "+player.getId());
//            }
//            
//            System.out.println("Would u like to invite:");
//            
//            Scanner s = new Scanner(System.in);
//            
//            int resp = s.nextInt();
//            
//            if(resp == 0){
//                
//                isFirst = true;
//                
//                System.out.println("Enter user id:");
//                
//                int userId = s.nextInt();
//                
//                sMan.sendInvite(userId);
//            }
//        }
//    };
//    
//    private static SessionManager.AuthListener listener = new SessionManager.AuthListener() {
//        @Override
//        public void onSuccess(Player p) {
//            
//            System.out.println("Login Success");
//            System.out.println("Player email is: " + p.getEmail());
//            System.out.println("Listing players...");
//            
//            sMan.setGameControlListener(gameControlListener);
//            sMan.sendListPlayers();
//            
//        }
//
//        @Override
//        public void onFailure() {
//            System.out.println("Login Failed");
//        }
//    };
//    
//    public static void main(String[] args) {
//        
//        Scanner s = new Scanner(System.in);
//        
//        System.out.println("Enter user name:");
//        
//        String username = s.nextLine();
//        
//        System.out.println("Enter password:");
//        
//        String password = s.nextLine();
//        
//        sMan = SessionManager.getInstance();
//        
//        sMan.setAuthListener(listener);
//        
//        sMan.login(username, password);
//        
//        System.out.println("Sent login");
//        
////        while(true){
////                
////            if(isMyTurn){
////
////                System.out.println("Enter x: ");
////                s = new Scanner(System.in);
////
////                int x = s.nextInt();
////
////                System.out.println("Enter y: ");
////
////                int y = s.nextInt();
////
////                GameManager.getInstance().move(x, y);
////            }else if(readyInvite){
////                
////                System.out.println("Enter user id to invite: ");
////            
////                int userId = s.nextInt();
////                
////                if(userId > 0){
////                    
////                    sMan.sendInvite(userId);
////                }
////                
////            }
////            
////            try {
////                Thread.sleep(2000);
////            } catch (InterruptedException ex) {
////                Logger.getLogger(TicTacToeClient.class.getName()).log(Level.SEVERE, null, ex);
////            }
////        }
//    }
//    
//}
