package musichub.net;

import musichub.ClientConnection;
import musichub.ServerConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private Server server = Server.getInstance();
    private Client client = new Client(6666, "Test");
    @AfterEach
    void tearDown() {

    }


    @BeforeEach
    void setUp() {
        System.out.println("Test of ClientClass");
        server.connect();
    }

    @Test
    void connect() {
        assertTrue(client.connect("127.0.0.1"));
    }

    @Test
    void sent() {

        assertTrue(client.connect("127.0.0.1"));
        String msg = "Hello From Client Test";
        assertEquals( msg.length(), client.sent( msg ));
    }
/**
    @Test
    void receive() throws IOException {
        assertTrue(client.connect("127.0.0.1"));
        System.out.println(server.getSize()+" "+server.getNbClients());
        String msg = "Hello From Client Test";
        //server.broadcast( msg );
        assertNotEquals(-1, client.sent("h"));
        System.out.println(client.receive());
        assertEquals("q: quit program", client.receive());
        //assertNotNull(client.receive());
    }
    */

}