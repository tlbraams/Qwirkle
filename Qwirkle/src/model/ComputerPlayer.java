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
	 * determining which Move to make. If the strategy doesn't find a move, the entire hand
	 * will be traded.
	 */
	/*
	 *@ ensure 		(\forall int i = 0; i >= 0 && i < \result.length; \result[i] instanceof Move);
	 */
	public/*@ non_null */Move[] determineMove(/*@ non_null */Board board) {
		Move[] result = null;
		Place[] place = strategy.findMove(hand, board);
		if (place[0] == null) {
			int toTrade = 0;
			if (board.getStack().size() < 6) {
				toTrade = board.getStack().size();
			} else {
				toTrade = hand.size();
			}
			result = new Move[toTrade];
			int i = 0;
			for (Piece piece: hand) {
				if (i < toTrade) {
					result[i] = new Trade(piece);
					i++;
				}
			}
		} else {
			result = place;
		}
		return result;
	}
	
	/**
	 * Finds the firstMove for a ComputerPlayer.
	 * This consists of finding the longest line of pieces in the hand
	 * and placing them on the board.
	 */
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
				boolean fitsColor = true;
				for (Piece cp: colorPieces) {
					if (rp.getColor().equals(cp.getColor())
									&& !rp.getShape().equals(cp.getShape())) {
						fitsColor = fitsColor && true;
					} else {
						fitsColor = false;
					}
				}
				boolean fitsShape = true;
				for (Piece sp: shapePieces) {
					if (!rp.getColor().equals(sp.getColor())
									&& rp.getShape().equals(sp.getShape())) {
						fitsShape = fitsShape && true;
					} else {
						fitsShape = false;
					}
				}
				if (fitsColor) {
					color++;
					colorPieces.add(rp);
				} else if (fitsShape) {
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
			System.out.println("Piece: " + p.toString());
			for (Move m: result) {
				System.out.println("Move Piece: " + m.getPiece().toString());
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
	
	/**
	 * Finds and creates a Strategy corresponding to the given String.
	 * @param strat the String describing the Strategy
	 * @return the created Strategy
	 */
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
