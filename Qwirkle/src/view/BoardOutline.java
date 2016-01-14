package view;

import model.Board;

public class BoardOutline {

	/**
	 * Returns a String with the indexes atop the board.
	 */
	public static String getEdges(Board b) {
		String topIndex = "   ";
		for (int i = b.getMinColumn(); i <= b.getMaxColumn(); i++) {
			if (i < 10) {
				topIndex += "   " + i;

			} else if (i < 100) {
				topIndex += "  " + i;

			} else {
				topIndex += " " + i;
			}
		}
		return topIndex;
	}
}
