package test;

import org.junit.Before;
import org.junit.Test;


import model.Board;
import model.HumanPlayer;
import model.Move;
import model.Piece;
import model.Place;
import model.Player;
import model.RandomComputerPlayer;
import view.TUI;

public class TestRandomAI {

	private int aiThinkTime;
	private Player[] players;
	private Board board;
	private TUI tui;
	private RandomComputerPlayer player;
	private Piece pieceGreenDiamond;
	private Piece pieceGreenSpade;
	private Piece pieceGreenCircle;
	private Piece pieceGreenHeart;
	private Piece piece;
	
	@Before
	public void setUp() {
		aiThinkTime = 9999;
		player = new RandomComputerPlayer("AI", 1, aiThinkTime);
		players = new Player[] {new HumanPlayer("Jeroen", 0), player}; 
		board = new Board();
		piece = new Piece(Piece.Color.ORANGE, Piece.Shape.CIRCLE);
		pieceGreenDiamond = new Piece(Piece.Color.GREEN, Piece.Shape.DIAMOND);
		pieceGreenSpade = new Piece(Piece.Color.GREEN, Piece.Shape.SPADE);
		pieceGreenCircle = new Piece(Piece.Color.GREEN, Piece.Shape.CIRCLE);
		pieceGreenHeart = new Piece(Piece.Color.GREEN, Piece.Shape.HEART);
		tui = new TUI(board, players.length);
	}
	
	@Test
	public void simpleTest() {
		for (int j = 0; j < 6; j++) {
			piece = board.draw();
			player.receive(piece);
		}	
		board.setPiece(91, 91, pieceGreenDiamond);
		board.setPiece(92, 91, pieceGreenSpade);
		board.setPiece(93, 91, pieceGreenCircle);
		board.setPiece(94, 91, pieceGreenHeart);
		Move[] moves = player.determineMove(board);
		System.out.println(player.getHand().toString());
		for (Move move: moves) {
			if (move instanceof Place) {
				board.setPiece(((Place) move).getRow(),
								((Place) move).getColumn(), move.getPiece());
			}
		}
		tui.update();
		
	}

}
