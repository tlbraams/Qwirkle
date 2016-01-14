package exceptions;

public class ShapeColorException extends InvalidMoveException {

	private static final long serialVersionUID = 601112008558511010L;

	public ShapeColorException() {
		super("One or more pieces that you wanted to place didn't follow the shape/color rules.");
	}
}
 