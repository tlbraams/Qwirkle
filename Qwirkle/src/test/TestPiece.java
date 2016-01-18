package test;

import org.junit.*;

import model.Piece;

import static org.junit.Assert.*;

/**
 * Test the class Piece. Testing the the toString() and the correct assignment of parameters in the constructor.
 */

public class TestPiece {
	
	private Piece pieceBlueDiamond;
	private Piece pieceRedSpade;
	private Piece pieceYellowCircle;
	private Piece pieceGreenHeart;
	private Piece pieceOrangeClubs;
	private Piece piecePurpleSquare;
	
	@Before
	public void setUp() {
		pieceBlueDiamond = new Piece(Piece.Color.BLUE, Piece.Shape.DIAMOND);
		pieceRedSpade = new Piece(Piece.Color.RED, Piece.Shape.SPADE);
		pieceYellowCircle = new Piece(Piece.Color.YELLOW, Piece.Shape.CIRCLE);
		pieceGreenHeart = new Piece(Piece.Color.GREEN, Piece.Shape.HEART);
		pieceOrangeClubs = new Piece(Piece.Color.ORANGE, Piece.Shape.CLUBS);
		piecePurpleSquare = new Piece(Piece.Color.PURPLE, Piece.Shape.SQUARE);
	}
	
	// ----- toString() -----
	
	@Test
	public void testToString() {
		assertEquals("Bd", pieceBlueDiamond.toString());
		assertEquals("Rx", pieceRedSpade.toString());
		assertEquals("Yo", pieceYellowCircle.toString());
		assertEquals("G*", pieceGreenHeart.toString());
		assertEquals("Ps", piecePurpleSquare.toString());
		assertEquals("Oc", pieceOrangeClubs.toString());
	}
	
	// ----- Constructor Assignment -----
	

	@Test
	public void testConstructor() {
		// Check 2 colors. 
		assertTrue(pieceBlueDiamond.getColor() == Piece.Color.BLUE);
		assertTrue(pieceOrangeClubs.getColor() == Piece.Color.ORANGE);
		
		// Check 2 shapes. 
		assertTrue(pieceGreenHeart.getShape() == Piece.Shape.HEART);
		assertTrue(pieceYellowCircle.getShape() == Piece.Shape.CIRCLE);
	}
	
}