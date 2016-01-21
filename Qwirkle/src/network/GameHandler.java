package network;

import java.util.List;

/**
 * This Class handles a game for the server and knows which players are participating.
 * @author Tycho
 *
 */
public class GameHandler extends Thread {
	
	private List<NetworkPlayer> players;
	private int aiTime;
	private NetworkGame game;
	
	
	public GameHandler(List<NetworkPlayer> newPlayers) {
		players = newPlayers;
	}

	
	// ---- Commands: -----
	
	public void run() {
		playGame();
	}

	
	public void playGame() {
		NetworkPlayer[] playing = players.toArray(new NetworkPlayer[players.size()]);
		game = new NetworkGame(players.size(), playing, aiTime, this);
		new Thread(game).start();
	}
	
	
	public void broadcast(String message) {
		for (NetworkPlayer p: players) {
			p.sendCommand(message);
		}
	}
	
	public void kick(int playerID) {
		players.removeIf(p -> p.getID() == playerID);
	}
}
