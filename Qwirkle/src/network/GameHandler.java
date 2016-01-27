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
	
	// ---- Constructor: ----
	/**
	 * Creates a new GameHandler with the given players.
	 * @param newPlayers the players participating
	 */
	public GameHandler(List<NetworkPlayer> newPlayers) {
		players = newPlayers;
		aiTime = 10000;
	}

	
	// ---- Commands: -----
	
	/**
	 * PlayGame() is called to start playing a Game.
	 */
	public void run() {
		playGame();
	}

	/**
	 * A new NetworkGame is started with the Players participating.
	 */
	public void playGame() {
		NetworkPlayer[] playing = players.toArray(new NetworkPlayer[players.size()]);
		game = new NetworkGame(players.size(), playing, aiTime, this);
		new Thread(game).start();
	}
	
	/**
	 * Sends the given message to all the players in the list of participating players.
	 * @param message the message to send
	 */
	public void broadcast(String message) {
		for (NetworkPlayer p: players) {
			p.sendCommand(message);
		}
	}
	
	/**
	 * Removes the Player with the given ID from the list of players.
	 * @param playerID the ID for the player to kick
	 */
	public void kick(int playerID) {
		players.removeIf(p -> p.getID() == playerID);
	}
}
