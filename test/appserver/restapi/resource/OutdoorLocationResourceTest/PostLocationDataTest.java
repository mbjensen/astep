package dk.aau.astep.appserver.restapi.resource.outdoorlocationresourcetest;

import dk.aau.astep.appserver.Main;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertEquals;

/**
 * Created by Morten on 16/04/2016.
 */
public class PostLocationDataTest {
    private static HttpServer server;
    private static WebTarget target;
    private static String username = "outdoor";
    private static String token = "6A71C299DAF515A28D3290FDE1AE2C55";
    private static String timestamp = "1360033892010";
    private static String coordinate = "57.038507;9.919930";
    private static String token_string = "authorization";
    private static String timestamp_string = "timestamp";
    private static String coordinate_string = "coordinate";
    private static String precision = "5.32";
    private static String precision_string = "precision";

    @BeforeClass
    public static void testSetup() {
        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    @AfterClass
    public static void testCleanup() {
        server.shutdownNow();
    }

    //TODO: Find a way to test post request
    /*@Test
    public void testCorrectData() {
        Form location = new Form().param("username", "alice").param(token_string, token).param(coordinate_string, coordinate)
                                  .param(precision_string, precision).param(timestamp_string, timestamp);

        Response response = target.path("locations/outdoor/"+username+"/locations")
                                  .request().post(Entity.json(location));

        int status = response.getStatus();
        assertEquals(200, status);
    }*/
}
