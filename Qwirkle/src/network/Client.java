package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import model.*;

public class Client extends Thread {

	private static final String USAGE = "usage java network.Client <address> <port>";
	
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println(USAGE);
			System.exit(0);
		}
		
		InetAddress host = null;
		int port = 0;
		
		try {
			host = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			print("ERROR: no valid hostname!");
			System.exit(0);
		}
		
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			print("ERROR: not a valid portnumber!");
			System.exit(0);
		}
		
		try {
			Client client = new Client(host, port);
			client.start();
		} catch (IOException e) {
			print("ERROR: couldn't construct a client object!");
		}
			
	}
		
	public static void print(String msg) {
		System.out.println(msg);
	}
	
	// ----------------------------
	
	private String clientName;
	private Socket sock;
	private Player player;
	private BufferedReader in;
	private BufferedWriter out;
	private BufferedReader playerInput;
	
	public Client(InetAddress host, int port) throws IOException {
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		playerInput = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void run() {
		System.out.println("What name would you like to use?");
		try {
			String line;
			while ((line = playerInput.readLine()) != null) {
				out.write("HELLO " + line);
				out.newLine();
				out.flush();
			}
			boolean waiting = true;
			while (waiting) {
				line = in.readLine();
				if (line.startsWith("WELCOME")) {
					Scanner scanner = new Scanner(line);
					scanner.next();
					clientName = scanner.next();
					int playerNumber = scanner.nextInt();
					player = new HumanPlayer(clientName, playerNumber);
					waiting = false;
				}
			}
			readCommands();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void readCommands() {
		boolean playing = true;
		while (playing) {
			String line;
			try {
				line = in.readLine();
				if (line.startsWith("NAMES")) {
					// print all names
				} else if (line.startsWith("NEXT")) {
					// make a move if the given number is your own
				} else if (line.startsWith("NEW")) {
					// add the given tile(s) to your hand
				} else if (line.startsWith("TURN")) {
					// make the given move on your own board
				} else if (line.startsWith("KICK")) {
					// a player is removed
				} else if (line.startsWith("WINNER")) {
					// display some info
					playing = false;
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
}
