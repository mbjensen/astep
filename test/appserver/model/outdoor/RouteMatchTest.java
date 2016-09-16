package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.exception.BusinessException;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RouteMatchTest {
    int routeId = 10;
    List<Location> DBAnswer = new ArrayList<Location>() {{
        add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "alex", new Precision(68, 5.43d)));
        add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "alex", new Precision(68, 0.43d)));
        add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "alex", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(57.124054, 9.734192), Instant.now(), "alex", new Precision(68, 6.54d)));
    }};
    List<Location> DBAnswerOne = new ArrayList<Location>() {{
        add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "carsten", new Precision(68, 5.43d)));
        add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "carsten", new Precision(68, 0.43d)));
        add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "carsten", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(57.124054, 9.734192), Instant.now(), "carsten", new Precision(68, 6.54d)));
    }};

    double score = 0.7;

    Route routeOne = new Route(DBAnswer, true, Instant.now(), routeId);
    Route routeTwo = new Route(DBAnswerOne, true, Instant.now(), routeId);


    RouteMatch routeMatch = new RouteMatch(routeOne, routeTwo, score);

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCorrectConstructor() {
        RouteMatch routeMatch = new RouteMatch(routeOne, routeTwo, score);
    }

    @Test
    public void testGetScore() throws Exception {
        Assert.assertEquals(routeMatch.getScore(), new RouteMatch(routeOne, routeTwo, score).getScore());
    }

    @Test
    public void testGetRouteOne() throws Exception {
        Assert.assertEquals(routeMatch.getRoute_one(), new RouteMatch(routeOne, routeTwo, score).getRoute_one());
    }

    @Test
    public void testGetRouteTwo() throws Exception {
        Assert.assertEquals(routeMatch.getRoute_two(), new RouteMatch(routeOne, routeTwo, score).getRoute_two());
    }

    @Test(expected=BusinessException.class)
    public void testConstructorRouteOneNull() {
        RouteMatch routeMatch = new RouteMatch(null, routeTwo, score);
    }

    @Test(expected=BusinessException.class)
    public void testConstructorRouteTwoNull() {
        RouteMatch routeMatch = new RouteMatch(routeOne, null, score);
    }

    @Test
    public void testGetTimeMatchFoundInstant() {
        RouteMatch routeMatch = new RouteMatch(routeOne, routeTwo, score);

        Instant timeStamp = routeMatch.getTimeMatchFoundInstant();
        double actual = (double) timeStamp.toEpochMilli();
        double expected = Instant.now().toEpochMilli();

        // because Instant.now() is used in routeMatch, there can be a small difference in the time.
        assertEquals(expected, actual, 1 );

    }

    @Test
    public void testGetTimeMatchFoundUTC() {
        RouteMatch routeMatch = new RouteMatch(routeOne, routeTwo, score);
        float expected = routeOne.getTimestamp().toEpochMilli();
        float actual = routeMatch.getTime_match_found();
        assertEquals(expected, actual, 0);
    }


    /*public Instant getTimeMatchFoundInstant() {
        return time_match_found;
    }

    @XmlElement(name="time_match_found")
    public float getTime_match_found() {
        return this.time_match_found.toEpochMilli();
    }

    public long getTimeMatchFound() {
        return this.time_match_found.toEpochMilli();
    }
    */
}