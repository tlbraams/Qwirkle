package network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread{
	
	private Socket sock;
	private String name;
	private GameHandler game;
	private BufferedReader in;
	private BufferedWriter out;
	

	public ClientHandler(GameHandler game, Socket sock) throws IOException {
		this.game = game;
		this.sock = sock;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		setName();
	}
	
	public void run() {
		
	}
	
	public void setName() throws IOException{
		boolean wait = true;
		while(wait) {
			String line = in.readLine();
			if(line != null && line.startsWith("HELLO ")) {
				Scanner scanLine = new Scanner(line);
				scanLine.next();
				name = scanLine.next();
				int number = game.validName(name);
				if(number != -1) {
					sendCommand("WELCOME " + name + " " + number);
				} else {
					this.shutDown();
				}
			}
		}
	}
	
	private void shutDown() {
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
	
	
}
