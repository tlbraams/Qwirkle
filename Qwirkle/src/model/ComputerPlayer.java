package model;

import java.util.HashSet;

/**
* The ComputerPlayer class extends the LocalPlayer class.
* It is the model of a computer player, a class that calculates moves automatically,
* most likely by using a strategy (which can have different grades of "smartness").
*/
public abstract class ComputerPlayer extends LocalPlayer {

	public static final int MAX_HAND = 6;
	
	/**
	 * Creates a new ComputerPlayer with a random name and an empty hand
	 * of the maximum size.
	 * @param name the given name
	 * @param age the given age
	 */
	public ComputerPlayer(String name, int number) {
		this.id = number;
		this.name = name;
		hand = new HashSet<Piece>(MAX_HAND);
	}
	
	// ----------- Queries -------------------
	
	/**
	 * Makes a Move[]. Subclasses of ComputerPlayer can have different strategies for 
	 * determining which Move to make. 
	 */
	/*
	 *@ ensure 		(\forall int i = 0; i >= 0 && i < \result.length; \result[i] instanceof Move);
	 */
	public abstract /*@ NonNull */Move[] determineMove(/*@ NonNull */Board board);
	
	/**
	 * Sets the Think time of this ComputerPlayer to the given value.
	 * @param thinkTime the time to think
	 */
	public abstract void setAITime(int thinkTime);
}
