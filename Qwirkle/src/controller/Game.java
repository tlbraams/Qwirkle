package controller;

import model.*;
import view.TUI;

public class Game {

	/**
	 * The game class will control the flow of a game using the Classes in the model package
	 * and interacting with the user via the View Clas(ses) in the view package.
	 */
	

	private Board board;
	private TUI view;
	private int playerCount;
	private Player[] players;
	
	// -------------- Constructor -------------------
	public Game(int playerCount, String[] playerNames, int[] playerAge){
		board = new Board();
		view = new TUI(this);
		this.playerCount = playerCount;
		players = new Player[this.playerCount];
		for(int i = 0; i < players.length; i++) {
			players[i] = new HumanPlayer(playerNames[i], playerAge[i]);
		}
	}
	
	// -------------- Queries -----------------------
	public Board getBoard() {
		return board;
	}
	
	public TUI getView() {
		return view;
	}

	
	
	
}