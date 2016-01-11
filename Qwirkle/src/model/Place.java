package model;

public class Place implements Move {

	Piece piece;
	int row;
	int column;
	
	public Place(Piece piece, int row, int column) {
		this.piece = piece;
		this.row = row;
		this.column = column;
	}
	
	// --------------------- Queries: -----------------
	
	public Piece getPiece() {
		return piece;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	public String toString() {
		return " " + piece.toString() + " " + row + " " + column;
	}
}
