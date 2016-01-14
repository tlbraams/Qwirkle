package exceptions;

public class InvalidMoveException extends Exception {

	private static final long serialVersionUID = -4181475150415857423L;

	private String infoMessage;
	
	public InvalidMoveException(String info) {
		infoMessage = info;
	}
	
	public void getInfo() {
		System.out.println(infoMessage);
	}
	
}
