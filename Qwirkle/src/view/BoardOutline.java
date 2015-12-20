package view;

import model.Board;

public class BoardOutline {

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
		String[] result = new String[4];
		String topIndex = "    ";
		String top = "   " +(char)TOP_LEFT_CORNER;
		String border = "   " + (char)LEFT_CROSS;
		String bottem = "   " + (char)BOTTEM_LEFT_CORNER;
		for (int i = 1; i < b.getSize(); i++) {
			topIndex = topIndex + i + " ";
			top = top + (char)HORIZONTAL_LINE + (char) TOP_CROSS;
			border = border + (char)HORIZONTAL_LINE + (char)CROSS;
			bottem = bottem + (char)HORIZONTAL_LINE + (char)BOTTEM_CROSS;
		}
		top = top + (char)HORIZONTAL_LINE + (char)TOP_RIGHT_CORNER;
		border = border + (char)HORIZONTAL_LINE + (char)RIGHT_CROSS;
		bottem = bottem + (char)HORIZONTAL_LINE + (char)BOTTEM_RIGHT_CORNER;
		result[0] = topIndex;
		result[1] = top;
		result[2] = border;
		result[3] = bottem;
		return result;
	}
}
