import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {

/* Complete the function below to print 2 integers separated by a single space which will be your next move 
   Refer section <i>Output format</i> for more details
*/
  static void nextMove(String player, String [] currBoard){
	  	Board board = new Board(player.charAt(0),currBoard);
	  	int[] result = board.getNextMove();
	  	System.out.println(result[0] + " " + result[1]);
  }
  
  public static void getMove(String turn, String[] lines) {
        String player;
        String board[] = new String[3];

        //If player is X, I'm the first player.
        //If player is O, I'm the second player.
        player = turn;

        //Read the board now. The board is a 3x3 array filled with X, O or _.
        for(int i = 0; i < 3; i++) {
            board[i] = lines[i];
        }
  		nextMove(player,board);	
    
    }
  
  public static class Board {
	public static char X = 'X';
	public static char O = 'O';
	public static char EMPTY = '_';
	private int[][] winConditions = {
			{0,1,2},
			{3,4,5},
			{6,7,8},
			{0,3,6},
			{1,4,7},
			{2,5,8},
			{0,4,8},
			{2,4,6}			
	};
    private char[] cells = new char[9];
    private char player;
    private char opponent;
    private char winner;
	  
    public Board(char playerChar, String[] boardArr) {
      player = playerChar;
      opponent = playerChar == X ? O : X;
      
      int index = 0;
      for(String str : boardArr) {
        for(char c : str.toCharArray()) {
           this.cells[index++] = c;  
        }
      }
    }
    
    public Board(char playerChar, char[] boardArr) {
	    player = playerChar;
	    opponent = playerChar == X ? O : X;
	    
	    int index = 0;
	    for(char c : boardArr) {
	         this.cells[index++] = c;  
	    }
	  }
    
    public String toString() {
    	String str = ""+ player;
    	for(int i = 0; i<cells.length; i++) {
    		if(i % 3 == 0) {
    			str+="\n";
    		}
    		str += cells[i];
    	}
    	return str;
    }
    
    public boolean isGameOver() {
    	for(int[] win : winConditions) {
    		if(cells[win[0]]!=EMPTY
    		&& cells[win[0]]==cells[win[1]]
    		&& cells[win[1]]==cells[win[2]]) {
    			if(cells[win[0]]==player) {
    				winner = player;
    			} else {
    				winner = opponent;
    			}
    			return true;
    		}
    	}
    	for(char cell : cells) {
			if(cell==EMPTY) {
				return false;
			}
		}
    	winner = EMPTY;
    	return true;
    }
    
    
    public int getScore() {
    	if(isGameOver()) {
    		if(winner==player) {
    			return 1;
    		} else if(winner==opponent) {
    			return -1;
    		}
    	}
    	return 0;
    }
    
    public int[] getNextMove() {
    	int[] returnArr = new int[2];
    	int nextMove = maximizedMove()[0];
    	int row = nextMove / 3;
    	int col = nextMove % 3;
    	returnArr[0] = row;
    	returnArr[1] = col;
    	return returnArr;
    }
    
    public int[] maximizedMove() {
    	int[] returnArr = new int[2];
    	int bestscore = -2;
    	int bestmove = -2;
    	
    	for(int i = 0;i<cells.length;i++) {
    		int score = -1;
    		
    		if(cells[i]==EMPTY) {
    			char[] posCopy = Arrays.copyOf(cells,cells.length);
    			posCopy[i] = player;
    			
    			Board nextBoard = new Board(player,posCopy);
    			if(nextBoard.isGameOver()) {
    				score = nextBoard.getScore(); 
    			} else {
    				score = nextBoard.minimizedMove()[1];
    			}
    			if(bestscore == -2 || score>bestscore) {
        			bestscore = score;
        			bestmove = i;
        		}
    		}
    	}
    	returnArr[0] = bestmove;
    	returnArr[1] = bestscore;
    	return returnArr;
    }
    
    public int[] minimizedMove() {
    	int[] returnArr = new int[2];
    	int bestscore = 2;
    	int bestmove = 2;
    	
    	for(int i = 0;i<cells.length;i++) {
    		int score = 1;
    		
    		if(cells[i]==EMPTY) {
    			char[] posCopy = Arrays.copyOf(cells,cells.length);
    			posCopy[i] = opponent;
    			
    			Board nextBoard = new Board(player,posCopy);
    			if(nextBoard.isGameOver()) {
    				score = nextBoard.getScore(); 
    			} else {
    				score = nextBoard.maximizedMove()[1];
    			}
    			if(bestscore == 2 || score<bestscore) {
        			bestscore = score;
        			bestmove = i;
        		}
    		}
    	}
    	returnArr[0] = bestmove;
    	returnArr[1] = bestscore;
    	return returnArr;
    }
  }
}
