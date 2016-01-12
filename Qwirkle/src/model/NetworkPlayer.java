package model;

import java.util.HashSet;

public class NetworkPlayer implements Player {

	/**
	 * The NetworkPlayer class implements the Player interface.
	 * It stores the local data needed for a player connected to the server
	 * and a member of the game this client is playing.
	 */
	
	private String name;
	private int id;
	
	public NetworkPlayer(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public int getID() {
		return id;
	}

	public void receive(Piece piece) {
		
	}

	public Move[] determineMove(Board board) {
		return null;
	}

	@Override
	public HashSet<Piece> getHand() {
		// TODO Auto-generated method stub
		return null;
	}
}
