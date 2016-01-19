package network;




/**
 * This Class handles a game for the server and knows which players are participating.
 * @author Tycho
 *
 */
public class GameHandler extends Thread {

	private NetworkPlayer[] players;
	private int playerCount;
	private int aiTime;
	private boolean started;
	private NetworkGame game;
	
	
	public GameHandler() {
		players = new NetworkPlayer[4];
		started = false;
		playerCount = 0;
	}
	
	// ----- Queries: -----
	
	public boolean isStarted() {
		return started;
	}
	
	// ---- Commands: -----
	
	public void run() {
		while (playerCount < 4) {
			started = false;
		}
		started = true;
		playGame();
	}
	
	public void addNetworkPlayer(NetworkPlayer player) { 
		players[playerCount] = player;
		playerCount++;
	}

	
	public void playGame() {
		game = new NetworkGame(playerCount, players, aiTime, this);
		game.run();
	}
	
	public int validName(String name) {
		int result = -1;
		if (!name.contains(" ") && name.length() < 17 && name.length() >= 1
						&& name.matches("[a-zA-Z]+")) {
			result = playerCount;
		}
		return result;
	}
	
	public void broadcast(String message) {
		for (NetworkPlayer p: players) {
			p.sendCommand(message);
		}
	}
}
