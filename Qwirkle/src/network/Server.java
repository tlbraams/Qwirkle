package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("resource")
public class Server {

	private static final String USAGE = "usage: " + Server.class.getName() + " <port>";
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println(USAGE);
			System.exit(0);
		}
		
		Server server = new Server(Integer.parseInt(args[0]));
		server.run();
	}
	
	// ------------
	
	private int port;
	private List<NetworkPlayer> readyPlayers;
	private List<GameHandler> threads;
	
	public Server(int portArg) {
		port = portArg;
		threads = new ArrayList<>();
		readyPlayers = new ArrayList<>();
	}
	
	public void run() {
		try {
			ServerSocket ssock = new ServerSocket(port);
			ArrayList<NetworkPlayer> players = new ArrayList<>();
			while (true) {
				print("Waiting for new Client.");
				Socket sock = ssock.accept();
				print("Received new connection: " + sock.getPort());
				NetworkPlayer networkPlayer = new NetworkPlayer(this, sock);
				new Thread(networkPlayer).start();
				players.add(networkPlayer);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

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
	

	public int validName(String name) {
		int result = -1;
		if (!name.contains(" ") && name.length() < 17 && name.length() >= 1
						&& name.matches("[a-zA-Z]+")) {
			result = readyPlayers.size();
		}
		return result;
	}
	
	
	public void print(String message) {
		System.out.println(message);
	}
}
