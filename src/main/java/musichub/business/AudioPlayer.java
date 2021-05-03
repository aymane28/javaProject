package musichub.business;

/**
 * Interface AudioPlayer represente la forme generale d'un Audio Player
 * */

public interface AudioPlayer {
    /**
     * Lancer la lecture d'un audio
     * @return True si le lancement est reusie, false si non
     */
    public boolean start();

    /**
     * Suspendre la music s'il deja lancer
     * @return True si le suspension est reusie, false si non
     */
    public boolean stop();

    /**
     * Fermer et arreter un audio
     * @return True s'il y a deja au audio ouvert, si non False
     */
    public boolean close();

    /**
     * Ouvrir un fichier audio
     * @return  True si l'ouverture est reusie, false si non
     */
    public boolean open();
}
