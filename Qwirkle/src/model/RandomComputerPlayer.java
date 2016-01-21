package model;

import controller.Game;

public class RandomComputerPlayer extends ComputerPlayer {

	// ----- Instance Variables -----
	private int timeToThink;
	
	/**
	 * Ook werken met Timers. Deze gooien ActionEvents naar alle ActionListeners. Dit kunnen we gebruiken om 
	 * de AI Time To think in te stellen. 
	 */
	
	
	// ----- Constructor -----
	public RandomComputerPlayer(String name, int ID, int timeToThink) {
		super(name, ID);
		this.timeToThink = timeToThink;
	}

	// ----- Queries -----
	/**
	 * Determines one Place to make by checking all possible places on the board 
	 * with all the Pieces in his hand. 
	 */
	public Move[] determineMove(Board board) {
		Move[] result = new Move[1];
		for(Piece piece: hand) {
			for(int row = board.getMinRow(); row < board.getMaxRow(); row++) {
				for(int column = board.getMinColumn(); column < board.getMaxColumn(); column++) {
					result[0] = new Place(piece, row, column);
					if (board.validMove(result, this)) {
						return result;
					}
				}
			}
		}
		for(Piece piece: hand) {
			for(int i = 0; i < hand.size(); i++) {
				result[i] = new Trade(piece);
			}
		}
		return result;
	}
}