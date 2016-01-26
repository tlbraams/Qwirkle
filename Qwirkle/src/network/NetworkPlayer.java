package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import exceptions.InvalidMoveException;
import exceptions.InvalidNameException;
import model.*;

@SuppressWarnings("resource")
public class NetworkPlayer implements Player, Runnable {
	public static final int MAX_HAND = 6;
	/**
	 * The NetworkPlayer class implements the Player interface.
	 * It stores the local data needed for a player connected to the server
	 * and a member of the game this client is playing.
	 */
	// ---- Instance variables: ----
	private HashSet<Piece> hand;
	private Server server;
	private Socket sock;
	private String name;
	private int id;
	private BufferedReader in;
	private BufferedWriter out;
	
	// ---- Constructor: ----
	/**
	 * Creates a new NetworkPlayer with the given Server on the given socket.
	 * Starts a Reader and Writer on the in/output of the socket.
	 * @param server the given server
	 * @param sock the given socket
	 * @throws IOException
	 */
	public NetworkPlayer(Server server, Socket sock) throws IOException {
		this.server = server;
		this.sock = sock;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		hand = new HashSet<>(MAX_HAND);
	}
	
	// ---- Queries: ----
	/**
	 * Returns the name of this NetworkPlayer.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the ID of this NetworkPlayer.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the hand of this NetworkPlayer.
	 */
	public HashSet<Piece> getHand() {
		return hand;
	}	
	
	// ---- Commands: ----
	/**
	 * Starts the setName command to find the name the player will use.
	 */
	public void run() {
		try {
			setName();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Reads a name from the socket and sets it if valid.
	 * Sends an acknowledgement to the socket. 
	 * @throws IOException
	 */
	public void setName() throws IOException {
		String line = in.readLine();
		System.out.println(line);
		if (line != null && line.startsWith("HELLO ")) {
			Scanner scanLine = new Scanner(line);
			scanLine.next();
			name = scanLine.next();
			try {
				id = server.validName(name);
				System.out.println("WELCOME " + name + " " + id);
				sendCommand("WELCOME " + name + " " + id);
				server.setReady(this);
			} catch (InvalidNameException e) {
				sendCommand("INVALID");
				this.shutDown();
			}
		}
	}
	
	/**
	 * Closes the socket.
	 */
	public void shutDown() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a given String to the output of the socket.
	 * @param msg the given String
	 */
	public void sendCommand(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Adds the given Piece to the hand of this player.
	 * @param piece the given Piece.
	 */
	public void receive(Piece piece) {
		hand.add(piece);
	}

	/**
	 * Asks the Player what kind of move he would like to make.
	 * If they chose a place they are asked what piece's they would like to place
	 * and where on the board.
	 * if they chose a trade they are asked what piece's they would like to trade.
	 * "Done" entered instead of a Piece marks the end of the user input.
	 */
	public Move[] determineMove(Board board) {
		String line;
		Move[] move = null;
		try {
			line = in.readLine();
			System.out.println(line);
			Scanner scanLine = new Scanner(line);
			if (line.startsWith("MOVE")) {
				scanLine.next();
				ArrayList<Place> places = new ArrayList<>();
				while (scanLine.hasNext()) {
					String pieceName = scanLine.next();
					if (pieceName.equals("empty")) {
						places.add(new Place(null, 91, 91));
					} else {
						int row = scanLine.nextInt();
						int column = scanLine.nextInt();
						Piece piece = null;
						try {
							piece = findPiece(pieceName);
						} catch (InvalidMoveException e) {
							System.out.println(e.getInfo());
						}
						if (piece != null) {
							places.add(new Place(piece, row, column));
						} else {
							sendCommand("Error: " + pieceName + " is not a piece in your hand.");
						}
						move = places.toArray(new Move[places.size()]);
					}
				}
			} else if (line.startsWith("SWAP")) {
				scanLine.next();
				ArrayList<Trade> trades = new ArrayList<>();
				while (scanLine.hasNext()) {
					String pieceName = scanLine.next();
					Piece piece = null;
					try {
						piece = findPiece(pieceName);
						trades.add(new Trade(piece));
					} catch (InvalidMoveException e) {
						System.out.println(e.getInfo());
					}
				}
				move = trades.toArray(new Move[trades.size()]);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return move;
		
	}
	
	/**
	 * Finds and returns the Piece in the NetworkPlayer's hand that is the same as 
	 * the pieceName. 
	 * @param pieceName the name of the Piece that is needed.
	 * @return the Piece object that has the same name as pieceName. 
	 */
	public /*@ NonNull */Piece findPiece(/*@ NonNull */String pieceName)
				throws InvalidMoveException {
		Piece result = null;
		boolean found = false;
		for (Piece p: hand) {
			if (p.toString().equals(pieceName) && !found) {
				result = p;
				found = true;
			}
		}
		if (!found) {
			throw new InvalidMoveException("The tile is not in your hand.");
		}
		return result;
	}

	/**
	 * Method from interface, not used.
	 */
	public Move[] determineFirstMove(Board board) {
		return null;
	}
	
	/**
	 * Removes the given Piece from the hand of this NetworkPlayer.
	 * @param piece the given Psiece to remove
	 */
	public void remove(Piece piece) {
		hand.remove(piece);
	}
}
