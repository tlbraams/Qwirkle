package test;

import controller.Game;
import model.*;
import view.*;
/**
 * Test the layout of the Board.
 */
public class TestingBoardLayout {

	public static void main(String[] args) {
		Player[] players = new Player[2];
		players[0] = new HumanPlayer("Tycho", 0);
		players[1] = new HumanPlayer("Jeroen", 1);
		Game game = new Game(2, players, 1000);
		Board board = game.getBoard();
		TUI view = game.getView();
		board.setPiece(10, 10, board.draw());
		board.setPiece(10, 11, board.draw());
		board.setPiece(10, 12, board.draw());
		board.setPiece(10, 13, board.draw());
		board.setPiece(10, 14, board.draw());
		board.setPiece(10, 15, board.draw());
		board.setPiece(11, 10, board.draw());
		board.setPiece(12, 10, board.draw());
		board.setPiece(13, 10, board.draw());
		board.setPiece(14, 10, board.draw());
		board.setPiece(15, 10, board.draw());
		board.setPiece(15, 11, board.draw());
		board.setPiece(15, 12, board.draw());
		board.setPiece(16, 11, board.draw());
		board.setPiece(16, 12, board.draw());
		board.setPiece(16, 13, board.draw());
		view.printBoard(board);
		
	}
}
