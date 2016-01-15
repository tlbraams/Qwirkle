package test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import model.*;
import controller.*;
import exceptions.UnconnectedMoveException;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestGame {

	Game game;
	Board board;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	/**
	 * Creates a game with 2 Players.
	 */
	@Before
	public void setUp() {
		Player[] players = new Player[] {new HumanPlayer("Tycho", 0), new HumanPlayer("Jeroen", 1)};
		game = new Game(2, players, 1000);
		board = game.getBoard();
	}

	/**
	 * Checks if the first move places a piece on 91, 91.
	 */
	@Test
	public void testFirstMove() {
		game.run();
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
	 * Checks if placing a piece that is not connected to the rest of the board,
	 * throws the correct exception.
	 */
	@Test
	public void testUnconnectedMove() {
		board.setPiece(91, 91, new Piece(Piece.Color.RED, Piece.Shape.CIRCLE));
		Place[] move = new Place[] {new Place(new Piece(Piece.Color.BLUE, Piece.Shape.CIRCLE),
									88, 88)};
		exception.expect(UnconnectedMoveException.class);
		game.isConnected(move);
	}
	
}