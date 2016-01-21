package exceptions;

public class InvalidMoveException extends Exception {

	private static final long serialVersionUID = -4181475150415857423L;

	private String message;
	
	public InvalidMoveException(String message) {
		this.message = message;
	}
	
	public String getInfo() {
		return message;
	}
	
}
