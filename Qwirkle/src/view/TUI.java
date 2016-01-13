package view;


import model.*;
import controller.*;

public class TUI {

	/**
	 * The TUI class is used as the textual user interface.
	 */
	private Game control;
	
	public TUI(Game game) {
		control = game;
	}
	
	
	
	// ------------ Commands ------------------------
	
	public void update() {
		printBoard(control.getBoard());
	}
	
	public void printBoard(Board b) {
		String edges = BoardOutline.getEdges(b);
		System.out.println(edges);
		for (int i = b.getMinRow(); i < b.getMaxRow(); i++) {
			String row = "";
			if (i < 10) {
				row = "  " + i;
			} else if (i < 100){
				row = " " + i;
			} else {
				row = "" + i;
			}
			for (int j = b.getMinColumn(); j < b.getMaxColumn(); j++) {
				if(b.getCell(i, j) != null) {
					row = row + "| " + b.getCell(i, j).toString();
				}
				else {
					row = row + "|   ";
				}
			}
			row = row + "|";
			System.out.println(row);
		}
	}
}