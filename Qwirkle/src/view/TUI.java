package view;

import model.*;
import controller.*;

/**
 * The TUI class is used as the textual user interface.
 */
public class TUI {

	// ----- Instance Variables -----
	private Game control;
	private Board board;
	int playerCount;
	
	// ----- Constructor -----
	public TUI(Game game) {
		control = game;
		board = control.getBoard();
		playerCount = control.getPlayerCount();
	}
	
	public TUI(Board board, int players) {
		control = null;
		this.board = board;
		playerCount = players;
	}
	
	
	// ----- Commands -----
	
	public void update() {
		printBoard(board);
		printScore(board);
	}
	
	public void printBoard(Board b) {
		String edges = BoardOutline.getEdges(b);
		System.out.println(edges);
		for (int i = b.getMinRow(); i <= b.getMaxRow(); i++) {
			String row = "";
			if (i < 10) {
				row = "  " + i;
			} else if (i < 100) {
				row = " " + i;
			} else {
				row = "" + i;
			}
			for (int j = b.getMinColumn(); j <= b.getMaxColumn(); j++) {
				if (b.getCell(i, j) != null) {
					row = row + "| " + b.getCell(i, j).toString();
				} else {
					row = row + "|   ";
				}
			}
			row = row + "|";
			System.out.println(row);
		}
	}
	
	public void printScore(Board b) {
		String result = "Scores:";
		for (int i = 0; i < playerCount; i++) {
			result += " player" + i + ": " + b.getScore(i);
		}
		System.out.println(result);
	}
}