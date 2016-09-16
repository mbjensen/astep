package dk.aau.astep.appserver.restapi.resource.indoor;

import dk.aau.astep.appserver.model.shared.Location;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.Instant;

import static org.junit.Assert.*;

public class GetDevicePositionTest extends IndoorJerseyTest {
    private static final String deviceID = "0002905aafffbb446b0010803fcea829adb2b53f1e84b839a74ad2295da61818";
    private String token = this.getToken();

    private WebTarget target;

    /**
     * Initialize a WebTarget for querying the position of a single device
     */
    @Before
    public void initialize() {
        target = target(indoorResourcePath).path("devices/");
    }

    /**
     * Test that response status code is 'No Content' when device is not found
     */
    @Test
    public void testResponseCodeWhenNotFound() {
        Response response = target.path("dev1234").request()
                .header("authorization", token).get();
        assertEquals("Expected response code: 204(No Content)", 204, response.getStatus());
    }

    /**
     * Test that response status code is OK
     */
    @Test
    public void testResponseCode() {
        Response response = target.path(deviceID).request()
                .header("authorization", token).get();
        assertEquals("Expected response code: 200(OK)", 200, response.getStatus());
    }

    /**
     * Test that response mediatype is JSON
     */
    @Test
    public void testMediaTypeIsJSON() {
        Response jsonResponse = target.path(deviceID).request()
                .accept(MediaType.APPLICATION_JSON)
                .header("authorization", token).get();
        assertEquals("Expected response code: 200(OK)", 200, jsonResponse.getStatus());
    }

    /**
     * Tests that attributes of a location are valid (not null)
     */
    @Test
    public void testReturnsValidLocation() {
        Location location = target.path(deviceID).request()
                .header("authorization", token)
                .get(Location.class);

        assertNotNull("Location not null", location);

        assertEquals("Location has correct device id", deviceID, location.getUsername());

        assertNotNull("Precision not null", location.getPrecision());
        assertTrue("Unit is a number", !Double.isNaN(location.getPrecision().getUnit()));
        assertTrue("Radius is a number", !Double.isNaN(location.getPrecision().getRadius()));

        assertNotNull("Coordinate not null", location.getCoordinate());
        assertTrue("Latitude is a number", !Double.isNaN(location.getCoordinate().getLatitude()));
        assertTrue("Longitude is a number", !Double.isNaN(location.getCoordinate().getLongitude()));

        assertTrue("Location has timestamp from the past", location.getTimestamp().isBefore(Instant.now()));
    }

    /**
     * Test that response returns http status code 404 when missing deviceId param
     */
    @Test
    public void testInvalidRequestParamNoDeviceID() {
        Response response = target.request()
                .header("authorization", token).get();
        assertEquals("Expected response code: 404(Not Found)", 404, response.getStatus());
    }
}