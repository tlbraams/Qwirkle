package experiments;

import org.fusesource.jansi.AnsiConsole;
//import static org.fusesource.jansi.Ansi.*;

public class LayOut {
	
	private final static int LENGTH = 10;
	private final static int WIDTH = 10;

	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		
		// Set up board markers
		String top = "" +(char)218;
		String border = "" + (char)195;
		String bottem = "" + (char)192;
		for (int i = 1; i < LENGTH; i++) {
			top = top + (char)196 + (char) 194;
			border = border + (char)196 + (char)197;
			bottem = bottem + (char)196 + (char)193;
		}
		top = top + (char)196 + (char)191;
		border = border + (char)196 + (char)180;
		bottem = bottem + (char)196 + (char)217;
		
		// Print board
		
		for (int i = 0; i < LENGTH; i++) {
			String row = "";
			for (int j = 0; j < WIDTH; j++) {
				row = row + (char)179 + (char)4;
			}
			row = row + (char)179;
			if(i == 0) {
				System.out.println(top);
				System.out.println(row);
			} else if(i == LENGTH -1) {
				System.out.println(border);
				System.out.println(row);
				System.out.println(bottem);
			} else {
				System.out.println(border);
				System.out.println(row);
			}
		}
	}
}
