package view;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import model.*;
import controller.*;

public class TUI {

	private static final int VERTICAL_LINE = 179;
	/**
	 * The TUI class is used as the textual user interface.
	 */
	@SuppressWarnings("unused")
	private Game control;
	
	public TUI(Game game) {
		control = game;
	}
	
	
	
	// ------------ Commands ------------------------
	
	public void printBoard(Board b) {
		AnsiConsole.systemInstall();
		String[] edges = BoardOutline.getEdges();
		for (int i = 0; i < Board.DIM; i++) {
			String row = i + " ";
			for (int j = 0; j < Board.DIM; j++) {
				row = row + (char)VERTICAL_LINE + b.get(i, j).getAnsiCode();
			}
			row = row + (char)VERTICAL_LINE;
			if(i == 0) {
				System.out.println(ansi().render(edges[0]));
				System.out.println(ansi().render(edges[1]));
				System.out.println(ansi().render(row));
			} else if(i == Board.DIM -1) {
				System.out.println(ansi().render(edges[2]));
				System.out.println(ansi().render(row));
				System.out.println(ansi().render(edges[3]));
			} else {
				System.out.println(ansi().render(edges[2]));
				System.out.println(ansi().render(row));
			}
		}
	}
}
