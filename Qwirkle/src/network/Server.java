package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	private static final String USAGE = "usage: " + Server.class.getName() + " <port>";
	
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println(USAGE);
			System.exit(0);
		}
		
		Server server = new Server(Integer.parseInt(args[0]));
		server.run();
	}
	
	// ------------
	
	private int port;
	private List<GameHandler> threads;
	
	public Server(int portArg) {
		port = portArg;
		threads = new ArrayList<>();
	}
	
	public void run() {
		try {
			ServerSocket ssock = new ServerSocket(port);
			while(true) {
				GameHandler game = new GameHandler(this);
				while(!game.isStarted()) {
					print("Waiting for new Client.");
					Socket sock = ssock.accept();
					print("Received new connection: " + sock.getPort());
					ClientHandler clientHandler = new ClientHandler(game, sock);
					game.addClientHandler(clientHandler);
					clientHandler.start();
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void print(String message) {
		System.out.println(message);
	}
}
