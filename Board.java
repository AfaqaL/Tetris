// Board.java

import junit.framework.Assert;

import java.util.Arrays;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private int maxH;
	private int b_maxH;
	public int[] rowWidth;
	private int[] b_rowWidth;
	public int[] colHeight;
	private int[] b_colHeight;
	private boolean[][] grid;
	private boolean[][] b_grid;
	private boolean DEBUG = true;
	boolean committed;


	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		rowWidth = new int[height];
		b_rowWidth = new int[height];
		colHeight = new int[width];
		b_colHeight = new int[width];
		maxH = 0;
		b_maxH = 0;
		grid = new boolean[width][height];
		b_grid = new boolean[width][height];
		committed = true;
	}
	
	public void setDebugMode(boolean mode){ DEBUG = mode; }
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	 
		return maxH;
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() throws RuntimeException {
		if (!DEBUG) return;

		int gridMaxH = 0;
		int[] gridWidths = new int[height];
		for(int i = 0; i < width; i++){
			int currColH = 0;
			for (int j = 0; j < height; j++) {
				if(grid[i][j]){
					currColH = Math.max(currColH, j + 1);
					gridWidths[j]++;
				}
			}
			checkColSanity(currColH, i);
			gridMaxH = Math.max(currColH, gridMaxH);
		}
		checkRowSanity(gridWidths, gridMaxH);

	}

	protected void checkRowSanity(int[] gridWidths, int gridMaxH) throws RuntimeException {
		if(!Arrays.equals(gridWidths, rowWidth)){
			throw new RuntimeException("Incorrectly stored width");
		}
		if(gridMaxH != maxH){
			throw new RuntimeException("Incorrectly stored max height!\nexp: "
					+ gridMaxH + ", got: " + maxH);
		}
	}

	protected void checkColSanity(int colH, int colIdx) throws RuntimeException{
		if(colH != colHeight[colIdx]) {
			throw new RuntimeException("Incorrectly stored column height on col: " + colIdx);
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int maxY = -1;
		int[] body = piece.getSkirt();
		for (int i = 0; i < body.length; i++) {
			int currx = x + i;
			maxY = Math.max(maxY, colHeight[currx] - body[i]);
		}
		return maxY;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return colHeight[x];
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		 return rowWidth[y];
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	 */
	public boolean getGrid(int x, int y) {
		return notValid(x, y) || grid[x][y];
	}

	private boolean notValid(int x, int y) {
		return x < 0 || x >= width || y < 0 || y >= height;
	}

	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.

	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
		committed = false;
		doBackup();

		if(!inBounds(piece, x, y)){ return PLACE_OUT_BOUNDS; }
		int result = PLACE_OK;

		for(TPoint pt : piece.getBody()){
			int currX = x + pt.x, currY = y + pt.y;
			if(grid[currX][currY]){	return PLACE_BAD; }

			grid[currX][currY] = true;
			if(++rowWidth[currY] == width){ result = PLACE_ROW_FILLED; }
			colHeight[currX] = Math.max(currY + 1, colHeight[currX]);
			maxH = Math.max(colHeight[currX], maxH);
		}
		sanityCheck();
		return result;
	}

	private void doBackup() {
		b_maxH = maxH;
		System.arraycopy(rowWidth, 0, b_rowWidth, 0, rowWidth.length);
		System.arraycopy(colHeight, 0, b_colHeight, 0, colHeight.length);
		for (int i = 0; i < grid.length; i++) {
			System.arraycopy(grid[i], 0, b_grid[i], 0, grid[i].length);
		}
	}

	private boolean inBounds(Piece piece, int x, int y) {
		int pw = piece.getWidth() + x;
		int ph = piece.getHeight() + y;
		return x >= 0 && pw <= width && y >= 0 && ph <= height;
	}

	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		if(committed) doBackup();
		committed = false;
		int rowsCleared = 0;
		int to = 0;
		for (int j = 0; j < maxH; j++) {
			rewrite(j, to);
			if(rowWidth[j] != width) to++;
			else rowsCleared++;
		}


		maxH -= rowsCleared;
		actualClear(rowsCleared);
		decreaseHeight(rowsCleared);
		sanityCheck();
		return rowsCleared;
	}

	private void actualClear(int rowsCleared) {
		for (int j = 0; j < rowsCleared; j++) {
			for (int i = 0; i < width; i++) {
				grid[i][maxH + j] = false;
			}
			rowWidth[maxH + j] = 0;
		}
	}
	private void decreaseHeight(int rowsCleared) {
		boolean updateFlag = false;
		for (int i = 0; i < width; i++) {
			colHeight[i] -= rowsCleared;
			if(goDown(i)) updateFlag = true;
		}
		if(updateFlag) searchForMaxH();
	}

	private void searchForMaxH() {
		int actualMax = -1;
		for(int h : colHeight){
			actualMax = Math.max(h, actualMax);
		}
		maxH = actualMax;
	}

	private boolean goDown(int col){
		boolean flag = false;
		int h = colHeight[col];
		while(--h >= 0 && !grid[col][h]){
			--colHeight[col];
			flag = true;
		}
		return flag;
	}
	private void rewrite(int from, int to) {
		if(from != to){
			for (int i = 0; i < width; i++) {
				grid[i][to] = grid[i][from];
			}
			rowWidth[to] = rowWidth[from];
		}
	}


	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if(!committed){
			maxH = b_maxH;

			int[] tmp = rowWidth;
			rowWidth = b_rowWidth;
			b_rowWidth = tmp;

			tmp = colHeight;
			colHeight = b_colHeight;
			b_colHeight = tmp;

			boolean[][] gtmp = grid;
			grid = b_grid;
			b_grid = gtmp;
			sanityCheck();
		}
		committed = true;
	}
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() { committed = true; }


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


