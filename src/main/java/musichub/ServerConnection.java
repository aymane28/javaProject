package musichub;

import musichub.net.Server;

import java.io.IOException;
import java.util.Scanner;

public class ServerConnection
{
	public static void main (String[] args) throws IOException {
		Server server = Server.getInstance();
		server.connect();
		Scanner sc = new Scanner(System.in);
		String s = sc.nextLine();
		server.broadcast(s);
	}
}