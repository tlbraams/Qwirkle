package experiments;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;


public class OutputWithColor {
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31;1m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34;1m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36;1m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
	private static char diamond = (char) 4;
	private static char heart = (char) 3;
	private static char spade = (char) 6;
	private static char horizontalLine = (char) 179;
	
	private static String sequence = ANSI_RED + diamond + ANSI_WHITE + horizontalLine + ANSI_GREEN + spade + ANSI_RESET;
	private static String sequence2 = ANSI_BLUE + diamond + ANSI_RESET + horizontalLine + ANSI_RED + heart +  ANSI_RESET;
	
	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		System.out.println(ansi().fg(RED).a("Hello").fg(GREEN).a(" World").reset());
		System.err.println("red?");
		System.out.println(ansi().render("@|red Hello|@"));
		System.out.println(ansi().render(sequence).reset());
		System.out.println(ansi().render(sequence2).reset());
		
	}

}
