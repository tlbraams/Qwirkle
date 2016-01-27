package model;

import java.util.HashSet;

import exceptions.InvalidMoveException;

public class RandomWithScoreStrategy implements Strategy {
	
	private long timeToThink;
	Player player;
	
	// ----- Constructor -----
	public RandomWithScoreStrategy(Player player, long thinkTime) {
		this.player = player;
		timeToThink = thinkTime;
	}

	// ----- Queries -----
	
	
	// ----- Commands -----
	
	/**
	 * Tries to find a possible Move for as long as the AI is allowed to think. 
	 * Until the time has passed, the method will look for the move (of placing 1 piece)
	 * that obtains the highest Score.
	 */
	public /*@ NonNull */Place[] findMove(/*@ NonNull */ HashSet<Piece> hand,
						/*@ NonNull */Board board) {
		long startTime = System.currentTimeMillis();
		Place[] result = new Place[1];
		int maxScore = 0;
		// Try to make a Place.
		outerloop:
		for (Piece piece: hand) {
			for (int row = board.getMinRow(); row <= board.getMaxRow(); row++) {
				for (int column = board.getMinColumn(); column <= board.getMaxColumn(); column++) {
					if ((System.nanoTime() - startTime) < timeToThink) {
						break outerloop;
					}
					Place[] temp = new Place[1];
					temp[0] = new Place(piece, row, column);
					try {
						if (board.validMove(temp, player)) {
							Board scoreBoard = board.deepCopy();
							scoreBoard.setPiece(row, column, piece);
							if (scoreBoard.getScore(temp) > maxScore) {
								maxScore = board.getScore(temp);
								result[0] = temp[0];
							}
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