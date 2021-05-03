package musichub.business;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * L'implementation de la classe AudioPlayerImpl qui implemente l'inetrface AudioPlayer et LineListener
 */
public class AudioPlayerImpl implements AudioPlayer, LineListener{

    private String audioFilePath;
    private Thread playAudioThread;
    private boolean playCompleted;
    private Clip audioClip;
    private String status;

    /**
     * Constructeur avec le chemin vers le fichier audio a ouvrir
     * @param audioFilePath Le chemin vers le fichier
     */
    public AudioPlayerImpl( String audioFilePath ) {
        this.audioFilePath = audioFilePath;
    }

    /**
     * renvoie le status de l'audio
     * @return Une chaine de caraceter qui represente le status actuel (OPEN, START, STOP ou CLOSE)
     */
    public String getStatus() {
        return status;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public boolean isPlayCompleted() {
        return playCompleted;
    }

    public void setPlayCompleted(boolean playCompleted) {
        this.playCompleted = playCompleted;
    }

    public Clip getAudioClip() {
        return audioClip;
    }

    public void setAudioClip(Clip audioClip) {
        this.audioClip = audioClip;
    }

    /**
     * Lancer la lecture d'un audio.
     * NB: il faut appeler la methode open avant d'apller start
     * @return True si le lancement est reusie, false si non
     */
    @Override
    public boolean start() {
        try{
            if(audioClip != null){
                audioClip.start();
                status = "START";
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Suspendre la music s'il est deja lancer
     * @return True si le suspension est reusie, false si non
     */
    @Override
    public boolean stop() {
        try {
            if( audioClip != null){
                audioClip.stop();
                status = "STOP";
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fermer et arreter un audio
     * @return True s'il y a deja au audio ouvert, si non False
     */
    @Override
    public boolean close() {
        try{
            if( audioClip != null){
                audioClip.close();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Ouvrir un fichier audio
     * @return  True si l'ouverture est reusie, false si non
     */
    @Override
    public boolean open() {
        try{
            File audioFile = new File(audioFilePath);
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); //send to client

                AudioFormat format = audioStream.getFormat();

                DataLine.Info info = new DataLine.Info(Clip.class, format);

                audioClip = (Clip) AudioSystem.getLine(info);

                audioClip.addLineListener(this);

                audioClip.open(audioStream);

                return true;
            } catch (UnsupportedAudioFileException ex) {
                System.out.println("The specified audio file is not supported.");
                ex.printStackTrace();
            } catch (LineUnavailableException ex) {
                System.out.println("Audio line for playing back is unavailable.");
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println("Error playing the audio file.");
                ex.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Informs the listener that a line's state has changed.  The listener can then invoke
     * <code>LineEvent</code> methods to obtain information about the event.
     *
     * @param event a line event that describes the change
     */
    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
        if (type == LineEvent.Type.START) {
            status = "START";
        } else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
            status = "STOP";
        }else if (type == LineEvent.Type.CLOSE) {
            status = "CLOSE";
        }else if (type == LineEvent.Type.OPEN) {
            status = "OPEN";
        }
    }
}
