package model;

import exceptions.InvalidMoveException;

public class RandomComputerPlayer extends ComputerPlayer {

	// ----- Instance Variables -----
	private int timeToThink;
	
	/**
	 * Ook werken met Timers. Deze gooien ActionEvents naar alle ActionListeners.
	 * Dit kunnen we gebruiken om de AI Time To think in te stellen. 
	 */
	
	
	// ----- Constructor -----
	public RandomComputerPlayer(String name, int id, int timeToThink) {
		super(name, id);
		this.timeToThink = timeToThink;
	}

	// ----- Queries -----
	/**
	 * Determines one Place to make by checking all possible places on the board 
	 * with all the Pieces in his hand. 
	 */
	public Move[] determineMove(Board board) {
		Move[] result = new Move[1];
		// Try to make a Place.
		for (Piece piece: hand) {
			for (int row = board.getMinRow(); row < board.getMaxRow(); row++) {
				for (int column = board.getMinColumn(); column < board.getMaxColumn(); column++) {
					result[0] = new Place(piece, row, column);
					try {
						if (board.validMove(result, this)) {
							return result;
						}
					} catch (InvalidMoveException e) {
						System.out.println(e.getInfo());
					}
				}
			}
		}
		result = new Move[hand.size()];
		int i = 0;
		for (Piece piece: hand) {
			result[i] = new Trade(piece);
			i++;
		}
		return result;
	}
	
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