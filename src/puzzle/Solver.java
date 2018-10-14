package puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utils.PuzzleUtils;
import utils.PuzzleUtils.DIRECTION;

public class Solver {
	
	public static void main(String[] args) {
		
		//mock cfg 

		int[][] tileCos = PuzzleUtils.pharseTileCoord("0,0,1,1;3,0,4,0;1,2,2,2;1,3,2,3;0,2,0,3;4,1,4,2;3,1,3,2;3,3,3,3;4,3,4,3");
												
												
		Board mainBoard = new Board();
		
		for ( int i = 0; i <tileCos.length;i++){
			
			int[] upLeft = new int[]{tileCos[i][0],tileCos[i][1]};
			int[] lowRight = new int[]{tileCos[i][2],tileCos[i][3]};

			int row = lowRight[0]-upLeft[0]+1;
			int col = lowRight[1]-upLeft[1]+1;
			
			if(!Board.isValidPosition(upLeft, lowRight))
				throw new IllegalArgumentException(" Invalid position ");
			
			mainBoard.insertTile(new Tile(i+1,row, col,mainBoard ),upLeft, lowRight );
		}

		//int[][] endTileCos = PuzzleUtils.pharseTileCoord("0,0,1,1;2,0,3,0;1,2,2,2;1,3,2,3;0,2,0,3;4,1,4,2;3,1,3,2;3,3,3,3;4,3,4,3");
		int[][] endTileCos = PuzzleUtils.pharseTileCoord("0,0,0,1;0,2,1,3;1,0,2,0;1,1,2,1;3,0,3,0;4,0,4,0;3,1,3,2;4,1,4,2;3,3,4,3");
		
		Board targetBoard = new Board();
		
		for (int i = 0; i < tileCos.length; i++) {

			int[] upLeft = new int[] { endTileCos[i][0], endTileCos[i][1] };
			int[] lowRight = new int[] { endTileCos[i][2], endTileCos[i][3] };

			int row = lowRight[0] - upLeft[0] + 1;
			int col = lowRight[1] - upLeft[1] + 1;

			if (!Board.isValidPosition(upLeft, lowRight))
				throw new IllegalArgumentException(" Invalid position ");

			targetBoard.insertTile(new Tile(i+1, row, col, targetBoard), upLeft, lowRight);
		}
		
		//System.out.println(mainBoard.isAllTilesInserted());
		if(mainBoard.isAllTilesInserted()  && targetBoard.isAllTilesInserted()){
			mainBoard.setSrcState();
			mainBoard.setTgtStateId(targetBoard.getBoardStateId());
		
		
		if(mainBoard.isReady()){
		
			//mainBoard.getStateOrTileId(true, true);
			mainBoard.getStateOrTileId(true, false);
		
			targetBoard.getStateOrTileId(true, false);
			
			System.out.println(" =============== START =================");
			solve(mainBoard);
			System.out.println(" ======= END =====");
		}
		}
		
		
		
		
	}
	
	public static void solve(Board mainBoard){
		
		Map<String,String> chldToPrt= new HashMap<>();
		Map<String,Integer> stateToStep = new HashMap<>();
		
		
		chldToPrt.put(mainBoard.getSrcState(), mainBoard.getSrcState());
		stateToStep.put(mainBoard.getSrcState(), 0);
		
		/*boolean foundPath = solve(mainBoard, null,-1, chldToPrt, stateToStep);
		
		if (foundPath){
			System.out.println(" found path ");
			mainBoard.getBoardTilesId();
		}*/
		
		BFS(mainBoard, null, -1, chldToPrt, stateToStep);
		
		if (chldToPrt.get(mainBoard.getTgtStateId())!=null){
			System.out.println(" need step == > "+ stateToStep.get(mainBoard.getTgtStateId()));
		}
	}
	
	
	//TODo
	public static void BFS(Board mainBoard, PuzzleUtils.DIRECTION prevDtn, int movedTileId, Map<String,String> chldToPrt, Map<String,Integer> stateToStep){
		
		List<String> boardTilesIdQ = new ArrayList<>();
		List<String> nextTilesIdQ = new ArrayList<>();
		String prevBorad = mainBoard.getBoardTilesId(false);
		
		boardTilesIdQ.add(prevBorad);
		
		while(!boardTilesIdQ.isEmpty()){
			String boardTilesId= boardTilesIdQ.remove(0); //remove first 
			mainBoard.update(boardTilesId);
			System.out.println(" pop tile to q "+mainBoard.getBoardTilesId(false)+ " ,, "+boardTilesIdQ.size());
			String prevState = mainBoard.getBoardStateId();
			
			prevBorad = mainBoard.getBoardTilesId(false);
			//System.out.println("  ===  poped state" + mainBoard.tileIdMap.get(8).getPosition()[0]+","+mainBoard.tileIdMap.get(8).getPosition()[1]);
			
			for (Iterator<Entry<Integer, Tile>> it = mainBoard.tileIdMap.entrySet().iterator(); it.hasNext();){
				Entry<Integer, Tile> entry = it.next();
			//System.out.println(mainBoard.tileIdMap.get(1).getPosition()[0]+","+mainBoard.tileIdMap.get(1).getPosition()[1]+","+mainBoard.tileIdMap.get(1).getPosition()[2]+","+mainBoard.tileIdMap.get(1).getPosition()[3]);
				
				
				for ( DIRECTION dtn : DIRECTION.values()){
					/*if(entry.getValue().id ==8 ){
						int tmp = 1;
						System.out.println("--- before "+dtn+entry.getValue().getPosition()[0]+","+entry.getValue().getPosition()[1]+", "+prevBorad);
					}*/
					boolean isMoved = mainBoard.moveTile(entry.getValue(), dtn.getDtn());
					
					/*if(entry.getValue().id ==8  && isMoved){
						int tmp = 1;
						mainBoard.getBoardTilesId(true);
						System.out.println("--- Moved "+dtn+entry.getValue().getPosition()[0]+","+entry.getValue().getPosition()[1]);
					}*/
					
					if (isMoved) {
						System.out.println("--- Moved "+dtn+", "+entry.getValue().id);
						
						String stateId = mainBoard.getBoardStateId();

						if (chldToPrt.get(stateId) == null && stateToStep.get(stateId) == null) {// new
																									// state
							chldToPrt.put(stateId, prevBorad);
							stateToStep.put(stateId, stateToStep.get(prevState) + 1);

							if (stateId.equals(mainBoard.getTgtStateId())) {
								System.out.println("----- found state " + stateToStep.get(stateId));
								mainBoard.update(prevBorad); // reset
								continue;
							}
							
							boardTilesIdQ.add(mainBoard.getBoardTilesId(false));
							System.out.println(" --1 adding tile to q "+mainBoard.getBoardTilesId(false)+ " ,, "+boardTilesIdQ.size());

						} else if (chldToPrt.get(stateId) != null && stateToStep.get(stateId) != null) {// Visited
																										// state
							int oldStep = stateToStep.get(stateId);
							int newStep = stateToStep.get(prevState) + 1;

							if (newStep < oldStep) {
								chldToPrt.put(stateId, prevBorad);
								stateToStep.put(stateId, stateToStep.get(prevState) + 1);

								System.out.println(" better move ");
								
								boardTilesIdQ.add(mainBoard.getBoardTilesId(false));
								System.out.println(" --2 adding tile to q "+mainBoard.getBoardTilesId(false)+ " ,, "+boardTilesIdQ.size());
							} else {

							}

						}
						mainBoard.update(prevBorad); // reset
					}
				}
			//System.out.println(mainBoard.tileIdMap.get(1).getPosition()[0]+","+mainBoard.tileIdMap.get(1).getPosition()[1]+","+mainBoard.tileIdMap.get(1).getPosition()[2]+","+mainBoard.tileIdMap.get(1).getPosition()[3]);
		
				} 
			/*System.out.println(" current Q " );
			Iterator<String> it =  boardTilesIdQ.iterator();
			while(it.hasNext()){
				System.out.println(" tile id "+ it.next());
			}*/
			
			
			}
		System.out.println( "exiting while loop "+boardTilesIdQ.size());

	}
	
	public static void printMoves(String srcTileId, String tgtTileId, Map<String,String> chldToPrt, Map<String,Integer> stateToStep){
		List<String> parent = new ArrayList<String>();
		
		
			
		
	}
	
	public static boolean solve(Board mainBoard, PuzzleUtils.DIRECTION prevDtn, int movedTileId, Map<String,String> chldToPrt, Map<String,Integer> stateToStep){
		
		/*if  (mainBoard.getBoardStateId().equals(tgtId)){
			System.out.println(" find path " );
			return true;
		}
		*/
		String prevState= mainBoard.getBoardStateId();
		boolean foundPath = false;
		
		for (Iterator<java.util.Map.Entry<Integer, Tile>> it=mainBoard.tileIdMap.entrySet().iterator();it.hasNext();){
			
			Entry<Integer, Tile> entry = it.next();
			
			boolean isMoved = false;
			
			if( !DIRECTION.MOVE_DOWN.equals(prevDtn) || !entry.getKey().equals(movedTileId)){
			isMoved = mainBoard.moveTile(entry.getValue(), DIRECTION.MOVE_UP.getDtn());
			if (isMoved){
				foundPath = checkState(mainBoard,prevState,entry.getKey(),DIRECTION.MOVE_UP, chldToPrt,stateToStep);
				
				/*System.out.println("tile "+entry.getValue().id+" moved UP ");
				mainBoard.getBoardTilesId();*/
				
				if(foundPath)
					return true;
				
				mainBoard.moveTile(entry.getValue(), DIRECTION.MOVE_DOWN.getDtn());//reset position 
				}
			}
			
			if( !DIRECTION.MOVE_UP.equals(prevDtn)|| !entry.getKey().equals(movedTileId)){
			isMoved = mainBoard.moveTile(entry.getValue(), DIRECTION.MOVE_DOWN.getDtn());
			if (isMoved){
				foundPath = checkState(mainBoard,prevState,entry.getKey(),DIRECTION.MOVE_DOWN, chldToPrt,stateToStep);
				/*System.out.println("tile "+entry.getValue().id+" moved DOWN ");
				mainBoard.getBoardTilesId();*/
				if (foundPath)
					return true;
				mainBoard.moveTile(entry.getValue(), DIRECTION.MOVE_UP.getDtn());//reset position 
			}
			}
			
			if( !DIRECTION.MOVE_RIGHT.equals(prevDtn)|| !entry.getKey().equals(movedTileId)){
			isMoved = mainBoard.moveTile(entry.getValue(), DIRECTION.MOVE_LEFT.getDtn());
			if (isMoved){
				foundPath = checkState(mainBoard,prevState,entry.getKey(),DIRECTION.MOVE_LEFT, chldToPrt,stateToStep);
				/*System.out.println("tile "+entry.getValue().id+" moved RIGHT ");
				mainBoard.getBoardTilesId();*/
				if (foundPath)
					return true;
				mainBoard.moveTile(entry.getValue(), DIRECTION.MOVE_RIGHT.getDtn());//reset position 
				
			}
			}
			
			if( !DIRECTION.MOVE_LEFT.equals(prevDtn)|| !entry.getKey().equals(movedTileId)){
			isMoved = mainBoard.moveTile(entry.getValue(), DIRECTION.MOVE_RIGHT.getDtn());
			if (isMoved){
				foundPath = checkState(mainBoard,prevState,entry.getKey(),DIRECTION.MOVE_RIGHT,chldToPrt,stateToStep);
				/*System.out.println("tile "+entry.getValue().id+" moved LEFT ");
				mainBoard.getBoardTilesId();*/
				if (foundPath)
					return true;
				mainBoard.moveTile(entry.getValue(), DIRECTION.MOVE_LEFT.getDtn());//reset position 
			}
			}
			
			
		}

		return foundPath;
	}
	
	//TODO redo check state
	private static boolean checkState(Board mainBoard, String prevState, int movedTileId,DIRECTION ptnDtn, Map<String,String> chldToPrt, Map<String,Integer> stateToStep){

		String stateId = mainBoard.getBoardStateId();
		if (stateId.equals(mainBoard.getTgtStateId())){
			System.out.println(" ====== found path steps= "+stateToStep.get(stateId) );
			return true;
		}
		
		if ( chldToPrt.get(stateId)==null && stateToStep.get(stateId)==null){//new state
			chldToPrt.put(stateId, prevState);
			stateToStep.put(stateId, stateToStep.get(prevState)+1);
			
		} else if ( chldToPrt.get(stateId)!=null && stateToStep.get(stateId)!=null){//Visited  state
			int oldStep= stateToStep.get(stateId);
			int newStep = stateToStep.get(prevState)+1;
			
			if (newStep<oldStep){
				chldToPrt.put(stateId, prevState);
				stateToStep.put(stateId, stateToStep.get(prevState)+1);
			}
			return false;
		}

		return solve(mainBoard, ptnDtn,movedTileId, chldToPrt, stateToStep);
		
		

	}
	

}

