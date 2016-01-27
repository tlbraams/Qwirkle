package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Observable;
import java.util.Set;

import exceptions.InvalidMoveException;

/**
 * Class for modelling the board and the stack used by the Qwirkle game.
 * @author Tycho Braams & Jeroen Mulder
 * @version $1.0
 */

public class Board extends Observable {

	/*@
	 	private invariant	0 <= stack.size() && stack.size() <= 108;
		private invariant 	0 <= minRow && minRow <= 86;
		private invariant	97 <= maxRow && maxRow <= 183;
		private invariant	0 <= minColumn && minColumn <= 85;
		private invariant	97 <= maxColumn && maxColumn <= 183;
	*/
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
	private int[] scores;
	
	// ----- Constructors -----
	
	/**
	 * Creates a new Board object of default size (183 x 183).
	 */
	/*
	 *@ ensures		getSize() == DIM;
	 *				getMinRow() == 86;
	 *				getMaxRow() == 97;
	 *				getMinColumn() == 85;
	 *				getMaxColumn() == 97;
	 *				getStack().size() == 108;
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
		scores = new int[4];
	}
	
	// ----- Queries -----
	/**
	 * Returns the Board of this Game. 
	 * @return the Board. 
	 */
	/*@ pure */public /*@ non_null */Piece[][] getBoard() {
		return board;
	}
	
	/**
	 * Returns the Piece in a given cell indicated by row and column. 
	 * @return piece the Piece in the given cell. 
	 */
	/*
	 *@ requires 	0 <= row && 183 < row;
	 *				0 <= column && column <= 183;
	 */
	/*@ pure */public Piece getCell(/*@ non_null */int row, /*@ non_null */int column) {
		return board[row][column];
	}
	
	/**
	 * Returns the size of the edges of a Board. 
	 * @return the size of a Board.
	 */
	/*
	 *@ ensures 	\result == 183;
	 */
	/*@ pure */public /*@ non_null */int getSize() {
		return size;
	}
	
	/**
	 * Tests if a cell in the given row and column is empty. 
	 * @param row the row of the cell.
	 * @param column the column of the cell.
	 * @return true if the cell is empty, false when occupied. 
	 */
	/*
	 *@ requires	0 <= row && 183 < row;
	 *				0 <= column && column <= 183;
	 *@ ensures		isEmpty(row, column) ==> board[row][column] == null;
	 *				!(isEmpty(row, column) ==> board[row][column] != null;
	 */
	/*@pure*/public /*@ non_null */boolean isEmpty(/*@ non_null */int row,
														/*@ non_null */int column) {
		return board[row][column] == null;
	}
	 
	/**
	 * Tests if the cell in the given row and column is on the <code>Board</code>.
	 * @return true if the row and column refer to a valid cell on the board, false otherwise.
	 */
	/*
	 *@ ensures 	0 <= row && row < 183 && 0 <= column && column <= 183
	 *					==> isField(row, column) == true;
	 *				row < 0 && 183 < row && column < 0 && 183 < column 
	 *					==> isField(row, column) == false;
	 */
	/*@pure*/public /*@ non_null */boolean isField(/*@ non_null */int row,
														/*@ non_null */int column) {
		return 0 <= row && row < size && 0 <= column && column < size;
	}
	
	/**
	 * Returns when the last Move was made. 
	 * @return the last made Move. 
	 */
	/*
	 *@ ensures 	0 <= \result; 
	 */
	/*@ pure */public /*@ non_null */int getLastMadeMove() {
		return lastMadeMove;
	}
	
	/**
	 * Returns the minimal size of the rows on the Board. 
	 * @return the minimal row size of the Board. 
	 */
	/*@ pure */public /*@ non_null */int getMinRow() {
		return minRow;
	}
	
	/**
	 * Returns the maximum size of the rows on the Board. 
	 * @return the maximum row size of the Board. 
	 */
	/*@ pure */public /*@ non_null */int getMaxRow() {
		return maxRow;
	}
	
	/**
	 * Returns the minimal size of the columns on the Board. 
	 * @return the minimal column size of the Board. 
	 */
	/*@ pure */public /*@ non_null */int getMinColumn() {
		return minColumn;
	}
	
	/**
	 * Returns the maximum size of the columns on the Board. 
	 * @return the maximum column size of the Board. 
	 */	
	/*@ pure */public /*@ non_null */int getMaxColumn() {
		return maxColumn;
	}
	
	/**
	 * Returns the stack. 
	 * @return the stack.
	 */
	/*
	 *@ ensures 	\result == stack;
	 */
	/*@ pure */public /*@ non_null */ ArrayList<Piece> getStack() {
		return stack;
	}
	
	/**
	 * Determines the length of a row given a certain cell.
	 * @return the length of a row.
	 */
	/*
	 *@ requires 	isField(row, column);
	 *@ ensures 	0 <= \result && 7 > \result;
	 */
	/*@ pure */public /*@ non_null */int getRowLength(int row, int column) {
		int result = 0;
		Boolean isRow = true;
		for (int i = column; isRow; i--) {
			if (!isEmpty(row, i)) {
				result++;
			} else {
				isRow = false;
			}
		}
		isRow = true;
		for (int i = column + 1; isRow; i++) {
			if (!isEmpty(row, i)) {
				result++;
			} else {
				isRow = false;
			}
		}
		return result;
	}
	
	/**
	 * Determines the length of a column given a certain cell.
	 * @return the length of a row.
	 */
	/*
	 *@ requires 	isField(row, column);
	 *@ ensures 	0 <= \result && 7 > \result;
	 */
	/*@ pure */public /*@ non_null */int getColumnLength(/*@ non_null */int row,
			/*@ non_null */int column) {
		int result = 0;
		Boolean isColumn = true;
		for (int i = row; isColumn; i--) {
			if (!isEmpty(i, column)) {
				result++;
			} else {
				isColumn = false;
			}
		}
		isColumn = true;
		for (int i = row + 1; isColumn; i++) {
			if (!isEmpty(i, column)) {
				result++;
			} else {
				isColumn = false;
			}
		}
		return result;
	}
	
	/**
	 * Returns the score of player with the given playerID. 
	 * @param playerID the ID of the Player whose score is asked.
	 */
	/*
	 *@ requires	0 <= playerID && playerID <= 3;
	 *@ ensures 	0 <= \result;
	 */
	/*@ pure */public /*@ non_null */int getScore(int playerID) {
		return scores[playerID];
	}
	
	/**
	 * Indicates if the stack is empty. 
	 * @return true when the size of the stack is 0 and false otherwise.
	 */
	/*
	 *@ ensures		stack.size() == 0 ==> \result == true;
	 *				stack.size() > 0 ==> \result == false;
	 */
	/*@ pure */public /*@ non_null */boolean emptyStack() {
		return stack.size() == 0;
	}
	
	
	// ----- Commands -----
	
	/**
	 * Adds a given score to the given player. 
	 * @param playerID the player whose score will be adjusted. 
	 * @param score the amount of points that the player gets. 
	 */
	/*
	 *@ requires 	score >= 0;
	 *				getScore(playerID) == \old(getScore(playerID)) + score;
	 */
	public void addScore(/*@ non_null */int playerID, /*@ non_null */int score) {
		this.scores[playerID] += score;
	}
	
	/**
	 * Places the given piece in the right cell, and then adjusts values of 
	 * minRow, maxRow, minColumn and maxColumn so that the relevent parts of 
	 * the board can be printed by the TUI. 
	 * @param row the row in which piece is to be placed.
	 * @param column the column in which piece is to be placed. 
	 * @param piece the piece that is to be placed.
	 */
	/*
	 *@ requires 	isField(row, column);
	 *@ ensures		getCell(row, column) == piece;
	 *				row <= minRow && row - 5 > 1 ==> minRow == row - 5;
	 *				row <= minRow && row -5 <= 1 ==> minRow == 1;
	 *				row >= maxRox ==> maxRow == row + 5;
	 *				column <= minColumn && column - 5 > 1 ==> minColumn == column - 5;
	 *				column <= minColumn && column -5 <= 1 ==> minColumn == 1;
	 *				column >= maxColumn ==> maxColumn == row + 5;
	 */
	public void setPiece(/*@ non_null */int row, /*@ non_null */int column,
						/*@ non_null */Piece piece) {
		board[row][column] = piece;
		if (row <= minRow) {
			if (row - 5 > 1) {
				minRow = row - 5;
			} else {
				minRow = 1;
			}
		} else if (row >= maxRow) {
			maxRow = row + 5;
		}
		if (column <= minColumn) {
			if (column - 5 > 1) {
				minColumn = column - 5;
			} else {
				minColumn = 1;
			}
		} else if (column >= maxColumn) {
			maxColumn = column + 5;
		}
		setChanged();
	}
	
	/**
	 * Sets the lastMadeMove to moveCount when a place or trade has been made. 
	 * @param moveCount the number of turns that have been take so far in the game.
	 */
	/*
	 *@ requires 	0 <= moveCount;
	 *@ ensures		lastMadeMove == moveCount;
	 */
	public void setLastMadeMove(/*@ non_null */int moveCount) {
		lastMadeMove = moveCount;
	}
	
	/**
	 * Empties the whole board.
	 */
	/*
	 *@ ensures		(\forall int i, j; 0 <= i, 0 <= j, i <= j, 0 j <= 183;
	 *					getCell(i, j) == null);
	 */
	public void reset() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = null;
			}
		}
	}
	
	/**
	 * Creates a new Board that exactly copies the occupation of this Board.
	 * @return a Board.
	 */
	/*
	 *@ ensures 	(\forall int i, j; 0 <= i, 0 <= j, i <= j, 0 j <= 183;
	 *					\result.getCell(i, j) == board.getCell(i, j));
	 */
	public /*@ non_null */Board deepCopy() {
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
	/*
	 *@ ensures 	stack.size() == \old(stack.size()) + 108;
	 */
	public void fillStack() {
		Set<Piece.Color> colors = EnumSet.complementOf(EnumSet.of(Piece.Color.DEFAULT));
		Set<Piece.Shape> shapes = EnumSet.complementOf(EnumSet.of(Piece.Shape.BLOCKED));
		for (Piece.Color color: colors) {
			for (Piece.Shape shape: shapes) {
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
	/*
	 *@ requires 	!(isEmpty());
	 *@ ensures		stack.size() == \old(stack.size()) - 1;
	 */
	public Piece draw() {
		return stack.remove(0);
	}
	
	/**
	 * Places the pieces received from a player in a trade back in the stack and shuffles the stack.
	 * @param pieces the pieces received from a player.
	 */
	/*
	 *@ ensures		stack.size() == \old(stack.size()) + pieces.length;
	 */
	public void tradeReturn(/*@ non_null */Piece[] pieces) {
		for (int i = 0; i < pieces.length; i++) {
			stack.add(pieces[i]);
		}
		Collections.shuffle(stack);
	}
	
	/**
	 * Determines the score of a given array of Moves. 
	 * @param moves the moves to be made. 
	 * @return the score of the given moves. 
	 */
	/*@
	 	requires 	moves.length >= 0;
	 	ensures		0 < \result;
	*/
	public /* @non_null */int getScore(/* @non_null */ Move[] moves) {
		Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
		int result = 0;
		
		// Determining the score when only one Place is made. 
		if (places.length == 1) {
			result = getScoreOnePlace(places);
			
		// Determining the score when multiple Places have been made. 
		} else {
			
			// Determining the score if the Places create a row. 
			if (places[0].getRow() == places[1].getRow()) {
				result = getScoreRowWise(places);
				
			// Determining the score if the Places create a column. 
			} else if (places[0].getColumn() == places[1].getColumn()) {
				result = getScoreColumnWise(places);
			}
		}
		return result;
	}
	
	/**
	 * Determines the score of a place with only one Piece.
	 * @param places the Place that is being made. 
	 * @return the score of the given place. 
	 */
	/*
	 *@ requires	places.length == 1;
	 *@ ensures		0 < \result;
	 */
	public /*@ non_null */int getScoreOnePlace(Place[] places) {
		int result = 0;
		int row = getRowLength(places[0].getRow(), places[0].getColumn());
		int column = getColumnLength(places[0].getRow(), places[0].getColumn());
		if (row > 1) {
			result += row;
			if (row == 6) {
				result += row;
			}
		}
		if (column > 1) {
			result += column;
			if (column == 6) {
				result += column;
			}
		}
		if (column == 1 && row == 1) {
			result = 1;
		}
		return result;
	}
	
	/**
	 * Calculates the score knowing the places are placed on the same Row.
	 * @param places the places for which the score is calculated.
	 * @return the score.
	 */
	/*
	 *@ requires	1 < place.length && place.length < 7;
	 *@ ensures		0 <= \result;
	 */
	public /*@ non_null */int getScoreRowWise(/*@ non_null */Place[] places) {
		int result = 0;
		result = getRowLength(places[0].getRow(), places[0].getColumn());
		if (result == 6) {
			result += result;
		}
		for (int i = 0; i < places.length; i++) {
			int column =  getColumnLength(places[i].getRow(), places[i].getColumn());
			if (column > 1) {
				result = result + column;
			}
			if (column == 6) {
				result += column;
			}
		}
		return result;
	}
	
	/**
	 * Calculates the score knowing the places are placed in the same Column.
	 * @param places the places for which the score is calculated
	 * @return the score
	 */
	/*
	 *@ requires	1 < place.length && place.length < 7;
	 *@ ensures		0 <= \result;
	 */
	public /* @non_null*/ int getScoreColumnWise(/*@ non_null */Place[] places) {
		int result = 0;
		result = getColumnLength(places[0].getRow(), places[0].getColumn());
		if (result == 6) {
			result += result;
		}
		for (int i = 0; i < places.length; i++) {
			int row = getRowLength(places[i].getRow(), places[i].getColumn());
			if (row > 1) {
				result = result + row; 
			}
			if (row == 6) {
				result += row;
			}
		}
		return result;
		
	}
	
	/**
	 * Tests if a given array of Moves contains only valid Moves.
	 * It tests if all the moves are of the same type,
	 * if a placing of Pieces is one straight row or column,
	 * and finally if the players makes Moves with Pieces that are in its Hand.
	 * If any of the above conditions is violated, the turn is skipped. 
	 * @param moves the Moves that the given Player wants to make. 
	 * @param player the Player that wants to make the given Moves. 
	 * @return true if the Moves are valid, false when invalid. 
	 */
	/*
	 *@ requires 	moves.length < player.getHand().size();
	 */
	public /* @non_null*/boolean validMove(/* @non_null*/Move[] moves, /* @non_null*/Player player)
			throws InvalidMoveException {
		boolean result = true;
		if (moves.length == 0) {
			throw new InvalidMoveException("No moves given");
		} else {
			if (moves[0] instanceof Place) {
				Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);				
				// Start with tests.
				allPlaceMoves(moves);
				cellsAreValid(places);
				cellsAreAvailable(places);
				isConnected(places);
				
				// Create a deep copy of the board and place Pieces on it. 
				Board deepCopyBoard = this.deepCopy();
				for (Place place: places) {
					deepCopyBoard.setPiece(place.getRow(), place.getColumn(), place.getPiece());
				}
				
				boolean isRow;
				boolean isColumn;
				
				// Continue the tests.
				isRow = deepCopyBoard.isRow(places);
				isColumn = deepCopyBoard.isColumn(places);
				
				if (isRow) {
					deepCopyBoard.isUninterruptedRow(places);
					deepCopyBoard.pieceIsConnectedRowAndUnique(places);
					deepCopyBoard.piecesFitInColumns(places);
				} else if (isColumn) {
					deepCopyBoard.isUninterruptedColumn(places);
					deepCopyBoard.pieceIsConnectedColumnAndUnique(places);
					deepCopyBoard.piecesFitInRows(places);
				} else {
					throw new InvalidMoveException("You are trying to place pieces " +
												"in multiple rows and columns.");
				}
				playerHasPiece(moves, player);
			}
			
			if (moves[0] instanceof Trade) {
				if (moves.length == 1 && moves[0].getPiece() == null) {
					result = true;
				} else {
					allTradeMoves(moves);
					playerHasPiece(moves, player);
				}
			}
		}
		return result;
	}
	
	/**
	 * Tests if all the moves of the Player are instances of Place. 
	 * @param moves the moves that the Player wants to make. 
	 * @throws InvalidMoveException
	 */
	/*
	 *@ requires	(\forall int i; i <= 0, moves.length < i; moves[i] instanceof Place);
	 */
	public void allPlaceMoves(/*@ non_null */Move[] moves) throws InvalidMoveException {
		for (int i = 1; i < moves.length; i++) {
			if (!(moves[i] instanceof Place)) {
				throw new InvalidMoveException("You are trying to place tiles and trade"
						+ " in the same turn. This is not allowed.");
			}
		}
	}
	
	
	
	/**
	 * Tests if all the moves of the Player are instances of Trade. 
	 * @param moves the moves that the Player wants to make. 
	 * @throws InvalidMoveException
	 */
	/*
	 *@ requires	(\forall int i; i <= 0, moves.length < i; moves[i] instanceof Place);
	 */
	public void allTradeMoves(/*@ non_null */Move[] moves) throws InvalidMoveException {
		for (int i = 1; i < moves.length; i++) {
			if (!(moves[i] instanceof Trade)) {
				throw new InvalidMoveException("You are trying to place tiles and trade"
						+ " in the same turn. This is not allowed.");
			}
		}
	}
	
	/**
	 * Tests that the cells that the Player wants to Place Pieces in are empty. 
	 * @param moves the moves that the Player wants to make.
	 * @throws InvalidMoveException 
	 */
	/*@ pure */ public void cellsAreAvailable(/*@ non_null */Place[] places) 
				throws InvalidMoveException {
		for (Place place: places) {
			if (!isEmpty(place.getRow(), place.getColumn())) {
				throw new InvalidMoveException("You are trying to place a tile"
						+ " in a cell that is already occupied.");
			}
		}
	}
	
	/**
	 * Tests if a Place places a tile in a cell inside the Board.
	 * @param moves the moves that the Player wants to make.
	 * @param board the Board that the Player wants to make the Move on. 
	 * @throws InvalidMoveException
	 */
	/*@ pure */public void cellsAreValid(/*@ non_null */Place[] places)
			throws InvalidMoveException {
		for (Place place: places) {
			if (!isField(place.getRow(), place.getColumn())) {
				throw new InvalidMoveException("You are trying to place a tile outside the board.");
			}
		}
	}
	
	/**
	 * Tests if the places are on one row.
	 * @param places the places to test
	 * @return true if all places are on the same row
	 */
	public /*@ non_null*/ boolean isRow(/*@ non_null*/ Place[] places) {
		boolean isRow = true;
		for (int i = 0; i < places.length; i++) {
			isRow = isRow && places[i].getRow() == places[0].getRow();
		}
		return isRow;
	}
	
	/**
	 * Tests if the places are on one column.
	 * @param places the places to test
	 * @return true if all the places are on the same column
	 */
	public /*@ non_null*/boolean isColumn(/*@ non_null*/ Place[] places) {
		boolean isColumn = true;
		for (int i = 0; i < places.length; i++) {
			isColumn = isColumn && places[i].getColumn() == places[0].getColumn();
		}
		return isColumn;
	}
	
	/**
	 * Tests if the Places are in 1 straight line (row) and if there are no gaps. 
	 * @param moves the Places to be placed on Board b. 
	 * @param b the Board on which the Places are put. 
	 * @return true when the Places create 1 straight line without gaps, false when otherwise. 
	 * @throws InvalidMoveException
	 */
	/*@ pure */public void isUninterruptedRow(/* @non_null */Place[] places) 
			throws InvalidMoveException {	
		if (places.length != 1) {
			int minColumnPlace = places[0].getColumn();
			int maxColumnPlace = minColumnPlace;
			for (int i = 1; i < places.length; i++) {
				if (places[0].getRow() != places[i].getRow()) {
					throw new InvalidMoveException("You are trying to place tiles"
							+ " on seperate rows.");
				}
				if (places[i].getColumn() < minColumnPlace) {
					minColumnPlace = places[i].getColumn();
				} else if (places[i].getColumn() > maxColumnPlace) {
					maxColumnPlace = places[i].getColumn();
				}
			}
			
			for (int i = minColumnPlace; i <= maxColumnPlace; i++) {
				if (isEmpty(places[0].getRow(), i)) {
					throw new InvalidMoveException("You are trying to place two seperate rows"
							+ " on the board.");
				}
			}
		}
	}
	
	/**
	 * Tests if the Places are in 1 straight line (column) and if there are no gaps. 
	 * @param moves the Places to be placed on Board b. 
	 * @param b the Board on which the Places are put. 
	 * @return true when the Places create 1 straight line without gaps, false when otherwise. 
	 */
	/*@ pure */public void isUninterruptedColumn(/* @NunNull */Place[] places)
			throws InvalidMoveException {
		if (places.length != 1) {
			int minRowPlace = places[0].getRow();
			int maxRowPlace = minRowPlace;
			for (int i = 1; i < places.length; i++) {
				if (places[0].getColumn() != places[i].getColumn()) {
					throw new InvalidMoveException("You are trying to place tiles"
							+ " on seperate columns.");
				}
				if (places[i].getRow() < minRowPlace) {
					minRowPlace = places[i].getRow();
				} else if (places[i].getRow() > maxRowPlace) {
					maxRowPlace = places[i].getRow();
				}	
			}	
			for (int i = minRowPlace; i <= maxRowPlace; i++) {
				if (isEmpty(i, places[0].getColumn())) {
					throw new InvalidMoveException("You are trying to place two seperate columns"
							+ " on the board.");
				}
			}
		}
	}
	
	
	/**
	 * Tests if the row is has a fixed color or shape. 
	 * @param piece the Piece that is placed on the board. 
	 * @param wholeRow a set with all the Pieces that are in the row in which the Piece belongs. 
	 * @throws InvalidMoveException
	 */
	/*@ pure */public void isUniqueColorOrShape(/*@ non_null */Piece piece,
					/*@ non_null */ArrayList<Piece> wholeRow) throws InvalidMoveException {
		boolean fixedColor = true;
		boolean fixedShape = true;
		for (int i = 0; i < wholeRow.size(); i++) {
			fixedColor = fixedColor && wholeRow.get(i).getColor() == piece.getColor();
		}
		for (int i = 0; i < wholeRow.size(); i++) {
			fixedShape = fixedShape && wholeRow.get(i).getShape() == piece.getShape();
		}
		if (!(fixedColor || fixedShape)) {
			throw new InvalidMoveException(piece.toString()
					+ ", does not fit in the row.");
		} else if (fixedColor) {
			hasUniqueShape(piece, wholeRow);
		} else if (fixedShape) {
			hasUniqueColor(piece, wholeRow);
		}
	}
	
	/**
	 * Checks if a Piece added to a set of Places has a unique color. 
	 */
	/*@ pure */public void hasUniqueColor(/*@ non_null */Piece piece,
						/*@ non_null */ArrayList<Piece> wholeRow)
			throws InvalidMoveException {
		for (Piece wholeRowPiece: wholeRow) {
			if (wholeRowPiece.getColor() == piece.getColor()) {
				throw new InvalidMoveException(piece.toString() + " does not have a unique color"
						+ " in the row that you try to add to.");
			}
		}
	}
	
	/**
	 * Checks if a Piece added to a set of Places has a unique shape. 
	 */
	/*@ pure */public void hasUniqueShape(/*@ non_null */Piece piece,
						/* @non_null */ArrayList<Piece> wholeRow)
			throws InvalidMoveException {
		for (Piece wholeRowPiece: wholeRow) {
			if (wholeRowPiece.getShape() == piece.getShape()) {
				throw new InvalidMoveException(piece.toString() + " does not have a unique shape"
						+ " in the row that you try to add to.");
			}
		}
	}	
	
	/**
	 * Creates an ArrayList of Pieces that are in the row that player wants to add to. 
	 * It then tests if the Piece the Player wants to add to this row is unique in it. 
	 * @param moves the array of Places that are to be made. 
	 * @param b the board on which the Places are made. 
	 * @return true is the Places are valid, false when not. 
	 */
	/*@ pure */public void pieceIsConnectedRowAndUnique(/*@ non_null */Place[] places)
			throws InvalidMoveException {
		
		// Create an ArrayList<Piece> of Pieces that are in the row
		// that is added to, but not in the Place[].
		ArrayList<Piece> wholeRow = new ArrayList<>();
		int row = places[0].getRow();
		int column = places[0].getColumn();
		boolean connected = true;
		for (int i = column - 1; connected; i--) {
			if (isEmpty(row, i)) {
				connected = false;
			} else {
				boolean found = false;
				for (Place place: places) {
					if (place.getColumn() == i) {
						found = true;
					}
				}
				if (!found) {
					wholeRow.add(getCell(row, i));
				}
			}
		}
		connected = true;
		for (int i = column + 1; connected; i++) {
			if (isEmpty(row, i)) {
				connected = false;
			} else {
				boolean found = false;
				for (Place place: places) {
					if (place.getColumn() == i) {
						found = true;
					}
				}
				if (!found) {
					wholeRow.add(getCell(row, i));
				}
			}
		}
				
		for (Place place: places) {	
			Piece piece = place.getPiece();
			isUniqueColorOrShape(piece, wholeRow);
			wholeRow.add(piece);
		} 
	}
	
	/**
	 * Checks if the given places fit in the columns. It is to be called knowing the places
	 * are all on the same row so it checks the places individually.
	 * @param places the places to check
	 * @throws InvalidMoveException if it finds a piece that does not fit in a column
	 */
	/*
	 * @requires	\forall places p, r; places.contains(p) && places.contains(r)
	 * 					&& !p.equals(r);  p.getRow() == r.getRow();
	 */
	/*@ pure */public void piecesFitInColumns(/*@ non_null */Place[] places)
					throws InvalidMoveException {
		for (Place place: places) {
			ArrayList<Piece> wholeRow = new ArrayList<>();
			int row = place.getRow();
			int column = place.getColumn();
			boolean connected = true;
			for (int i = row - 1; connected; i--) {
				if (isEmpty(i, column)) {
					connected = false;
				} else {
					wholeRow.add(getCell(i, column));
				}
			}
			connected = true;
			for (int i = row + 1; connected; i++) {
				if (isEmpty(i, column)) {
					connected = false;
				} else {
					wholeRow.add(getCell(i, column));
				}
			}
			Piece piece = place.getPiece();
			isUniqueColorOrShape(piece, wholeRow);
		} 
	}
	
	/**
	 * Creates an ArrayList of Pieces that are in the row that player wants to add to. 
	 * It then tests if the Piece the Player wants to add to this row is unique in it. 
	 * @param moves the array of Places that are to be made. 
	 * @param b the board on which the Places are made. 
	 * @return true is the Places are valid, false when not. 
	 */
	/*
	 * @requires	moves.length < 7;
	 */
	/*@ pure */public void pieceIsConnectedColumnAndUnique(/*@ non_null */Place[] places)
			throws InvalidMoveException {
		// Create an ArrayList<Piece> of Pieces that are in the row that is added to,
		// but not in the Place[].
		ArrayList<Piece> wholeRow = new ArrayList<>();
		int row = places[0].getRow();
		int column = places[0].getColumn();
		boolean connected = true;
		for (int i = row - 1; connected; i--) {
			if (isEmpty(i, column)) {
				connected = false;
			} else {
				boolean found = false;
				for (Place place: places) {
					if (place.getRow() == i) {
						found = true;
					}
				}
				if (!found) {
					wholeRow.add(getCell(i, column));
				}
			}
		}
		connected = true;
		for (int i = row + 1; connected; i++) {
			if (isEmpty(i, column)) {
				connected = false;
			} else {
				boolean found = false;
				for (Place place: places) {
					if (place.getRow() == i) {
						found = true;
					}
				}
				if (!found) {
					wholeRow.add(getCell(i, column));
				}
			}
		}		
		for (Place place: places) {	
			Piece piece = place.getPiece();
			isUniqueColorOrShape(piece, wholeRow);
			wholeRow.add(piece);
		} 
	}
	
	/**
	 * Checks if the given places fit in the rows. It is to be called knowing all places are
	 * in the same Column so it checks the places individually.
	 * @param places the places to check
	 * @throws InvalidMoveException if it finds a piece that does not fit in a row.
	 */
	/*
	 * @requires	\forall places p, r; places.contains(p) && places.contains(r)
	 * 					&& !p.equals(r);  p.getColumn() == r.getColumn();
	 */
	/*@ pure */public void piecesFitInRows(/*@ non_null*/Place[] places)
					throws InvalidMoveException {
		for (Place place: places) {
			ArrayList<Piece> wholeRow = new ArrayList<>();
			int row = place.getRow();
			int column = place.getColumn();
			boolean connected = true;
			for (int i = column - 1; connected; i--) {
				if (isEmpty(row, i)) {
					connected = false;
				} else {
					wholeRow.add(getCell(row, i));
				}
			}
			connected = true;
			for (int i = column + 1; connected; i++) {
				if (isEmpty(row, i)) {
					connected = false;
				} else {
					wholeRow.add(getCell(row, i));
				}
			}
			Piece piece = place.getPiece();
			isUniqueColorOrShape(piece, wholeRow);
		}
	}
	
	/**
	 * Tests if the Player has the given Piece in its Hand.
	 */
	/*@ pure */public void playerHasPiece(/*@ non_null */Move[] moves,
					/*@ non_null */Player player) throws InvalidMoveException {
		for (Move move: moves) {
			if (!(player.getHand().contains(move.getPiece()))) {
				throw new InvalidMoveException("You are trying to place"
						+ " a tile that you do not have in your hand.");
			}
		}
	}
	
	/**
	 * Tests if every Place of places is connected to the another Piece.
	 * @param places the Places to be made. 
	 */
	/*@ pure */public void isConnected(/*@ non_null */Place[] places) throws InvalidMoveException {
		if (!this.isEmpty(91, 91)) {
			boolean result = false;
			for (Place p: places) {
				result = result || ((!this.isEmpty(p.getRow() - 1, p.getColumn())) ||
						(!this.isEmpty(p.getRow() + 1, p.getColumn())) ||
						(!this.isEmpty(p.getRow(), p.getColumn() - 1)) ||
						(!this.isEmpty(p.getRow(), p.getColumn() + 1)));
			}
			if (!result) {
				throw new InvalidMoveException("Your row of tiles is not connected to other rows.");
			}
		}
	}

	
	
}
