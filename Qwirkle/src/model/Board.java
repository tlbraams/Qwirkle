package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Class for modelling the board and the stack used by the Qwirkle game.
 * The board will be stored as a Piece[][] of 183x183 with the starting point being 91,91.
 * 
 * @author Tycho Braams & Jeroen Mulder
 * @version $1.0
 */
public class Board {

	// ----- Constants -----
	public static final int DIM = 183;
	public static final int MAX_STACK_SIZE = 108;

	// ----- Instance Variables -----
	private int minRow;
	private int maxRow;
	private int minColumn;
	private int maxColumn;
	
	private int lastMadeMove;
	
	private int size;
	private Piece[][] board;
	private ArrayList<Piece> stack;
	private int[] score;
	
	// ----- Constructors -----
	
	/**
	 * Creates a new Board object of default size (183 x 183).
	 */
	public Board() {
		size = DIM;
		board = new Piece[DIM][DIM];
		stack = new ArrayList<Piece>();
		fillStack();
		minRow = 86;
		maxRow = 97;
		minColumn = 85;
		maxColumn = 97;
		score = new int[4];
	}
	
	/**
	 * Creates a new Board object of a size of length dimension.
	 *  @param dimension the length of the edges of a Board. 
	 */
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
	
	// ----- Queries -----
	/**
	 * Returns the Board of this Game. 
	 * @return the Board. 
	 */
	/* @pure */public /* @NonNull */Piece[][] getBoard() {
		return board;
	}
	
	/**
	 * Returns the Piece in a given cell indicated by row and column. 
	 * @return piece the Piece in the given cell. 
	 */
	/*
	 * @requires 	row >= minRow && row <= maxRow;
	 * 				column >= minColumn && column <= maxColumn;
	 */
	/* @pure */public Piece getCell(/* @NonNull */int row, /* @NonNull */int column) {
		return board[row][column];
	}
	
	/**
	 * Returns the size of the edges of a Board. 
	 * @return the size of a Board.
	 */
	/*
	 * @ensures 	\result == maxRow;
	 */
	/* @pure */public /* @NonNullable */int getSize() {
		return size;
	}
	
	/**
	 * Tests if a cell in the given row and column is empty. 
	 * @param row the row of the cell.
	 * @param column the column of the cell.
	 * @return true if the cell is empty, false when occupied. 
	 */
	/* @pure */public /* @NonNull */boolean isEmpty(/* @NonNull */int row, /* @NonNull */int column) {
		return board[row][column] == null;
	}
	 
	/**
	 * Tests if the cell in the given row and column is on the <code>Board</code>.
	 * @return true if the row and column refer to a valid cell on the board, false otherwise.
	 */
	/*
	 * @ensures 	\result == (0 <= row && row < size && 0 <= column && column < size);
	 */
	/* @pure */public /* @NonNull */boolean isField(/* @NonNull */int row, /* @NonNull */int column) {
		return 0 <= row && row < size && 0 <= column && column < size;
	}
	
	/**
	 * Returns when the last made Move was made. 
	 * @return the last made Move. 
	 */
	/* @pure */public /* @NonNull */int getLastMadeMove() {
		return lastMadeMove;
	}
	
	/**
	 * Returns the minimal size of the rows on the Board. 
	 * @return the minimal row size of the Board. 
	 */
	/* @pure */public /* @NonNull */int getMinRow() {
		return minRow;
	}
	
	/**
	 * Returns the maximum size of the rows on the Board. 
	 * @return the maximum row size of the Board. 
	 */
	/* @pure */public /* @NonNull */int getMaxRow() {
		return maxRow;
	}
	
	/**
	 * Returns the minimal size of the columns on the Board. 
	 * @return the minimal column size of the Board. 
	 */
	/* @pure */public /* @NonNull */int getMinColumn() {
		return minColumn;
	}
	
	/**
	 * Returns the maximum size of the columns on the Board. 
	 * @return the maximum column size of the Board. 
	 */	
	/* @pure */public /* @NonNull */int getMaxColumn() {
		return maxColumn;
	}
	
	/**
	 * Determines how long a row is given a certain cell, given by row and column. 
	 * 
	 *@return the length of a row.
	 */
	/*
	 * @requires 	0 <= row && row < size && 0 <= column && column < size;
	 * @ensures 	1 <= \result && 7 > \result;
	 */
	/* @pure */public /* @NonNull */int getRowLength(int row, int column) {
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
	
	/* @pure */public /* @NonNull */int getColumnLength(int row, int column) {
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
	
	/* @pure */public /* @NonNull */int getScore(int playerID) {
		return score[playerID];
	}
	
	/* @pure */public /* @NonNull */boolean emptyStack() {
		return stack.size() == 0;
	}
	
	// ----- Commands -----
	
	public void addScore(int playerID, int score) {
		this.score[playerID] += score;
	}
	
	public void setPiece(int row, int column, Piece piece) {
		board[row][column] = piece;
		if (row < minRow) {
			minRow = row;
		} else if (row > maxRow) {
			maxRow = row;
		}
		if (column < minColumn) {
			minColumn = column;
		} else if (column > maxColumn) {
			maxColumn = column;
		}
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
	
	public Board deepCopy() {
		Board result = new Board();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				result.setPiece(i, j, this.getCell(i, j));
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
