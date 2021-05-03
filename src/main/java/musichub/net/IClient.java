package musichub.net;

/**
 * L'interface IClient represente la forme generale d'un client.
 *
 */
public interface IClient {
    /**
     * Etablir une connection TCP avec le serveur dont l'adresse est passer en parametre
     * @param ip l'adresse IP du serveur
     * @return Une valeur true si la connection est bien etablie, false dans le cas contraire
     */
    public boolean connect(String ip);

    /**
     * Envoyer un message vers le serveur
     * NB: le client doit être connecter avant d'apeller cette methode
     * @param msg Le message a envoyer vers le serveur
     * @return Retourn la taille du message si il est bien envoyé, -1 s'il ya un probleme (Pb: Client deconnecté)
     */
    public long sent(final String msg);

    /**
     * Recuperer le dernier message recu par le client
     * @return Le message recu par le client, si non renvoie null
     */
    public String receive();

    /**
     * Deconncter le client du serveur
     */
    public void shutDown();
}
