package model;

import java.util.HashSet;

public class HumanPlayer extends LocalPlayer {

	/**
	 * The HumanPlayer class extends LocalPlayer.
	 * It is the model of a player that gives input about what moves it wants to make.
	 */
	public static final int MAX_HAND = 6;
	
	
	/**
	 * Creates a new <code>HumanPlayer</code> with the given name and age and an empty hand
	 * of the maximum size.
	 * @param name the given name
	 * @param age the given age
	 */
	public HumanPlayer(String name, int number) {
		this.id = number;
		this.name = name;
		hand = new HashSet<Piece>(MAX_HAND);
	}
	
	// ----------- Queries -------------------
	
	/**
	 * Asks the player which <code>Piece</code>'s from his hand to place where on the given board,
	 * or which <code>Piece</code>'s he wants to trade with the stack.
	 * @param board the given board.
	 * @return An array of the chosen Moves, either all place moves or swap moves.
	 */
	public Move[] determineMove(Board board){
		return null;
	}
	
	/**
	 * How to handle kick/exit??.
	 */
}
