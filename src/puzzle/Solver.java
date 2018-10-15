package puzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import utils.PuzzleUtils;
import utils.PuzzleUtils.DIRECTION;

public class Solver {
	
	public static void main(String[] args) {

		try {
			PrintStream o = new PrintStream(new File("output.txt"));
			System.setOut(o);
			System.setErr(o);
			int i = 1;
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Properties prop = new Properties();
		String filename = "setup.properties";
		InputStream propertyIS = Solver.class.getClassLoader().getResourceAsStream(filename);
		if (propertyIS != null) {
			try {
				prop.load(propertyIS);
				for (String name : prop.stringPropertyNames()) {
					String value = prop.getProperty(name);
					System.setProperty(name, value);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					propertyIS.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.out.println(" use default properties setup");
		}
		
		
		Board mainBoard = new Board();
		Board targetBoard = new Board();
		setup(mainBoard,targetBoard);
		
		//System.out.println(mainBoard.isAllTilesInserted());
		if(mainBoard.isAllTilesInserted()  && targetBoard.isAllTilesInserted()){
			mainBoard.setSrcState();
			mainBoard.setTgtStateId(targetBoard.getBoardStateId());
		
		
		if(mainBoard.isReady()){
		
			//mainBoard.getStateOrTileId(true, true);
			System.out.println(" =============== SETUP =================");
			System.out.println(" src state ");
			//mainBoard.getStateOrTileId(true, false);
			mainBoard.getStateOrTileId(true, true);
		
			System.out.println(" tgt state ");
			//targetBoard.getStateOrTileId(true, false);
			targetBoard.getStateOrTileId(true, true);
			
			System.out.println(" =============== START =================");
			solve(mainBoard);
			System.out.println(" =============== END ===================");
			
			
		}
		}
		
	}
	
	private static void setup(Board mainBoard,Board targetBoard) {
		String start_setup=System.getProperty("start.tiles.setup", "0,0,1,1;3,0,4,0;1,2,2,2;1,3,2,3;0,2,0,3;4,1,4,2;3,1,3,2;3,3,3,3;4,3,4,3");
		String end_setup=System.getProperty("end.tiles.setup", "0,0,0,1;0,2,1,3;1,0,2,0;1,1,2,1;3,0,3,0;4,0,4,0;3,1,3,2;4,1,4,2;3,3,4,3");
		int[][] tileCos = PuzzleUtils.pharseTileCoord(start_setup);
																				
		//Board mainBoard = new Board();
		
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
		int[][] endTileCos = PuzzleUtils.pharseTileCoord(end_setup);
		
		//Board targetBoard = new Board();
		
		for (int i = 0; i < tileCos.length; i++) {

			int[] upLeft = new int[] { endTileCos[i][0], endTileCos[i][1] };
			int[] lowRight = new int[] { endTileCos[i][2], endTileCos[i][3] };

			int row = lowRight[0] - upLeft[0] + 1;
			int col = lowRight[1] - upLeft[1] + 1;

			if (!Board.isValidPosition(upLeft, lowRight))
				throw new IllegalArgumentException(" Invalid position ");

			
			
			targetBoard.insertTile(new Tile(i+1, row, col, targetBoard), upLeft, lowRight);
		}
		
	}

	public static void solve(Board mainBoard){
		
		Map<String,String> chldToPrt= new HashMap<>();
		Map<String,Integer> stateToStep = new HashMap<>();
		
		
		chldToPrt.put(mainBoard.getSrcState(), mainBoard.getBoardTilesId(false));
		stateToStep.put(mainBoard.getSrcState(), 0);
		
		String srcTilesId = mainBoard.getBoardTilesId(false);
		
		BFS(mainBoard, chldToPrt, stateToStep);
		
		if (chldToPrt.get(mainBoard.getTgtStateId())!=null){
			System.out.println("NEED MOVES = "+ stateToStep.get(mainBoard.getTgtStateId()));
			 //mainBoard.getBoardTilesId(true);
			System.out.println(" Printing moves ");
			
			printMoves( mainBoard ,srcTilesId ,  mainBoard.getBoardTilesId(false), chldToPrt);
		}
	}
	
	
	public static void BFS(Board mainBoard, Map<String,String> chldToPrt, Map<String,Integer> stateToStep){
		
		List<String> boardTilesIdQ = new ArrayList<>();
		String prevBorad = mainBoard.getBoardTilesId(false);
		
		boardTilesIdQ.add(prevBorad);
		
		while(!boardTilesIdQ.isEmpty()){
			String boardTilesId= boardTilesIdQ.remove(0); //remove first 
			mainBoard.update(boardTilesId);
			String prevState = mainBoard.getBoardStateId();
			
			if( prevState.equals(mainBoard.getTgtStateId()))
				break;
			
			prevBorad = mainBoard.getBoardTilesId(false);

			for (Iterator<Entry<Integer, Tile>> it = mainBoard.tileIdMap.entrySet().iterator(); it.hasNext();){
				Entry<Integer, Tile> entry = it.next();

				for ( DIRECTION dtn : DIRECTION.values()){

					boolean isMoved = mainBoard.moveTile(entry.getValue(), dtn.getDtn());
					
					if (isMoved) {
						String stateId = mainBoard.getBoardStateId();

						if (chldToPrt.get(stateId) == null && stateToStep.get(stateId) == null) {// new
																									// state
							chldToPrt.put(stateId, prevBorad);
							stateToStep.put(stateId, stateToStep.get(prevState) + 1);

							if (stateId.equals(mainBoard.getTgtStateId())) {
								System.out.println("----- found tgt state " + stateToStep.get(stateId));

							}
							
							boardTilesIdQ.add(mainBoard.getBoardTilesId(false));

						} else if (chldToPrt.get(stateId) != null && stateToStep.get(stateId) != null) {// Visited
																										// state
							int oldStep = stateToStep.get(stateId);
							int newStep = stateToStep.get(prevState) + 1;

							if (newStep < oldStep) {
								chldToPrt.put(stateId, prevBorad);
								stateToStep.put(stateId, stateToStep.get(prevState) + 1);

								boardTilesIdQ.add(mainBoard.getBoardTilesId(false));
							} 
						}
						mainBoard.update(prevBorad); // reset
					}
				}
				} 
			}
	}
	
	public static void printMoves(Board board , String srcTileId, String tgtTileId, Map<String,String> chldToPrt){
		List<String> parents = new ArrayList<String>();
		parents.add(0,tgtTileId);

		String parentTilesId = chldToPrt.get(board.getTgtStateId());
		board.update(parentTilesId);
		while(parentTilesId !=null ){
			parents.add(0,board.getBoardTilesId(false));
			if (board.getBoardTilesId(false).equals(srcTileId) ){
				break;
			}
			parentTilesId = chldToPrt.get(board.getBoardStateId());
			board.update(parentTilesId);

			
		}

		int i =0 ;
		for( String tilesId: parents){
			System.out.println(" step - "+(i++));
			board.update(tilesId);
			board.getBoardTilesId(true);
		}		
	}
}

