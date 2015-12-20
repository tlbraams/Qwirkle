package experiments;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;


public class SymbolsWithColor {

	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		for(int i = 0; i < 256; i++) {
			System.out.println(ansi().render("@|red " + i + " " + (char) i + "|@"));
		}
	}
}
