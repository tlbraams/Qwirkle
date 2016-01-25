package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("resource")
public class Server {

	private static final String USAGE = "When starting the Server '"
							+ Server.class.getName() + "', please declare the <port>.";
	/**
	 * Runs the server, using the argument given on startup as the port the server will use.
	 * Prints a standard message if an error occurs
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println(USAGE);
			System.exit(0);
		}
		
		Server server = null;
		try {
			server = new Server(Integer.parseInt(args[0]));
		} catch (NumberFormatException e) {
			System.out.println("This is not a valid port number. Please only use numbers.");
		}
		server.run();
	}
	
	// ----- Instance Variables -----
	
	private int port;
	private List<NetworkPlayer> readyPlayers;
	private List<GameHandler> threads;
	
	// ---- Constructor: ----
	/**
	 * Creates a new Server with the given port.
	 * @param portArg the port the server will listen to
	 */
	public Server(int portArg) {
		port = portArg;
		threads = new ArrayList<>();
		readyPlayers = new ArrayList<>();
	}
	
	// ---- Commands: ----
	/**
	 * Creates the ServerSocket and waits for connections.
	 * If a connection is established, a new NetworkPlayer is made to handle the connection.
	 * This Player is started as a new Thread.
	 */
	public void run() {
		try {
			ServerSocket ssock = new ServerSocket(port);
			while (true) {
				print("Waiting for new Client.");
				Socket sock = ssock.accept();
				print("Received new connection: " + sock.getPort());
				NetworkPlayer networkPlayer = new NetworkPlayer(this, sock);
				new Thread(networkPlayer).start();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Adds the given player to the list of ready Players waiting for a game.
	 * If the amount of readyPlayers reaches 4, a new GameHandler is started and the list 
	 * of ready Players is cleared.
	 * @param player the player to add.
	 */
	public void setReady(NetworkPlayer player) {
		readyPlayers.add(player);
		if (readyPlayers.size() == 4) {
			GameHandler game = new GameHandler(readyPlayers);
			game.start();
			threads.add(game);
			print("Created new game");
			readyPlayers = new ArrayList<>();
		}
	}
	
	/**
	 * Checks if a given name statisfies the standard requirements.
	 * Returns the ID given to the player if the name is valid, else -1.
	 * @param name the name to check
	 * @return -1 or the ID.
	 */
	public int validName(String name) {
		int result = -1;
		if (!name.contains(" ") && name.length() < 17 && name.length() >= 1
						&& name.matches("[a-zA-Z]+")) {
			result = readyPlayers.size();
		}
		return result;
	}
	
	/**
	 * Prints the given message on the standard output.
	 * @param message the message to print
	 */
	public void print(String message) {
		System.out.println(message);
	}
}
