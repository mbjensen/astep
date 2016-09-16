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
public class PostRouteTest {
    private static HttpServer server;
    private static WebTarget target;
    private static String username = "outdoor";
    private static String token = "6A71C299DAF515A28D3290FDE1AE2C55";
    private static String token_string = "authorization";
    private static String route_one = "57.004963;9.852982;3.23;1460555589000";
    private static String route_two = "56.999354;9.990997;3.9;1460555589000";
    private static String route_three = "57.105413;10.050049;3.5;1460555589000";
    private static String route_four = "57.124054;9.734192;3.23;1460555589000";
    private static String route_string = "route";
    private static String distance_weight = "250";
    private static String distance_weight_string = "distance_weight";
    private static String time_weight = "120";
    private static String time_weight_string = "time_weight";
    private static String largest_acceptable_detour_length = "146";
    private static String largest_acceptable_detour_length_string = "largest_acceptable_detour_length";
    private static String acceptable_time_difference = "32";
    private static String acceptable_time_difference_string = "acceptable_time_difference";

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

    //TODO: Find a way to post routes
    /*@Test
    public void testCorrectData() {
        Response response = target.path("locations/outdoor/"+username+"/routes")
                .queryParam(token_string, token).queryParam(route_string, route_one).queryParam(route_string, route_two)
                .queryParam(route_string, route_three).queryParam(route_string, route_four)
                .queryParam(distance_weight_string, distance_weight).queryParam(time_weight_string, time_weight)
                .queryParam(largest_acceptable_detour_length_string, largest_acceptable_detour_length)
                .queryParam(acceptable_time_difference_string, acceptable_time_difference).request().get();
        int status = response.getStatus();
        assertEquals(200, status);
    }*/
}
