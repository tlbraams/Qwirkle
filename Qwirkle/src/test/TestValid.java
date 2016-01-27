package test;

import org.junit.*;
import org.junit.rules.ExpectedException;

import exceptions.InvalidMoveException;
import model.Board;
import model.HumanPlayer;
import model.Piece;
import model.Place;
import model.Player;
import model.Trade;


/**
 * Test the valid methods of Board, used for determening if a move is valid for the board.
 * @author Tycho
 *
 */
public class TestValid {

	private Board board;
	private Piece pieceGreenDiamond;
	private Piece pieceGreenSpade;
	private Piece pieceGreenCircle;
	private Piece pieceGreenHeart;
	private Piece pieceRedDiamond;
	private Piece pieceRedCircle;
	private Piece pieceOrangeSpade;
	private Piece pieceOrangeDiamond;
	private Player player;
	
	@Before
	public void setUp() {
		board = new Board();
		player = new HumanPlayer("Tycho", 0);
		pieceRedDiamond = new Piece(Piece.Color.RED, Piece.Shape.DIAMOND);
		pieceOrangeSpade = new Piece(Piece.Color.ORANGE, Piece.Shape.SPADE);
		pieceOrangeDiamond = new Piece(Piece.Color.ORANGE, Piece.Shape.DIAMOND);
		pieceRedCircle = new Piece(Piece.Color.RED, Piece.Shape.CIRCLE);
		pieceGreenDiamond = new Piece(Piece.Color.GREEN, Piece.Shape.DIAMOND);
		pieceGreenSpade = new Piece(Piece.Color.GREEN, Piece.Shape.SPADE);
		pieceGreenCircle = new Piece(Piece.Color.GREEN, Piece.Shape.CIRCLE);
		pieceGreenHeart = new Piece(Piece.Color.GREEN, Piece.Shape.CIRCLE);
	}
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	// test Connected to rest of board.
	
	@Test
	public void testConnected() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenDiamond);
		Place[] place = new Place[]{new Place(pieceGreenSpade, 91, 92)};
		
		board.isConnected(place);
	}
	
	@Test
	public void testUnConnected() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenDiamond);
		Place[] place = new Place[]{new Place(pieceGreenSpade, 91, 93)};
		exception.expect(InvalidMoveException.class);
		board.isConnected(place);
	}
	
	// Test if field is valid
	@Test
	public void testValidField() throws InvalidMoveException {
		Place[] place = new Place[]{new Place(pieceGreenSpade, 91, 93)};
		board.cellsAreValid(place);
	}
	
	@Test
	public void testInValidField() throws InvalidMoveException {
		Place[] place = new Place[]{new Place(pieceGreenSpade, -1, -1)};
		exception.expect(InvalidMoveException.class);
		board.cellsAreValid(place);
	}
	
	// Test if a field is occupied
	@Test
	public void testFreeField() throws InvalidMoveException {
		Place[] place = new Place[]{new Place(pieceGreenSpade, 91, 93)};
		board.cellsAreAvailable(place);
	}
	
	@Test
	public void testOccupiedField() throws InvalidMoveException {
		board.setPiece(91, 91, pieceRedDiamond);
		Place[] place = new Place[]{new Place(pieceGreenSpade, 91, 91)};
		exception.expect(InvalidMoveException.class);
		board.cellsAreAvailable(place);
	}
	
	// Test Player Has Pieces
	@Test
	public void testOwnsPieces() throws InvalidMoveException {
		player.receive(pieceOrangeSpade);
		Place[] place = new Place[]{new Place(pieceOrangeSpade, 91, 92)};
		board.playerHasPiece(place, player);
	}
	
	@Test
	public void testDoesNotOwnPieces() throws InvalidMoveException {
		player.receive(pieceGreenCircle);
		Place[] place = new Place[]{new Place(pieceOrangeSpade, 91, 92)};
		exception.expect(InvalidMoveException.class);
		board.playerHasPiece(place, player);
	}
	
	// Test is one Line
	@Test
	public void testOneRow() throws InvalidMoveException {
		Place[] place = new Place[]{new Place(pieceGreenCircle, 91, 91),
									new Place(pieceRedCircle, 91, 92)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		board.isUninterruptedRow(place);
	}

	
	@Test
	public void testOneColumn() throws InvalidMoveException {
		Place[] place = new Place[]{new Place(pieceGreenCircle, 91, 91),
									new Place(pieceRedCircle, 92, 91)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		board.isUninterruptedColumn(place);
	}
	
	@Test
	public void testRowGap() throws InvalidMoveException {
		Place[] place = new Place[]{new Place(pieceGreenCircle, 91, 91),
									new Place(pieceRedCircle, 91, 93)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		exception.expect(InvalidMoveException.class);
		board.isUninterruptedRow(place);
	}
	
	@Test
	public void testColumnGap() throws InvalidMoveException {
		Place[] place = new Place[]{new Place(pieceGreenCircle, 91, 91),
									new Place(pieceRedCircle, 93, 91)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		exception.expect(InvalidMoveException.class);
		board.isUninterruptedColumn(place);
	}
	
	@Test
	public void testMultipleRow() throws InvalidMoveException {
		Place[] place = new Place[]{new Place(pieceGreenCircle, 91, 91),
									new Place(pieceGreenHeart, 92, 93)};
		player.receive(pieceGreenCircle);
		player.receive(pieceGreenHeart);
		exception.expect(InvalidMoveException.class);
		board.validMove(place, player);
	}
	
	@Test
	public void testMultipleColumn() throws InvalidMoveException {
		Place[] place = new Place[]{new Place(pieceGreenCircle, 91, 91),
									new Place(pieceRedCircle, 91, 92)};
		player.receive(pieceGreenCircle);
		player.receive(pieceGreenHeart);
		exception.expect(InvalidMoveException.class);
		board.validMove(place, player);
	}
	
	// Test if piece fits in Row
	@Test
	public void testFitsRow() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenDiamond);
		Place[] place = new Place[] {new Place(pieceGreenCircle, 91, 92),
										new Place(pieceGreenSpade, 91, 93)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		board.pieceIsConnectedRowAndUnique(place);
	}
	
	@Test
	public void testChangesMatchTypeInRow() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenDiamond);
		Place[] place = new Place[] {new Place(pieceGreenCircle, 91, 92),
										new Place(pieceRedCircle, 91, 93)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		exception.expect(InvalidMoveException.class);
		board.pieceIsConnectedRowAndUnique(place);
	}
	
	@Test
	public void testDoesNotMatchRow() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenDiamond);
		Place[] place = new Place[] {new Place(pieceOrangeSpade, 91, 92),
										new Place(pieceOrangeDiamond, 91, 93)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		exception.expect(InvalidMoveException.class);
		board.pieceIsConnectedRowAndUnique(place);
	}
	
	// Test if piece fits in Column
	@Test
	public void testFitsColumn() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenDiamond);
		Place[] place = new Place[] {new Place(pieceGreenCircle, 92, 91),
										new Place(pieceGreenSpade, 93, 91)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		board.pieceIsConnectedColumnAndUnique(place);
	}
	
	@Test
	public void testChangesMatchTypeInColumn() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenDiamond);
		Place[] place = new Place[] {new Place(pieceGreenCircle, 92, 91),
										new Place(pieceRedCircle, 93, 91)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		exception.expect(InvalidMoveException.class);
		board.pieceIsConnectedColumnAndUnique(place);
	}
	
	@Test
	public void testDoesNotMatchColumn() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenDiamond);
		Place[] place = new Place[]{new Place(pieceOrangeSpade, 92, 91),
									new Place(pieceOrangeDiamond, 92, 91)};
		board.setPiece(place[0].getRow(), place[0].getColumn(), place[0].getPiece());
		board.setPiece(place[1].getRow(), place[1].getColumn(), place[1].getPiece());
		exception.expect(InvalidMoveException.class);
		board.pieceIsConnectedColumnAndUnique(place);
	}
	
	// Test if a piece fits in a board
	@Test
	public void testValidMove() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenCircle);
		board.setPiece(91, 92, pieceGreenDiamond);
		board.setPiece(91, 93, pieceGreenSpade);
		board.setPiece(91, 94, pieceGreenHeart);
		board.setPiece(90, 92, pieceOrangeDiamond);
		board.setPiece(90, 93, pieceOrangeSpade);
		player.receive(pieceRedCircle);
		player.receive(pieceRedDiamond);
		Place[] place = new Place[]{new Place(pieceRedCircle, 92, 91),
									new Place(pieceRedDiamond, 92, 92)};
		board.validMove(place, player);
	}
	
	@Test
	public void testNonValidMove() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenCircle);
		board.setPiece(91, 92, pieceGreenDiamond);
		board.setPiece(91, 93, pieceGreenSpade);
		board.setPiece(91, 94, pieceGreenHeart);
		board.setPiece(90, 92, pieceOrangeDiamond);
		board.setPiece(90, 93, pieceOrangeSpade);
		player.receive(pieceRedCircle);
		player.receive(pieceRedDiamond);
		Place[] place = new Place[]{new Place(pieceRedCircle, 92, 92),
									new Place(pieceRedDiamond, 92, 91)};
		exception.expect(InvalidMoveException.class);
		board.validMove(place, player);
	}
	
	@Test
	public void testValidColumnMove() throws InvalidMoveException {
		board.setPiece(91, 91, pieceGreenCircle);
		board.setPiece(91, 92, pieceGreenDiamond);
		board.setPiece(91, 93, pieceGreenSpade);
		board.setPiece(91, 94, pieceGreenHeart);
		board.setPiece(92, 91, pieceRedCircle);
		board.setPiece(90, 93, pieceOrangeSpade);
		player.receive(pieceOrangeDiamond);
		player.receive(pieceRedDiamond);
		Place[] place = new Place[]{new Place(pieceOrangeDiamond, 90, 92),
									new Place(pieceRedDiamond, 92, 92)};
		board.validMove(place, player);
	}
	
	// Test trades
	@Test
	public void testValidSwap() throws InvalidMoveException {
		player.receive(pieceGreenCircle);
		player.receive(pieceGreenDiamond);
		Trade[] trade = new Trade[]{new Trade(pieceGreenCircle), 
									new Trade(pieceGreenDiamond)};
		board.validMove(trade, player);
	}
	
	@Test
	public void testNonValidSwap() throws InvalidMoveException {
		player.receive(pieceGreenCircle);
		player.receive(pieceGreenDiamond);
		Trade[] trade = new Trade[]{new Trade(pieceOrangeSpade), 
									new Trade(pieceOrangeDiamond)};
		exception.expect(InvalidMoveException.class);
		board.validMove(trade, player);
	}

}
