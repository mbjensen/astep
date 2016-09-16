package dk.aau.astep.appserver.restapi.resource.indoor;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.context.DistanceBetweenTwoGPSLocations;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Haversine;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetAllDevicesInRadiusTest extends IndoorJerseyTest {
    private WebTarget target;

    private final Coordinate center = new Coordinate(57.015993, 9.983633);
    private final double radius = 0.2; // 200 meters

    /**
     * Makes a sample REST API request for a circular area
     * @return WebTarget with predefined center and radius params
     */
    private WebTarget makeSampleRadiusRequest() {
        // circular area around aau

        return target
                .queryParam("center", center.getLatitude() + ";" + center.getLongitude())
                .queryParam("radius", Double.toString(radius));
    }

    /**
     * Initialize a WebTarget for querying areas within radius in the indoor resource
     */
    @Before
    public void initialize() {
        target = target(indoorResourcePath).path("devices/radius");
    }

    /**
     * Tests if the response status is OK
     */
    @Test
    public void testResponseCode() {
        Response response = makeSampleRadiusRequest()
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
        Response jsonResponse = makeSampleRadiusRequest().request()
                .accept(MediaType.APPLICATION_JSON)
                .header("authorization", getToken()).get();
        assertEquals("Expected response code: 200(OK)", 200, jsonResponse.getStatus());
    }

    /**
     * Tests that a query missing required parameters is not accepted
     */
    @Test
    public void testMissingQueryParamsCircle() {
        assertEquals("Expected response code: 422", 422, target
                .request().header("authorization", getToken())
                .get().getStatus());
        assertEquals("Expected response code: 422", 422, target
                .queryParam("center", "76.4532544;86.3245473")
                .request().header("authorization", getToken())
                .get().getStatus());
        assertEquals("Expected response code: 422", 422, target
                .queryParam("radius", "10000")
                .request().header("authorization", getToken())
                .get().getStatus());
    }

    /**
     * Tests that an invalid timestamp parameter is not accepted
     */
    @Test
    public void testInvalidTimestamp() {
        Response response = makeSampleRadiusRequest()
                .queryParam("time", "abcd")
                .request()
                .header("authorization", getToken())
                .get();

        assertEquals("Expected response code: 400", 400, response.getStatus());
    }

    // TODO: mock cases so the range tests can pass

    /**
     * Tests that the returned coordinates are within the requested circle
     */
    @Test
    public void testLocationCoordinatesWithinRadius() {
        List<Location> locations = makeSampleRadiusRequest()
                .queryParam("time", "2016-05-11T13:00:21Z") // checking at specific time
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        // TODO: we assume this data remains in the database
        assertTrue(locations.size() == 2643);

        DistanceBetweenTwoGPSLocations dist = new DistanceBetweenTwoGPSLocations(new Haversine());

        for (final Location location : locations) {
            assertTrue(dist.getDistance(center, location.getCoordinate()) <= this.radius);
        }
    }

    /**
     * Tests that the returned coordinates are within the correct time frame, tests for 5 minutes
     */
    @Test
    public void testTimeWithinFive() {
        Instant earlier = Instant.parse("2016-05-11T13:00:21Z");
        List<Location> locations = makeSampleRadiusRequest()
                .queryParam("time", earlier.toString()) // checking at specific time
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
        List<Location> locations = makeSampleRadiusRequest()
                .queryParam("time", earlier.toString()) // checking at specific time
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
        List<Location> locations = makeSampleRadiusRequest()
                .queryParam("time", earlier.toString()) // checking at specific time
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        for (final Location location : locations) {
            Instant locationTime = location.getTimestamp();
            assertTrue(locationTime.isBefore(earlier.minus(Duration.ofMinutes(55))));
            assertTrue(locationTime.isAfter(earlier.minus(Duration.ofMinutes(60).plusMillis(1))));
        }
    }

    @Test
    @Ignore
    public void testLocationTimestampRangeNow() {
        Instant now = Instant.now();

        List<Location> locations = makeSampleRadiusRequest()
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

    @Test
    @Ignore
    public void testLocationTimestampRangeHistory() {
        Instant earlier = Instant.parse("2016-04-19T13:37:15Z");

        List<Location> locations = makeSampleRadiusRequest()
                .queryParam("time", earlier.toString())
                .request()
                .header("authorization", getToken())
                .get(new GenericType<List<Location>>() {});

        for (final Location location : locations) {
            Instant locationTime = location.getTimestamp();
            assertTrue(locationTime.isBefore(earlier));
            assertTrue(locationTime.isAfter(earlier.minus(Duration.ofMinutes(5))));
        }
    }

    // TODO: check that locations are valid as in GetDevicePositionTest?
}
