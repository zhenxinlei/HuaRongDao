package puzzle;

public class Tile {

	public  final int shape_type;
	public final int row ;
	public final int col;
	public  final int id;
	private final int[] upLeft = new int[]{-1,-1};
	private final int[] lowRight = new int[]{-1,-1};
	private final Board board;
	private int[] positon = new int[]{-1,-1,-1,-1};
	
	
	public Tile(int tile_id , int row, int col, Board board) {
		id= tile_id;
		this.row = row;
		this.col = col;
		shape_type = (row<<1)+(col);
		this.board = board;
		
	}
	
	public int[] getPosition(){
		
		return this.positon;
	}
	
	public void setPosition(int upLeftRow, int upLeftCol,  int lowRightRow, int lowRightCol){
		
		if (lowRightRow-upLeftRow+1!=row || lowRightCol-upLeftCol+1!=col )
			throw new IllegalArgumentException(" Invalid position vs tile shape ");
		
		this.upLeft[0] = upLeftRow;
		this.upLeft[1] = upLeftCol;
		this.lowRight[0] = lowRightRow;
		this.lowRight[1] = lowRightCol;
		
		for (int i =0; i<4;i++){
			if (i<2)
				this.positon[i]=upLeft[i];
			else
				this.positon[i]=lowRight[i-2];
		}
		
		//System.out.println(" tile "+id +" , "+ row+", "+col+", "+shape_type+", pos ("+upLeft[0]+","+upLeft[1]+") -> ("+lowRight[0]+","+lowRight[1]+")");
	}
	
	
}
