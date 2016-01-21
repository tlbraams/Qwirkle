package test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import model.*;
import controller.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestGame {

	Game game;
	Board board;
	Player player1;
	Player player2;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	/**
	 * Creates a game with 2 Players.
	 */
	@Before
	public void setUp() {
		player1 = new HumanPlayer("Tycho", 0);
		player2 = new HumanPlayer("Jeroen", 1);
		Player[] players = new Player[] {player1, player2};
		game = new Game(2, players, 1000);
		board = game.getBoard();
	}

	/**
	 * Checks if the first move places a piece on 91, 91.
	 */
	@Test
	public void testFirstMove() {
		board.fillStack();
		player1.receive(board.draw());
		game.findFirstMove();
		assertFalse("Piece on 91, 91:", board.isEmpty(91, 91));
	}
	
	/**
	 * Checks if the game really has the playerCount given in the constructor.
	 */
	@Test
	public void testPlayers() {
		assertTrue(game.getPlayerCount() == 2);
	}
	
	/**
	 * Checks if placing a 7th stone in a row results in false.
	 */
	@Test
	public void testSeventhMove() {
		board.setPiece(91, 91, new Piece(Piece.Color.RED, Piece.Shape.CIRCLE));
		board.setPiece(91, 92, new Piece(Piece.Color.RED, Piece.Shape.SQUARE));
		board.setPiece(91, 93, new Piece(Piece.Color.RED, Piece.Shape.CLUBS));
		board.setPiece(91, 94, new Piece(Piece.Color.RED, Piece.Shape.DIAMOND));
		board.setPiece(91, 95, new Piece(Piece.Color.RED, Piece.Shape.HEART));
		board.setPiece(91, 96, new Piece(Piece.Color.RED, Piece.Shape.SPADE));
		Place[] move = new Place[] { 
			new Place(new Piece(Piece.Color.RED, Piece.Shape.DIAMOND), 91, 97)
		};
		assertFalse(game.validMove(move, player1));
	}
	
	
	
	
	/**
	 * Checks if placing a stone on an occupied slot results in false.
	 */
	@Test
	public void testOccupiedMove() {
		board.setPiece(91, 91, new Piece(Piece.Color.RED, Piece.Shape.CIRCLE));
		Place[] move = new Place[] {
			new Place(new Piece(Piece.Color.PURPLE, Piece.Shape.CIRCLE), 91, 91)
		};
		assertFalse(game.validMove(move, player1));
	}
	
	/**
	 * Checks if a move with 2 tiles on the same spot results in false.
	 */
	@Test
	public void testDuplicateMove() {
		board.setPiece(91, 91, new Piece(Piece.Color.RED, Piece.Shape.CIRCLE));
		Place[] move = new Place[] {
			new Place(new Piece(Piece.Color.PURPLE, Piece.Shape.CIRCLE), 91, 92),
			new Place(new Piece(Piece.Color.BLUE, Piece.Shape.CIRCLE), 91, 92)
		};
		assertFalse(game.validMove(move, player1));
	
	}
	
	
	/**
	 * Checks if MaxScore finds the player with the longest line.
	 */
	@Test
	public void testMaxScore() {
		player1.receive(new Piece(Piece.Color.RED, Piece.Shape.CIRCLE));
		player1.receive(new Piece(Piece.Color.RED, Piece.Shape.SQUARE));
		player1.receive(new Piece(Piece.Color.RED, Piece.Shape.DIAMOND));
		player1.receive(new Piece(Piece.Color.BLUE, Piece.Shape.SQUARE));
		player1.receive(new Piece(Piece.Color.PURPLE, Piece.Shape.HEART));
		player1.receive(new Piece(Piece.Color.YELLOW, Piece.Shape.CIRCLE));
		player2.receive(new Piece(Piece.Color.RED, Piece.Shape.CIRCLE));
		player2.receive(new Piece(Piece.Color.BLUE, Piece.Shape.CIRCLE));
		player2.receive(new Piece(Piece.Color.RED, Piece.Shape.SQUARE));
		player2.receive(new Piece(Piece.Color.BLUE, Piece.Shape.SQUARE));
		player2.receive(new Piece(Piece.Color.YELLOW, Piece.Shape.DIAMOND));
		player2.receive(new Piece(Piece.Color.PURPLE, Piece.Shape.HEART));
		assertTrue(game.findMaxScore(player1.getHand()) == 3);
		assertTrue(game.findMaxScore(player2.getHand()) == 2);
		//assertTrue()
	}
	
	/**
	 * Checks if getScore gives the bonus for finishing a row.
	 */
	@Test
	public void testGetScore() {
		board.setPiece(91, 91, new Piece(Piece.Color.RED, Piece.Shape.CIRCLE));
		board.setPiece(91, 92, new Piece(Piece.Color.RED, Piece.Shape.SQUARE));
		board.setPiece(91, 93, new Piece(Piece.Color.RED, Piece.Shape.CLUBS));
		board.setPiece(91, 94, new Piece(Piece.Color.RED, Piece.Shape.HEART));
		board.setPiece(91, 95, new Piece(Piece.Color.RED, Piece.Shape.SPADE));
		Place[] move = new Place[] { 
			new Place(new Piece(Piece.Color.RED, Piece.Shape.DIAMOND), 91, 96)
		};
		game.place(move, player1);
		assertTrue(game.getBoard().getScore(move) == 12);
	
	}
	
	
}