package dk.aau.astep.appserver.business.service.indoor;

import dk.aau.astep.appserver.dataaccess.internal.hotloc.db.Mongodb;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Polygon;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class IndoorDeviceApiService {

    private Mongodb database = new Mongodb();

    private final int lookbackInterval = 5; // minutes
    private final int maximumLookback = 60; // minutes

//    private DataGenerator dataGen = new DataGenerator();
//    private Location exampleLoc = dataGen.randomUserLoc("IndoorUser", Instant.now());

//    private List<Location> exampleLocations = new ArrayList<Location>() {{
//        // TODO: add static entry for tests (mock this)
//        add(new Location(new Coordinate(57.017417907714844, 9.988870620727539), Instant.parse("2016-04-04T14:53:33Z"), "IndoorUserAlice", new Precision(95, 25f)));
//        // add(dataGen.randomUserLoc("IndoorUserAlice", LocalDateTime.of(2016, Month.APRIL, 4, 14, 53, 33))); // of(int year, int month, int dayOfMonth, int hour, int minute, int second)
//        add(dataGen.randomUserLoc("IndoorUserAlice", Instant.parse("2016-04-04T15:00:12Z")));
//        add(dataGen.randomUserLoc("IndoorUserAlice", Instant.parse("2016-04-04T15:12:49Z")));
//        add(dataGen.randomUserLoc("IndoorUserCharlie", Instant.parse("2016-04-04T08:52:44Z")));
//        add(dataGen.randomUserLoc("IndoorUserCharlie", Instant.parse("2016-04-04T09:16:00Z")));
//        add(dataGen.randomUserLoc("IndoorUserCharlie", Instant.parse("2016-04-04T09:23:50Z")));
//    }};


    /**
     * Get the location of a specific device
     *
     * @param deviceId the id of the device (location username)
     * @return Location of the device, or null if no location is found
     */
    public Location getDevicePosition(String deviceId) {
        List<Location> locations = database.getNewestLocation(asList(deviceId));
        if (locations.size() > 0) {
            return locations.get(0);
        }
        return null;
    }

    /**
     * Gets a list of locations within a rectangle specified by two coordinates
     *
     * @param upperLeft   The upper left coordinate of the rectangle
     * @param bottomRight The bottom right coordinate of the rectangle
     * @return A list of the locations within the specified rectangle
     */
    public List<Location> getAllDevicesInRectangle(Coordinate upperLeft, Coordinate bottomRight) {
        return getAllDevicesInRectangle(upperLeft, bottomRight, Instant.now());
    }

    /**
     * Gets a list of locations within a rectangle specified by two coordinates within a timestamp and 5-60 minutes back
     *
     * @param upperLeft   The upper left coordinate of the rectangle
     * @param bottomRight The bottom right coordinate of the rectangle
     * @param time        The time from which and 5-60 minutes back the locations should be retrieved
     * @return A list of the locations within the specified rectangle
     */
    public List<Location> getAllDevicesInRectangle(Coordinate upperLeft, Coordinate bottomRight, Instant time) {
        Coordinate upperRight = new Coordinate(bottomRight.getLatitude(), upperLeft.getLongitude());
        Coordinate bottomLeft = new Coordinate(upperLeft.getLatitude(), bottomRight.getLongitude());

        List<Coordinate> coordinates = asList(upperLeft, upperRight, bottomRight, bottomLeft);

        return getAllDevicesInArea(coordinates, time);
    }

    /**
     * Gets a list of locations withing a polygon specified by a list of coordinates
     *
     * @param coordinates A list of coordinates specifying a polygon
     * @return A list of the locations within the specified polygon
     */
    public List<Location> getAllDevicesInArea(List<Coordinate> coordinates) {
        return getAllDevicesInArea(coordinates, Instant.now());
    }

    /**
     * Gets the list of locations within a polygon specified by a list of coordinates within a timestamp and 5-60 minutes back
     *
     * @param coordinates A list of coordinates specifying a polygon
     * @param time        The time from which and 5-60 minutes back the locations should be retrieved
     * @return A list of the locations within the specified polygon, or an empty list if no locations was found
     */
    public List<Location> getAllDevicesInArea(List<Coordinate> coordinates, Instant time) {
        Polygon area = new Polygon(coordinates);
        for (int i = lookbackInterval; i <= maximumLookback; i += lookbackInterval) {
            Instant fromTime = time.minus(Duration.ofMinutes(i));
            List<Location> locations = database.getLocationsInAreaAtTimeInterval(area, fromTime, time);
            if (locations.size() > 0) {
                return locations;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Gets all locations within a circle specified by a coordinate and a radius
     *
     * @param center The center coordinate for the circle
     * @param radius The radius for the circle
     * @return A list of the locations within the specified circle
     */
    public List<Location> getAllDevicesInCircle(Coordinate center, double radius) {
        return getAllDevicesInCircle(center, radius, Instant.now());
    }

    /**
     * Gets all locations within a circle specified by a coordinate and a radius within a timestamp and 5-60 minutes back
     *
     * @param center The center coordinate for the circle
     * @param radius The radius for the circle
     * @param time   The time from which and 5-60 minutes back the locations should be retrieved
     * @return A list of the locations within the specified circle, or an empty list if no locations was found
     */
    public List<Location> getAllDevicesInCircle(Coordinate center, double radius, Instant time) {
        for (int i = lookbackInterval; i <= maximumLookback; i += lookbackInterval) {
            Instant fromTime = time.minus(Duration.ofMinutes(i));
            List<Location> locations = database.getAllLocationsInRadius(center, radius, fromTime, time);
            if (locations.size() > 0) {
                return locations;
            }
        }
        return Collections.emptyList();
    }
}
