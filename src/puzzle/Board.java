package puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import com.sun.swing.internal.plaf.basic.resources.basic;

public class Board {
	
	public static final int ROWS = Integer.getInteger("board.rows", 5);
	public static final int COLS = Integer.getInteger("board.cols", 4);
	
	public static final int TILES_NUM = 9;
	
	public static final int boardSize = ROWS* COLS;

	public final int[][] mtx;
	
	//public final int[][] mtx_tile;
	
	public final int[] mtx_vct = new int[boardSize];
	
	//public final List<Tile> tiles = new ArrayList<>();
	
	public final Map<Integer, Tile>  tileIdMap = new HashMap<>();
	
	private String srcStateId;
	
	private String tgtStateId;
	
	private int[] zero1=new int[]{-1,-1};
	
	private int[] zero2=new int[]{-1,-1};

	public Board() {
		this.mtx= new int[ROWS][COLS];
		//mtx_tile = new int[ROWS][COLS];
	}
	
	public boolean isAllTilesInserted(){
		return tileIdMap.size()==TILES_NUM;
	}
	
	public void insertTile(Tile t, int[] upLeft, int[] lowRight){
		if (!isAbleToInsert(upLeft, lowRight))
			throw new IllegalArgumentException("Trying to insert Illegal position "+ upLeft[0]+","+upLeft[1]+" ; " + lowRight[0]+","+lowRight[1]);
		
		//insert 2x2 
		if (upLeft[0]!=lowRight[0] && upLeft[1]!= lowRight[1]){
			mtx[upLeft[0]][upLeft[1]] = t.id;
			mtx[upLeft[0]+1][upLeft[1]] =  t.id ;
			mtx[upLeft[0]][upLeft[1]+1] =  t.id ;
			mtx[lowRight[0]][lowRight[1]] =  t.id;
		}else{
			mtx[upLeft[0]][upLeft[1]] =  t.id;
			mtx[lowRight[0]][lowRight[1]] =  t.id;
		}

		t.setPosition(upLeft[0],upLeft[1], lowRight[0],lowRight[1]);
		//tiles.add(t);
		tileIdMap.put(t.id, t);
	}
	
	public boolean isAbleToInsert(int[] upLeft, int[] lowRight){
		if (!Board.isValidPosition(upLeft, lowRight))
			return false;
		
		//check 2x2
		if (upLeft[0]!=lowRight[0] && upLeft[1]!= lowRight[1]){
			if (mtx[upLeft[0]][upLeft[1]] == 0 && mtx[upLeft[0]+1][upLeft[1]] == 0 && mtx[upLeft[0]][upLeft[1]+1] == 0 && mtx[lowRight[0]][lowRight[1]] == 0 )
				return true;
		}
		
		if (mtx[upLeft[0]][upLeft[1]] == 0 && mtx[lowRight[0]][lowRight[1]] == 0 )
			return true;
		
		return false;
	}
	
	
	/*public int[] getPositionIndex(int[] upLeft, int[] lowRight){
		if (!Board.isValidPosition(upLeft, lowRight))
			throw new IllegalArgumentException(" Illegal position " );
		
		if (upLeft[0]!=lowRight[0] && upLeft[1]!= lowRight[0]){
			
			return new int[]{};
		}
		
		
		return lowRight;
		
	}*/
	
	/**
	 * 
	 * @param t
	 * @param dtn [+-1,+-1] move tile in direction row +-1|0, col+-1|0 
	 */
	
	//TODO reDO move tile
	public boolean moveTile(Tile t, int[] dtn){

		boolean canMove = false;
		try{
		if (dtn[0] != 0) {
			
			// check down
			if (dtn[0] > 0 && t.getPosition()[2] + 1 < this.ROWS) {

				canMove = mtx[t.getPosition()[2] + 1][t.getPosition()[3]] == 0;

				if(canMove){
					if (t.getPosition()[1] != t.getPosition()[3]) {
						canMove = canMove && mtx[t.getPosition()[2] + 1][t.getPosition()[1]] == 0;
						if(canMove){								
								mtx[t.getPosition()[2]+1][t.getPosition()[1]]=mtx[t.getPosition()[0]][t.getPosition()[1]];
								mtx[t.getPosition()[0]][t.getPosition()[1]]=0;
						} else
							return canMove;
					}
					
					
						mtx[t.getPosition()[2]+1][t.getPosition()[3]]=mtx[t.getPosition()[0]][t.getPosition()[3]];
						mtx[t.getPosition()[0]][t.getPosition()[3]]=0;
					
					t.setPosition(t.getPosition()[0]+1, t.getPosition()[1], t.getPosition()[2]+1, t.getPosition()[3]);
					
					/*System.out.println("tile "+t.id+" moved DOWN ");
					this.getBoardTilesId(true);*/
					//return;
				}

			} // check up
			else if (dtn[0] < 0 && t.getPosition()[0] - 1 >= 0) {
				canMove = mtx[t.getPosition()[0] - 1][t.getPosition()[1]] == 0;
				if (canMove){

					if (t.getPosition()[1] != t.getPosition()[3]) {
						canMove = canMove && mtx[t.getPosition()[0] - 1][t.getPosition()[3]] == 0;
						if(canMove){
							mtx[t.getPosition()[0]-1][t.getPosition()[3]]=mtx[t.getPosition()[2]][t.getPosition()[3]];
							mtx[t.getPosition()[2]][t.getPosition()[3]]=0;
						} else
							return canMove;

					}
					
					mtx[t.getPosition()[0]-1][t.getPosition()[1]]=mtx[t.getPosition()[2]][t.getPosition()[1]];
					mtx[t.getPosition()[2]][t.getPosition()[1]]=0;
					

					t.setPosition(t.getPosition()[0]-1, t.getPosition()[1], t.getPosition()[2]-1, t.getPosition()[3]);
					/*System.out.println("tile "+t.id+" moved UP ");
					this.getBoardTilesId(true);*/
					//return;
				}
			}
		} else if (dtn[1] != 0) {
			// check right
			if (dtn[1] > 0 && t.getPosition()[3] + 1 < this.COLS) {

				canMove = mtx[t.getPosition()[2]][t.getPosition()[3] + 1] == 0;
				if(canMove){
					if (t.getPosition()[0] != t.getPosition()[2]) {
						canMove = canMove && mtx[t.getPosition()[0]][t.getPosition()[3] + 1] == 0;
						if(canMove){
							mtx[t.getPosition()[0]][t.getPosition()[3]+1]=mtx[t.getPosition()[0]][t.getPosition()[1]];
							mtx[t.getPosition()[0]][t.getPosition()[1]]=0;
						}else
							return canMove;
						
					}
					
					mtx[t.getPosition()[2]][t.getPosition()[3]+1]=mtx[t.getPosition()[2]][t.getPosition()[1]];
					mtx[t.getPosition()[2]][t.getPosition()[1]]=0;

					t.setPosition(t.getPosition()[0], t.getPosition()[1]+1, t.getPosition()[2], t.getPosition()[3]+1);
					/*System.out.println("tile "+t.id+" moved RIGHT ");
					this.getBoardTilesId(true);*/
					//return;
				}
			} // check left
			else if (dtn[1] < 0 && t.getPosition()[1] - 1 >= 0) {

				canMove = mtx[t.getPosition()[0]][t.getPosition()[1] - 1] == 0;
				
				if(canMove){
					if (t.getPosition()[0] != t.getPosition()[2]) {
						canMove = canMove && mtx[t.getPosition()[2]][t.getPosition()[1] - 1] == 0;
						if(canMove){
							mtx[t.getPosition()[2]][t.getPosition()[1]-1]=mtx[t.getPosition()[2]][t.getPosition()[3]];
							mtx[t.getPosition()[2]][t.getPosition()[3]]=0;
						} else
							return canMove;
					}
					
					mtx[t.getPosition()[0]][t.getPosition()[1]-1]=mtx[t.getPosition()[0]][t.getPosition()[3]];
					mtx[t.getPosition()[0]][t.getPosition()[3]]=0;
					
					t.setPosition(t.getPosition()[0], t.getPosition()[1]-1, t.getPosition()[2], t.getPosition()[3]-1);
					/*System.out.println("tile "+t.id+" moved LEFT ");
					this.getBoardTilesId(true);*/
					//return;
				}
			}
		}
		} catch (ArrayIndexOutOfBoundsException e){
			System.err.println(" Arr out of bound, tile id "+ t.id+" direction "+dtn[0]+","+dtn[1]);
			this.getStateOrTileId(true, false);
			e.printStackTrace();
			throw e;
		} 
			return canMove;
		
		
		
		
	}
	
	public String getBoardStateId(){
		return getStateOrTileId(false, true);
		
	}
	
	public String getStateOrTileId(boolean isPrintBoard, boolean isState){
		StringBuilder id = new StringBuilder();
		if(isPrintBoard){
			System.out.println("\n----------------");
		}
		
		for (int i=0;i<mtx.length;i++){
			for(int j=0;j<mtx[0].length;j++){
				
				int shapeOrTileId =0;
				if ( isState){
					if ( mtx[i][j]!=0){
					 shapeOrTileId =tileIdMap.get(mtx[i][j]).shape_type;
					}
				} else {
					shapeOrTileId = mtx[i][j];
				}
				
				if(j==0 && isPrintBoard){
					System.out.print("\n|"+shapeOrTileId);
				}
				else if( isPrintBoard){
				System.out.print("|"+shapeOrTileId);
				}
				id.append(shapeOrTileId);
			}
		}
		if(isPrintBoard){
			//System.out.println("\n----------------");
			System.out.println((isState?"\n state id ":"\n board id ")+ id.toString());
		}
		
		return id.toString();
		
	}
	
	public int[][] getBoardTilesMtx(){
		int[][] output = this.mtx.clone();
		return output;
	}
	
	public String getBoardTilesId(boolean isPrint){
		
		/*
		StringBuilder id = new StringBuilder();
		//System.out.println("----------------");
		for (int i=0;i<mtx.length;i++){
			for(int j=0;j<mtx[0].length;j++){
				if(j==0){
					System.out.print("\n|"+mtx[i][j]);
				}
				else{
				System.out.print("|"+mtx[i][j]);
				}
				id.append(mtx[i][j]);
			}
		}
		System.out.println("\n----------------");
		System.out.println(" Board Tiles Id"+ id.toString());*/
		
		return getStateOrTileId(isPrint, false);
		
	}
	
	public static boolean isValidPosition(int[] upLeft, int[] lowRight){
		if (upLeft.length != 2 || lowRight.length != 2){
			System.err.println(" Invalid Co ");
			return false;
		}
		
		if (upLeft[0]<0 || upLeft[1]<0 ||lowRight[0]<0 || lowRight[1]<0){
			System.err.println(" Negative Co ");
			return false ;
		}
		
		if (upLeft[0]>=Board.ROWS || upLeft[1]>=Board.COLS ||lowRight[0]>=Board.ROWS || lowRight[1]>=Board.COLS){
			System.err.println(" Out of bound Co ");
			return false ;
		}
		
		if (upLeft[0]>lowRight[0]|| upLeft[1]>lowRight[1] ){
			System.err.println(" Inverted Co ");
			return false;
		}
		
		return true;
	}

	public void setSrcState() {
		if (this.isAllTilesInserted() && srcStateId == null) {
			srcStateId = getStateOrTileId(false,true);
		}
	}

	public String getSrcState() {
		return srcStateId;
	}
	
	public String getTgtStateId() {
		return tgtStateId;
	}

	public void setTgtStateId(String tgtStateId) {
		if (this.isAllTilesInserted() && this.tgtStateId == null)
			this.tgtStateId = tgtStateId;
	}

	public boolean isReady() {
		// TODO Auto-generated method stub
		return (this.isAllTilesInserted() && srcStateId != null && tgtStateId != null) ;
	}

	public void update(String boardTilesId) {
		int row = 0;
		int col = 0;
		
		int[] counter = new int[tileIdMap.size()+1];
		int[][] newTileStartPos = new int [tileIdMap.size()+1][4];
		for (Iterator<Entry<Integer, Tile>> it = tileIdMap.entrySet().iterator(); it.hasNext();){
			Entry<Integer, Tile> entry = it.next();
			Tile t = entry.getValue();
			counter[t.id]=t.row*t.col;
			for (int i =0 ; i<4 ; i++){
				newTileStartPos[t.id][i]=-1;
			}
		}
		
		for (int i =0; i<boardTilesId.length();i++){
			row = i/COLS;
			col = i%COLS;
			int tid = Character.getNumericValue(boardTilesId.charAt(i));
			mtx[row][col]=tid;
			
			if (tid==0)
				continue;
			
			Tile t = tileIdMap.get(tid);
			if(newTileStartPos[tid][0]!=-1){// encounter
				try {
				t.setPosition(newTileStartPos[tid][0], newTileStartPos[tid][1], row, col);
				} catch (IllegalArgumentException e){

				}
			} else if (newTileStartPos[tid][0]==-1){//first encounter
				newTileStartPos[tid][0]=row;
				newTileStartPos[tid][1]=col;
				
				if ( t.row==1 && t.col==1){
				newTileStartPos[tid][2]=row;
				newTileStartPos[tid][3]=col;
				t.setPosition(newTileStartPos[tid][0], newTileStartPos[tid][1], row, col);
				}
			}  
		}
	}
	
	public void allNextPosisbleMove() {
		
	}
	
	
}
