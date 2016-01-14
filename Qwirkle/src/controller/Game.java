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
/* 
 * @invariant	
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
		moveCounter = 0;
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
		moveCounter = 0;
	}
	
	// ----- Queries -----
	/**
	 * Returns the Board object of this Game.
	 * @return the Board object.
	 */
	/* @pure */public /* @NonNull*/ Board getBoard() {
		return board;
	}
	
	/**
	 * Returns a TUI object that is used to communicate with the player. 
	 * @return the TUI.
	 */
	/* @pure */public /* @NonNull*/ TUI getView() {
		return view;
	}
	
	/**
	 * Returns the amount of players that participate in this Game. 
	 * @return the amount of players in the game. 
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
	 * Starts and ends Game. At the beginning it fills all Hands, and then finds the players that is allowed
	 * to make a Move first. It prints the board on the System.Out and executes the first Move.
	 * 
	 * During the Game it lets the Players makes Moves, checks them for validity and keeps the scores. 
	 * 
	 * When the Game has finished it stops the Game and displays the winner and scores. 
	 */
	public void playGame() {
		// Fills the Hands of each Player with 6 Pieces. 
		for(int i = 0; i < playerCount; i++) {
			for(int j = 0; j < 6; j++) {
				Piece piece = board.draw();
				players[i].receive(piece);
			}	
		}
		// Start the Game. 
		findFirstPlayer();
		view.update();
		findFirstMove();
		
		// During the Game.
		while (!endGame()) {
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
		// Finishing the Game off. 
		ending();
	}
	
	/**
	 * Displays the winner and scores of other players to the Player. 
	 */
	public void ending() {
		System.out.println("The game has ended! " + players[isWinner()].getName() + "has won.");
		System.out.println("The scores: ");
		for (Player p: players) {
			System.out.println(p.getName() + " : " + board.getScore(p.getID()));
		}
	}

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
	
	/**
	 * Finds the Player at the beginning of a Game that can make a Move first. 
	 * The first Player is the Player that has the highest possible points in its Hand
	 * at the beginning of the Game. When 2 players have the same maximum possible score, 
	 * the Player who joined the Game the earliest is given the turn. 
	 */
	/*
	 * @ensure		currentPlayerID < this.playerCount();
	 * 				currentPlayerID > 0;
	 */
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
	
	/**
	 * Requests the first Player for a Move and makes sure this is executed. 
	 * After execution, the score of the first Player is raised, the moveCounter incremented, 
	 * and the Board is printed. 
	 */
	/*
	 * @ensure		moveCounter == 1;
	 */
	public void findFirstMove() {
		Move[] moves = players[currentPlayerID].determineFirstMove(board);
		if(validMove(moves, players[currentPlayerID])){
			moveCounter++;
			place(moves, players[currentPlayerID]);
			int score = getScore(moves);
			board.addScore(currentPlayerID, score);
		}			
		currentPlayerID = (currentPlayerID + 1) % playerCount;
		view.update();
	}
	
	/**
	 * Tests if a given array of Moves contains only valid Moves. It tests if all the moves are of the same 
	 * type, if a placing of Pieces is one straight row or column, and finally if the players makes Moves
	 * with Pieces that are in its Hand. If any of the above conditions is violated, the turn is skipped. 
	 * @param moves the Moves that the given Player wants to make. 
	 * @param player the Player that wants to make the given Moves. 
	 * @return true if the Moves are valid, false when invalid. 
	 */
	/*
	 * @requires 	moves.length < player.getHand().size();
	 */
	public /* @NonNull*/boolean validMove(/* @NonNull*/Move[] moves, /* @NonNull*/Player player) {
		boolean result = true;
		
		// Check if all Moves are of type Place. 
		if (moves[0] instanceof Place) {
			for(int i = 1; i < moves.length; i++) {
				result = result && moves[i] instanceof Place;
			}
			
			// Check if the cells of the Places are empty. 
			Board b = board.deepCopy();
			Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
			for(Place p : places) {
				result = result && b.isEmpty(p.getRow(), p.getColumn());
				b.setPiece(p.getRow(), p.getColumn(), p.getPiece());
			}
			
			// Check if the Places create an uninterrupted row or column. 
			result = result && (isRow(moves, b) || isColumn(moves, b));
			
			// Check if the uninterrupted row or column is valid. 
			result = result && isValidRow(places, b);
			result = result && isValidColumn(places, b);
			if(moveCounter > 0) {
				result = result && isConnected(places);
			}
			
		// Check if all Moves are of type Trade. 
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
	
	/**
	 * Tests if an array of Places on Board b is a valid row. 
	 * It checks if the Places are connected to other Pieces of the Board and if 
	 * the row it creates is valid. 
	 * 
	 * @param moves the array of Places that are to be made. 
	 * @param b the board on which the Places are made. 
	 * @return true is the Places are valid, false when not. 
	 */
	/*
	 * @requires	moves.length < 7;
	 */
	public /* @NonNull */boolean isValidRow(/* @NonNull */Place[] moves, /* @NonNull */Board b) {
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
					result = this.isValidConnectedPlace(piece, b, row, i);
				}
			}
			connected = true;
			for(int i = column + 1; connected; i++) {
				if(b.isEmpty(row, i)) {
					connected = false;
				} else {
					result = this.isValidConnectedPlace(piece, b, row, i);
				}
			}
		}
		return result;
	}
	
	/**
	 * Tests if an array of Places on Board b is a valid column. 
	 * It checks if the Places are connected to other Pieces of the Board and if 
	 * the column it creates is valid. 
	 * 
	 * @param moves the array of Places that are to be made. 
	 * @param b the board on which the Places are made. 
	 * @return true is the Places are valid, false when not. 
	 */
	/*
	 * @requires	moves.length < 7;
	 */
	public /* @NonNull */boolean isValidColumn(/* @NonNull */Place[] moves, /* @NonNull */Board b) {
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
					result = this.isValidConnectedPlace(piece, b, i, column);
				}
			}
			connected = true;
			for(int i = row + 1; connected; i++) {
				if(b.isEmpty(i, column)) {
					connected = false;
				} else {
					result = this.isValidConnectedPlace(piece, b, i, column);
				}
			}
		}
		return result;
	}
	
	/**
	 * Tests if a connected Place is connected in a valid way. 
	 * It is considered valid when either the color of the Pieces are the same 
	 * or the shape of the Pieces are the same. 
	 * 
	 * @param piece the Piece that is placed on Board b.
	 * @param b the Board. 
	 * @param row the row number that the Piece is placed in. 
	 * @param i the column number that the Piece is placed in.
	 * @return true is the Places are valid, false when not.
	 */
	public /* @NonNull */boolean isValidConnectedPlace(/* @NonNull */Piece piece, 
			/* @NonNull */Board b, /* @NonNull */int row, /* @NonNull */int i) {
		Boolean result = false;
		Piece p = b.getCell(row, i);
		if (piece.getColor().equals(p.getColor()) && !piece.getShape().equals(p.getShape())) {
			result = result && true;
		} else if (!piece.getColor().equals(p.getColor()) && piece.getShape().equals(p.getShape())) {
			result = result && true;
		} else {
			result = false;
		}
		return result;
	}
	
	/**
	 * Tests if the Places are in 1 straight line (row) and if there are no gaps. 
	 * 
	 * @param moves the Places to be placed on Board b. 
	 * @param b the Board on which the Places are put. 
	 * @return true when the Places create 1 straight line without gaps, false when otherwise. 
	 */
	public /* @NunNull */boolean isRow(/* @NunNull */Move[] moves, /* @NunNull */Board b) {
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
			
			// Check for gaps. 
			for(int i = minColumn; i <= maxColumn && result; i++) {
				result = result && !b.isEmpty(places[0].getRow(), i);
			}
		}
		
		return result;
	}
	
	/**
	 * Tests if the Places are in 1 straight line (column) and if there are no gaps. 
	 * 
	 * @param moves the Places to be placed on Board b. 
	 * @param b the Board on which the Places are put. 
	 * @return true when the Places create 1 straight line without gaps, false when otherwise. 
	 */
	public /* @NunNull */boolean isColumn(/* @NunNull */Move[] moves, /* @NunNull */Board b) {
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
	
	/**
	 * Tests if every Place of places is connected to the another Piece.
	 * @param places the Places to be made. 
	 * @return true when the Places are connected to other Pieces, false when otherwise. 
	 */
	/*
	 * @requires 	places.length < 7;
	 */
	public /* @NonNull */boolean isConnected(/* @NonNull */Place[] places) {
		boolean result = false;
		for(Place p: places) {
			result = result || ((!board.isEmpty(p.getRow() - 1, p.getColumn())) ||
					(!board.isEmpty(p.getRow() + 1, p.getColumn())) ||
					(!board.isEmpty(p.getRow(), p.getColumn() - 1)) ||
					(!board.isEmpty(p.getRow(), p.getColumn() + 1)));
		}
		return result;
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
		
		// Determining the score when multiple Places have been made. 
		} else {
			
			// Determining the score if the Places create a row. 
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
			// Determining the score if the Places create a column. 
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
	 * @return the playerID of the winner of the Game. 
	 */
	/*
	 * @ensures 	\result < 5 && \result >= 0;
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
	 * @return true when the Game has ended, false when the Game is still running. 
	 */
	private boolean endGame() {
		// Check if one player has an empty hand and remember that playerID.
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