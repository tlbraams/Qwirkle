package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

@SuppressWarnings("resource")
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
	 * If the player wants to place tiles,
	 * it is checked whether the given cells exist and are empty. 
	 * @param board the given board.
	 * @return An array of the chosen Moves, either all place moves or swap moves.
	 */
	/*
	 *@ ensure 		(\forall int i = 0; i >= 0 && i < \result.length; \result[i] instanceof Move);
	 */
	public /*@ NonNull */Move[] determineMove(/*@ NonNull */Board board) {
		showHand();
		ArrayList<Move> moves = new ArrayList<Move>();
		int type = showOptions();
		if (type == 5) {
			moves = place(); 
		} else if (type == 6) {
			boolean cont = true;
			while (cont) {
				moves = trade();
			}
		}
		Move[] result = moves.toArray(new Move[moves.size()]);
		return result;
	}
	
	public /*@ NonNull */ArrayList<Move> place() {
		ArrayList<Move> result = new ArrayList<Move>();
		boolean cont = true;
		while (cont) {
			System.out.println("Please enter the piece you would like to place.");
			Scanner line = new Scanner(System.in);
			try (Scanner scLine = new Scanner(line.next())) {
				String input = scLine.next();
				if (input.equals("Done")) {
					cont = false;
				} else {
					boolean found = false;
					for (Piece p: hand) {
						if (p.toString().equals(input) && !found) {
							int row = requestRow();
							int column = requestColumn();
							result.add(new Place(p, row, column));							
							found = true;
						}
					}
				}
			}		
		} return result;
	}
	
	public /*@NonNull */ArrayList<Move> trade() {
		ArrayList<Move> result = new ArrayList<Move>();
		boolean cont = true;
		while (cont) {
			System.out.println("Please enter the piece you would like to trade.");
			Scanner line = new Scanner(System.in);
			try (Scanner scLine = new Scanner(line.next())) {
				String input = scLine.next();
				if (input.equals("Done")) {
					cont = false;
				} else {
					boolean found = false;
					for (Piece p : hand) {
						if (p.toString().equals(input) && !found) {
							result.add(new Trade(p));
							found = true;
						}
					}
				}
			}
		}
		return result;
	}
	
	public Move[] determineFirstMove(Board board) {
		showHand();
		ArrayList<Move> moves = new ArrayList<Move>();
		boolean cont = true;
		boolean firstPiece = true;
		while (cont) {
			if (firstPiece) {
				System.out.println(this.getName() 
								+ ": What piece would you like to place first? (91, 91).");
			} else {
				System.out.println("Please enter the piece you would like to place.");
			}
			Scanner line = new Scanner(System.in);
			try (Scanner scLine = new Scanner(line.next())) {
				String input = scLine.next();
				if (input.equals("Done")) {
					cont = false;
				} else {
					boolean found = false;
					for (Piece p : hand) {
						if (p.toString().equals(input) && !found) {
							int row;
							int column;
							if (firstPiece) {
								row = 91;
								column = 91;
							} else {
								row = requestRow();
								column = requestColumn();
								moves.add(new Place(p, row, column));
								firstPiece = false;
							}
							found = true;
						}
					}
				}
			}
		}
		Move[] result = moves.toArray(new Move[moves.size()]);
		return result;
	}
	
	
	public void showHand() {
		if (hand.size() < 6) {
			System.out.println("No more pieces in the stack.");
		}
		String result = "Your hand:";
		for (Piece p : hand) {
			result += " | " + p.toString();
		}
		System.out.println(result);
	}
	
	public int showOptions() {
		System.out.println(this.getName() + ": What would you like to do?");
		System.out.println("Place tiles ................... 5");
		System.out.println("Trade tiles ................... 6");
		int result = 0;
		boolean intRead = false;
		Scanner line = new Scanner(System.in);
		while (!intRead) {
			try (Scanner scannerLine = new Scanner(line.next())) {
				if (scannerLine.hasNextInt()) {
					result = scannerLine.nextInt();
					if (result == 5 || result == 6) {
						intRead = true;
					} else {
						System.out.println("Please make a valid choice. (5/6)");
					}
				} else {
					System.out.println("Please make a valid choice. (5/6)");
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * Requests row - coordinates from the player. 
	 */
	public int requestRow() {
		System.out.println("In which row would you like to place your piece?");
		int row = 0;
		boolean rowRead = false;
		Scanner line = new Scanner(System.in);
		while (!rowRead) {
			try (Scanner scannerLine = new Scanner(line.next())) {
				if (scannerLine.hasNextInt()) {
					row = scannerLine.nextInt();
					rowRead = true;
				} else {
					System.out.println("Please enter a valid integer.");
				}
			}
		}
		return row;
	}
	
	/**
	 * Requests column coordinates from the player.
	 */
	public int requestColumn() {
		System.out.println("In which column would you like to place your piece?");
		int column = 0;
		boolean columnRead = false;
		Scanner line = new Scanner(System.in);
		while (!columnRead) {
			try (Scanner scannerLine = new Scanner(line.next())) {
				if (scannerLine.hasNextInt()) {
					column = scannerLine.nextInt();
					columnRead = true;
				} else {
					System.out.println("Please enter a valid integer.");
				}
			}
		}
		return column;
	}
	
	/**
	 * How to handle kick/exit??.
	 */
}
