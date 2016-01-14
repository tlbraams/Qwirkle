package exceptions;

public class UnconnectedMoveException extends InvalidMoveException {
	
	private static final long serialVersionUID = -3581543510344922705L;

	public UnconnectedMoveException() {
		super("Your move is not allowed,"
				+ " at least one piece needs to connect to the already placed Pieces");
	}
}
