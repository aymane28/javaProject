package musichub.net;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * La classe Client, c'est une classe qui implemente l'interface IClient
 * @author Mohammed
 */
public class Client implements IClient{

    private Socket clientSocket = null;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner sc = new Scanner(System.in);//pour lire à partir du clavier
    private int port;
    private String username;
    public static final String FILE_PATH = System.getProperty("user.dir") + "\\client-files\\";
    private int idClient;
    private String lastMessage;


    /**
     * Conctructeur de la classe Client
     * @param port C'est le port dont le serveur il écoute
     * @param username Le nom du client, C'est pas dans notre cas
     */
    public Client(final int port, final String username) {
      this.port = port;
      this.username = username;
    }


    /**
     * Etablir une connection TCP avec le serveur dont l'adresse est passer en parametre.
     * Creer deux Thread, sendThread pour se charger de l'envoie de ce qu'on tape
     * et receiveThread pour recevoir ce que le serveur il nous envoie
     * @param host L'adresse IP du Serveur
     * @return Une valeur true si la connection est bien etablie, false dans le cas contraire
     */
    @Override
    public boolean connect(final String host){
        try {
            /*
             * les informations du serveur ( port et adresse IP ou nom d'hote
             * 127.0.0.1 est l'adresse local de la machine )
             */

            clientSocket = new Socket( host, this.port );

            //flux pour envoyer
            out = new PrintWriter(clientSocket.getOutputStream());
            //flux pour recevoir
            in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread send = new Thread(new Runnable() {
                String message;
                @Override
                public void run() {
                    while(true){
                        message = sc.nextLine();//q
                        out.println(message);
                        out.flush();//vider le buffer
                        if(message.equals("q"))
                            break;
                    }
                }
            });
            send.start();

            Thread receive = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        String msg = in.readLine();
                        idClient = Integer.parseInt( msg.substring(msg.indexOf('>')+2) );
                        while(msg!=null && !msg.equals("Good bye...")){
                            System.out.println(msg);
                            if(msg.equals("<<START:SAIS>>")){ //SAIS: SENDING AUDIO INPUT STREAM
                                InputStream inputStream = clientSocket.getInputStream();
                                byte[] bytes = new byte[16*1024];
                                File f = new File(FILE_PATH+idClient+".wav");
                                if(!f.exists()) f.createNewFile();
                                try (FileOutputStream fOut = new FileOutputStream(f)) {
                                    int count;
                                    while ((count = inputStream.read(bytes)) > 0) {
                                        fOut.write(bytes, 0, count);
                                    }
                                    fOut.flush();
                                }
                                /**
                                Path path = new File(FILE_PATH+idClient+".wav").toPath();
                                InputStream inputStream = clientSocket.getInputStream();
                                byte[] bytes = new byte[16*1024];
                                int l;
                                while( ( l = inputStream.read(bytes)) > 0){
                                    System.out.println("length: "+l);
                                    if(bytes.length == 0)
                                        break;
                                    Files.write(path, bytes, StandardOpenOption.APPEND);
                                }
                                 */
                                /**
                                Files.copy(clientSocket.getInputStream(), new File(FILE_PATH+idClient+".wav").toPath(),
                                        StandardCopyOption.REPLACE_EXISTING);

                                 */
                                System.out.println("fin");
                            }
                            msg = in.readLine();
                            lastMessage = msg;
                            up();
                        }
                        System.out.println("From Server: "+msg);
                        shutDown();
                        /**out.close();
                        clientSocket.close();*/
                    } catch (IOException  ex) {
                        ex.printStackTrace();
                    }
                }
            });
            receive.start();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void up(){
        System.out.println(lastMessage);
    }


    /**
     * Deconncter le client du serveur
     * Liberer toutes les ressources, Scanner, OutputStream, InputStream et fermer la socket
     * Aprer on arrete l'application
     */
    @Override
    public void shutDown() {
        try{
            this.sc.close();
            System.out.println("Scanner closed...");

            //this.in.close();
            this.clientSocket.shutdownInput();
            System.out.println("Input closed...");

            //this.out.close();
            this.clientSocket.shutdownOutput();
            System.out.println("Output closed...");

            this.clientSocket.close();
            System.out.println("Connection closed...");
            System.exit(0);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Getter, qui renvoie le nom d'utilisateur
     * @return le nom du client connecter
     */
    public String getUsername() {
        return username;
    }

    /**
     * Envoyer un message vers le serveur
     * NB: le client doit être connecter avant d'apeller cette methode
     * @param msg Le message a envoyer vers le serveur
     * @return Retourn la taille du message s'il est bien envoyé, -1 s'il ya un probleme (Exp: Client deconnecté)
     */
    @Override
    public long sent(final String msg){
        if( this.out != null){
            this.out.println(msg);
            return msg.length();
        }
        return -1;
    }

    /**
     * Recuperer le dernier message recu par le client
     * @return Le message recu par le client, si non renvoie null
     */
    @Override
    public String receive(){
        return lastMessage;
    }

    /**
    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(final String msg) {
        this.out.println(msg);
        this.out.flush();
    }
    */
}
