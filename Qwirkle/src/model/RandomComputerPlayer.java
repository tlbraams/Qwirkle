package model;

import exceptions.InvalidMoveException;

public class RandomComputerPlayer extends ComputerPlayer {

	// ----- Instance Variables -----
	private int timeToThink;
	long startTime;       
	
	// ----- Constructor -----
	public RandomComputerPlayer(String name, int id, int timeToThink) {
		super(name, id);
		this.timeToThink = timeToThink;
	}

	// ----- Queries -----
	/**
	 * Tries to find a possible Move for as long as the AI is allowed to think. 
	 * It first tries to find a Place. If no Place was found the whole hand is traded. 
	 */
	
	public /*@ NonNull */Move[] determineMove(/*@ NonNull */Board board) {
		startTime = System.nanoTime();
		Move[] result = new Move[1];
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
						
						if (board.validMove(result, this)) {
						return result;
						}
					} catch (InvalidMoveException e) {
						//System.out.println(e.getInfo());
					}
				}
			}
		}
		// Trade the whole hand. 
		result = new Move[hand.size()];
		int i = 0;
		for (Piece piece: hand) {
			result[i] = new Trade(piece);
			i++;
		}
		return result;
	}
	
	/**
	 * Tries to find a possible Move given the board and the Pieces in the hand
	 * of the ComputerPlayer. It prefers a Place over a Trade. 
	 */
	
	public Move[] determineFirstMove(Board board) {
		Move[] result = new Move[1];
		boolean filled = false;
		for (Piece piece: hand) {
			if (!filled) {
				result[0] = new Place(piece, 91, 91);
				filled = true;
			}
		}
		return result;
		
	}
}