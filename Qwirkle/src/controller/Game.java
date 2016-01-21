package controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import exceptions.InvalidMoveException;
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
	
	boolean kickOccured;
	
	// ----- Constructor -----
	
	/**
	 * Creates a new Game object with a board of a default size (183 x 183).
	 * @param playerCount	the amount of players participating in this Game. 
	 * @param players 		the array with all players participating in this Game.
	 * @param thinkTime 	the time in milliseconds that a computerplayer can take
	 * 						before making a Move. 
	 */
	/*
	 * @requires 	playerCount < 5 && playerCount > 1;
	 * 				players.length() < 5 && players.length() > 1;
	 * 				thinkTime > 0;
	 * @ensures		this.playerCount = playerCount;
	 * 				// All players in players are now in players. 
	 * 				this.thinkTime = aiTime;
	 */
	public Game(int playerCount, /*@ NonNull */Player[] players, int thinkTime) {
		board = new Board();
		this.playerCount = playerCount;
		this.players = new Player[this.playerCount];
		for (int i = 0; i < playerCount; i++) {
			this.players[i] = players[i];
		}
		view = new TUI(this);
		aiTime = thinkTime;
		moveCounter = 0;
		kickOccured = false;
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
	 * @param hand the Hand a player holds. 
	 * @return the maximum amount of points one can get with the given Hand. 
	 */
	/* 
	 * @requires	hand.size() == 6;
	 * @ensures		\result <= 0 && \result < 7;
	 */
	/* @pure */public /* @NonNull*/ int findMaxScore(HashSet<Piece> hand) {
		int max = 0;
		for (Piece p : hand) {
			Set<Piece> restHand = new HashSet<>(hand);
			restHand.remove(p);
			int color = 1;
			int shape = 1;
			
			// Check if either the color or the shape of each rp matches that of p.
			// If so, add to color or shape. 
			for (Piece rp : restHand) {
				if (rp.getColor().equals(p.getColor()) && !rp.getShape().equals(p.getShape())) {
					color++;
				} else if (!rp.getColor().equals(p.getColor())
								&& rp.getShape().equals(p.getShape())) {
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
	 * Starts and ends Game.
	 * At the beginning it fills all Hands, and then finds the players that is allowed
	 * to make a Move first. It prints the board on the System.Out and executes the first Move.
	 * During the Game it lets the Players makes Moves,
	 * checks them for validity and keeps the scores. 
	 * When the Game has finished it stops the Game and displays the winner and scores. 
	 */
	public void playGame() {
		// Fills the Hands of each Player with 6 Pieces. 
		for (int i = 0; i < playerCount; i++) {
			for (int j = 0; j < 6; j++) {
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
			boolean valid = false;
			try {
				valid = board.validMove(moves, players[currentPlayerID]);
			} catch (InvalidMoveException e) {
				System.out.print(e.getInfo());
			}
			if (valid) {
				moveCounter++;
				if (moves[0] instanceof Place) {
					place(moves, players[currentPlayerID]);
					int score = board.getScore(moves);
					board.addScore(currentPlayerID, score);
				} else if (moves[0] instanceof Trade) {
					tradePieces(moves, players[currentPlayerID]);
				}
			}			
			currentPlayerID = nextPlayer();
			view.update();
		}
		// Finishing the Game off. 
		ending();
	}
	
	/**
	 * Determines which Player is next. 
	 * @return
	 */
	public int nextPlayer() {
		int nextPlayerID = 0;
		if (kickOccured) {
			nextPlayerID = players[currentPlayerID % playerCount].getID();
		} else {
			nextPlayerID = players[(currentPlayerID + 1) % playerCount].getID();
		}
		return nextPlayerID;
	}

	/**
	 * Displays the winner and scores of other players to the Player. 
	 */
	public void ending() {
		System.out.println("The game has ended! " + players[isWinner()].getName() + " has won.");
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
		for (int i = 0; i < playerCount; i++) {
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
	 * @param moves the Moves that a Player wants to make.
	 * @param player the Player who wants to make the Moves.
	 */
	/*
	 * @requires	(\forall int i = 0; 0 <= i && i < moves.length;
	 *  			myArray[i] instanceof Place)          
	 */
	public void place(/* @NonNul*/Move[] moves, /* @NonNul*/Player player) {
		Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
		for (Place m: places) {
			Piece piece = m.getPiece();
			player.remove(piece);
			int row = m.getRow();
			int column = m.getColumn();
			board.setPiece(row, column, piece);
			if (!board.emptyStack()) {
				player.receive(board.draw());
			}
		}
		board.setLastMadeMove(moveCounter);
	}
	
	/**
	 * Trades the given Pieces in the Moves from a Player with random Pieces in the Stack.
	 * The Pieces are removed from the Players Hand and added to the Stack. 
	 * The Players Hand is refilled again to 6 Pieces (if the Stack is not empty). 
	 * @param moves the Moves that a Player wants to make.
	 * @param player the Player who wants to make the Moves.
	 */
	/*
	 * @requires	(\forall int i = 0; 0 <= i && i < moves.length;
	 *  			myArray[i] instanceof Trade)
	 */
	public void tradePieces(/* @NonNul*/Move[] moves, /* @NonNul*/Player player) {
		Piece[] pieces = new Piece[moves.length];
		for (int i = 0; i < moves.length; i++) {
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
		for (int i = 0; i < playerCount; i++) {
			HashSet<Piece> hand = players[i].getHand();
			int temp = findMaxScore(hand);
			if (temp > maxScore) {
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
		boolean valid = false;
		try {
			valid = board.validMove(moves, players[currentPlayerID]);
		} catch (InvalidMoveException e) {
			System.out.print(e.getInfo());
		}
		if (valid) {
			moveCounter++;
			place(moves, players[currentPlayerID]);
			int score = board.getScore(moves);
			board.addScore(currentPlayerID, score);
		}			
		currentPlayerID = (currentPlayerID + 1) % playerCount;
		view.update();
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
		if (endGame()) {
			for (Player p: players) {
				if (board.getScore(p.getID()) > maxScore) {
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
			if (!emptyHand) {
				emptyHand = p.getHand().size() == 0;
				if (emptyHand) {
					board.addScore(p.getID(), 6);
				}
			}
		}
		return (board.emptyStack() && emptyHand) ||
					(board.getLastMadeMove() < moveCounter - (2 * playerCount)); 
	}
	
	/**
	 * Kicks a player when he send an invalid command or move. 
	 */
	public void kick(int playerID) {
		returnPieces(playerID);
		playerCount--;
		setPlayers(playerID);
		kickOccured = true;
	}
	
	/**
	 * Create a new Player[] without the player that is kicked. 
	 * @param playerID the ID of the player that is being kicked. 
	 */
	/*
	 *@ ensure	players.size() == \old(players.size()) - 1;
	 */
	public void setPlayers(int playerID) {
		Player[] newPlayers = new Player[playerCount];
		int i = 0;
		for (Player player: players) {
			if (player.getID() != playerID) {
				newPlayers[i] = player;
				i++;
			} 
		}
		players = newPlayers;
	}
	
	/**
	 * Returns all the Pieces of a kicked Players hand.
	 */
	/* 
	 *@ ensures 	board.getStack().size() == \old(board.getStack().size())
	 *												 + players[playerID].getHand().size();
	 */
	public void returnPieces(int playerID) {
		Piece[] piecesToReturn = new Piece[players[playerID].getHand().size()];
		int i = 0;
		for (Piece piece: players[playerID].getHand()) {
			piecesToReturn[i] = piece;
			i++;
		}
		board.tradeReturn(piecesToReturn);
	}
	
}