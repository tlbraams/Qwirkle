package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Board {

	public static final int DIM = 183;
	public static final int MAX_STACK_SIZE = 108;

	/**
	 * The class that models the board used by the Qwirkle game.
	 * The board will be stored as a Piece[][]. of 185*185 with start point being 91,91.
	 * The board will most likely also store the stack of pieces that are available for drawing.
	 */
	private int minRow;
	private int maxRow;
	private int minColumn;
	private int maxColumn;
	
	private int lastMadeMove;
	
	private int size;
	private Piece[][] board;
	private ArrayList<Piece> stack;
	
	// --------------- Constructors -----------------
	public Board() {
		size = DIM;
		board = new Piece[DIM][DIM];
		stack = new ArrayList<Piece>();
		fillStack();
		minRow = 85;
		maxRow = 97;
		minColumn = 85;
		maxColumn = 97;
	}
	public Board(int dimension) {
		size = dimension;
		board = new Piece[dimension][dimension];
		stack = new ArrayList<Piece>();
		fillStack();
		minRow = 0;
		maxRow = size-1;
		minColumn = 0;
		maxColumn = size-1;
	}
	
	// --------------- Queries ----------------------
	public Piece[][] getBoard() {
		return board;
	}
	
	public Piece getCell(int row, int column) {
		return board[row][column];
	}
	
	public int getSize() {
		return size;
	}
	
	public boolean isEmpty(int row, int column) {
		return board[row][column] == null;
	}
	  /**
	   * Block toevoegen? 
	   */
	
	/**
	 * Returns true if the row and column refer to a valid cell on the <code>Board</code>.
	 */
	public boolean isField(int row, int column) {
		return 0 <= row && row < size && 0 <= column && column < size;
	}
	
	public int getLastMadeMove() {
		return lastMadeMove;
	}
	
	public int getMinRow() {
		return minRow;
	}
	
	public int getMaxRow() {
		return maxRow;
	}
	
	public int getMinColumn() {
		return minColumn;
	}
	
	public int getMaxColumn() {
		return maxColumn;
	}
	
	public int getRowLength(int row, int column) {
		int result = 0;
		Boolean isRow = true;
		for(int i = column; isRow; i--) {
			if(!isEmpty(row, i)) {
				result++;
			}
			else {
				isRow = false;
			}
		}
		isRow = true;
		for(int i = column + 1; isRow; i++) {
			if(!isEmpty(row, i)) {
				result++;
			} else {
				isRow = false;
			}
		}
		return result;
	}
	
	public int getColumnLength(int row, int column) {
		int result = 0;
		Boolean isColumn = true;
		for(int i = row; isColumn; i--) {
			if(!isEmpty(i, column)) {
				result++;
			}
			else {
				isColumn = false;
			}
		}
		isColumn = true;
		for(int i = row + 1; isColumn; i++) {
			if(!isEmpty(i, column)) {
				result++;
			} else {
				isColumn = false;
			}
		}
		return result;
	}
	
	// --------------- Commands ---------------------
	
	public void setPiece(int row, int column, Piece piece) {
		board[row][column] = piece;
		//change min/max row/column if outside border.
	}
	
	public void setLastMadeMove(int moveCount) {
		lastMadeMove = moveCount;
	}
	
	public void reset() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = null;
			}
		}
	}
	
	public Piece[][] deepCopy() {
		Piece[][] result = new Piece[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				result[i][j] = board[i][j];
			}
		}
		return result;
	}
	
	/**
	 * Fills the stack with the pieces needed for the game and shuffles it.
	 */
	public void fillStack() {
		Set<Piece.Color> colors = EnumSet.complementOf(EnumSet.of(Piece.Color.DEFAULT));
		Set<Piece.Shape> shapes = EnumSet.complementOf(EnumSet.of(Piece.Shape.BLOCKED));
		for(Piece.Color color: colors) {
			for(Piece.Shape shape: shapes){
				stack.add(new Piece(color, shape));
				stack.add(new Piece(color, shape));
				stack.add(new Piece(color, shape));
			}
		}
		Collections.shuffle(stack);
	}
	
	/**
	 * Draws one piece from the stack.
	 * @return the piece drawn
	 */
	public Piece draw() {
		return stack.remove(0);
	}
	
	/**
	 * Places the pieces received from a player in a trade back in the stack and shuffles the stack.
	 * @param pieces the pieces received from a player.
	 */
	public void tradeReturn(Piece[] pieces) {
		for(int i = 0; i < pieces.length; i++) {
			stack.add(pieces[i]);
		}
		Collections.shuffle(stack);
	}
}
