package musichub.net;

/**
 * L'interface Observer
 */
public interface Observer {
    /**
     * Sert a notifier les clients ('listeners') s'il ya un changement au niveau des fichier coté serveur
     * Exp: si un client a ajouter un nouvelle chanson, Toutes seront notifier
     * @param message le message recu par le MusicHub
     */
    public void update(final String message);
}
