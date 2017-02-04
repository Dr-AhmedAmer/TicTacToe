package gameai;

public abstract class AIPlayer{
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
