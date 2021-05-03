package musichub.net;

import musichub.business.*;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;

/**
 * La classe Conversation, c'est la classe qui va guerer la communication entre le serveur est chaque client
 * La classe herite de la classe Thread, et implemente l'interface Observer
 */
public class Conversation extends Thread implements Observer{

    private static String MENU;
    public static final String FILE_PATH = System.getProperty("user.dir") + "\\files\\test.wav";
    private Socket socket;
    private int numClient;
    private MusicHub theHub;
    private boolean fin = false;
    private String albumTitle = null;
    private String request ;

    private BufferedReader br;
    private PrintWriter pw;


    /**
     * Constructeur de la classe Converstation
     * @param socket C'est la socket creer par le serveur pour un client connecter
     * @param numClient Le numero du client associe a cette socket
     */
    public Conversation(Socket socket, int numClient) {
        super();
        this.socket = socket;
        this.numClient = numClient;
        theHub = new MusicHub();
        MusicHub.subscribe( this ); //abonnée
        prepareMenu();
    }

    /**
     * Envoyer un message vers le client
     * @param msg le message a envoye vers le client
     * @return Retourn la taille du message s'il est bien envoyé, -1 s'il ya un probleme.
     */
    public long sent(final String msg){
        if( this.pw != null){
            this.pw.println(msg);
            return msg.length();
        }
        return -1;
    }

    /**
     * Recuperer le dernier message recu par la converstaion
     * @return Le message recu par la Conversation, si non renvoie null
     */
    public String receive(){
        if(this.br != null){
            return request;
        }
        return null;
    }

    @Override
    public void run() {
        try {
            prepareMenu();

            //Receive
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            //Send
            OutputStream os = socket.getOutputStream();
            pw = new PrintWriter(os,true);

            String IP = socket.getRemoteSocketAddress().toString();

            System.out.println("Client numero "+numClient+" IP="+IP);
            pw.println("Bienvenue, Vous etes le client numero > "+numClient);// Bienvenue, Vous etes le client numero > 12
            pw.println("Type h for available commands");


            //while(true) {

                while((request=br.readLine())!=null) {
                    System.out.println(IP+" a envoyé : "+request);
                    /**/
                    //String rep = "Size="+request.length();
                    //pw.println(rep);
                    exec(request);
                }
            //}
            /*
            * soc close
            * in / out
            *
            * */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
main(){

}
* A() throws Exception{
*   B();
* }
*
* B() throws Exception {
*   C();
* }
*
* C() throws Exception, IOException {
*   int i=1/0;
* }
*
* */
    /*
     class CommandLine{
        exec();
     }
     public class AlbumTitlesOrderByDate() implements CommandLine{
        exec(theHub, pw){
            pw.println(theHub.getAlbumsTitlesSortedByDate());
            printAvailableCommands();
        }
     }

     */

    /**
     * Notifier le client s'il ya un changement au niveau des fichier coté serveur
     * Exp: si un client a ajouter un nouvelle chanson, Toutes seront notifier
     * @param message le message recu par le MusicHub
     */
    @Override
    public void update(final String message){
        //Notification: La liste des chnason a ete modifier
        //Nouveau element: {nom de la chanson}
        this.pw.println( message );
    }

    /**
     * Exec, sert a traiter le demande recu par le client.
     * @param choice C'est le commande envoyer par le client (q, a , j...)
     */
    private void exec(String choice){


        if (choice.length() == 0) {
            //System.exit(0);
            fin = true;
            return;
        }

            switch (choice.charAt(0)) 	{

                case 'q':
                    try {
                        pw.println("Good bye...");
                        System.out.println("Client numéro "+this.numClient+" est déconnecté!");
                        this.socket.shutdownInput();
                        this.socket.shutdownOutput();
                        this.socket.close();
                        MusicHub.unsubscribe(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 'h':
                    printAvailableCommands();
                    break;
                case 't':
                    //album titles, ordered by date

                    pw.println(theHub.getAlbumsTitlesSortedByDate());
                    printAvailableCommands();
                    break;
                case 'g':
                    //songs of an album, sorted by genre
                    pw.println("Songs of an album sorted by genre will be displayed; available albums are:");
                    pw.println(theHub.getAlbumsTitlesSortedByDate());
                    try {
                        pw.println("Enter album title: ");
                        albumTitle = br.readLine();
                        pw.println(theHub.getAlbumSongsSortedByGenre(albumTitle));
                    } catch (NoAlbumFoundException ex) {
                        pw.println("No album found with the requested title.\n");
                    }catch(Exception e){
                        pw.println("ERROR> "+e.getMessage());
                    }
                    printAvailableCommands();
                    break;
                case 'd':
                    //songs of an album
                    pw.println("Songs of an album will be displayed; available albums are:");
                    pw.println(theHub.getAlbumsTitlesSortedByDate());
                    try {
                        pw.println("Enter album title: ");
                        albumTitle = br.readLine();
                        pw.println(theHub.getAlbumSongs(albumTitle));
                    } catch (NoAlbumFoundException ex) {
                        pw.println("No album found with the requested title.\n");
                    }catch(Exception e){
                        pw.println("ERROR> "+e.getMessage());
                    }
                    printAvailableCommands();
                    break;
                case 'u':
                    //audiobooks ordered by author
                    pw.println(theHub.getAudiobooksTitlesSortedByAuthor());
                    printAvailableCommands();
                     ;
                case 'c':
                    // add a new song
                    try{
                        pw.println("Enter a new song: ");
                        pw.println("Song title: ");
                        String title = br.readLine();
                        pw.println("Song genre (jazz, classic, hiphop, rock, pop, rap):");
                        String genre = br.readLine();
                        pw.println("Song artist: ");
                        String artist = br.readLine();
                        pw.println ("Song length in seconds: ");
                        int length = Integer.parseInt(br.readLine());
                        pw.println("Song content: ");
                        String content = br.readLine();
                        Song s = new Song (title, artist, length, content, genre);
                        theHub.addElement(s);
                        pw.println("New element list: ");
                        Iterator<AudioElement> it = theHub.elements();
                        while (it.hasNext()) pw.println(it.next().getTitle());
                        pw.println("Song created!");

                    }catch (Exception e){
                        pw.println("ERROR!"+e.getMessage());
                        e.printStackTrace();
                    }
                    printAvailableCommands();
                    break;
                case 'a':
                    // TODO Auto-generated catch block
                    /*
                    *  3   + Bug au niveau d'ajout d'un nouveau album {infi}
                    *  4   + Tester les autre fonctions {20min}
                    *  5   + Lire les fichier audio {3h}
                    *  6   + UML {1h30}
                    *  1.2 + (2) Design patterns {2h}
                    *  2   + Script Maven build {!}
                    *  1.1 + Les testes unitaires (2 classe au choix) {2h}
                    *
                    * */
                    try{
                        // add a new album
                        pw.println("Enter a new album: ");
                        pw.println("Album title: ");
                        String aTitle = br.readLine();
                        pw.println("Album artist: ");
                        String aArtist = br.readLine();
                        pw.println ("Album length in seconds: ");
                        int aLength = Integer.parseInt(br.readLine());
                        pw.println("Album date as YYYY-DD-MM: ");
                        String aDate = br.readLine();
                        Album a = new Album(aTitle, aArtist, aLength, aDate);
                        theHub.addAlbum(a);
                        pw.println("New list of albums: ");
                        Iterator<Album> ita = theHub.albums();
                        while (ita.hasNext()) pw.println(ita.next().getTitle());
                        pw.println("Album created!");

                    }catch (IOException e) {
                        pw.println("ERROR!"+e.getMessage());
                        e.printStackTrace();
                    }
                    printAvailableCommands();
                    break;
                case '+':
                    try{
                        //add a song to an album:
                        pw.println("Add an existing song to an existing album");
                        pw.println("Type the name of the song you wish to add. Available songs: ");
                        Iterator<AudioElement> itae = theHub.elements();
                        while (itae.hasNext()) {
                            AudioElement ae = itae.next();
                            if ( ae instanceof Song) pw.println(ae.getTitle());
                        }
                        pw.println("Enter song title: ");
                        String songTitle = br.readLine();

                        pw.println("Type the name of the album you wish to enrich. Available albums: ");
                        Iterator<Album> ait = theHub.albums();
                        while (ait.hasNext()) {
                            Album al = ait.next();
                            pw.println(al.getTitle());
                        }
                        pw.println("Enter album title: ");
                        String titleAlbum = br.readLine();
                        try {
                            theHub.addElementToAlbum(songTitle, titleAlbum);
                        } catch (NoAlbumFoundException ex){
                            pw.println (ex.getMessage());
                        } catch (NoElementFoundException ex){
                            pw.println (ex.getMessage());
                        }
                        pw.println("Song added to the album!");

                    }catch (IOException e) {
                        pw.println("ERROR! "+e.getMessage());
                        e.printStackTrace();
                    }
                    printAvailableCommands();
                    break;
                case 'l':
                    try{
                        // add a new audiobook
                        pw.println("Enter a new audiobook: ");
                        pw.println("AudioBook title: ");
                        String bTitle = br.readLine();
                        pw.println("AudioBook category (youth, novel, theater, documentary, speech)");
                        String bCategory = br.readLine();
                        pw.println("AudioBook artist: ");
                        String bArtist = br.readLine();
                        pw.println ("AudioBook length in seconds: ");
                        int bLength = Integer.parseInt(br.readLine());
                        pw.println("AudioBook content: ");
                        String bContent = br.readLine();
                        pw.println("AudioBook language (french, english, italian, spanish, german)");
                        String bLanguage = br.readLine();
                        AudioBook b = new AudioBook (bTitle, bArtist, bLength, bContent, bLanguage, bCategory);
                        theHub.addElement(b);
                        pw.println("Audiobook created! New element list: ");
                        Iterator<AudioElement> itl = theHub.elements();
                        while (itl.hasNext()) pw.println(itl.next().getTitle());

                    }catch (IOException e) {
                        pw.println("ERROR! "+e.getMessage());
                        e.printStackTrace();
                    }
                    printAvailableCommands();
                    break;

                case 'p':
                    try{
                        //create a new playlist from existing elements
                        pw.println("Add an existing song or audiobook to a new playlist");
                        pw.println("Existing playlists:");
                        Iterator<PlayList> itpl = theHub.playlists();
                        while (itpl.hasNext()) {
                            PlayList pl = itpl.next();
                            pw.println(pl.getTitle());
                        }
                        pw.println("Type the name of the playlist you wish to create:");
                        String playListTitle = br.readLine();
                        PlayList pl = new PlayList(playListTitle);
                        theHub.addPlaylist(pl);
                        pw.println("Available elements: ");

                        Iterator<AudioElement> itael = theHub.elements();
                        while (itael.hasNext()) {
                            AudioElement ae = itael.next();
                            pw.println(ae.getTitle());
                        }

                        while (choice.charAt(0)!= 'n') 	{
                            pw.println("Type the name of the audio element you wish to add or 'n' to exit:");
                            String elementTitle = br.readLine(); //n
                            try {
                                theHub.addElementToPlayList(elementTitle, playListTitle);
                            } catch (NoPlayListFoundException ex) {
                                pw.println (ex.getMessage());
                            } catch (NoElementFoundException ex) {
                                pw.println (ex.getMessage());
                            }

                            pw.println("Type y to add a new one, n to end");
                            choice = br.readLine();
                        }
                        pw.println("Playlist created!");

                    } catch (IOException e) {
                        pw.println("ERROR! "+e.getMessage());
                        e.printStackTrace();
                    }
                    printAvailableCommands();
                    break;
                case '-':
                    try{
                        //delete a playlist
                        pw.println("Delete an existing playlist. Available playlists:");
                        Iterator<PlayList> itp = theHub.playlists();
                        while (itp.hasNext()) {
                            PlayList p = itp.next();
                            pw.println(p.getTitle());
                        }
                        String plTitle = br.readLine();
                        try {
                            theHub.deletePlayList(plTitle);
                        }	catch (NoPlayListFoundException ex) {
                            pw.println (ex.getMessage());
                        }
                        pw.println("Playlist deleted!");
                    } catch (IOException e) {
                        pw.println("ERROR! "+e.getMessage());
                        e.printStackTrace();
                    }
                    printAvailableCommands();
                    break;
                case 'j':
                    try{
                        //nom// 0 > < -
                        pw.println("<<START:SAIS>>");
                        File file = new File(FILE_PATH);
                        InputStream in = new FileInputStream(file);
                        byte[] bytes = new byte[16*1024];
                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            //String s = new String(bytes, StandardCharsets.UTF_8);
                            //System.out.println("length: "+count+" >"+s);
                            socket.getOutputStream().write(bytes, 0, count);
                        }
                        byte[] done = new byte[0];
                        socket.getOutputStream().flush();
                        socket.getOutputStream().write(done, 0, 0);
                        in.close();
                        //socket.getOutputStream().write(0);
                        pw.println("Done...");
                    }catch(Exception e){
                        pw.println(e.getMessage());
                    }
                    break;
                case 's':
                    try{
                        //save elements, albums, playlists
                        theHub.saveElements();
                        theHub.saveAlbums(); //saveAlbums()
                        theHub.savePlayLists(); //savePlayLists()
                        pw.println("Elements, albums and playlists saved!");
                        printAvailableCommands();
                    }catch(Exception e){
                        pw.println(e.getMessage());
                    }
                    break;
                default:
                    break;
            }
    }


    /**
     * Envoyer le menu vers le client en question
     */
    private void printAvailableCommands() {
        this.pw.println(MENU);
    }

    /**
     * Une methode appeler une seul fois dans le constructeur pour preparer le menu.
     * ( Les differentes commande possible )
     */
    private void prepareMenu(){
        MENU = "t: display the album titles, ordered by date"
                +"\ng: display songs of an album, ordered by genre"
                +"\nd: display songs of an album"
                +"\nu: display audiobooks ordered by author"
                +"\nc: add a new song"
                +"\na: add a new album"
                +"\n+: add a song to an album"
                +"\nl: add a new audiobook"
                +"\np: create a new playlist from existing songs and audio books"
                +"\n-: delete an existing playlist"
                +"\ns: save elements, albums, playlists"
                +"\nq: quit program";
    }
}
