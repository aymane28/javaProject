package musichub.business;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import javax.sound.sampled.LineEvent;

import static org.junit.jupiter.api.Assertions.*;



@TestMethodOrder(OrderAnnotation.class)
class AudioPlayerImplTest {


    public static final String AUDIO_FILE_PATH = System.getProperty("user.dir") + "\\files\\test.wav";
    private static AudioPlayerImpl audioPlayer = new AudioPlayerImpl( AUDIO_FILE_PATH );

    @BeforeEach
    void setUp() {
        System.out.println("Test of AudioPlayerImpl");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Order(2)
    void start() {
        assertTrue(audioPlayer.start());
        assertEquals("START", audioPlayer.getStatus());
    }

    @Test
    @Order(3)
    void stop() {
        assertTrue(audioPlayer.stop());
        assertEquals("STOP", audioPlayer.getStatus());
    }

    @Test
    @Order(4)
    void close() {
        assertTrue(audioPlayer.close());
        assertEquals("CLOSE", audioPlayer.getStatus());
    }

    @Test
    @Order(1)
    void open() {
        assertTrue(audioPlayer.open());
        assertEquals("OPEN", audioPlayer.getStatus());
    }

    @Test
    void update() {
    }
}