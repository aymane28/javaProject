package musichub.net;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Server, c'est une classe Singleton qui implemente l'inetrface IServer
 */
public class Server extends Thread implements IServer
{
	private ServerSocket serverSocket;
	int nbClients = 0;
	private static List<Socket> conversations;


	private static Server server;

	/**
	 * Renvoie une instance de Type Server
	 * @return Retourn une instance static de Type Server s'il existe, si non on creer et on renvoie une nouvelle insatnce
	 */
	public static Server getInstance(){
		if( server == null ){
			server = new Server();
		}
		return server;
	}

	/**
	 * Constructeur de la classe Server sans parametrs
	 */
	private Server(){ conversations = new ArrayList<>(); }

	@Override
	public void run() {
		try {
			System.out.println("Server start on port [ 6666 ]...");

			serverSocket = new ServerSocket(6666);
			while(true) {
				Socket s = serverSocket.accept();
				nbClients++;
				Conversation conversation = new Conversation(s,nbClients);
				conversation.start();
				conversations.add(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Defuser un message aupres de toutes les clients,
	 * @param msg Message a defuser
	 * @throws IOException Throw une exception si l'envoie d'un message vers un client et echoue
	 */
	public void broadcast(final String msg) throws IOException {
		System.out.println(conversations.size());
		Iterator<Socket> it = conversations.iterator();
		while( it.hasNext() ){
			new PrintWriter(it.next().getOutputStream()).println(msg);
		}
	}

	/**
	 * Lancer un Thread qui va s'ecouter sur un port et creer une Conversation pour chaque Connection
	 * @return Retourn False si le servuer est déja en ecoute, Si non on lance le serveur pour qu'il ecoute et apres en renvoie True
	 */
	public boolean connect() {
		try{
			if(serverSocket == null) {
				this.start();
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Envoyer l'etat du serveur
	 * @return True s'il est déja connecté, False si non
	 */
	@Override
	public boolean isConnected() {
		return serverSocket != null;
	}

	/**
	 * Retourne le nombre des client connecté au serveur
	 * @return Renvoie un entier superieur a 0 s'il y a des client deja connecte, 0 si non
	 */
	public int getSize(){
		return conversations.size();
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

}