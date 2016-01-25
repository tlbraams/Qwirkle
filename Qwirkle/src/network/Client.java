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
import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.InvalidMoveException;
import exceptions.InvalidNameException;
import model.*;
import view.TUI;


@SuppressWarnings("resource")
public class Client extends Thread {

	public static void main(String[] args) {
		
		try {
			Client client = new Client();
			client.start();
		} catch (IOException e) {
			print("ERROR: Could not construct a Client object.");
		}
			
	}
		
	public static void print(String msg) {
		System.out.println(msg);
	}
	
	// ----- Instance Variables -----
	private String clientName;
	private Boolean firstTurn;
	private Socket sock;
	private Player player;
	private Board board;
	private TUI view;
	private ArrayList<String> players;
	private int tilesInStack;
	private BufferedReader in;
	private BufferedWriter out;
	private BufferedReader playerInput;
	private InetAddress host;
	private int port;
	
	// ----- Constructor -----
	/**
	 * Creates a new Client with a connection to the given parameters.
	 * @param host the adress of the server
	 * @param port the port of the server
	 */
	public Client() throws IOException {
		playerInput = new BufferedReader(new InputStreamReader(System.in));
		firstTurn = true;
	}
	
	/**
	 * Finds the name, port number and IP address and reads commands from the socket.
	 */
	public void run() {
		host = requestIP();
		port = requestPort();
		try { 
			sock = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) { 
			System.out.println(e.getMessage()); 
		} 
		
		findName();
		readCommands();
	}
	
	/**
	 * Requests the user to enter a port number. 
	 */
	public int requestPort() {
		int result = 0;
		System.out.println("Please enter a port number.");
		Boolean running = true;
		String line;
		while (running) {
			try {
				line = playerInput.readLine();
				result = Integer.parseInt(line);
				running = false;
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid port number.");
			}
		}
		return result;
	}
	
	/**
	 * Requests the user to enter an IP address.
	 */
	public InetAddress requestIP() {
		InetAddress result = null;
		System.out.println("Please enter an IP address.");
		Boolean running = true;
		String line;
		while (running) {
			try {
				line = playerInput.readLine();
				result = InetAddress.getByName(line);
				running = false;
			} catch (UnknownHostException e) {
				System.out.println("The given IP address is unknown. Please enter a new one.");
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} 
		}
		return result;
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
				if (line == null) {
					shutDown();
				} else if (line.startsWith("NAMES")) {
					startGame(line);
				} else if (line.startsWith("NEXT")) {
					view.update();
					lineScan.next();
					int playerID = lineScan.nextInt();
					if (this.player.getID() == playerID) {
						if (firstTurn) {
							findFirstMove();
						} else {
							findMove();
						}
					}
				} else if (line.startsWith("NEW")) {
					receiveTiles(line);
				} else if (line.startsWith("TURN")) {
					firstTurn = false;
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
		System.out.println("What is your name"
						+ " (can only contain letters with maximum length of 16)?");
		Boolean running = true;
		String line;
		while (running) {
			try {
				line = playerInput.readLine();
				isRightLength(line);
				hasOnlyLetters(line);
				
				out.write("HELLO " + line);
				out.newLine();
				out.flush();
				running = waitForConfirmation();
			} catch (InvalidNameException e) {
				e.getInfo();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Tests if a given name is of the right length.
	 * @param name
	 * @throws InvalidNameException
	 */
	/*
	 *@ ensures 	0 < name.length && name.length < 17;
	 */
	public void isRightLength(/*@ NonNull */String name) throws InvalidNameException {
		if (name.length() > 16) {
			throw new InvalidNameException("Your name must have a maximum length of 16."
							+ " Please enter a new name.");
		} else if (name.length() < 1) {
			throw new InvalidNameException("Your name is too short. Please enter a new name.");	
		}
	}
	
	/**
	 * Tests if a given name only contains letters. 
	 * @param name
	 * @throws InvalidNameException
	 */
	public void hasOnlyLetters(/*@ NonNull */String name) throws InvalidNameException {
		char[] characters = name.toCharArray();
		if (name.contains(" ")) {
			throw new InvalidNameException("Your name cannot contain a space."
					+ " Please enter a new name.");
		} 
		for (char character: characters) {
			int ascii = (int) character;
			if (!((64 < ascii && ascii < 91) || (96 < ascii && ascii < 123))) {
				throw new InvalidNameException("Your name contains characters other than letters."
						+ " Please enter a new name.");
			} 
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
				displayPlayerMenu(clientName, playerNumber);
				waiting = false;
				scanner.close();
			}
		}
		return waiting;
		
	}
	
	/**
	 * Asks the user if he wants to play himself or let the computer play. 
	 * It constructs an appropriate Player object (ComputerPlayer or HumanPlayer). 
	 */
	public void displayPlayerMenu(/*@ NonNull */String nameOfClient,
				/*@ NonNull */ int playerNumber) {
		System.out.println("What kind of player would you like to register?");
		System.out.println("Human player ...... ............. 1");
		System.out.println("Computer player ................. 2");
		Boolean running = true;
		Scanner line = new Scanner(System.in);
		while (running) {
			String kindOfPlayer = line.nextLine();
			if (kindOfPlayer.equals("1")) {
				player = new HumanPlayer(nameOfClient, playerNumber);
				running = false;
				System.out.println(nameOfClient + " was added to the game.");
			}
			if (kindOfPlayer.equals("2")) {
				int aiTimeToThink = requestAITimeToThink();
				player = new  ComputerPlayer(nameOfClient, playerNumber, "Random", aiTimeToThink);
				running = false;
				System.out.println("'" + nameOfClient + "' was added to the game.");
			}
		}	
	}
	
	/**
	 * Requests the user how many miliseconds he wants the ComputerPlayer to think. 
	 * If the user input is not of type int, then the AITimeToThink is set at 1000 miliseconds. 
	 */
	public /*@ NonNull */int requestAITimeToThink() {
		int result = 1000;
		System.out.println("What is the maximum amount of time (in miliseconds)"
						+ " that you allow the ComputerPlayer to think?");
		Boolean running = true;
		Scanner line = new Scanner(System.in);
		while (running) {
			try {
				result = line.nextInt();
				running = false;
			} catch (InputMismatchException e) {
				System.out.println("You did not type an integer. The maximum time that the AI"
								+ " is allowed to think is now set at 1 second.");
				running = false;
			}
		}
		return result;
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
			removeFromStack(6);
			if (scanLine.hasNextInt()) {
				notAI = false;
				if (player instanceof ComputerPlayer) {
					((ComputerPlayer) player).setAITime(scanLine.nextInt());
				}
			}	
		}
		view = new TUI(board, players.size());
		scanLine.close();
	}
	

	/**
	 * Asks the player associated with this Client to make a move.
	 * If the move is valid it is given to translateMove to send over the socket.
	 */
	public void findMove() {
		Move[] move = player.determineMove(board);
		boolean valid = false;
		while (!valid) {
			try {
				valid = board.validMove(move, player);
				for (int i = 0; i < move.length; i++) {
					player.remove(move[i].getPiece());
					print(i + " " + move[i].getPiece().toString());
				}
			} catch (InvalidMoveException e) {
				print(e.getInfo());
				move = player.determineMove(board);
			}
		}
		translateMove(move);
	}
	
	/**
	 * Asks the player associated with this Client to make the first move.
	 * If the move is valid it is given to translateMove to send over the socket.
	 */
	public void findFirstMove() {
		Move[] move = player.determineFirstMove(board);
		boolean valid = false;
		while (!valid) {
			try {
				valid = board.validMove(move, player);
				for (int i = 0; i < move.length; i++) {
					player.remove(move[i].getPiece());
					print(i + " " + move[i].getPiece().toString());
				}
			} catch (InvalidMoveException e) {
				print(e.getInfo());
				move = player.determineFirstMove(board);
			}
		}
		translateMove(move);
	}
	
	/**
	 * Transslate the given move according to the Protocol to send if out using sendCommand().
	 * @param move the move to translate
	 */
	public void translateMove(Move[] move) {
		String result = "";
		if (move[0] instanceof Place) {
			result = "MOVE";
			for (int i = 0; i < move.length; i++) {
				result += move[i].toString();
			}
		} else if (move[0] instanceof Trade) {
			result = "TRADE";
			if (move[0].getPiece() == null) {
				result += " empty";
			} else {
				for (int i = 0; i < move.length; i++) {
					result += move[i].toString();
				}
			}
		}
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
					removeFromStack(1);
				}
				board.setPiece(row, column, piece);
				places.add(new Place(piece, row, column));
			}
			scanLine.close();
			int score = board.getScore(places.toArray(new Move[places.size()]));
			board.addScore(playerID, score);
		}
		print(tilesInStack + " " + board.getStack().size());
	}
	
	/**
	 * Handles a kick message.
	 */
	public void handleKick(String line) {
		Scanner scanLine = new Scanner(line);
		scanLine.next();
		int playerID = scanLine.nextInt();
		int tiles = scanLine.nextInt();
		addToStack(tiles);
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
	
	/**
	 * Decreases the count of the pieces in the stack by the given amount.
	 * @param toRemove the amount of pieces to remove
	 */
	private void removeFromStack(int toRemove) {
		tilesInStack = tilesInStack - toRemove;
		for (int i = 0; i < toRemove; i++) {
			board.draw();
		}
		
	}
	
	/**
	 * Increases the count of the pieces in the stack by the given amount.
	 */
	private void addToStack(int toAdd) {
		tilesInStack += toAdd;
		for (int i = 0; i < toAdd; i++) {
			board.getStack().add(new Piece(Piece.Color.DEFAULT, Piece.Shape.BLOCKED));
		}
	}
	
	/**
	 * ShutsDown the Client.
	 */
	public void shutDown() {
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		
	}
}

	