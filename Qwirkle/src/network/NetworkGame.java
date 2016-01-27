package network;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import exceptions.InvalidMoveException;
import model.*;

/**
 * Class for controlling a game ones it has started. It keeps the scores and 
 * ends the game when the game has finished. The game is modelled in the model-package
 * and the game communicates with players through the classes in "view". 
 * 
 * @author Tycho Braams & Jeroen Mulder
 * @version $1.0
 */


public class NetworkGame implements Runnable {

	/*
	 *@ invariant	2 <= playerCount && playerCount <= 4;
	 *@ invariant	0 <= moveCounter;
	 *@ invariant	0 <= currentPlayerID() && currentPlayerID() < playerCount;
	 */
	
	// ----- Instance Variables -----
	
	private Board board;
	private int playerCount;
	private NetworkPlayer[] players;
	private int aiTime;
	private GameHandler handler;
	
	private int currentPlayerID;
	private int moveCounter;
	
	private boolean kickOccured;
	
	// ----- Constructor -----
	
	/**
	 * Creates a new Game object with a board of a default size (183 x 183).
	 * @param playerCount	the amount of players participating in this Game. 
	 * @param players 		the array with all players participating in this Game.
	 * @param thinkTime 	the time in milliseconds that a computer player can take
	 * 						before making a Move. 
	 */
	/*
	 * @requires 	playerCount < 5 && playerCount > 1;
	 * 				players.length() < 5 && players.length() > 1;
	 * @ensures		this.playerCount = playerCount;
	 * 				moveCounter == 0;
	 * 				aiTime = thinkTime;
	 * 				kickOccured == false;
	 * 				handler == gameHandler;
	 * 				(\forall int i; 0 <= i, i <= playerCount; this.players[i] == players [i]);
	 */
	public NetworkGame(int playerCount, /*@ non_null */NetworkPlayer[] players, /*@ non_null */ int thinkTime, 
					/*@ non_null */GameHandler h) {
		board = new Board();
		this.playerCount = playerCount;
		players = new NetworkPlayer[this.playerCount];
		handler = gameHandler;
		for (int i = 0; i < playerCount; i++) {
			this.players[i] = players[i];
		}
		aiTime = thinkTime;
		moveCounter = 0;
		kickOccured = false;
	}
	
	
	// ----- Queries -----
	/**
	 * Returns the Board object of this Game.
	 * @return the Board object.
	 */
	/*@ pure */public /*@ non_null*/ Board getBoard() {
		return board;
	}
	
	
	/**
	 * Returns the amount of players that participate in this Game. 
	 * @return the amount of players in the game. 
	 */
	/*@ pure */public /*@ non_null*/ int getPlayerCount() {
		return playerCount;
	}
	
	/**
	 * Returns the maximum amount of points of a given hand. 
	 * This method is called for each Player at the beginning of this Game to 
	 * determine which player is allowed to start the game. 
	 * @param hand the hand a player holds. 
	 * @return the maximum amount of points one can get with the given hand. 
	 */
	/* 
	 * @ensures		\result <= 0 && \result <= hand.size();
	 */
	/*@ pure */public /*@ non_null*/ int findMaxScore(HashSet<Piece> hand) {
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
	 * Starts and ends Game. At the beginning it fills all Hands,
	 * and then finds the players that is allowed to make a Move first.
	 * During the Game it lets the Players makes Moves,
	 * checks them for validity and keeps the scores.
	 * Notifies the other players using broadcast from handler.
	 * After a player makes a move, it sends a command to that player with the pieces
	 * to replace the pieces it just used.
	 * If no Move or an incorrect Move is given, the player is kicked.
	 * When the Game has finished it stops the Game and displays the winner and scores. 
	 */
	/*
	 *@ ensures 	endGame();
	 */
	public void playGame() {
		// Fills the Hands of each Player with 6 Pieces. 
		for (int i = 0; i < playerCount; i++) {
			String command = "NEW"; 
			for (int j = 0; j < 6; j++) {
				Piece piece = board.draw();
				players[i].receive(piece);
				command += " " + piece.toString();
			}	
			players[i].sendCommand(command);
		}
		// Start the Game. 
		findFirstPlayer();
		
		// During the Game.
		while (!endGame()) {
			handler.broadcast("NEXT " + currentPlayerID);
			Move[] moves = players[currentPlayerID].determineMove(board);
			if (board.emptyStack() && moves[0].getPiece() == null) {
				handler.broadcast("Turn " + currentPlayerID + " empty");
			} else if (moves == null || moves.length == 0) {
				kick(currentPlayerID, "No move given");
			} else {
				boolean valid = false;
				try {
					valid = board.validMove(moves, players[currentPlayerID]);
				} catch (InvalidMoveException e) {
					kick(currentPlayerID, e.getInfo());
				}
				if (valid) {
					moveCounter++;
					String newPieces = "NEW";
					String move = "TURN " + currentPlayerID;
					if (moves[0] instanceof Place) {
						String[] result = place(moves, players[currentPlayerID]);
						newPieces += result[0];
						move += result[1];
						int score = board.getScore(moves);
						board.addScore(currentPlayerID, score);
					} else if (moves[0] instanceof Trade) {
						newPieces += tradePieces(moves, players[currentPlayerID]);
						move += " empty";
					}
					players[currentPlayerID].sendCommand(newPieces);
					handler.broadcast(move);
				}
			}
			board.notifyObservers();
			currentPlayerID = nextPlayer();
		}
		// Finishing the Game off. 
		ending();
	}
	
	/**
	 * Determines which Player is next. 
	 * @return the ID of the player who is next. 
	 */
	/*
	 *@ ensures 	0 <= \result && \result < getPlayerCount();
	 */
	public /*@ NonNull */int nextPlayer() {
		int nextPlayerID = 0;
		if (kickOccured) {
			nextPlayerID = players[currentPlayerID % playerCount].getID();
		} else {
			nextPlayerID = players[(currentPlayerID + 1) % playerCount].getID();
		}
		return nextPlayerID;
	}
	
	/**
	 * Broadcasts the Winner of the game. 
	 */
	/*@ pure */public void ending() {
		handler.broadcast("WINNER " + isWinner());
	}

	/**
	 * Notifies all players of the names and IDs of the participating Players.
	 * Then starts the game. 
	 */
	public void run() {
		String welcome = "NAMES";
		for (int i = 0; i < playerCount; i++) {
			welcome += " " + players[i].getName() + " " + players[i].getID();
		}
		welcome += " " + aiTime;
		handler.broadcast(welcome);
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
	 *@ requires	(\forall int i = 0; 0 <= i && i < moves.length;
	 *  			moves[i] instanceof Place);    
	 *  			moves.length <= player.getHand.size();      
	 *@ ensures		moves.length <= board.getStack().size() ==> 
	 *					\old(player.getHand().size()) == player.getHand().size();
	 */
	public /*@ non_null */String[] place(/*@ non_null */Move[] moves, /*@ non_null*/Player player) {
		Place[] places = Arrays.copyOf(moves, moves.length, Place[].class);
		String[] result = new String[2];
		String newPieces = "";
		String moveString = "";
		for (Place m: places) {
			Piece piece = m.getPiece();
			player.remove(piece);
			int row = m.getRow();
			int column = m.getColumn();
			board.setPiece(row, column, piece);
			if (!board.emptyStack()) {
				Piece newPiece = board.draw();
				player.receive(newPiece);
				newPieces += " " + newPiece.toString(); 
			}
			moveString += m.toString();
		}
		result[0] = newPieces;
		result[1] = moveString;
		board.setLastMadeMove(moveCounter);
		return result;
	}
	
	/**
	 * Trades the given Pieces in the Moves from a Player with random Pieces in the Stack.
	 * The Pieces are removed from the Players Hand and added to the Stack. 
	 * The Players Hand is refilled again to 6 Pieces (if the Stack is not empty). 
	 * @param moves the Moves that a Player wants to make.
	 * @param player the Player who wants to make the Moves.
	 */
	/*
	 *@ requires	(\forall int i = 0; 0 <= i && i < moves.length;
	 *  			moves[i] instanceof Trade);
	 *@ ensures		player.getHand().size() == \old(player.getHand().size());
	 *				board.getStack().size() == \old(board.getStack().size());
	 */
	public /*@ NonNull */String tradePieces(/* @NonNul*/Move[] moves, /* @NonNul*/Player player) {
		Piece[] pieces = new Piece[moves.length];
		String result = "";
		for (int i = 0; i < moves.length; i++) {
			pieces[i] = moves[i].getPiece();
			player.remove(pieces[i]);
			Piece newPiece = board.draw();
			player.receive(newPiece);
			result += " " + newPiece.toString();
		}
		board.tradeReturn(pieces);
		return result;
	}
	
	/**
	 * Finds the Player at the beginning of a Game that can make a Move first. 
	 * The first Player is the Player that has the highest possible points in its Hand
	 * at the beginning of the Game. When 2 players have the same maximum possible score, 
	 * the Player who joined the Game the earliest is given the turn. 
	 */
	/*@ pure */public void findFirstPlayer() {
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
		return (playerCount < 2) || (board.emptyStack() && emptyHand) ||
						(board.getLastMadeMove() < moveCounter - (2 * playerCount)); 
	}
	

	/**
	 * Kicks a player when he send an invalid command or move.
	 * Notifies all other players of the Kick.
	 */
	public void kick(int playerID, String reason) {
		int tiles = returnPieces(playerID);
		playerCount--;
		setPlayers(playerID);
		kickOccured = true;
		handler.kick(playerID);
		handler.broadcast("KICK " + playerID + " " + tiles + " " + reason);
		if (playerCount < 2) {
			handler.broadcast("Not enough Players to continue.");
		}
	}
	
	/**
	 * Create a new Player[] without the player that is kicked. 
	 * @param playerID the ID of the player that is being kicked. 
	 */
	/*
	 *@ ensure	players.size() == \old(players.size()) - 1;
	 */
	public void setPlayers(int playerID) {
		NetworkPlayer[] newPlayers = new NetworkPlayer[playerCount];
		int i = 0;
		for (NetworkPlayer player: players) {
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
	public int returnPieces(int playerID) {
		Piece[] piecesToReturn = new Piece[players[playerID].getHand().size()];
		int i = 0;
		for (Piece piece: players[playerID].getHand()) {
			piecesToReturn[i] = piece;
			i++;
		}
		board.tradeReturn(piecesToReturn);
		return i;
	}
}