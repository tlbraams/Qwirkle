package exceptions;

public class NoMoveException extends InvalidMoveException {

	private static final long serialVersionUID = -4009038438061598733L;

	public NoMoveException() {
		super("It's not allowed to not make a move");
	}
}
