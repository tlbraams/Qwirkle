package model;

public class Piece {

	/**
	 * The class that models the game pieces used by the game player.
	 * It has a color and a shape attribute. These will be constrained, using enum,
	 * to the 6 colors and shapes used by the game.
	 */
	
	
	// The possible colors of a piece.
	public enum Color {RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE, DEFAULT};
	// The possible shapes of a piece.
	public enum Shape {DIAMOND, SQUARE, SPADE, HEART, CLUBS, CIRCLE, BLOCKED};
	
	// The color and shape of this piece.
	private final Color color;
	private final Shape shape;
	
	/**
	 * Creates a piece with the given color and shape.
	 * @param color the given color
	 * @param shape the given shape
	 */
	/*@
	 * ensures this.color == color && this.shape = shape;
	 */
	public Piece(Color color, Shape shape){
		this.color = color;
		this.shape = shape;
	}
	
	/*@ pure */ public Color getColor() {
		return color;
	}
	
	/*@ pure */ public Shape getShape() {
		return shape;
	}
	
	/**
	 * Returns a textual representation of this <code>Piece</code>.
	 */
	/*@ pure */ public String toString() {
		String result = "";
		switch(color) {
		case RED:
			result += "red ";
			break;
		case BLUE:
			result += "blue ";
			break;
		case GREEN:
			result += "green ";
			break;
		case ORANGE:
			result += "orange ";
			break;
		case PURPLE:
			result += "purple ";
			break;
		case YELLOW:
			result += "yellow ";
			break;
		default:
			break;
		}
		switch(shape){
		case CIRCLE:
			result += "circle";
			break;
		case CLUBS:
			result += "club";
			break;
		case DIAMOND:
			result += "diamond";
			break;
		case HEART:
			result += "heart";
			break;
		case SPADE:
			result += "spade";
			break;
		case SQUARE:
			result += "square";
			break;
		default:
			break;
		}
		return result;
	}
	
	/*@ pure */ public String getAnsiCode() {
		String result = "";
		switch(shape){
		case CIRCLE:
			result = "" + CIRCLE_CHAR;
			break;
		case CLUBS:
			result = "" + CLUB_CHAR;
			break;
		case DIAMOND:
			result = "" + DIAMOND_CHAR;
			break;
		case HEART:
			result = "" + HEART_CHAR;
			break;
		case SPADE:
			result = "" + SPADE_CHAR;
			break;
		case SQUARE:
			result = "" + SQUARE_CHAR;
			break;
		default:
			result = " ";
			break;
		}
		switch(color) {
		case RED:
			result = ANSI_RED + result + ANSI_RESET;
			break;
		case BLUE:
			result = ANSI_BLUE + result + ANSI_RESET;
			break;
		case GREEN:
			result = ANSI_GREEN + result + ANSI_RESET;
			break;
		case ORANGE:
			result = ANSI_CYAN + result + ANSI_RESET;
			break;
		case PURPLE:
			result = ANSI_PURPLE + result + ANSI_RESET;
			break;
		case YELLOW:
			result = ANSI_YELLOW + result + ANSI_RESET;
			break;
		default:
			break;
		}
		
		return result;
	}
	
	public static final char DIAMOND_CHAR = (char) 4;
	public static final char SQUARE_CHAR = (char) 254;
	public static final char SPADE_CHAR = (char) 6;
	public static final char CLUB_CHAR = (char) 5;
	public static final char HEART_CHAR = (char) 3;
	public static final char CIRCLE_CHAR = (char) 2;
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31;1m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33;1m";
	public static final String ANSI_BLUE = "\u001B[34;1m";
	public static final String ANSI_PURPLE = "\u001B[35;1m";
	public static final String ANSI_CYAN = "\u001B[36;1m";
	public static final String ANSI_WHITE = "\u001B[37m";

}
