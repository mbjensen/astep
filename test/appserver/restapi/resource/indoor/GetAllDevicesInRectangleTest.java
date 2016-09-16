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
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetAllDevicesInRectangleTest extends IndoorJerseyTest {
    private WebTarget target;

    /**
     * Initialize a WebTarget for querying rectangular areas in the indoor resource
     */
    @Before
    public void initialize() {
        target = target(indoorResourcePath).path("devices/rectangle");
    }

    /**
     * Tests if the response status is OK
     */
    @Test
    public void testResponseCode() {
        Response response = target
                .queryParam("upperLeft", "57.011791;9.990075")
                .queryParam("bottomRight", "57.013023;9.991738")
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
        Response jsonResponse = target
                .queryParam("upperLeft", "76.4532544;86.3245473")
                .queryParam("bottomRight", "84.3487124;75.9674423")
                .request().accept(MediaType.APPLICATION_JSON)
                .header("authorization", getToken()).get();
        assertEquals("Expected response code: 200(OK)", 200, jsonResponse.getStatus());
    }

    /**
     * Tests that a query missing required parameters is not accepted
     */
    @Test
    public void testMissingQueryParamsRectangle() {
        assertEquals("Expected response code: 422", 422, target
                .request()
                .header("authorization", getToken())
                .get().getStatus());
        assertEquals("Expected response code: 422", 422, target
                .queryParam("upperLeft", "76.4532544;86.3245473")
                .request()
                .header("authorization", getToken())
                .get().getStatus());
        assertEquals("Expected response code: 422", 422, target
                .queryParam("bottomRight", "84.3487124;75.9674423")
                .request()
                .header("authorization", getToken())
                .get().getStatus());
    }

    /**
     * Test that an invalid timestamp is not accepted
     */
    @Test
    public void testInvalidTimestamp() {
        Response response = target
                .queryParam("upperLeft", "76.4532544;86.3245473")
                .queryParam("bottomRight", "84.3487124;75.9674423")
                .queryParam("time", "abcd")
                .request()
                .header("authorization", getToken())
                .get();

        assertEquals("Expected response code: 400", 400, response.getStatus());
    }

    // TODO: mock cases to ensure locations are tested

    /**
     * Tests that the returned coordinates are within the boundary of the requested rectangle
     */
    @Test
    public void testLocationCoordinatesWithinBoundaries() {
        Polygon bounds = new Polygon(asList(
                new Coordinate(57.011791, 9.990075),
                new Coordinate(57.011791, 9.991738),
                new Coordinate(57.013023, 9.991738),
                new Coordinate(57.013023, 9.990075)
        ));

        List<Location> locations = target
                .queryParam("upperLeft", "57.011791;9.990075")
                .queryParam("bottomRight", "57.013023;9.991738")
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        // TODO: mock or something, ensure there are locations to test
        //assertTrue(locations.size() > 0);

        for (final Location location : locations) {
            assertTrue(bounds.contains(location.getCoordinate()));
        }
    }

    /**
     * Tests that the returned coordinates are within the correct time frame, tests for 5 minutes
     */
    @Test
    public void testTimeWithinFive() {
        Instant earlier = Instant.parse("2016-05-11T13:00:21Z");

        List<Location> locations = target
                .queryParam("upperLeft", "57.011791;9.990075")
                .queryParam("bottomRight", "57.013023;9.991738")
                .queryParam("time", earlier.toString())
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        // TODO: mock or something, ensure there are locations to test
        //assertTrue(locations.size() > 0);

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

        List<Location> locations = target
                .queryParam("upperLeft", "57.011791;9.990075")
                .queryParam("bottomRight", "57.013023;9.991738")
                .queryParam("time", earlier.toString())
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        // TODO: mock or something, ensure there are locations to test
        //assertTrue(locations.size() > 0);

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

        List<Location> locations = target
                .queryParam("upperLeft", "57.011791;9.990075")
                .queryParam("bottomRight", "57.013023;9.991738")
                .queryParam("time", earlier.toString())
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        // TODO: mock or something, ensure there are locations to test
        //assertTrue(locations.size() > 0);

        for (final Location location : locations) {
            Instant locationTime = location.getTimestamp();
            assertTrue(locationTime.isBefore(earlier.minus(Duration.ofMinutes(55))));
            assertTrue(locationTime.isAfter(earlier.minus(Duration.ofMinutes(60).plusMillis(1))));
        }
    }

    // TODO: check that locations are valid as in GetDevicePositionTest?
}
