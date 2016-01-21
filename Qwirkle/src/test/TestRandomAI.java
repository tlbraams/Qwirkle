package test;

import org.junit.Before;
import org.junit.Test;


import model.Board;
import model.HumanPlayer;
import model.Piece;
import model.Player;
import model.RandomComputerPlayer;
import view.TUI;

public class TestRandomAI {

	private int aiThinkTime;
	private Player[] players;
	private Board board;
	private TUI tui;
	private RandomComputerPlayer player;
	private int playerCount;
	private Piece pieceGreenDiamond;
	private Piece pieceGreenSpade;
	private Piece pieceGreenCircle;
	private Piece pieceGreenHeart;
	
	@Before
	public void setUp() {
		playerCount = 2;
		aiThinkTime = 9999;
		player = new RandomComputerPlayer("AI", 1, aiThinkTime);
		players = new Player[] {new HumanPlayer("Jeroen", 0), player}; 
		board = new Board();
		pieceGreenDiamond = new Piece(Piece.Color.GREEN, Piece.Shape.DIAMOND);
		pieceGreenSpade = new Piece(Piece.Color.GREEN, Piece.Shape.SPADE);
		pieceGreenCircle = new Piece(Piece.Color.GREEN, Piece.Shape.CIRCLE);
		pieceGreenHeart = new Piece(Piece.Color.GREEN, Piece.Shape.HEART);
		tui = new TUI(board, players.length);
	}
	
	@Test
	public void simpleTest() {
		// Fill the hand of the Players with Pieces
		for (int i = 0; i < playerCount; i++) {
			for (int j = 0; j < 6; j++) {
				Piece piece = board.draw();
				players[i].receive(piece);
			}	
		}
		
		board.setPiece(91, 91, pieceGreenDiamond);
		board.setPiece(92, 91, pieceGreenSpade);
		board.setPiece(93, 91, pieceGreenCircle);
		board.setPiece(94, 91, pieceGreenHeart);
		player.determineMove(board);
		tui.update();
		
	}

}
