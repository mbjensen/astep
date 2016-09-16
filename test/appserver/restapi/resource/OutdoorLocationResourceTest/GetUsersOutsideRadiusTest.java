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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by friea on 12-04-2016.
 */
public class GetUsersOutsideRadiusTest {
    private static HttpServer server;
    private static WebTarget target;
    private static String token = "4D03F5B2F89F5977167AB61BFBB05DD4";
    private static String coordinates1 = "57.004963;9.852982";
    private static String usernames = "alex_holder,alex_fisker,carsten_hansen";
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
        Response response = target.path("locations/outdoor/users/"+usernames+"/outsideradius")
                .queryParam("center", coordinates1)
                .queryParam("radius", 5).request().header(token_string, token).get();
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void testResponseNegativeRadius() {
        Response response = target.path("locations/outdoor/users/"+usernames+"/outsideradius")
                .queryParam("center", coordinates1)
                .queryParam("radius", -1).request().header(token_string, token).get();
        int status = response.getStatus();
        assertEquals(400, status);
    }

    @Test
    public void testResponseRadiusIsZero() {
        Response response = target.path("locations/outdoor/users/"+usernames+"/outsideradius")
                .queryParam("center", coordinates1)
                .queryParam("radius", 0).request().header(token_string, token).get();
        int status = response.getStatus();
        assertEquals(400, status);
    }

    @Test
    public void testResponseWronglyFormattedCoordinateComma() {
        Response response = target.path("locations/outdoor/users/"+usernames+"/outsideradius")
                .queryParam("center", "57.004963,9.852982")
                .queryParam("radius", 5).request().header(token_string, token).get();

        int status = response.getStatus();
        assertEquals(400, status);
    }

    @Test
    public void testResponseWronglyFormattedCoordinateTwoSemicolons() {
        Response response = target.path("locations/outdoor/users/"+usernames+"/outsideradius")
                .queryParam("center", "57.004963;;9.852982")
                .queryParam("polyCoordinate", 5).request().header(token_string, token).get();

        int status = response.getStatus();
        assertEquals(400, status);
    }

    @Test
    public void testCoordinateOutOfBounds() {
        Response response = target.path("locations/outdoor/users/"+usernames+"/outsideradius")
                .queryParam("center", "181.004963;9.852982").queryParam("polyCoordinate", 5).request()
                .header(token_string, token).get();

        int status = response.getStatus();
        assertEquals(400, status);
    }
}
