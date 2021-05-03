package musichub.net;


/**
 * L'interface IServer represente la forme generale d'un Serveur.
 *
 */
public interface IServer
{
	/**
	 * Lnacer le serveur pour su'il ecoute sur un porte est une adresse IP
	 * @return Retourn False si le servuer est déja en ecoute, Si non on lance le serveur pour qu'il ecoute et apres en renvoie True
	 */
	public boolean connect();

	/**
	 * Envoyer l'etat du serveur
	 * @return True s'il est déja connecté, False si non
	 */
	public boolean isConnected();

} 