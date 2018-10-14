package utils;

import puzzle.Board;
import puzzle.BoardState;

public class PuzzleUtils {
	
	public static final int ROWS = Integer.getInteger("board.rows", 4);
	public static final int COLS = Integer.getInteger("board.cols", 5);
	
	public static final int BOARD_SIZE = ROWS* COLS;
	
	public static final int[] MOVE_UP=new int[]{-1,0};
	public static final int[] MOVE_DOWN=new int[]{1,0};
	public static final int[] MOVE_LEFT=new int[]{0,-1};
	public static final int[] MOVE_RIGHT=new int[]{0,1};
	
	
	public enum DIRECTION{
		MOVE_UP(0,new int[]{-1,0},"MOVE UP"), MOVE_DOWN(0,new int[]{1,0},"MOVE DOWN"),
		MOVE_LEFT(0,new int[]{0,-1},"MOVE LEFT"), MOVE_RIGHT(0,new int[]{0,1},"MOVE RIGHT");

		private final int id;
		private final int[] direction;
		private final String name;
		DIRECTION(int id, int[] direction,String name){
			this.id= id;
			this.direction = direction;
			this.name= name;
		}
		public int[] getDtn() {
			// TODO Auto-generated method stub
			return this.direction;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.name();
		}
		
		
	}
	/**
	 * 
	 * @param board_vector
	 * @return
	 * 
	 * shape id 
	 * [[4,4,7,7],
	 *  [4,4,5,5],
	 *  [0,0,5,5],
	 *  [5,7,7,1],
	 *  [5,7,7,1]]
	 */
	public static boolean validateSetup(int[] board_vector){
		
		
		
		
		return false;
	}
	
	public static BoardState getBoardState(int[][] board){
		
		
		return null;
		
	}
	
	public static int[][] pharseTileCoord(String str){
		// str="0,0,1,1;2,1,2,2;...;3,1,4,1;"
		String[] diagCos =str.split(";");
		
		if( diagCos.length!=Board.TILES_NUM)
			throw new IllegalArgumentException("Missing tiles input");
		
		int[][] tileCos = new int[diagCos.length][4];
		
		for (int i=0; i<diagCos.length;i++){
			String[] co = diagCos[i].split(",");
			if (co.length!=4)
				throw new IllegalArgumentException("Invalid input"+co.length);
			for (int j=0; j<4;j++){
				tileCos[i][j]=Integer.valueOf(co[j]);
			}
			
		}
		
		return tileCos;
		
	}
}
