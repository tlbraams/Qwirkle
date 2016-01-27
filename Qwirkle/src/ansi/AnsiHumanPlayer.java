package ansi;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import model.Board;
import model.LocalPlayer;
import model.Move;
import model.Piece;
import model.Place;
import model.RandomWithScoreStrategy;
import model.Strategy;
import model.Trade;

@SuppressWarnings("resource")
public class AnsiHumanPlayer extends LocalPlayer {

	/**
	 * The HumanPlayer class extends LocalPlayer.
	 * It is the model of a player that gives input about what moves it wants to make.
	 */
	public static final int MAX_HAND = 6;
	
	private Strategy hints;
	
	
	/**
	 * Creates a new <code>HumanPlayer</code> with the given name and age and an empty hand
	 * of the maximum size.
	 * @param name the given name
	 * @param age the given age
	 */
	public AnsiHumanPlayer(String name, int number) {
		this.id = number;
		this.name = name;
		hand = new HashSet<Piece>(MAX_HAND);
		hints = new RandomWithScoreStrategy(this, 1000);
	}
	
	// ----------- Queries -------------------
	
	/**
	 * Asks the player which <code>Piece</code>'s from his hand to place where on the given board,
	 * or which <code>Piece</code>'s he wants to trade with the stack.
	 * @param board the given board.
	 * @return An array of the chosen Moves, either all place moves or swap moves.
	 */
	/*
	 *@ ensure 		(\forall int i = 0; i >= 0 && i < \result.length; \result[i] instanceof Move);
	 */
	public /*@ non_null */Move[] determineMove(/*@ non_null */Board board) {
		showHand();
		ArrayList<Move> moves = new ArrayList<Move>();
		int type = showOptions(board);
		if (type == 5) {
			moves = place(); 
		} else if (type == 6) {
			boolean cont = true;
			while (cont) {
				moves = trade();
				cont = false;
			}
		} else if (type == 7) {
			moves = new ArrayList<Move>();
			moves.add(new Place(null, 91, 91));
		}
		Move[] result = moves.toArray(new Move[moves.size()]);
		return result;
	}
	
	/**
	 * Asks the user for the Piece(s) it would like to place.
	 * If the player has such a piece in its hand, the user will be asked
	 * where on the board they would like to place it.
	 * This is repeated until "Done" is entered.
	 * @return The ArrayList<Move> with the decided placements
	 */
	public /*@ non_null */ArrayList<Move> place() {
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
		}
		return result;
	}
	
	/**
	 * Asks the User which pieces it would like to trade.
	 * If the player owns the piece, it is added and the player can enter a new piece,
	 * or "Done" to signal the end of his turn.
	 * @return the ArrayList<Move> with the decided trades
	 */
	public /*@non_null */ArrayList<Move> trade() {
		ArrayList<Move> result = new ArrayList<Move>();
		boolean cont = true;
		while (cont) {
			System.out.println("Please enter the piece you would like to trade:");
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
	
	/**
	 * Finds the first move. For the first Piece it will be automatically placed on 91, 91.
	 * After that the the player can chose where to place them. (Respecting the rules)
	 * It is not possible to trade on the first Move.
	 */
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
								moves.add(new Place(p, 91, 91));
								firstPiece = false;
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
	
	/**
	 * Prints a textual representation of the different Pieces owned by the player.
	 */
	public void showHand() {
		AnsiConsole.systemInstall();
		if (hand.size() < 6) {
			System.out.println("No more pieces in the stack.");
		}
		String result = "Your hand:";
		String ansi = "In symbol:";
		for (Piece p : hand) {
			result += " | " + p.toString();
			ansi += " | " + p.getAnsiCode() + " ";
		}
		System.out.println(result);
		System.out.println(ansi().render(ansi));
	}
	
	/**
	 * Shows the different options for making a move.
	 * These are: Place, Trade, (Pass if no longer able to trade) and Hint.
	 * After a Hint the player will have to chose again.
	 * @param board the board the move will be made on.
	 * @return the chosen option.
	 */
	public int showOptions(Board board) {
		System.out.println(this.getName() + ": What would you like to do?");
		System.out.println("Place tiles ................... 5");
		if (board.getStack().size() == 0) {
			System.out.println("Pass ......................... 7");
		} else {
			System.out.println("Trade tiles ................... 6");
			if (board.getStack().size() < 6) {
				System.out.println("Maximum amount of tradeble pieces: "
								+ board.getStack().size());
			}
		}
		System.out.println("Receive a hint ................ 8");
		int result = 0;
		boolean intRead = false;
		Scanner line = new Scanner(System.in);
		while (!intRead) {
			try (Scanner scannerLine = new Scanner(line.next())) {
				if (scannerLine.hasNextInt()) {
					result = scannerLine.nextInt();
					if (result == 5 || result == 6 || result == 7) {
						intRead = true;
					} else if (result == 8) {
						printHint(board);
					} else {
						System.out.println("Please make a valid choice. (5/6/8)");
					}
				} else {
					System.out.println("Please make a valid choice. (5/6/8)");
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
	 * Prints the hint found by the hint Strategy associated with this class.
	 * @param board the board for which a hint is found.
	 */
	public void printHint(Board board) {
		Place[] hint = hints.findMove(hand, board);
		if (hint[0] == null) {
			System.out.println("No hint possible at this time.");
		} else {
			System.out.println("The following hint has been provided: ");
			for (Place m: hint) {
				System.out.println(m.getPiece() + " " + m.getRow() + " " + m.getColumn());
			}
		}
		System.out.println("Please make a new choice.");
	}
	
	
}
