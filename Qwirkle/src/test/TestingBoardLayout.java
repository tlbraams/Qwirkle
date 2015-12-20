package test;

import controller.Game;
import model.*;
import view.*;

public class TestingBoardLayout {

	public static void main(String[] args) {
		Game game = new Game(2, new String[]{"Tycho", "Stephan"}, new int[]{22,20});
		Board board = game.getBoard();
		TUI view = game.getView();
		board.setPiece(91, 91, board.draw());
		board.setPiece(91, 92, board.draw());
		board.setPiece(91, 93, board.draw());
		board.setPiece(91, 94, board.draw());
		board.setPiece(91, 95, board.draw());
		board.setPiece(91, 96, board.draw());
		board.setPiece(92, 91, board.draw());
		board.setPiece(93, 91, board.draw());
		board.setPiece(94, 91, board.draw());
		board.setPiece(95, 91, board.draw());
		board.setPiece(96, 91, board.draw());
		board.setPiece(96, 92, board.draw());
		board.setPiece(96, 93, board.draw());
		board.setPiece(95, 93, board.draw());
		board.setPiece(95, 94, board.draw());
		board.setPiece(95, 95, board.draw());
		view.printBoard(board);
		
	}
}
