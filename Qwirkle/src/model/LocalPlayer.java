package model;

import java.util.HashSet;

public abstract class LocalPlayer implements Player {

	/**
	 * The LocalPlayer class implements the Player interface.
	 * The LocalPlayer class is used to model players that are available on the system.
	 */
	protected HashSet<Piece> hand;
	protected String name;
	protected int id;
	
	/**
	 * @return The IDnumber of this player.
	 */
	/*@ pure */ public int getID() {
		return id;
	}
	
	/**
	 * @return The name of this player.
	 */
	/*@ pure */ public String getName() {
		return name;
	}
	
	/**
	 * @return The hand of this player.
	 */
	/*@ pure */ public HashSet<Piece> getHand() {
		return hand;
	}
	
	public void setID(int number){
		id = number;
	}
	
	public Move[] determineMove(Board board) {
		return null;
	}
	
	public void receive(Piece piece) {
		hand.add(piece);
	}
}
