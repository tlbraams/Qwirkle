package test;

import controller.Game;
import model.*;
import view.*;

public class TestingBoardLayout {

	public static void main(String[] args) {
		Game game = new Game(21, 2, new String[]{"Tycho", "Stephan"}, new int[]{22,20});
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
