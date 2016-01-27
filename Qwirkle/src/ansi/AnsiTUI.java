package ansi;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;

import java.util.Observable;
import java.util.Observer;

import model.*;
import controller.*;

public class AnsiTUI implements Observer {

	private static final int VERTICAL_LINE = 179;
	/**
	 * The TUI class is used as the textual user interface.
	 */
	private Game control;
	private Board board;
	int playerCount;
	
	public AnsiTUI(Game game) {
		control = game;
		board = game.getBoard();
		playerCount = control.getPlayerCount();
		board.addObserver(this);
	}
	
	public AnsiTUI(Board board, int players) {
		control = null;
		this.board = board;
		playerCount = players;
		board.addObserver(this);
	}
	
	
	
	
	// ------------ Commands ------------------------
	
	public void update(Observable o, Object arg) {
		printBoard(board);
		printScore(board);
	}
	
	public void printBoard(Board b) {
		AnsiConsole.systemInstall();
		String[] edges = BoardOutlineAnsi.getEdges(b);
		for (int i = 0; i < b.getSize(); i++) {
			String row = "";
			if (i < 10) {
				row = "  " + i;
			} else if (i < 100) {
				row = " " + i;
			} else {
				row = "" + i;
			}
			
			for (int j = 0; j < b.getSize(); j++) {
				if (b.getCell(i, j) != null) {
					row = row + (char) VERTICAL_LINE + b.getCell(i, j).getAnsiCode();
				} else {
					row = row + (char) VERTICAL_LINE + " ";
				}
			}
			row = row + (char) VERTICAL_LINE;
			if (i == 0) {
				System.out.println(ansi().render(edges[0]));
				System.out.println(ansi().render(edges[1]));
				System.out.println(ansi().render(edges[2]));
				System.out.println(ansi().render(edges[3]));
				System.out.println(ansi().render(row));
			} else if (i == b.getSize() - 1) {
				System.out.println(ansi().render(edges[4]));
				System.out.println(ansi().render(row));
				System.out.println(ansi().render(edges[5]));
			} else {
				System.out.println(ansi().render(edges[4]));
				System.out.println(ansi().render(row));
			}
		}
	}
	
	/**
	 * Prints the score for the board.
	 * @param b the board for the score to print.
	 */
	public void printScore(Board b) {
		String result = "Scores:";
		for (int i = 0; i < playerCount; i++) {
			result += " player" + i + ": " + b.getScore(i);
		}
		System.out.println(result);
	}
}
