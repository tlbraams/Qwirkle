package model;

public class Trade implements Move {

	
	private Piece piece;
	
	// ----- Constructor -----
	public Trade(Piece piece) {
		this.piece = piece;
	}
	
	// --------------------- Queries: ------------------------
	
	public Piece getPiece() {
		return piece;
	}
	
	/**
	 * Returns a textual representation of this Trade.
	 */
	public String toString() {
		return " " + piece;
	}
}
