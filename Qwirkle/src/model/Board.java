package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import exceptions.InvalidMoveException;

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
	private int[] scores;
	
	// ----- Constructors -----
	
	/**
	 * Creates a new Board object of default size (183 x 183).
	 */
	/*
	 *@ ensures		this.getSize == DIM;
	 *				this.getMinRow == 86;
	 *				this.getMaxRow == 97;
	 *				this.getMinColumn == 85;
	 *				this.getMaxColumn == 97;
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
	/*@ pure */public /*@ NonNull */Piece[][] getBoard() {
		return board;
	}
	
	/**
	 * Returns the Piece in a given cell indicated by row and column. 
	 * @return piece the Piece in the given cell. 
	 */
	/*
	 *@ requires 	row >= minRow && row <= maxRow;
	 * 				column >= minColumn && column <= maxColumn;
	 */
	/*@ pure */public Piece getCell(/*@ NonNull */int row, /*@ NonNull */int column) {
		return board[row][column];
	}
	
	/**
	 * Returns the size of the edges of a Board. 
	 * @return the size of a Board.
	 */
	/*
	 *@ ensures 	\result == maxRow;
	 */
	/*@ pure */public /*@ NonNullable */int getSize() {
		return size;
	}
	
	/**
	 * Tests if a cell in the given row and column is empty. 
	 * @param row the row of the cell.
	 * @param column the column of the cell.
	 * @return true if the cell is empty, false when occupied. 
	 */
	/*@pure*/public /*@ NonNull */boolean isEmpty(/*@ NonNull */int row, /*@ NonNull */int column) {
		return board[row][column] == null;
	}
	 
	/**
	 * Tests if the cell in the given row and column is on the <code>Board</code>.
	 * @return true if the row and column refer to a valid cell on the board, false otherwise.
	 */
	/*
	 *@ ensures 	\result == (0 <= row && row < size && 0 <= column && column < size);
	 */
	/*@pure*/public /*@ NonNull */boolean isField(/*@ NonNull */int row, /*@ NonNull */int column) {
		return 0 <= row && row < size && 0 <= column && column < size;
	}
	
	/**
	 * Returns when the last made Move was made. 
	 * @return the last made Move. 
	 */
	/*@ pure */public /*@ NonNull */int getLastMadeMove() {
		return lastMadeMove;
	}
	
	/**
	 * Returns the minimal size of the rows on the Board. 
	 * @return the minimal row size of the Board. 
	 */
	/*@ pure */public /*@ NonNull */int getMinRow() {
		return minRow;
	}
	
	/**
	 * Returns the maximum size of the rows on the Board. 
	 * @return the maximum row size of the Board. 
	 */
	/*@ pure */public /*@ NonNull */int getMaxRow() {
		return maxRow;
	}
	
	/**
	 * Returns the minimal size of the columns on the Board. 
	 * @return the minimal column size of the Board. 
	 */
	/*@ pure */public /*@ NonNull */int getMinColumn() {
		return minColumn;
	}
	
	/**
	 * Returns the maximum size of the columns on the Board. 
	 * @return the maximum column size of the Board. 
	 */	
	/*@ pure */public /*@ NonNull */int getMaxColumn() {
		return maxColumn;
	}
	
	/**
	 * Determines how long a row is given a certain cell, given by row and column. 
	 * 
	 *@return the length of a row.
	 */
	/*
	 *@ requires 	0 <= row && row < size && 0 <= column && column < size;
	 *@ ensures 	1 <= \result && 7 > \result;
	 */
	/*@ pure */public /*@ NonNull */int getRowLength(int row, int column) {
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
	
	/*@ pure */public /*@ NonNull */int getColumnLength(int row, int column) {
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
	
	/*@ pure */public /*@ NonNull */int getScore(int playerID) {
		return scores[playerID];
	}
	
	/*@ pure */public /*@ NonNull */boolean emptyStack() {
		return stack.size() == 0;
	}
	
	/*@ pure */public /*@ NonNull */ ArrayList<Piece> getStack() {
		return stack;
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
	public void addScore(int playerID, int score) {
		this.scores[playerID] += score;
	}
	
	public void setPiece(int row, int column, Piece piece) {
		board[row][column] = piece;
		if (row < minRow) {
			minRow = row - 5;
		} else if (row > maxRow) {
			maxRow = row + 5;
		}
		if (column < minColumn) {
			minColumn = column - 5;
		} else if (column > maxColumn) {
			maxColumn = column + 5;
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
	public Piece draw() {
		return stack.remove(0);
	}
	
	/**
	 * Places the pieces received from a player in a trade back in the stack and shuffles the stack.
	 * @param pieces the pieces received from a player.
	 */
	public void tradeReturn(Piece[] pieces) {
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
	public int getScore(/* @NonNull */ Move[] moves) {
		Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
		int result = 0;
		
		// Determining the score when only one Place is made. 
		if (places.length == 1) {
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
		
		// Determining the score when multiple Places have been made. 
		} else {
			
			// Determining the score if the Places create a row. 
			if (places[0].getRow() == places[1].getRow()) {
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
			// Determining the score if the Places create a column. 
			} else if (places[0].getColumn() == places[1].getColumn()) {
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
	 * @requires 	moves.length < player.getHand().size();
	 */
	public /* @NonNull*/boolean validMove(/* @NonNull*/Move[] moves, /* @NonNull*/Player player) {
		boolean result = true;
		
		if (moves[0] instanceof Place) {
			Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
			try {				
				// Start with tests.
				allPlaceMoves(moves);
				cellsAreAvailable(places, this);
				cellsAreValid(places, this);
				isConnected(places);
				
				// Create a deep copy of the board and place Pieces on it. 
				Board deepCopyBoard = new Board().deepCopy();
				for (Place place: places) {
					deepCopyBoard.setPiece(place.getRow(), place.getColumn(), place.getPiece());
				}
				
				// Continue the tests.
				isUninterruptedRow(places, deepCopyBoard);
				isUninterruptedColumn(places,deepCopyBoard);
				pieceIsConnectedRowAndUnique(places, deepCopyBoard);
				pieceIsConnectedColumnAndUnique(places, deepCopyBoard);
				playerHasPiece(moves, player);
			} catch (InvalidMoveException e) {
				e.getInfo();
				result = false;
			}
		}
		
		if (moves[0] instanceof Trade) {
			try {
				allTradeMoves(moves);
				playerHasPiece(moves, player);
			} catch (InvalidMoveException e) {
				e.getInfo();
				result = false;
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
	 *@ requires	moves[0] instanceof Place;
	 */
	public void allPlaceMoves(/*@ NonNull */Move[] moves) throws InvalidMoveException {
		 for (int i = 1; i < moves.length; i++) {
			 if (!(moves[i] instanceof Place)) {
				throw new InvalidMoveException("You are trying to place tiles and trade in the same turn. This is not allowed.");
			}
		}
	}
	
	
	
	/**
	 * Tests if all the moves of the Player are instances of Trade. 
	 * @param moves the moves that the Player wants to make. 
	 * @throws InvalidMoveException
	 */
	/*
	 *@ requires	moves[0] instanceof Trade;
	 */
	public void allTradeMoves(/*@ NonNull */Move[] moves) throws InvalidMoveException {
		 for (int i = 1; i < moves.length; i++) {
			 if (!(moves[i] instanceof Trade)) {
				throw new InvalidMoveException("You are trying to place tiles and trade in the same turn. This is not allowed.");
			}
		}
	}
	
	/**
	 * Tests that the cells that the Player wants to Place Pieces in are empty. 
	 * @param moves the moves that the Player wants to make.
	 * @throws InvalidMoveException 
	 */
	/*
	 *@ requires	
	 */
	public void cellsAreAvailable(/*@ NonNull */Place[] places, /*@ NonNull */Board board) throws InvalidMoveException {
		for (Place place: places) {
			if (!board.isEmpty(place.getRow(), place.getColumn())) {
				throw new InvalidMoveException("You are trying to place a tile in a cell that is already occupied.");
			}
		}
	}
	
	/**
	 * Tests if a Place places a tile in a cell inside the Board.
	 * @param moves the moves that the Player wants to make.
	 * @param board the Board that the Player wants to make the Move on. 
	 * @throws InvalidMoveException
	 */
	public void cellsAreValid(/*@ NonNull */Place[] places, /*@ NonNull */Board board) throws InvalidMoveException{
		for (Place place: places) {
			if (!board.isField(place.getRow(), place.getColumn())) {
				throw new InvalidMoveException("You are trying to place a tile outside the board.");
			}
		}
	}
	
	/**
	 * Tests if the Places are in 1 straight line (row) and if there are no gaps. 
	 * @param moves the Places to be placed on Board b. 
	 * @param b the Board on which the Places are put. 
	 * @return true when the Places create 1 straight line without gaps, false when otherwise. 
	 * @throws InvalidMoveException
	 */
	public void isUninterruptedRow(/* @NunNull */Place[] places, /* @NunNull */Board board) throws InvalidMoveException {
		boolean isRow = true;
		for (int i = 0; i < places.length; i++) {
			isRow = isRow && places[i].getRow() == places[0].getRow();
		}
		if (places.length != 1 && isRow) {
			int minColumn = places[0].getColumn();
			int maxColumn = minColumn;
			for (int i = 1; i < places.length; i++) {
				if (places[0].getRow() != places[i].getRow()) {
					throw new InvalidMoveException("You are trying to place Pieces on seperate rows.");
				}
				if (places[i].getColumn() < minColumn) {
					minColumn = places[i].getColumn();
				} else if (places[i].getColumn() > maxColumn) {
					maxColumn = places[i].getColumn();
				}
			}
			
			for (int i = minColumn; i <= maxColumn; i++) {
				if (board.isEmpty(places[0].getRow(), i)) {
					throw new InvalidMoveException("You are trying to place two seperate rows on the board.");
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
	public void isUninterruptedColumn(/* @NunNull */Place[] places, /* @NunNull */Board board) throws InvalidMoveException {
		boolean isColumn = true;
		for (int i = 0; i < places.length; i++) {
			isColumn = isColumn && places[i].getColumn() == places[0].getColumn();
		}
		if (places.length != 1 && isColumn) {
			int minRow = places[0].getRow();
			int maxRow = minRow;
			for (int i = 1; i < places.length; i++) {
				if (places[0].getColumn() != places[i].getColumn()) {
					throw new InvalidMoveException("You are trying to place Pieces on seperate columns.");
				}
				if (places[i].getRow() < minRow) {
					minRow = places[i].getRow();
				} else if (places[i].getRow() > maxRow) {
					maxRow = places[i].getRow();
				}	
			}	
			for (int i = minRow; i <= maxRow; i++) {
				if (board.isEmpty(places[0].getColumn(), i)) {
					throw new InvalidMoveException("You are trying to place two seperate columns on the board.");
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
	public void isUniqueColorOrShape(/* @NonNull */Piece piece, /* @NonNull */ArrayList<Piece> wholeRow) throws InvalidMoveException {
		boolean fixedColor = true;
		boolean fixedShape = true;
		for (int i = 0; i < wholeRow.size(); i++) {
			fixedColor = fixedColor && wholeRow.get(0).getColor() == piece.getColor();
		}
		for (int i = 0; i < wholeRow.size(); i++) {
			fixedShape = fixedShape && wholeRow.get(0).getShape() == piece.getShape();
		}
		if (! (fixedColor || fixedShape)) {
			throw new InvalidMoveException("The row that you try to place does not have one fixed color or shape.");
		} else if (fixedColor) {
			hasUniqueShape(piece, wholeRow);
		} else if (fixedShape) {
			hasUniqueColor(piece, wholeRow);
		}
	}
	
	/**
	 * Checks if a Piece added to a set of Places has a unique color. 
	 */
	public void hasUniqueColor(/* @NonNull */Piece piece, /* @NonNull */ArrayList<Piece> wholeRow) throws InvalidMoveException {
		for (Piece wholeRowPiece: wholeRow) {
			if (wholeRowPiece.getColor() == piece.getColor()) {
				throw new InvalidMoveException("You try to place a row with tiles of the same color.");
			}
		}
	}
	
	/**
	 * Checks if a Piece added to a set of Places has a unique shape. 
	 */
	public void hasUniqueShape(/* @NonNull */Piece piece, /* @NonNull */ArrayList<Piece> wholeRow) throws InvalidMoveException {
		for (Piece wholeRowPiece: wholeRow) {
			if (wholeRowPiece.getShape() == piece.getShape()) {
				throw new InvalidMoveException("You try to place a row with tiles of the same shape.");
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
	/*
	 * @requires	moves.length < 7;
	 */
	public void pieceIsConnectedRowAndUnique(/*@ NonNull */Place[] places, /*@ NonNull */Board board) throws InvalidMoveException {
		
		// Create an ArrayList<Piece> of Pieces that are in the row that is added to, but not in the Place[].
		ArrayList<Piece> wholeRow = new ArrayList<>();
		int row = places[0].getRow();
		int column = places[0].getColumn();
		boolean connected = true;
		for (int i = column - 1; connected; i--) {
			if (board.isEmpty(row, i)) {
				connected = false;
			} else {
				boolean found = false;
				for (Place place: places) {
					if(place.getColumn() == i) {
						found = true;
					}
				} if (!found) {
					wholeRow.add(board.getCell(row, i));
				}
			}
		}
		connected = true;
		for (int i = column + 1; connected; i++) {
			if (board.isEmpty(row, i)) {
				connected = false;
			} else {
				boolean found = false;
				for (Place place: places) {
					if(place.getColumn() == i) {
						found = true;
					}
				} if (!found) {
					wholeRow.add(board.getCell(row, i));
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
	 * Creates an ArrayList of Pieces that are in the row that player wants to add to. 
	 * It then tests if the Piece the Player wants to add to this row is unique in it. 
	 * @param moves the array of Places that are to be made. 
	 * @param b the board on which the Places are made. 
	 * @return true is the Places are valid, false when not. 
	 */
	/*
	 * @requires	moves.length < 7;
	 */
	public void pieceIsConnectedColumnAndUnique(/*@ NonNull */Place[] places, /*@ NonNull */Board board) throws InvalidMoveException {
		// Create an ArrayList<Piece> of Pieces that are in the row that is added to, but not in the Place[].
		ArrayList<Piece> wholeRow = new ArrayList<>();
		int row = places[0].getRow();
		int column = places[0].getColumn();
		boolean connected = true;
		for (int i = row - 1; connected; i--) {
			if (board.isEmpty(i, column)) {
				connected = false;
			} else {
				boolean found = false;
				for (Place place: places) {
					if(place.getRow() == i) {
						found = true;
					}
				} if (!found) {
					wholeRow.add(board.getCell(i, column));
				}
			}
		}
		connected = true;
		for (int i = row + 1; connected; i++) {
			if (board.isEmpty(i, column)) {
				connected = false;
			} else {
				boolean found = false;
				for (Place place: places) {
					if(place.getRow() == i) {
						found = true;
					}
				} if (!found) {
					wholeRow.add(board.getCell(i, column));
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
	 * Tests if the Player has the given Piece in its Hand.
	 */
	public void playerHasPiece(Move[] moves, Player player) throws InvalidMoveException {
		for (Move move: moves) {
			if (!(player.getHand().contains(move.getPiece()))) {
				throw new InvalidMoveException("You are trying to place a tile that you do not have in your hand.");
			}
		}
	}
	
	/**
	 * Tests if every Place of places is connected to the another Piece.
	 * @param places the Places to be made. 
	 */
	/*
	 * @requires 	places.length < 7;
	 */
	public void isConnected(/* @NonNull */Place[] places) throws InvalidMoveException {
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
