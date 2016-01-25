package model;

import java.util.HashSet;

import exceptions.InvalidMoveException;

public class RandomStrategy implements Strategy {

	// ----- Instance Variables -----
	private long timeToThink;
	private Player player;
      
	
	// ----- Constructor -----
	public RandomStrategy(Player player, long timeToThink) {
		this.timeToThink = timeToThink;
		this.player = player;
	}

	// ----- Queries -----
	/**
	 * Tries to find a possible Move for as long as the AI is allowed to think. 
	 * It first tries to find a Place. If no Place was found the whole hand is traded. 
	 */
	
	public /*@ NonNull */Place[] findMove(/*@ NonNull */HashSet<Piece> hand,
						/*@ NonNull */Board board) {
		long startTime = System.currentTimeMillis();
		Place[] result = new Place[1];
		// Try to make a Place.
		outerloop:
		for (Piece piece: hand) {
			for (int row = board.getMinRow(); row < board.getMaxRow(); row++) {
				for (int column = board.getMinColumn(); column < board.getMaxColumn(); column++) {
					if ((System.nanoTime() - startTime) < timeToThink) {
						break outerloop;
					}
					result[0] = new Place(piece, row, column);
					try {
						if (board.validMove(result, player)) {
							return result;
						}
					} catch (InvalidMoveException e) {
					}
				}
			}
		}
		return result;
	}
	
	
	/**
	 * Sets the ai think time to the given value.
	 * @param aiTime
	 */
	public void setAITime(int aiTime) {
		timeToThink = aiTime;
	}
}