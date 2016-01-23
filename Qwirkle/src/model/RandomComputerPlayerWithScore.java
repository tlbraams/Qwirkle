package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import exceptions.InvalidMoveException;

public class RandomComputerPlayerWithScore extends ComputerPlayer {

	// ----- Instance Variables -----
	private int timeToThink;
	long startTime;       
	
	// ----- Constructor -----
	public RandomComputerPlayerWithScore(String name, int id, int timeToThink) {
		super(name, id);
		this.timeToThink = timeToThink;
	}

	// ----- Queries -----
	/**
	 * Tries to find a possible Move for as long as the AI is allowed to think. 
	 * It first tries to find a Place. If no Place was found the whole hand is traded. 
	 */
	
	public /*@ NonNull */Move[] determineMove(/*@ NonNull */Board board) {
		startTime = System.currentTimeMillis();
		Move[] result = new Move[1];
		int maxScore = 0;
		// Try to make a Place.
		outerloop:
		for (Piece piece: hand) {
			for (int row = board.getMinRow(); row <= board.getMaxRow(); row++) {
				for (int column = board.getMinColumn(); column <= board.getMaxColumn(); column++) {
					if ((System.nanoTime() - startTime) < timeToThink) {
						break outerloop;
					}
					Move[] temp = new Move[1];
					temp[0] = new Place(piece, row, column);
					try {
						if (board.validMove(temp, this)) {
							Board scoreBoard = board.deepCopy();
							scoreBoard.setPiece(row, column, piece);
							if (scoreBoard.getScore(temp) > maxScore) {
								maxScore = board.getScore(temp);
								result[0] = temp[0];
							}
						}
					} catch (InvalidMoveException e) {
						//System.out.println(e.getInfo());
					}
				}
			}
		}
		if (result[0] == null) {
			// Trade the whole hand. 
			result = new Move[hand.size()];
			int i = 0;
			for (Piece piece: hand) {
				result[i] = new Trade(piece);
				i++;
			}
		}
		return result;
	}
	
	/**
	 * Finds the longest row of pieces in the hand.
	 */
	public Move[] determineFirstMove(Board board) {
		Move[] result = null;
		int max = 0;
		for (Piece p : hand) {
			ArrayList<Piece> colorPieces = new ArrayList<>();
			ArrayList<Piece> shapePieces = new ArrayList<>();
			colorPieces.add(p);
			shapePieces.add(p);
			Set<Piece> restHand = new HashSet<>(hand);
			restHand.remove(p);
			int color = 1;
			int shape = 1;
			
			// Check if either the color or the shape of each rp matches that of p.
			// If so, add to color or shape. 
			for (Piece rp : restHand) {
				if (rp.getColor().equals(p.getColor()) && !rp.getShape().equals(p.getShape())) {
					color++;
					colorPieces.add(rp);
				} else if (!rp.getColor().equals(p.getColor())
								&& rp.getShape().equals(p.getShape())) {
					shape++;
					shapePieces.add(rp);
				}
			}
			if (color > max) {
				max = color;
				result = new Move[colorPieces.size()];
				for (int i = 0; i < colorPieces.size(); i++) {
					result[i] = new Place(colorPieces.get(i), 91, 91 + i);
				}
			}
			if (shape > max) {
				max = shape;
				result = new Move[shapePieces.size()];
				for (int i = 0; i < shapePieces.size(); i++) {
					result[i] = new Place(shapePieces.get(i), 91, 91 + i);
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