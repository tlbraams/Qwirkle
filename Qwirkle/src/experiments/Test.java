package experiments;

import model.*;

public class Test {

	public static void main(String[] args) {
		ComputerPlayer cp = new ComputerPlayer("Tycho", 0, "Random", 1000);
		Board b = new Board();
		Piece p = new Piece(Piece.Color.RED, Piece.Shape.CIRCLE);
		Piece p1 = new Piece(Piece.Color.BLUE, Piece.Shape.CIRCLE);
		Piece p2 = new Piece(Piece.Color.BLUE, Piece.Shape.CIRCLE);
		cp.receive(p);
		cp.receive(p1);
		cp.receive(p2);
		cp.determineFirstMove(b);
	}
	
}
