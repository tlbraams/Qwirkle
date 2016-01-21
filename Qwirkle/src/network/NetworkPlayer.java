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

import model.*;

@SuppressWarnings("resource")
public class NetworkPlayer implements Player, Runnable {
	public static final int MAX_HAND = 6;
	/**
	 * The NetworkPlayer class implements the Player interface.
	 * It stores the local data needed for a player connected to the server
	 * and a member of the game this client is playing.
	 */
	
	private HashSet<Piece> hand;
	private Server server;
	private Socket sock;
	private String name;
	private int id;
	private BufferedReader in;
	private BufferedWriter out;
	

	public NetworkPlayer(Server server, Socket sock) throws IOException {
		this.server = server;
		this.sock = sock;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		hand = new HashSet<>(MAX_HAND);
	}
	
	public void run() {
		try {
			setName();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Reads a name and sets it if valid. 
	 * @throws IOException
	 */
	public void setName() throws IOException {
		String line = in.readLine();
		System.out.println(line);
		if (line != null && line.startsWith("HELLO ")) {
			Scanner scanLine = new Scanner(line);
			scanLine.next();
			name = scanLine.next();
			id = server.validName(name);
			if (id != -1) {
				System.out.println("WELCOME " + name + " " + id);
				sendCommand("WELCOME " + name + " " + id);
				server.setReady(this);
			} else {
				sendCommand("INVALID");
				this.shutDown();
			}
		}
	}
	
	public void shutDown() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendCommand(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return id;
	}

	public void receive(Piece piece) {
		hand.add(piece);
	}

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
					int row = scanLine.nextInt();
					int column = scanLine.nextInt();
					Piece piece = null;
					boolean found = false;
					for (Piece p: hand) {
						if (p.toString().equals(pieceName) && !found) {
							piece = p;
							found = true;
						}
					}
					if (found) {
						System.out.println(piece.toString());
						places.add(new Place(piece, row, column));
					} else {
						sendCommand("Error: " + pieceName + " is not a piece in your hand.");
					}
					move = places.toArray(new Move[places.size()]);
				}
			} else if (line.startsWith("SWAP")) {
				scanLine.next();
				ArrayList<Trade> trades = new ArrayList<>();
				while (scanLine.hasNext()) {
					String pieceName = scanLine.next();
					Piece piece = null;
					boolean found = false;
					for (Piece p: hand) {
						if (p.toString().equals(pieceName) && !found) {
							piece = p;
							found = true;
						}
					}
					if (found) {
						trades.add(new Trade(piece));
						
					} else {
						sendCommand("Error: " + pieceName + " is not a piece in your hand.");
					}
				}
				move = trades.toArray(new Move[trades.size()]);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return move;
		
	}
	
	public Move[] determineFirstMove(Board board) {
		return null;
	}
	

	public HashSet<Piece> getHand() {
		return hand;
	}
	
	public void remove(Piece piece) {
		hand.remove(piece);
	}
}
