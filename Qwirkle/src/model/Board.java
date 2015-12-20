package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Board {

	public static final int DIM = 185;
	public static final int MAX_STACK_SIZE = 108;

	/**
	 * The class that models the board used by the Qwirkle game.
	 * The board will be stored as a Piece[][]. of 185*185 with start point being 91,91.
	 * The board will most likely also store the stack of pieces that are available for drawing.
	 */
	private int size;
	private Piece[][] board;
	private ArrayList<Piece> stack;
	
	// --------------- Constructors -----------------
	public Board() {
		size = DIM;
		board = new Piece[DIM][DIM];
		stack = new ArrayList<Piece>();
		fillStack();
	}
	public Board(int dimension) {
		size = dimension;
		board = new Piece[dimension][dimension];
		stack = new ArrayList<Piece>();
		fillStack();
	}
	
	// --------------- Queries ----------------------
	public Piece[][] getBoard() {
		return board;
	}
	
	public Piece get(int row, int column) {
		return board[row][column];
	}
	
	public int getSize() {
		return size;
	}
	// --------------- Commands ---------------------
	public void setPiece(int row, int column, Piece piece) {
		board[row][column] = piece;
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
	
	public Piece draw() {
		return stack.remove(0);
	}
}
