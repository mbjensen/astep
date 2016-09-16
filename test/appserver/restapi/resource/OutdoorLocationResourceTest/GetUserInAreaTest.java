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

public class GetUserInAreaTest {
    private static HttpServer server;
    private static WebTarget target;
    private static String token = "4D03F5B2F89F5977167AB61BFBB05DD4";
    private static String coordinates1 = "57.004963;9.852982";
    private static String coordinates2 = "56.999354;9.990997";
    private static String coordinates3 = "57.105413;10.050049";
    private static String coordinates4 = "57.124054;9.734192";
    private static String poly_coordinate = "poly_coordinate";
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
        Response response = target.path("locations/outdoor/users/"+usernames+"/area")
                .queryParam(poly_coordinate, coordinates1)
                .queryParam(poly_coordinate, coordinates2)
                .queryParam(poly_coordinate, coordinates3)
                .queryParam(poly_coordinate, coordinates4).request().header(token_string, token).get();

        int status = response.getStatus();

        assertEquals(200, status);
    }
    @Test
    public void testWronglyFormattedCoordinateComma() {
        Response response = target.path("locations/outdoor/users/"+usernames+"/area")
                .queryParam(poly_coordinate, "57.004963,9.852982")
                .queryParam(poly_coordinate, coordinates2)
                .queryParam(poly_coordinate, coordinates3)
                .queryParam(poly_coordinate, coordinates4).request().header(token_string, token).get();

        int status = response.getStatus();
        assertEquals(400, status);
    }
    @Test
    public void testWronglyFormattedCoordinateTwoSemicolons() {
        Response response = target.path("locations/outdoor/users/"+usernames+"/area")
                .queryParam(poly_coordinate, "57.004963;;9.852982")
                .queryParam(poly_coordinate, coordinates2)
                .queryParam(poly_coordinate, coordinates3)
                .queryParam(poly_coordinate, coordinates4).request().header(token_string, token).get();

        int status = response.getStatus();
        assertEquals(400, status);
    }

    @Test
    public void testCoordinateOutOfBounds() {
        Response response = target.path("locations/outdoor/users/"+usernames+"/area")
                .queryParam(poly_coordinate, "181.004963;9.852982")
                .queryParam(poly_coordinate, coordinates2)
                .queryParam(poly_coordinate, coordinates3)
                .queryParam(poly_coordinate, coordinates4).request().header(token_string, token).get();

        int status = response.getStatus();
        assertEquals(400, status);
    }
}
