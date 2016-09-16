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
public class GetAllEntityHistoriyInAreaTest {
    private static HttpServer server;
    private static WebTarget target;
    private static String username = "outdoor";
    private static String token = "4D03F5B2F89F5977167AB61BFBB05DD4";
    private static String period_begin = "2013-02-05T03:11:32Z";
    private static String period_end = "2016-04-07T13:14:52Z";
    private static String coordinate1 = "57.004963;9.852982";
    private static String coordinate2 = "56.999354;9.990997";
    private static String coordinate3 = "57.105413;10.050049";
    private static String coordinate4 = "57.124054;9.734192";
    private static String poly_coordinate = "poly_coordinates";
    private static String token_string = "authorization";
    private static String periode_begin_string = "periode_begin";
    private static String periode_end_string = "periode_end";

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
        Response response = target.path("locations/outdoor/histories/area")
                .queryParam(poly_coordinate, coordinate1).queryParam(poly_coordinate, coordinate2)
                .queryParam(poly_coordinate, coordinate3).queryParam(poly_coordinate, coordinate4).queryParam(periode_begin_string, period_begin)
                .queryParam(periode_end_string, period_end).request()
                .header(token_string, token).get();

        int status = response.getStatus();
        assertEquals(200, status);
    }
}
