package model;

public class Trade implements Move {

	
	private Piece piece;
	
	public Trade(Piece piece) {
		this.piece = piece;
	}
	
	// --------------------- Queries: ------------------------
	
	public Piece getPiece() {
		return piece;
	}
	
	public String toString() {
		return " " + piece;
	}
}
