package network;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class handles a game for the server and knows which players are participating.
 * @author Tycho
 *
 */
public class GameHandler extends Thread {

	private List<ClientHandler> players;
	private Server server;
	private boolean started;
	
	
	public GameHandler(Server server) {
		this.server = server;
		players = new ArrayList<>();
		started = false;
	}
	
	// ----- Queries: -----
	
	public boolean isStarted() {
		return started;
	}
	
	// ---- Commands: -----
	
	public void run() {
		while(players.size() <= 4) {
			started = false;
		}
		started = true;
		playGame();
	}
	
	public void addClientHandler(ClientHandler player) { 
		players.add(player);
	}
	
	public void playGame() {
	}
	
	public int validName(String name) {
		int result = -1;
		if(!name.contains(" ") && name.length() < 17 && name.length() >= 1 && name.matches("[a-zA-Z]+")) {
			result = players.size();
		}
		return result;
	}
}
