package test;

import org.junit.*;

import model.Board;
import model.HumanPlayer;
import model.Piece;
import model.Player;

import static org.junit.Assert.*;

/**
 * Test the class Board. Testing the stack, the board and the scores.
 * @author JeroenMulder
 */

public class TestBoard {
	
	private Board board;
	private Piece[] piecesOne;
	private Piece[] piecesThree;
	private Piece pieceGreenDiamond;
	private Piece pieceGreenSpade;
	private Piece pieceGreenCircle;
	private Piece pieceGreenHeart;
	
	@Before
	public void setUp() {
		board = new Board();
		piecesOne = new Piece[] {new Piece(Piece.Color.RED, Piece.Shape.DIAMOND)};
		piecesThree = new Piece[] {new Piece(Piece.Color.GREEN, Piece.Shape.DIAMOND),
								   new Piece(Piece.Color.ORANGE, Piece.Shape.SPADE),
								   new Piece(Piece.Color.RED, Piece.Shape.CIRCLE)};
		pieceGreenDiamond = new Piece(Piece.Color.GREEN, Piece.Shape.DIAMOND);
		pieceGreenSpade = new Piece(Piece.Color.GREEN, Piece.Shape.SPADE);
		pieceGreenCircle = new Piece(Piece.Color.GREEN, Piece.Shape.CIRCLE);
		pieceGreenHeart = new Piece(Piece.Color.GREEN, Piece.Shape.CIRCLE);
		
		
	}
	// ----- Stack -----
	/**
	 * 1. Tests if the stack is filled with 108 cards.
	 * 2. Tests if a draw decreases the size of the stack with 1.
	 * 3. Tests if emptyStack correctly indicates if the stack is empty. 
	 */
	@Test
	public void testDraw() {
		assertEquals("Initial size of stack.", 108, board.getStack().size());
		board.draw();
		assertEquals("After 1 draw.", 107, board.getStack().size());
		for (int i = 0; i < 107; i++) {
			board.draw();
		}
		assertTrue(board.emptyStack());
	}
	
	/*
	 *@ requires 	defaultBoard.getStack.size() == 108;
	 */
	@Test
	public void testTradeReturn() {
		board.tradeReturn(piecesOne);
		assertEquals("Returning 1 piece", 109, board.getStack().size());
		board.tradeReturn(piecesThree);
		assertEquals("Returning 1 piece", 112, board.getStack().size());
	}
	
	// ----- Scores -----
	
	@Test
	public void testAddingScores() {
		board.addScore(0, 4);
		assertEquals("Score of playerID 0", board.getScore(0), 4);
		board.addScore(0, 3);
		assertEquals("Score of playerID 0", board.getScore(0), 7);
		board.addScore(3, 2);
		assertEquals("Score of playerID 3", board.getScore(3), 2);
		board.addScore(3, 1);
		assertEquals("Score of playerID 3", board.getScore(3), 3);
	}
	
	// ----- Board -----
	
	@Test
	public void testBoardDimensions() {
		assertTrue(board.getMinColumn() == 85);
		assertTrue(board.getMinRow() == 86);
		assertTrue(board.getMaxColumn() == 97);
		assertTrue(board.getMaxRow() == 97);
		assertTrue(board.getSize() == 183);
		
		assertTrue(board.getBoard().length == 183);
	}
	
	@Test
	public void testGetRowAndColumnLength() {
		// Test getRowLength().
		board.setPiece(91, 91, pieceGreenDiamond);
		assertTrue(board.getRowLength(91, 91) == 1);
		board.setPiece(91, 92, pieceGreenCircle);
		board.setPiece(91, 93, pieceGreenSpade);
		assertTrue(board.getRowLength(91, 91) == 3);
		assertTrue(board.getRowLength(91, 92) == 3);
		
		// Test getColumnLength().
		assertTrue(board.getColumnLength(91, 91) == 1);
		board.setPiece(92, 91, pieceGreenCircle);
		board.setPiece(93, 91, pieceGreenSpade);
		board.setPiece(94, 91, pieceGreenHeart);
		assertTrue(board.getColumnLength(93, 91) == 4);
		assertTrue(board.getColumnLength(94, 91) == 4);
		
		// Test if fields are empty. 
		assertFalse(board.isEmpty(91, 91));
		assertFalse(board.isEmpty(91, 93));
		assertFalse(board.isEmpty(94, 91));
		assertTrue(board.isEmpty(1, 1));
		assertTrue(board.isEmpty(182, 182));
		
		// Test if the correct Pieces are place in the correct cells. 
		assertEquals("Piece in the cell is correct", board.getCell(91, 91), pieceGreenDiamond);
		assertEquals("Piece in the cell is correct", board.getCell(94, 91), pieceGreenHeart);
		}
}
