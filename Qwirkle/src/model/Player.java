package model;

import java.util.HashSet;

public interface Player {

	/**
	 * The interface that models a player.
	 */
	
	public String getName();
	
	public int getID();
	
	public void receive(Piece piece);
	
	public Move[] determineMove(Board board);
	
	public HashSet<Piece> getHand();
	
	public void remove(Piece p);
	
}
