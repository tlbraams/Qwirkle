package model;

import java.util.ArrayList;
import java.util.HashSet;

/**
* The ComputerPlayer class extends the LocalPlayer class.
* It is the model of a computer player, a class that calculates moves automatically,
* most likely by using a strategy (which can have different grades of "smartness").
*/
public class ComputerPlayer extends LocalPlayer {

	public static final int MAX_HAND = 6;
	
	private long timeToThink;
	private Strategy strategy;
	
	/**
	 * Creates a new ComputerPlayer with a random name and an empty hand
	 * of the maximum size.
	 * @param name the given name
	 * @param age the given age
	 */
	public ComputerPlayer(String name, int number, String strat, long thinkTime) {
		this.id = number;
		this.name = name;
		strategy = findStrat(strat);
		timeToThink = thinkTime;
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
	public/*@ NonNull */Move[] determineMove(/*@ NonNull */Board board) {
		Move[] result = null;
		Place[] place = strategy.findMove(hand, board);
		if (place[0] == null) {
			result = new Move[hand.size()];
			int i = 0;
			for (Piece piece: hand) {
				result[i] = new Trade(piece);
				i++;
			}
		} else {
			result = place;
		}
		return result;
	}
	
	public Move[] determineFirstMove(Board board) {
		Move[] result = null;
		int max = 0;
		for (Piece p: hand) {
			ArrayList<Piece> colorPieces = new ArrayList<>();
			ArrayList<Piece> shapePieces = new ArrayList<>();
			colorPieces.add(p);
			shapePieces.add(p);
			HashSet<Piece> restHand = new HashSet<>(hand);
			restHand.remove(p);
			int color = 1;
			int shape = 1;
			
			for (Piece rp: restHand) {
				if (rp.getColor().equals(p.getColor()) && !rp.getShape().equals(p.getShape())) {
					color++;
					colorPieces.add(rp);
				} else if (!rp.getColor().equals(p.getColor())
							 && rp.getShape().equals(p.getShape())) {
					shape++;
					shapePieces.add(p);
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
	 * Sets the Think time of this ComputerPlayer to the given value.
	 * @param thinkTime the time to think
	 */
	public void setAITime(int thinkTime) {
		timeToThink = thinkTime;
	}
	
	public Strategy findStrat(String strat) {
		Strategy result = null;
		if (strat.equals("RandomWithScore")) {
			result = new RandomWithScoreStrategy(this, timeToThink);
		} else {
			result = new RandomStrategy(this, timeToThink);
		}
		return result;
	}
}
