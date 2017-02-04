package tictactoe.game;

public class GameAI {

    
    public static void main(String[] args) {
      // B for blank cell
      char[][] gameBoard={{'B','B','B'},{'B','B','B'},{'B','B','B'}};
      // O represent computer and X represent opnent
      AIPlayer computerplayer=new AIMiniMax(gameBoard,'o','x');
      int[] move=computerplayer.move();
      for (int e : move)
          if (e != -1)
          System.out.println(e);
          else
              System.out.println("Game Over");
    }
    
}
 