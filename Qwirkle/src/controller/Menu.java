package controller;

import java.util.Scanner;

import exceptions.InvalidGameException;
import exceptions.InvalidNameException;
import model.*;

/**
 * Asks the player to register. Once enough players have gathered or they
 * start manually a new game is started. This Game is player locally. 
 */

@SuppressWarnings("resource")
public class Menu {
	
	// ----- Instance Variables -----
	private Player[] players;
	int playerCount;
	int aiTime;
	
	public static void main(String[] args) {
		try {
			new Menu().run();
		} catch (InvalidGameException e) {
			System.out.println(e.getInfo());
		}
		
	}
	
	public void run() throws InvalidGameException {
		players = new Player[4];
		Scanner in = new Scanner(System.in);
		Boolean running = true;
		playerCount = 0;
		aiTime = 1000;
		while (running) {
			displayOptions();
			String line = in.nextLine();
			if (line.equals("1")) {
				registerNewPlayer();
			} else if (line.equals("2")) {
				if (playerCount > 1) {
					new Game(playerCount, players, aiTime).run();
					playerCount = 0;
					players = new Player[4];
				} else {
					System.out.println("This game does not have enough players yet. Please wait.");
				}
			} else if (line.equals("3")) {
				setAiTime();
			} else if (line.equals("4")) {
				in.close();
				running = false;
				System.out.println("Goodbye!");
			}
		}
	}
	
	public void displayOptions() {
		System.out.println("What would you like to do?");
		System.out.println("Register new Player ............. 1");
		System.out.println("Start the Game .................. 2");
		System.out.println("Set the AI time ................. 3");
		System.out.println("Exit the application ............ 4");
	}
	
	public void setAiTime() {
		System.out.println("How long would you look the computerplayer to think? (ms)");
		Scanner in = new Scanner(System.in);
		if (in.hasNextInt()) {
			aiTime = in.nextInt();
		} else {
			System.out.println("Not a valid entry. Only integers are accepted.");
		}
	}
	
	public void displayRegistrationOptions() {
		System.out.println("What kind of player would you like to register?");
		System.out.println("Human player ...... ............. 7");
		System.out.println("Computer player ................. 8");
	}
	
	public void registerNewPlayer() {
		displayRegistrationOptions();
		Boolean running = true;
		Scanner line = new Scanner(System.in);
		while (running) {
			String kindOfPlayer = line.nextLine();
			if (kindOfPlayer.equals("7")) {
				registerHumanPlayer();
				running = false;
			}
			if (kindOfPlayer.equals("8")) {
				registerComputerPlayer();
				running = false;
			}
		}
	}
	
	public void registerHumanPlayer() {
		System.out.println("What is your name "
						+ " (can only contain letters with maximum length of 16)?");
		Boolean running = true;
		Scanner line = new Scanner(System.in);
		while (running) {
			String name = line.nextLine();
			try {
				isRightLength(name);
				hasOnlyLetters(name);
				
				players[playerCount] = new HumanPlayer(name, playerCount);
				playerCount++;
				running = false;
			} catch (InvalidNameException e) {
				e.getInfo();
			}
			if (playerCount == 4) {
				new Game(playerCount, players, aiTime).run();
				playerCount = 0;
				players = new Player[4];
			}
		}
	}
	
	public void registerComputerPlayer() {
		System.out.println("What name do you want the Computer Player to have?"
						+ " (can only contain letters with maximum length of 16)");
		Boolean running = true;
		Scanner line = new Scanner(System.in);
		while (running) {
			String name = line.nextLine();
			try {
				isRightLength(name);
				hasOnlyLetters(name);
				
				players[playerCount] = new RandomComputerPlayer(name, playerCount, aiTime);
				playerCount++;
				running = false;
			} catch (InvalidNameException e) {
				e.getInfo();
			}
			if (playerCount == 4) {
				new Game(playerCount, players, aiTime).run();
				playerCount = 0;
				players = new Player[4];
			}
		}
	}
	
	/**
	 * Tests if a given name is of the right length.
	 * @param name
	 * @throws InvalidNameException
	 */
	/*
	 *@ ensures 	0 < name.length && name.length < 17;
	 */
	public void isRightLength(/*@ NonNull */String name) throws InvalidNameException {
		if (name.length() > 16) {
			throw new InvalidNameException("Your name must have a maximum length of 16."
							+ " Please enter a new name.");
		} else if (name.length() < 1) {
			throw new InvalidNameException("Your name is too short. Please enter a new name.");	
		}
	}
	
	/**
	 * Tests if a given name only contains letters. 
	 * @param name
	 * @throws InvalidNameException
	 */
	public void hasOnlyLetters(/*@ NonNull */String name) throws InvalidNameException {
		char[] characters = name.toCharArray();
		if (name.contains(" ")) {
			throw new InvalidNameException("Your name cannot contain a space."
					+ " Please enter a new name.");
		} 
		for (char character: characters) {
			int ascii = (int) character;
			if (!((64 < ascii && ascii < 91) || (96 < ascii && ascii < 123))) {
				throw new InvalidNameException("Your name contains characters other than letters."
						+ " Please enter a new name.");
			} 
		}
	}
}
