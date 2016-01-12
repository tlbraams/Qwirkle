package controller;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

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
	
	public int findMaxScore(HashSet<Piece> hand) {
		int max = 0;
		for(Piece p : hand) {
			Set<Piece> restHand = hand;
			restHand.remove(p);
			int color = 1;
			int shape = 1;
			for(Piece rp : restHand) {
				if(rp.getColor().equals(p.getColor()) && !rp.getShape().equals(p.getShape())) {
					color++;
				} else if (!rp.getColor().equals(p.getColor()) && rp.getShape().equals(p.getShape())) {
					shape++;
				}
			}
			if (color > max) {
				max = color;
			}
			if (shape > max) {
				max = shape;
			}
		}
		return max;
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
	
	public void tradePieces(Move[] moves, Player player) {
		Piece[] pieces = new Piece[moves.length];
		for(int i = 0; i < moves.length; i++) {
			pieces[i] = moves[i].getPiece();
			player.receive(board.draw());
		}
		board.tradeReturn(pieces);
	}
	
	public void findFirstPlayer() {
		int maxScore = 0;
		int playerNumber = 0;
		for(int i = 0; i < playerCount; i++) {
			HashSet<Piece> hand = players[i].getHand();
			int temp = findMaxScore(hand);
			if (temp > maxScore){
				maxScore = temp;
				playerNumber = i;
			}
		}
		currentPlayerID = playerNumber;
	}
	
	public void playGame() {
		for(int i = 0; i < playerCount; i++) {
			for(int j = 0; j < 6; j++) {
				Piece piece = board.draw();
				players[i].receive(piece);
			}	
		}
		findFirstPlayer();
		boolean running = true;
		while (running) {
			Move[] moves = players[currentPlayerID].determineMove(board);
			if(validMove(moves)){
				if(moves[0] instanceof Place) {
					makeMove(moves);
				} else if (moves[0] instanceof Trade) {
					tradePieces(moves, players[currentPlayerID]);
				}
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