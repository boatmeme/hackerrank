import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class TicTacInteractive {

/* Complete the function below to print 2 integers separated by a single space which will be your next move 
   Refer section <i>Output format</i> for more details
*/
  static void nextMove(String player, String [] currBoard){
	  	Board board = new Board(player.charAt(0),currBoard);
	  	int[] result = board.getNextMove();
	  	System.out.println(result[0] + " " + result[1]);
  }
  
  public static void main(String[]args)  
  {  
   Scanner readUserInput=new Scanner(System.in);  
   
   String[] emptyBoard = {"___","___","___"};
   Board board = new Board(Board.O, emptyBoard);
   
   System.out.println("Go first? [Y/N]: ");
   String input=readUserInput.nextLine();  
   if("N".equals(input.toUpperCase())) {
	   board.addMark(Board.O,board.maximizedMove()[0]);
   }
   
   while(!board.isGameOver()) {	   
	   System.out.println(board);
	   
	   System.out.println("Enter your play [1-9, Q]: ");  
	   input=readUserInput.nextLine();  
	   if("Q".equals(input.toUpperCase())) {
		   System.out.println("Goodbye!");
		   System.exit(0);
	   }
	   int nextMove = Integer.parseInt(input);
	   if(!board.addMark(Board.X, nextMove-1)) {
		   System.out.println("Position already occupied. Try again.");
		   continue;
	   }
	   if(board.isGameOver()) {
		   break;
	   }
	   board.addMark(Board.O,board.maximizedMove()[0]);
   }
   //Print what it store in myName  
   int score = board.getScore();
   System.out.println(board);
   System.out.println(score > 0 ? "**So sorry. You lost.**" : score < 0 ? "**Congrats, Shang. You win!**" : "**The game has ended in a draw. You are a worthy opponent.**");
   System.exit(0);
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
	private int[][] suboptimalHeuristic = {
			{1,0,0,0,-1,0,0,0,1},
			{0,0,1,0,-1,0,1,0,0}
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
    
    public boolean addMark(char mark, int index) {
    	if(this.cells[index]==EMPTY) {
    		this.cells[index] = mark;
    		return true;
    	}
    	return false;
    }
    
    public String toString() {
    	//String str = ""+ player;
    	String str = "";
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
    
    public int breakTies(Integer[] ties) {
    	
    	for(int i = 0; i< ties.length; i++) {
    		char[] posCopy = Arrays.copyOf(cells,cells.length);
			posCopy[ties[i]] = player;
			
optimal: for(int j = 0;j<this.suboptimalHeuristic.length; j++) {
				int[] compareCells = suboptimalHeuristic[j];
				for(int x = 0; x<compareCells.length; x++) {
					int compare = compareCells[x];
					if(compare==0&&posCopy[x]!=EMPTY) {
						continue optimal;
					} else if (compare==1&&posCopy[x]!=player) {
						continue optimal;
					} else if (compare == -1&&posCopy[x]!=opponent) {
						continue optimal;
					}
				}
				return ties[i];
			}
    	}
    	
    	Random random = new Random();
    	return ties[random.nextInt(ties.length)];
    }
    
    public int[] maximizedMove() {
    	int[] returnArr = new int[2];
    	int bestscore = Integer.MIN_VALUE;
    	int bestmove = -2;
    	
    	// Collect ties for use in tie/breaker method
    	Map<Integer,List<Integer>> tieMap = new HashMap<Integer, List<Integer>>();
    	tieMap.put(0, new ArrayList<Integer>());
    	tieMap.put(-1, new ArrayList<Integer>());
    	tieMap.put(1, new ArrayList<Integer>());
    	
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
    			tieMap.get(score).add(i);
    			if(score>bestscore) {
        			bestscore = score;
        			bestmove = i;
        		}
    		}
    	}
    	List<Integer> ties = tieMap.get(bestscore);
    	if(ties.size()>1) {
    		bestmove = breakTies(ties.toArray(new Integer[ties.size()]));
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
        		} else if (score==bestscore && Math.random()>0.5) {
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
