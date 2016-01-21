package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import exceptions.InvalidMoveException;
import model.*;
import view.TUI;

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
	
	// Instance variables
	private String clientName;
	private Socket sock;
	private Player player;
	private Board board;
	private TUI view;
	private ArrayList<String> players;
	private int tilesInStack;
	private BufferedReader in;
	private BufferedWriter out;
	private BufferedReader playerInput;
	
	
	/**
	 * Creates a new Client with a connection to the given parameters.
	 * @param host the adress of the server
	 * @param port the port of the server
	 */
	public Client(InetAddress host, int port) throws IOException {
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		playerInput = new BufferedReader(new InputStreamReader(System.in));
	}
	
	/**
	 * Finds the name and reads commands from the socket.
	 */
	public void run() {
		findName();
		readCommands();
	}
	
	/**
	 * Tries read commands from the socket input.
	 * Onces it has a line, it tries to match it to one of the Protocol
	 * keywords and calls the corresponding method.
	 */
	public void readCommands() {
		boolean playing = true;
		print("Waiting for game to start.");
		while (playing) {
			String line;
			try {
				line = in.readLine();
				Scanner lineScan = new Scanner(line);
				if (line.startsWith("NAMES")) {
					startGame(line);
				} else if (line.startsWith("NEXT")) {
					view.update();
					lineScan.next();
					int playerID = lineScan.nextInt();
					if (this.player.getID() == playerID) {
						findMove();
					}
				} else if (line.startsWith("NEW")) {
					receiveTiles(line);
				} else if (line.startsWith("TURN")) {
					makeMove(line);
				} else if (line.startsWith("KICK")) {
					lineScan.next();
					handleKick(line);
				} else if (line.startsWith("WINNER")) {
					handleEndGame(line);
					view.printScore(board);
					playing = false;
				} else {
					print(line);
				}
				lineScan.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Writess the given string to the output of the socket.
	 * @param message
	 */
	public void sendCommand(String message) {
		try {
			out.write(message);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Asks the user for a name. Once it has been send to the server,
	 * it waits for an acknowledgement of the name.
	 */
	public void findName() {
		System.out.println("What name would you like to use?");
		try {
			String line;
			boolean waiting = true;
			while (waiting) {
				line = playerInput.readLine();
				out.write("HELLO " + line);
				out.newLine();
				out.flush();
				waiting = waitForConfirmation();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Waits for the reply from the server to the name message.
	 * @return false to acknowledge it has been confirmed and calling method no longer has to wait.
	 * @throws IOException
	 */
	public boolean waitForConfirmation() throws IOException {
		boolean waiting = true;
		String line;
		while (waiting) {
			line = in.readLine();
			print(line);
			if (line.startsWith("WELCOME")) {
				Scanner scanner = new Scanner(line);
				scanner.next();
				clientName = scanner.next();
				int playerNumber = scanner.nextInt();
				player = new HumanPlayer(clientName, playerNumber);
				waiting = false;
				scanner.close();
			}
		}
		return waiting;
		
	}
	
	/**
	 * Prints the given name and starts the local board and view.
	 */
	public void startGame(String line) {
		System.out.println(line);
		board = new Board();
		
		players = new ArrayList<>();
		tilesInStack = 108;
		Scanner scanLine = new Scanner(line);
		scanLine.next();
		boolean notAI = true;
		while (notAI) {
			String name = scanLine.next();
			int playerID = scanLine.nextInt();
			players.add(playerID, name);
			tilesInStack = tilesInStack - 6;
			if (scanLine.hasNextInt()) {
				notAI = false;
			}
		}
		view = new TUI(board, players.size());
		scanLine.close();
	}
	
	/**
	 * Asks the player associated with this Client to make a move.
	 * Once it has this move it translates it to a String.
	 * This String is made according to the protocol and given to sendCommand().
	 */
	public void findMove() {
		Move[] move = player.determineMove(board);
		boolean valid = true;
		while (!valid) {
			try {
				valid = board.validMove(move, player);
				for (int i = 0; i < move.length; i++) {
					player.remove(move[i].getPiece());
				}
			} catch (InvalidMoveException e) {
				print(e.getInfo());
				move = player.determineMove(board);
			}
		}
		String result = "";
		if (move[0] instanceof Place) {
			result = "MOVE";
			for (int i = 0; i < move.length; i++) {
				result += move[i].toString();
			}
		} else if (move[0] instanceof Trade) {
			result = "TRADE";
			for (int i = 0; i < move.length; i++) {
				result += move[i].toString();
			}
		}
		print(result);
		sendCommand(result);
	}
	
	/**
	 * Gives the player pieces according to the given String.
	 * @param line
	 */
	public void receiveTiles(String line) {
		print(line);
		Scanner scanLine = new Scanner(line);
		scanLine.next();
		while (scanLine.hasNext()) {
			String chars = scanLine.next();
			if (!chars.equals("empty")) {
				char color = chars.charAt(0);
				char shape = chars.charAt(1);
				player.receive(new Piece(Piece.charToColor(color),
								Piece.chatToShape(shape)));
			}
		}
		scanLine.close();
	}
	
	/**
	 * Translates the given String to moves and places them on the local board.
	 * @param line the given String
	 */
	public void makeMove(String line) {
		print(line);
		Scanner scanLine = new Scanner(line);
		scanLine.next();
		String firstElement = scanLine.next();
		if (!firstElement.equals("empty")) {
			int playerID = Integer.parseInt(firstElement);
			ArrayList<Place> places = new ArrayList<>();
			while (scanLine.hasNext()) {
				String pieceString = scanLine.next();
				int row = scanLine.nextInt();
				int column = scanLine.nextInt();
				Piece piece = new Piece(Piece.charToColor(pieceString.charAt(0)),
								Piece.chatToShape(pieceString.charAt(1)));
				if (tilesInStack != 0) {
					tilesInStack--;
				}
				board.setPiece(row, column, piece);
				places.add(new Place(piece, row, column));
			}
			scanLine.close();
			int score = board.getScore(places.toArray(new Move[places.size()]));
			board.addScore(playerID, score);
		}
	}
	
	/**
	 * Handles a kick message.
	 */
	public void handleKick(String line) {
		Scanner scanLine = new Scanner(line);
		scanLine.next();
		int playerID = scanLine.nextInt();
		int tiles = scanLine.nextInt();
		tilesInStack += tiles;
		String reason = scanLine.nextLine();
		print(players.get(playerID) + " was kicked.");
		print(reason);
		scanLine.close();
	}
	
	/**
	 * Prints who has won the game and the score of all the players.
	 */
	public void handleEndGame(String line) {
		Scanner scanLine = new Scanner(line);
		scanLine.next();
		print(players.get(scanLine.nextInt()) + " heeft gewonnen.");
		print("Scores:");
		for (int i = 0; i < players.size(); i++) {
			print(players.get(i) + ": " + board.getScore(i));
		}
		scanLine.close();
	}	
}
