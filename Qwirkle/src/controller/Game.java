package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import exceptions.*;
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
	
	boolean kickOccured = false;
	
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
	public Game(int playerCount, /* @NonNull */Player[] players, int thinkTime) {
		board = new Board();
		view = new TUI(this);
		this.playerCount = playerCount;
		this.players = new Player[this.playerCount];
		for (int i = 0; i < playerCount; i++) {
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
			if (validMove(moves, players[currentPlayerID])) {
				moveCounter++;
				if (moves[0] instanceof Place) {
					place(moves, players[currentPlayerID], board);
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
	public void place(/* @NonNul*/Move[] moves, /* @NonNul*/Player player, /*@ NonNull */ Board board) {
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
		if (validMove(moves, players[currentPlayerID])) {
			moveCounter++;
			place(moves, players[currentPlayerID], board);
			int score = board.getScore(moves);
			board.addScore(currentPlayerID, score);
		}			
		currentPlayerID = (currentPlayerID + 1) % playerCount;
		view.update();
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

				cellsAreAvailable(moves, board);
				cellsAreValid(moves, board);
				isUninterruptedRow(moves, board);
				isUninterruptedColumn(moves, board);
				pieceIsConnectedRowAndUnique(moves, board);
				pieceIsConnectedColumnAndUnique(moves, board);

				cellsAreAvailable(places, board);
				cellsAreValid(places, board);
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
		if (!board.isEmpty(91, 91)) {
			boolean result = false;
			for (Place p: places) {
				result = result || ((!board.isEmpty(p.getRow() - 1, p.getColumn())) ||
						(!board.isEmpty(p.getRow() + 1, p.getColumn())) ||
						(!board.isEmpty(p.getRow(), p.getColumn() - 1)) ||
						(!board.isEmpty(p.getRow(), p.getColumn() + 1)));
			}
			if (!result) {
				throw new InvalidMoveException("Your row of tiles is not connected to other rows.");
			}
		}
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
	 *@ ensures 	board.getStack().size() == \old(board.getStack().size()) + players[playerID].getHand().size();
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