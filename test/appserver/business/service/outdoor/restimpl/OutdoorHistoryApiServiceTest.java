package dk.aau.astep.appserver.business.service.outdoor.restimpl;

import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.implementation.OutdoorHistoryApiService;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces.HistoryApiService;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.Outdoordb;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.TempDBWrapper;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class OutdoorHistoryApiServiceTest {
    final HistoryApiService outdoorHistoryDelegate = new OutdoorHistoryApiService(new TempDBWrapper());
    Coordinate center = new Coordinate(57.038507,9.919930);
    double radius = 5.32;
    Instant periodBegin = Instant.parse("2013-02-05T03:11:32Z");
    Instant periodEnd = Instant.parse("2016-04-07T13:14:52Z");
    double distanceWeight = 145;
    double timeWeight = 135;
    double largestAcceptableDetourLength = 255;
    double acceptableTimeDifference = 34;
    List<Coordinate> polyCoordinates = new ArrayList<Coordinate>() {{
        add(new Coordinate(57.004963,9.852982));
        add(new Coordinate(56.999354,9.990997));
        add(new Coordinate(57.105413,10.050049));
        add(new Coordinate(57.124054,9.734192));
    }};
    String username = "outdoor";
    List<String> usernames = new ArrayList<String>() {{
        add("jens");
        add("svend");
        add("niels");
    }};
    List<Location> listLocations = new ArrayList<Location>() {{
        add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "alex", new Precision(68,5.43d)));
        add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "alex", new Precision(68, 0.43d)));
        add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "alex", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(57.124054, 9.734192), Instant.now(), "alex", new Precision(68, 6.54d)));
    }};

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetAllHistoryDataInTimePeriode() {
        assertEquals(true, outdoorHistoryDelegate.getAllHistoryDataInTimePeriode(periodBegin, periodEnd, username).hasEntity());
    }

    @Test
    public void testGetAllHistoryDataInAreaInTimePeriod() {
        assertEquals(true, outdoorHistoryDelegate.getAllHistoryDataInAreaInTimePeriod(polyCoordinates, periodBegin,
                periodEnd, username).hasEntity());
    }

    @Test
    public void testGetAllHistoryDataInRadiusInTimePeriod() {
        assertEquals(true, outdoorHistoryDelegate.getAllHistoryDataInRadiusInTimePeriod(center, radius, periodBegin,
                periodEnd, username).hasEntity());
    }

    @Ignore
    @Test
    public void testPostRoute() {
        assertEquals(true, outdoorHistoryDelegate.postRoute(listLocations, username, distanceWeight, timeWeight,
                largestAcceptableDetourLength, acceptableTimeDifference).hasEntity());
    }

    @Test
    public void testMatchRoute() {
        assertEquals(true, outdoorHistoryDelegate.matchRoute(usernames, distanceWeight ,timeWeight,
                largestAcceptableDetourLength, acceptableTimeDifference).hasEntity());
    }
}