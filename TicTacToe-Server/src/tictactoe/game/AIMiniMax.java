package tictactoe.game;

import java.util.ArrayList;
import java.util.List;
import tictactoe.models.Player;
import tictactoe.network.Game;
import tictactoe.network.Session;
import tictactoe.network.messages.GameChatTextMessage;
import tictactoe.network.messages.GameMoveMessage;


public class AIMiniMax extends AIPlayer {
    
    private Session.GameMessagesListener gameListener;
    
    public AIMiniMax(char[][] board,char comseed,char oppseed)
    {
        super(board,comseed,oppseed);
    }
    
    
    @Override
    public int[] move()
    {
        int[] selectedMove=minimax(2,comSeed);
        return new int[] {selectedMove[1],selectedMove[2]};
    }
    
    private int[] minimax(int depth,char seed)
    {
        List<int[]> emptyCells=getEmptyCells();
        
        int bestScore,currentScore;
        int bestRow=-1;
        int bestCol=-1;
        
        if (seed == comSeed)
            bestScore=Integer.MIN_VALUE;
        else
            bestScore=Integer.MAX_VALUE;
       
        if (emptyCells.isEmpty()||depth==0)
        {
            bestScore=evaluate();
        }
        else
        {
            for (int[] cell : emptyCells)
            {
                Board[cell[0]][cell[1]]=seed;
                if (seed == comSeed)
                {
                    currentScore=minimax(depth-1,oppSeed)[0];
                    if(currentScore>bestScore)
                    {
                        bestScore=currentScore;
                        bestRow=cell[0];
                        bestCol=cell[1];
                    }
                }
                else
                {
                    currentScore=minimax(depth-1,comSeed)[0];
                    if(currentScore<bestScore)
                    {
                        bestScore=currentScore;
                        bestRow=cell[0];
                        bestCol=cell[1];
                    }                       
                }
             Board[cell[0]][cell[1]]='B';
            }
        }
        
        return new int[] {bestScore,bestRow,bestCol};
        
    }
    
    private List<int[]> getEmptyCells()
    {
        List<int[]> emptyCells=new ArrayList<int[]>();
        
        if(hasWon(comSeed)||hasWon(oppSeed))
        {
            return emptyCells;
        }
        for (int row=0;row<3;row++)
        {
            for(int col=0;col<3;col++)
            {
                if (Board[row][col]=='B')
                {
                    emptyCells.add(new int[] {row,col});
                }
            }
        }
        return emptyCells;
    }
    
    final int[] winingPatterns={0b111000000, 0b000111000, 0b000000111,0b100100100, 0b010010010, 0b001001001,0b100010001, 0b001010100};
    
    private boolean hasWon(char seed)
    {
        int pattern=0b000000000;
        for (int row=0;row<3;row++)
        {
            for(int col=0;col<3;col++)
            {
                if (Board[row][col]==seed)
                {
                    pattern |=(1<<(row*3+col));
                }
            }
        }
        
        for (int winingPattern : winingPatterns)
        {
              if ((pattern & winingPattern)== winingPattern)
                  return true;
        }
        return false;
    }
    
    private int evaluate()
    {
        int score =0;
        score+=evaluateLine(0,0,0,1,0,2);
        score+=evaluateLine(1,0,1,1,1,2);
        score+=evaluateLine(2,0,2,1,2,2);
        
        score+=evaluateLine(0,0,1,0,2,0);
        score+=evaluateLine(0,1,1,1,2,1);
        score+=evaluateLine(0,2,1,2,2,2);
        
        score+=evaluateLine(0,0,1,1,2,2);
        score+=evaluateLine(0,2,1,1,2,0);
        
        return score;
    }
    
    private int evaluateLine(int row1,int col1,int row2, int col2,int row3,int col3)
    {
        int score=0;
        if (Board[row1][col1] != comSeed)
        {
            if(Board[row1][col1] == oppSeed)
            {
                score=-1;
            }
        }
        else 
        {
            score=1;
        }
        
        if (Board[row2][col2] == comSeed)
        {
            if (score ==1) score=10;
            else if (score==-1) return 0;
            else score=1;
        }
        else if (Board[row2][col2]== oppSeed)
        {
            if (score ==-1) score=-10;
            else if (score==1) return 0;
            else score=-1;
        }
        
        if (Board[row3][col3] == comSeed)
        {
            if (score >0) score*=10;
            else if (score<0) return 0;
            else score=1;
        }
        else if (Board[row3][col3]== oppSeed)
        {
            if (score <0) score*=10;
            else if (score>1) return 0;
            else score=-1;
        }
        return score;        
    }

    @Override
    public void move(Game.cellState[][] gameBoard, int x, int y) {
        
        for(int i = 0; i < gameBoard.length; i++){
            
            Game.cellState[] row = gameBoard[i];
            
            for(int j = 0; j < row.length; j++){
                
                Game.cellState cell = row[j];
                
                switch (cell) {
                    case X:
                        
                        this.Board[i][j] = 'x';
                        
                        break;
                    case O:
                        
                        this.Board[i][j] = 'o';
                        
                        break;
                    case Blank:
                        
                        this.Board[i][j] = 'B';
                        
                        break;
                
                }
            
            }   
        }
        
        int[] move = this.move();
        
        GameMoveMessage mvMsg = new GameMoveMessage();
        mvMsg.setX(move[0]);
        mvMsg.setY(move[1]);
        
        this.gameListener.onGameMoveMessage(this, mvMsg);
    }

    @Override
    public void end(String status) {
        
    }

    @Override
    public void sendChatTextMessage(GameChatTextMessage txtMsg) {
        
        GameChatTextMessage aiMsg= new GameChatTextMessage();
        
        Player p = new Player();
        p.setDisplayName("JARVIS");
        p.setEmail("jarvis@ai.no.where");
        p.setImage("");
        p.setPoints(Integer.MAX_VALUE);
        
        aiMsg.setSender(p);
        
        aiMsg.setContent("I am human than you can imagine!!");
        
        this.gameListener.onGameChatTextMessage(this, aiMsg);
    }

    @Override
    public void addPoints(int points) {
        
    }

    @Override
    public void setGameMessageListener(Session.GameMessagesListener listener) {
        this.gameListener = listener;
    }

    @Override
    public void setStatus(String status) {
        
    }

    @Override
    public boolean isStarted() {
        return true;
    }
}
