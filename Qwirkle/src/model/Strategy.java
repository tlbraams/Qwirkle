package model;

import java.util.HashSet;

public interface Strategy {

	public Place[] findMove(HashSet<Piece> hand, Board board);
}
