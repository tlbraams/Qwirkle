package ansi;

import model.Board;

public class BoardOutlineAnsi {

	/**
	 * A list of all the char encodings used for the borders of the board with Jansi.
	 */
	public static final int TOP_LEFT_CORNER = 218;
	public static final int TOP_RIGHT_CORNER = 191;
	public static final int BOTTEM_LEFT_CORNER = 192;
	public static final int BOTTEM_RIGHT_CORNER = 217;
	public static final int TOP_CROSS = 194;
	public static final int LEFT_CROSS = 195;
	public static final int RIGHT_CROSS = 180;
	public static final int BOTTEM_CROSS = 193;
	public static final int CROSS = 197;
	public static final int HORIZONTAL_LINE = 196;
	
	/**
	 * Returns a String[] with all the Strings needed to print a board with markings.
	 */
	public static String[] getEdges(Board b) {
		String[] result = new String[6];
		String topIndex0 = "    ";
		String topIndex10 = "    ";
		String topIndex100 = "    ";
		String top = "   " + (char) TOP_LEFT_CORNER;
		String border = "   " + (char) LEFT_CROSS;
		String bottem = "   " + (char) BOTTEM_LEFT_CORNER;
		for (int i = b.getMinColumn(); i < b.getMaxColumn(); i++) {
			if (i < 10) {
				topIndex0 += "  ";
				topIndex10 += "  ";
				topIndex100 += i + " ";
			} else if (i < 100) {
				topIndex0 += "  ";
				topIndex10 += i / 10 + " ";
				topIndex100 += (i - (i / 10) * 10) + " ";
			} else {
				topIndex0 += i / 100 + " ";
				topIndex10 += i / 10 - (i / 100) * 10 + " ";
				topIndex100 += i - (i / 100) * 100 - ((i / 10) * 10 - (i / 100) * 100) + " ";
			}
			top = top + (char) HORIZONTAL_LINE + (char) TOP_CROSS;
			border = border + (char) HORIZONTAL_LINE + (char) CROSS;
			bottem = bottem + (char) HORIZONTAL_LINE + (char) BOTTEM_CROSS;
		}
		top = top + (char) HORIZONTAL_LINE + (char) TOP_RIGHT_CORNER;
		border = border + (char) HORIZONTAL_LINE + (char) RIGHT_CROSS;
		bottem = bottem + (char) HORIZONTAL_LINE + (char) BOTTEM_RIGHT_CORNER;
		result[0] = topIndex0;
		result[1] = topIndex10;
		result[2] = topIndex100;
 		result[3] = top;
		result[4] = border;
		result[5] = bottem;
		return result;
	}
}
