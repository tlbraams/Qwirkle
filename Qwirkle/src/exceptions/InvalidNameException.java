package exceptions;

public class InvalidNameException extends Exception {

	private static final long serialVersionUID = 1L;
	private String message;
	
	public InvalidNameException(String message) {
		this.message = message;
	}
	
	public void getInfo() {
		System.out.println(message);
	}
	
}