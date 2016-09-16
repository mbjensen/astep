package dk.aau.astep.appserver.restapi.resource.indoor;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Polygon;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.*;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetAllDevicesInAreaTest extends IndoorJerseyTest {
    private WebTarget target;

    /**
     * Makes a sample REST API request for a triangular polygon
     * @return WebTarget with three predefined coordinate params
     */
    private WebTarget makeSampleAreaRequest() {
        // Triangular area around AAU
        return target
                .queryParam("coordinate", "57.032294;9.920826")
                .queryParam("coordinate", "57.029679;10.038586")
                .queryParam("coordinate", "56.982005;9.981594");
    }

    /**
     * Initialize a WebTarget for querying areas in the indoor resource
     */
    @Before
    public void initialize() {
        target = target(indoorResourcePath).path("devices/area");
    }

    /**
     * Tests if the response status is OK
     */
    @Test
    public void testResponseCode() {
        Response response = makeSampleAreaRequest()
                .request()
                .header("authorization", getToken())
                .get();
        assertEquals("Expected response code: 200(OK)", 200, response.getStatus());
    }

    /**
     * Tests if the returned mediatype is JSON
     */
    @Test
    public void testMediaTypeIsJSON() {
        // javax.ws.rs.NotAcceptableException: HTTP 406 Not Acceptable
        Response jsonResponse = makeSampleAreaRequest()
                .request()
                .header("authorization", getToken())
                .accept(MediaType.APPLICATION_JSON).get();
        assertEquals("Expected response code: 200(OK)", 200, jsonResponse.getStatus());
    }

    /**
     * Tests that less than three coordinates are not accepted
     */
    @Test
    public void testTooFewCoordinateParams() {
        // Must supply at least 3 coordinates
        assertEquals("Expected response code: 422", 422, target
                .request()
                .header("authorization", getToken())
                .get().getStatus());
        assertEquals("Expected response code: 422", 422, target
                .queryParam("coordinate", "76.4532544;86.3245473")
                .request()
                .header("authorization", getToken())
                .get().getStatus());
        assertEquals("Expected response code: 422", 422, target
                .queryParam("coordinate", "76.4532544;86.3245473")
                .queryParam("coordinate", "84.3487124;75.9674423")
                .request()
                .header("authorization", getToken())
                .get().getStatus());
    }

    /**
     * Test that an invalid timestamp is not accepted
     */
    @Test
    public void testInvalidTimestamp() {
        Response response = makeSampleAreaRequest()
                .queryParam("time", "abcd")
                .request()
                .header("authorization", getToken())
                .get();

        assertEquals("Expected response code: 400", 400, response.getStatus());
    }

    /**
     * Tests that the returned locations are within the boundary of the requested polygon
     */
    @Test
    public void testLocationCoordinatesWithinBoundaries() {
        Polygon bounds = new Polygon(asList(
                new Coordinate(57.032294, 9.920826),
                new Coordinate(57.029679, 10.038586),
                new Coordinate(56.982005, 9.981594)
        ));

        List<Location> locations = makeSampleAreaRequest()
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        // TODO: mock or something, ensure there are locations to test
        //assertTrue(locations.size() > 0);

        for (final Location location : locations) {
            assertTrue(bounds.contains(location.getCoordinate()));
        }
    }

    @Test
    @Ignore
    public void testLocationTimestampRangeNow() {
        Instant now = Instant.now();

        List<Location> locations = makeSampleAreaRequest()
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        // TODO: define minutes
        for (final Location location : locations) {
            Instant locationTime = location.getTimestamp();
            assertTrue(locationTime.isBefore(now));
            assertTrue(locationTime.isAfter(now.minus(Duration.ofMinutes(5))));
        }
    }

    /**
     * Tests that the returned coordinates are within the correct time frame, tests for 5 minutes
     */
    @Test
    public void testTimeWithinFive() {
        Instant earlier = Instant.parse("2016-05-11T13:00:21Z");

        List<Location> locations = makeSampleAreaRequest()
                .queryParam("time", earlier.toString())
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        for (final Location location : locations) {
            Instant locationTime = location.getTimestamp();
            assertTrue(locationTime.isBefore(earlier));
            assertTrue(locationTime.isAfter(earlier.minus(Duration.ofMinutes(5).plusMillis(1))));
        }
    }

    /**
     * Tests that the returned coordinates are within the correct time frame, tests for 20 minutes
     */
    @Test
    public void testTimeWithinTwenty() {
        Instant earlier = Instant.parse("2016-05-11T13:45:21Z");

        List<Location> locations = makeSampleAreaRequest()
                .queryParam("time", earlier.toString())
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        for (final Location location : locations) {
            Instant locationTime = location.getTimestamp();
            assertTrue(locationTime.isBefore(earlier.minus(Duration.ofMinutes(15))));
            assertTrue(locationTime.isAfter(earlier.minus(Duration.ofMinutes(20).plusMillis(1))));
        }
    }

    /**
     * Tests that the returned coordinates are within the correct time frame, tests for 60 minutes
     */
    @Test
    public void testTimeWithinSixty() {
        Instant earlier = Instant.parse("2016-05-11T14:25:21Z");

        List<Location> locations = makeSampleAreaRequest()
                .queryParam("time", earlier.toString())
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        for (final Location location : locations) {
            Instant locationTime = location.getTimestamp();
            assertTrue(locationTime.isBefore(earlier.minus(Duration.ofMinutes(55))));
            assertTrue(locationTime.isAfter(earlier.minus(Duration.ofMinutes(60).plusMillis(1))));
        }
    }

    // TODO: check that locations are valid as in GetDevicePositionTest?

}
