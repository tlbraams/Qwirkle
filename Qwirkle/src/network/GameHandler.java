package network;

import java.util.List;

/**
 * This Class handles a game for the server and knows which players are participating.
 * @author Tycho
 *
 */
public class GameHandler extends Thread {
	
	private NetworkPlayer[] players;
	private int aiTime;
	private NetworkGame game;
	
	
	public GameHandler(List<NetworkPlayer> newPlayers) {
		players = newPlayers.toArray(new NetworkPlayer[newPlayers.size()]);
	}

	
	// ---- Commands: -----
	
	public void run() {
		playGame();
	}

	
	public void playGame() {
		game = new NetworkGame(players.length, players, aiTime, this);
		new Thread(game).start();
	}
	
	
	public void broadcast(String message) {
		for (NetworkPlayer p: players) {
			p.sendCommand(message);
		}
	}
}
