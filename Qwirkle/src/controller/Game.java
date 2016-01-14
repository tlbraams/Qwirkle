package controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import model.*;
import view.TUI;

/**
 * Class for controlling a game ones it has started. It keeps the scores and 
 * ends the game when the game has finished. The game is modelled in the model-package
 * and the game communicates with players through the classes in "view". 
 * 
 * @author Tycho Braams & Jeroen Mulder
 * @version $1.0
 */

public class Game implements Runnable {

	// ----- Instance Variables -----
	
	private Board board;
	private TUI view;
	private int playerCount;
	private Player[] players;
	private int aiTime;
	
	private int currentPlayerID;
	private int moveCounter;
	
	// ----- Constructor -----
	
	/**
	 * Creates a new Game object with a board of a default size (183 x 183).
	 * 
	 * @param playerCount	the amount of players participating in this Game. 
	 * @param players 		the array with all players participating in this Game.
	 * @param thinkTime 	the time in milliseconds that a computer player can take before making a Move. 
	 */
	/*
	 * @requires 	playerCount < 5 && playerCount > 1;
	 * 				players.length() < 5 && players.length() > 1;
	 * 				thinkTime > 0;
	 * @ensures		this.playerCount = playerCount;
	 * 				// All players in players are now in players. 
	 * 				this.thinkTime = aiTime;
	 */
	public Game(int playerCount, /* @NonNull */Player[] players, int thinkTime){
		board = new Board();
		view = new TUI(this);
		this.playerCount = playerCount;
		this.players = new Player[this.playerCount];
		for(int i = 0; i < playerCount; i++) {
			this.players[i] = players[i];
		}
		aiTime = thinkTime;
	}
	
	/**
	 * Creates a new Game object with a board of a variable size.
	 * 
	 * @param playerCount	the amount of players participating in this Game. 
	 * @param players 		the array with all players participating in this Game.
	 * @param thinkTime 	the time in milliseconds that a computer player can take before making a Move. 
	 * @param boardSize		the length of the edges of the board that is to be played on.
	 */
	/*
	 * @requires 	playerCount < 5 && playerCount > 1;
	 * 				players.length() < 5 && players.length() > 1;
	 * 				thinkTime > 0;
	 * @ensures		this.playerCount = playerCount;
	 * 				// All players in players are now in players. 
	 * 				this.thinkTime = aiTime;
	 */
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
	
	// ----- Queries -----
	/**
	 * Returns the Board object of this Game.
	 */
	/* @pure */public /* @NonNull*/ Board getBoard() {
		return board;
	}
	
	/**
	 * Returns a TUI object that is used to communicate with the player. 
	 */
	/* @pure */public /* @NonNull*/ TUI getView() {
		return view;
	}
	
	/**
	 * Returns the amount of players that participate in this Game. 
	 */
	/* @pure */public /* @NonNull*/ int getPlayerCount() {
		return playerCount;
	}
	
	/**
	 * Returns the maximum amount of points of a given hand. 
	 * This method is called for each Player at the beginning of this Game to 
	 * determine which player is allowed to start the game. 
	 * 
	 * @param hand the Hand a player holds. 
	 * @return the maximum amount of points one can get with the given Hand. 
	 */
	/* 
	 * @requires	hand.size() == 6;
	 * @ensures		\result <= 0 && \result < 7;
	 */
	/* @pure */public /* @NonNull*/ int findMaxScore(HashSet<Piece> hand) {
		int max = 0;
		for(Piece p : hand) {
			Set<Piece> restHand = new HashSet<>(hand);
			restHand.remove(p);
			int color = 1;
			int shape = 1;
			
			// Check if either the color or the shape of each rp matches that of p. If so, add to color or shape. 
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
	
	
	// ----- Commands -----
	
	/**
	 * Welcomes the players and starts the game. 
	 */
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
	 * Places the Piece from a Player on the Board in the indicated cell.
	 * After placing a Piece, it is removed from the Players hand and the Player 
	 * gets a new Piece (if the Stack is not empty). 
	 * 
	 * @param moves the Moves that a Player wants to make.
	 * @param player the Player who wants to make the Moves.
	 */
	/*
	 * @requires	(\forall int i = 0; 0 <= i && i < moves.length;
	 *  			myArray[i] instanceof Place)
     *           
     *            
	 */
	public void place(/* @NonNul*/Move[] moves, /* @NonNul*/Player player) {
		Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
		for(Place m: places) {
			Piece piece = m.getPiece();
			player.remove(piece);
			int row = m.getRow();
			int column = m.getColumn();
			board.setPiece(row, column, piece);
			if (!board.emptyStack()) {
				player.receive(board.draw());
			} else {
				System.out.println("The stack is empty.");
			}
		}
		board.setLastMadeMove(moveCounter);
	}
	
	/**
	 * Trades the given Pieces in the Moves from a Player with random Pieces in the Stack.
	 * The Pieces are removed from the Players Hand and added to the Stack. 
	 * The Players Hand is refilled again to 6 Pieces (if the Stack is not empty). 
	 * 
	 * @param moves the Moves that a Player wants to make.
	 * @param player the Player who wants to make the Moves.
	 */
	/*
	 * @requires	(\forall int i = 0; 0 <= i && i < moves.length;
	 *  			myArray[i] instanceof Trade)
     *           
     *            
	 */
	public void tradePieces(/* @NonNul*/Move[] moves, /* @NonNul*/Player player) {
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
	
	public void findFirstMove() {
		Move[] moves = players[currentPlayerID].determineFirstMove(board);
		if(validMove(moves, players[currentPlayerID])){
			moveCounter ++;
			if(moves[0] instanceof Place) {
				makeMove(moves, players[currentPlayerID]);
				int score = getScore(moves);
				board.addScore(currentPlayerID, score);
			} else if (moves[0] instanceof Trade) {
				tradePieces(moves, players[currentPlayerID]);
			}
		}			
		currentPlayerID = (currentPlayerID + 1) % playerCount;
		view.update();
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
		findFirstMove();
		while (running) {
			Move[] moves = players[currentPlayerID].determineMove(board);
			if(validMove(moves, players[currentPlayerID])){
				moveCounter ++;
				if(moves[0] instanceof Place) {
					place(moves, players[currentPlayerID]);
					int score = getScore(moves);
					board.addScore(currentPlayerID, score);
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
			result = result && (isRow(moves, b) || isColumn(moves, b));
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
	
	public boolean isRow(Move[] moves, Board b) {
		boolean result = true;
		if(moves.length != 1) {
			Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
			int minColumn = places[0].getColumn();
			int maxColumn = minColumn;
			for(int i = 1; i < places.length; i++) {
				result = result && places[0].getRow() == places[i].getRow();
				if (places[i].getColumn() < minColumn) {
					minColumn = places[i].getColumn();
				} else if (places[i].getColumn() > maxColumn) {
					maxColumn = places[i].getColumn();
				}
			}
			for(int i = minColumn; i <= maxColumn && result; i++) {
				result = result && !b.isEmpty(places[0].getRow(), i);
			}
		}
		
		return result;
	}
	/**
	 * Checks if moves (Places's) are in 1 straight line and if there are no gaps. 
	 * @param moves
	 * @return
	 */
	public boolean isColumn(Move[] moves, Board b) {
		boolean result = true;
		if(moves.length != 1) {
			Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
			int minRow = places[0].getRow();
			int maxRow = minRow;
			// Check for straight line Place. 
			for(int i = 1; i < places.length; i++) {
				result = result && places[0].getColumn() == places[i].getColumn();
				if (places[i].getRow() < minRow) {
					minRow = places[i].getRow();
				} else if (places[i].getRow() > maxRow) {
					maxRow = places[i].getRow();
				}
			}
			// Check for gaps. 
			for(int i = minRow; i <= maxRow && result; i++) {
				result = result && !b.isEmpty(i, places[0].getColumn());
			}
		}
		return result;
	}
	
	public int getScore(Move[] moves) {
		Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
		int result = 0;
		if(places.length == 1) {
			int row = board.getRowLength(places[0].getRow(), places[0].getColumn());
			int column = board.getColumnLength(places[0].getRow(), places[0].getColumn());
			if (row > 1) {
				result += row;
				if(row == 6) {
					result += row;
				}
			}
			if (column > 1) {
				result += column;
				if(column == 6) {
					result += column;
				}
			}
		} else {
			if (isRow(moves, board)) {
				result = board.getRowLength(places[0].getRow(), places[0].getColumn());
				if(result == 6) {
					result += result;
				}
				for(int i = 0; i < places.length; i++) {
					int column =  board.getColumnLength(places[i].getRow(), places[i].getColumn());
					if (column > 1) {
						result = result + column;
					}
					if(column == 6) {
						result += column;
					}
				}
			} else if (isColumn(moves, board)) {
				result = board.getColumnLength(places[0].getRow(), places[0].getColumn());
				if(result == 6) {
					result += result;
				}
				for(int i = 0; i < places.length; i++) {
					int row = board.getRowLength(places[i].getRow(), places[i].getColumn());
					if (row > 1) {
						result = result + row; 
					}
					if(row == 6) {
						result += row;
					}
					
				}
			}
		}
		return result;
	}
	
	
	/**
	 * Returns the playerID of the player with the most points after a game has ended.
	 * @return
	 */
	public int isWinner() {
		int result = -1;
		int maxScore = 0;
		if(endGame()) {
			for(Player p: players) {
				if(board.getScore(p.getID()) > maxScore) {
					result = p.getID();
					maxScore = board.getScore(result);
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns true when the game has ended and false when not.
	 * The game has ended when the pile is empty and one of the players does not have
	 * any pieces in its hand. Also returns true if two rounds have passed without a move
	 * being made.
	 */
	private boolean endGame() {
		// Check if one player has an empty hand and remember that playerID/
		boolean emptyHand = false;
		for (Player p: players) {
			if(!emptyHand) {
				emptyHand = p.getHand().size() == 0;
				board.addScore(p.getID(), 6);
			}
		}
		return (board.emptyStack() && emptyHand) || (board.getLastMadeMove() < moveCounter - (2 * playerCount)); 
	}
}