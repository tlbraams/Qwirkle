package model;

import exceptions.InvalidMoveException;

/**
 * This class models a ComputerPlayer that can only make one Place every turn. 
 * @author Tycho Braams & Jeroen Mulder
 * @version $1.1
 *
 */

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
	
	
	
	// ----- Commands -----
	/**
	 * Tries to find a possible Move for as long as the AI is allowed to think. 
	 * It first tries to find a Place. If no Place was found the whole hand is traded. 
	 */
	
	public /*@ NonNull */Move[] determineMove(/*@ NonNull */Board board) {
		startTime = System.currentTimeMillis();
		boolean validPlace = false;
		Piece highestScoringPiece = null;
		int highestScore = 0;
		int highestScoringRow = 0;
		int highestScoringColumn = 0;
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
							int score = board.getScore(result);
							if (highestScore < score) {
								highestScore = score;
								highestScoringPiece = piece;
								highestScoringRow = row;
								highestScoringColumn = column;
								validPlace = true;
							}
						}
					} catch (InvalidMoveException e) {
						// System.out.println(e.getInfo());
					}
					
				}
			}
		}
		if (validPlace) {
			result[0] = new Place(highestScoringPiece, highestScoringRow, highestScoringColumn);
			return result;
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
	 * Make a Move by placing one Piece (the first in the hand) at (91,91). 
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
	
	/**
	 * Sets the ai think time to the given value.
	 * @param aiTime
	 */
	public void setAITime(int aiTime) {
		timeToThink = aiTime;
	}
}