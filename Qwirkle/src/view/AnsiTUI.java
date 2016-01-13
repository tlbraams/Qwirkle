package view;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import model.*;
import controller.*;

public class AnsiTUI {

	private static final int VERTICAL_LINE = 179;
	/**
	 * The TUI class is used as the textual user interface.
	 */
	private Game control;
	
	public AnsiTUI(Game game) {
		control = game;
	}
	
	
	
	// ------------ Commands ------------------------
	
	public void update() {
		printBoard(control.getBoard());
	}
	
	public void printBoard(Board b) {
		AnsiConsole.systemInstall();
		String[] edges = BoardOutlineAnsi.getEdges(b);
		for (int i = 0; i < b.getSize(); i++) {
			String row = "";
			if (i < 10) {
				row = "  " + i;
			} else if (i < 100){
				row = " " + i;
			} else {
				row = "" + i;
			}
			
			for (int j = 0; j < b.getSize(); j++) {
				if(b.getCell(i, j) != null) {
					row = row + (char)VERTICAL_LINE + b.getCell(i, j).getAnsiCode();
				}
				else row = row + (char)VERTICAL_LINE + " ";
			}
			row = row + (char)VERTICAL_LINE;
			if(i == 0) {
				System.out.println(ansi().render(edges[0]));
				System.out.println(ansi().render(edges[1]));
				System.out.println(ansi().render(edges[2]));
				System.out.println(ansi().render(edges[3]));
				System.out.println(ansi().render(row));
			} else if(i == b.getSize() -1) {
				System.out.println(ansi().render(edges[4]));
				System.out.println(ansi().render(row));
				System.out.println(ansi().render(edges[5]));
			} else {
				System.out.println(ansi().render(edges[4]));
				System.out.println(ansi().render(row));
			}
		}
	}
}
