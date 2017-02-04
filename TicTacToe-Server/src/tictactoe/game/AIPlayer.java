package tictactoe.game;

public abstract class AIPlayer implements GamePlayer{
    protected char[][] Board;
    protected char comSeed;
    protected char oppSeed;
    
    public AIPlayer(char[][] gameBoard,char comseed,char oppseed)
    {
        Board=gameBoard;
        comSeed=comseed;
        oppSeed=oppseed;
    }
    
    public abstract int[] move();
        
}
