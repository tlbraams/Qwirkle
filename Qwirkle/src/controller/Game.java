package controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import model.*;
import view.TUI;

public class Game implements Runnable {

	/**
	 * The game class will control the flow of a game using the Classes in the model package
	 * and interacting with the user via the View Clas(ses) in the view package.
	 */
	
	private Board board;
	private TUI view;
	private int playerCount;
	private Player[] players;
	private int aiTime;
	
	private int currentPlayerID;
	private int moveCounter;
	
	// -------------- Constructor -------------------
	public Game(int playerCount, Player[] players, int thinkTime){
		board = new Board();
		view = new TUI(this);
		this.playerCount = playerCount;
		this.players = new Player[this.playerCount];
		for(int i = 0; i < playerCount; i++) {
			this.players[i] = players[i];
		}
		aiTime = thinkTime;
	}
	
	public Game(int boardSize, int playerCount, Player[] players, int thinkTime){
		board = new Board(boardSize);
		view = new TUI(this);
		this.playerCount = playerCount;
		this.players = new Player[this.playerCount];
		for(int i = 0; i < playerCount; i++) {
			this.players[i] = players[i];
		}
		aiTime = thinkTime;
	}
	
	// -------------- Queries -----------------------
	public Board getBoard() {
		return board;
	}
	
	public TUI getView() {
		return view;
	}
	
	public int findMaxScore(HashSet<Piece> hand) {
		int max = 0;
		for(Piece p : hand) {
			Set<Piece> restHand = new HashSet<>(hand);
			restHand.remove(p);
			int color = 1;
			int shape = 1;
			for(Piece rp : restHand) {
				if(rp.getColor().equals(p.getColor()) && !rp.getShape().equals(p.getShape())) {
					color++;
				} else if (!rp.getColor().equals(p.getColor()) && rp.getShape().equals(p.getShape())) {
					shape++;
				}
			}
			if (color > max) {
				max = color;
			}
			if (shape > max) {
				max = shape;
			}
		}
		return max;
	}
	
	
	// --------------- Commands: -------------------------
	
	
	public void run() {
		String welcome = "NAMES";
		for(int i = 0; i < playerCount; i++) {
			welcome += " " + players[i].getName() + " " + players[i].getID();
		}
		welcome += " " + aiTime;
		System.out.println(welcome);
		playGame();
	}
	
	/**
	 * A given players makes the given valid moves. After making the moves,
	 * the player removes these tiles from its hand and fills the hand up to 6 again. 
	 * @param moves given moves
	 * @param player given player
	 */
	public void makeMove(Move[] moves, Player player) {
		Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
		for(Place m: places) {
			Piece piece = m.getPiece();
			player.remove(piece);
			int row = m.getRow();
			int column = m.getColumn();
			board.setPiece(row, column, piece);
			player.receive(board.draw());
		}
		board.setLastMadeMove(moveCounter);
	}
	
	public void tradePieces(Move[] moves, Player player) {
		Piece[] pieces = new Piece[moves.length];
		for(int i = 0; i < moves.length; i++) {
			pieces[i] = moves[i].getPiece();
			player.remove(pieces[i]);
			player.receive(board.draw());
		}
		board.tradeReturn(pieces);
	}
	
	public void findFirstPlayer() {
		int maxScore = 0;
		int playerNumber = 0;
		for(int i = 0; i < playerCount; i++) {
			HashSet<Piece> hand = players[i].getHand();
			int temp = findMaxScore(hand);
			if (temp > maxScore){
				maxScore = temp;
				playerNumber = i;
			}
		}
		currentPlayerID = playerNumber;
	}
	
	public void playGame() {
		for(int i = 0; i < playerCount; i++) {
			for(int j = 0; j < 6; j++) {
				Piece piece = board.draw();
				players[i].receive(piece);
			}	
		}
		findFirstPlayer();
		moveCounter = 0;
		boolean running = true;
		view.update();
		while (running) {
			Move[] moves = players[currentPlayerID].determineMove(board);
			if(validMove(moves, players[currentPlayerID])){
				moveCounter ++;
				if(moves[0] instanceof Place) {
					makeMove(moves, players[currentPlayerID]);
				} else if (moves[0] instanceof Trade) {
					tradePieces(moves, players[currentPlayerID]);
				}
			}			
			currentPlayerID = (currentPlayerID + 1) % playerCount;
			view.update();
		}
		
	}

	public boolean validMove(Move[] moves, Player player) {
		boolean result = true;
		if (moves[0] instanceof Place) {
			for(int i = 1; i < moves.length; i++) {
				result = result && moves[i] instanceof Place;
			}
			Board b = board.deepCopy();
			Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
			for(Place p : places) {
				b.setPiece(p.getRow(), p.getColumn(), p.getPiece());
				}
			result = result && isValidRow(places, b);
			result = result && isValidColumn(places, b);
		} else if (moves[0] instanceof Trade) {
			for (int i = 1; i < moves.length; i++) {
				result = result && moves[i] instanceof Trade;
			}
		}
		// Check if player has the tiles. 
		for (Move m: moves) {
			result = result && player.getHand().contains(m.getPiece());
		}
		return result;
	}
	
	public boolean isValidRow(Place[] moves, Board b) {
		boolean result = true;
		for(Place m : moves) {
			int row = m.getRow();
			int column = m.getColumn();
			Piece piece = m.getPiece();
			boolean connected = true;
			for(int i = column - 1; connected; i--) {
				if(b.isEmpty(row, i)) {
					connected = false;
				} else {
					Piece p = b.getCell(row, i);
					if (piece.getColor().equals(p.getColor()) && !piece.getShape().equals(p.getShape())) {
						result = result && true;
					} else if (!piece.getColor().equals(p.getColor()) && piece.getShape().equals(p.getShape())) {
						result = result && true;
					} else {
						result = false;
					}
				}
			}
			connected = true;
			for(int i = column + 1; connected; i++) {
				if(b.isEmpty(row, i)) {
					connected = false;
				} else {
					Piece p = b.getCell(row, i);
					if (piece.getColor().equals(p.getColor()) && !piece.getShape().equals(p.getShape())) {
						result = result && true;
					} else if (!piece.getColor().equals(p.getColor()) && piece.getShape().equals(p.getShape())) {
						result = result && true;
					} else {
						result = false;
					}
				}
			}
		}
		return result;
	}
	
	public boolean isValidColumn(Place[] moves, Board b) {
		boolean result = true;
		for(Place m : moves) {
			int row = m.getRow();
			int column = m.getColumn();
			Piece piece = m.getPiece();
			boolean connected = true;
			for(int i = row - 1; connected; i--) {
				if(b.isEmpty(i, column)) {
					connected = false;
				} else {
					Piece p = b.getCell(i, column);
					if (piece.getColor().equals(p.getColor()) && !piece.getShape().equals(p.getShape())) {
						result = result && true;
					} else if (!piece.getColor().equals(p.getColor()) && piece.getShape().equals(p.getShape())) {
						result = result && true;
					} else {
						result = false;
					}
				}
			}
			connected = true;
			for(int i = row + 1; connected; i++) {
				if(b.isEmpty(i, column)) {
					connected = false;
				} else {
					Piece p = b.getCell(i, column);
					if (piece.getColor().equals(p.getColor()) && !piece.getShape().equals(p.getShape())) {
						result = result && true;
					} else if (!piece.getColor().equals(p.getColor()) && piece.getShape().equals(p.getShape())) {
						result = result && true;
					} else {
						result = false;
					}
				}
			}
		}
		return result;
	}
}