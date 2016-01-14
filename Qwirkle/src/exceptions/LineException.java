package exceptions;

public class LineException extends InvalidMoveException {
	
	private static final long serialVersionUID = 1092760962418508345L;

	public LineException() {
		super("You are only allowed to lay pieces in a straight, connected line.");
	}

}
