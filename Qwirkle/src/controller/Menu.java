package controller;

import java.util.Scanner;

import model.*;

public class Menu {

	/**
	 * 3 commando's: exit, aanmelden, start. 
	 */
	
	/**
	 * Asks the player to register. Once enough players have gathered or they
	 * start manually a new game is started.
	 */
	private Player[] players;
	int playerCount;
	int aiTime;
	
	public static void main(String[] args) {
		new Menu().run();
	}
	
	public void run() {
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
					System.out.println("Not enough players to start a game, please wait untill more"
									+ " players register.");
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
	
	public void registerNewPlayer() {
		System.out.println("What is your name?"
						+ " (can only contain letters with maximum length of 16)");
		Boolean running = true;
		Scanner line = new Scanner(System.in);
		while (running) {
			String name = line.nextLine();
			if (name.contains(" ")) {
				System.out.println("Your name cannot contain a space. Please enter a new name.");
			} else if (name.length() > 16) {
				System.out.println("Your name must have a maximum length of 16."
								+ " Please enter a new name.");
			} else if (name.length() < 1) {
				System.out.println("Your name is too short. Please enter a new name.");
			} else {
				players[playerCount] = new HumanPlayer(name, playerCount);
				playerCount++;
				running = false;
			}
			if (playerCount == 4) {
				new Game(playerCount, players, aiTime).run();
				playerCount = 0;
				players = new Player[4];
			}

		}
	}
}
