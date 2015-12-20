package model;

public class HumanPlayer extends LocalPlayer {

	/**
	 * The HumanPlayer class extends LocalPlayer.
	 * It is the model of a player that gives input about what moves it wants to make.
	 */
	public static final int MAX_HAND = 6;
	
	private String name;
	private int age;
	private Piece[] hand;
	
	public HumanPlayer(String name, int age) {
		this.name = name;
		this.age = age;
		hand = new Piece[MAX_HAND];
	}
	
	// ----------- Queries -------------------
	public int getAge() {
		return age;
	}
	
	public String getName() {
		return name;
	}
	
	public Piece[] getHand() {
		return hand;
	}
	
	
	
}
