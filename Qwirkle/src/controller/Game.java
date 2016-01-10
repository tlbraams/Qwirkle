package controller;

import model.*;
import view.TUI;

public class Game implements Runnable {

	/**
	 * The game class will control the flow of a game using the Classes in the model package
	 * and interacting with the user via the View Clas(ses) in the view package.
	 */
	
	private Board board;
	private TUI view;
	private int playerCount;
	private Player[] players;
	private int aiTime;
	
	private int currentPlayerID;
	
	// -------------- Constructor -------------------
	public Game(int playerCount, Player[] players, int thinkTime){
		board = new Board();
		view = new TUI(this);
		this.playerCount = playerCount;
		this.players = new Player[this.playerCount];
		for(int i = 0; i < playerCount + 1; i++) {
			this.players[i] = players[i];
		}
		aiTime = thinkTime;
	}
	
	public Game(int boardSize, int playerCount, Player[] players, int thinkTime){
		board = new Board(boardSize);
		view = new TUI(this);
		this.playerCount = playerCount;
		this.players = new Player[this.playerCount];
		for(int i = 0; i < playerCount; i++) {
			this.players[i] = players[i];
		}
		aiTime = thinkTime;
	}
	
	// -------------- Queries -----------------------
	public Board getBoard() {
		return board;
	}
	
	public TUI getView() {
		return view;
	}
	
	public int findScore(Move[] moves) {
		return 0;
	}
	
	
	// --------------- Commands: -------------------------
	
	
	public void run() {
		String welcome = "NAMES";
		for(int i = 0; i < playerCount; i++) {
			welcome += " " + players[i].getName() + " " + players[i].getID();
		}
		welcome += " " + aiTime;
		System.out.println(welcome);
		playGame();
	}
	
	public void makeMove(Move[] moves) {
		
	}
	
	public void findFirstPlayer() {
		int maxScore = 0;
		int playerNumber = 0;
		Move[] maxMove = null;
		for(int i = 0; i < playerCount; i++) {
			Move[] move = players[i].determineMove(board);
			if (findScore(move) > maxScore){
				maxScore = findScore(move);
				playerNumber = i;
				maxMove = move;
			}
		}
		makeMove(maxMove);
		currentPlayerID = (playerNumber + 1) % playerCount;
	}
	
	public void playGame() {
		for(int i = 0; i < playerCount; i++) {
			for(int j = 0; j < 6; j++) {
				players[i].receive(board.draw());
			}	
		}
		findFirstPlayer();
		boolean running = true;
		while (running) {
			Move[] moves = players[currentPlayerID].determineMove(board);
			if(validMove(moves)){
				makeMove(moves);
				
			}			
			currentPlayerID = (currentPlayerID + 1) % playerCount;
			view.update();
		}
		
	}

	private boolean validMove(Move[] moves) {
		// TODO Auto-generated method stub
		return false;
	}
	
}