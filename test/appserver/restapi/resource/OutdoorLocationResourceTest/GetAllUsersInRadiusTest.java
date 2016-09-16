package dk.aau.astep.appserver.restapi.resource.outdoorlocationresourcetest;

import dk.aau.astep.appserver.Main;
import dk.aau.astep.appserver.restapi.resource.TestClientBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by Morten on 16/04/2016.
 */
public class GetAllUsersInRadiusTest {
    private static HttpServer server;
    private static WebTarget target;
    private static String token = "4D03F5B2F89F5977167AB61BFBB05DD4";
    private static String center = "57.004963;9.852982";
    private static String radius = "5.32";
    private static String token_string = "authorization";

    @BeforeClass
    public static void testSetup() {
        server = Main.startServer();
        Client c = TestClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    @AfterClass
    public static void testCleanup() {
        server.shutdownNow();
    }

    @Test
    public void testCorrectData() {
        Response response = target.path("locations/outdoor/users/radius")
                .queryParam("center", center).queryParam("radius", radius).request().header(token_string, token).get();
        int status = response.getStatus();
        assertEquals(200, status);
    }
}
