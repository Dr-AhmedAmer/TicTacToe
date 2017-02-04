package gameai;

public class GameAI {

    
    public static void main(String[] args) {
      // B for blank cell
      char[][] gameBoard={{'o','x','o'},{'x','x','o'},{'o','o','x'}};
      // X represent computer and O represent opnent
      AIPlayer computerplayer=new AIMiniMax(gameBoard,'x','o');
      int[] move=computerplayer.move();
      for (int e : move)
          if (e != -1)
          System.out.println(e);
          else
              System.out.println("Game Over");
    }
    
}
 