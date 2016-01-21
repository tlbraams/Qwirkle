package test;

import org.junit.*;

import model.Board;
import model.HumanPlayer;
import model.Move;
import model.Piece;
import model.Place;
import model.Trade;

import static org.junit.Assert.*;

/**
 * Test the class HumanPlayer. 
 * Testing the the toString() and the correct assignment of parameters in the constructor.
 */

public class TestHumanPlayer {
	
	private HumanPlayer player;
	
	private Piece piece;
	private Piece pieceBlueDiamond;
	private Piece pieceRedSpade;
	private Piece pieceYellowCircle;
	private Piece pieceGreenHeart;
	private Piece pieceOrangeClubs;
	private Piece piecePurpleSquare;
	
	private Board board;
	
	@Before
	public void setUp() {
		player = new HumanPlayer("Jeroen", 0);
		piece = new Piece(Piece.Color.BLUE, Piece.Shape.CIRCLE);
		pieceBlueDiamond = new Piece(Piece.Color.BLUE, Piece.Shape.DIAMOND);
		pieceRedSpade = new Piece(Piece.Color.RED, Piece.Shape.SPADE);
		pieceYellowCircle = new Piece(Piece.Color.YELLOW, Piece.Shape.CIRCLE);
		pieceGreenHeart = new Piece(Piece.Color.GREEN, Piece.Shape.HEART);
		pieceOrangeClubs = new Piece(Piece.Color.ORANGE, Piece.Shape.CLUBS);
		piecePurpleSquare = new Piece(Piece.Color.PURPLE, Piece.Shape.SQUARE);
		board = new Board();
		
	}
	
	// ----- Inherited Methods -----
	
	@Test
	public void testInheritedMethods() {
		assertEquals("Inherited getName().", player.getName(), "Jeroen");
		assertEquals("Inherited getID().", player.getID(), 0);
		assertEquals("Inherited getHand().", player.getHand().size(), 0);
		
		// Test the removal and receiving of a Piece.
		player.receive(piece);
		assertEquals("Add a Piece to Hand.", player.getHand().size(), 1);
		
		player.remove(piece);
		assertEquals("Remove a Piece from the Hand", player.getHand().size(), 0);	
	}
	
	// ----- determineMove() -----
	/**
	 * Test is determineMove() results into an array of Trades or Places.
	 */
	@Test
	public void testDetermineMove() {
		player.receive(pieceBlueDiamond);
		player.receive(pieceGreenHeart);
		player.receive(pieceOrangeClubs);
		player.receive(piecePurpleSquare);
		player.receive(pieceRedSpade);
		player.receive(pieceYellowCircle);
		Move[] result = player.determineMove(board);
		assertTrue(result instanceof Move[]);
		boolean sameInstance = true;
		if (result[0] instanceof Trade) {
			for (int i = 1; i < result.length && sameInstance == true; i++) {
				sameInstance = result[i] instanceof Trade;
			}
		} else {
			for (int i = 0; i < result.length && sameInstance == true; i++) {
				sameInstance = result[i] instanceof Place;
			}
		}
		assertTrue(sameInstance);
	}

	// ----- Invalid Cells -----
	
	@Test 
	public void testDetermineFirstMove() {
		
		
		
	}
}