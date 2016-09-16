package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JsonResponseMatchRouteTest {
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

    List<Location> DBAnswerTwo = new ArrayList<Location>() {{
        add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "svend", new Precision(68, 5.43d)));
        add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "svend", new Precision(68, 0.43d)));
        add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(57.124054, 9.734192), Instant.now(), "svend", new Precision(68, 6.54d)));
    }};

    List<Location> DBAnswerThree = new ArrayList<Location>() {{
        add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "niels", new Precision(68, 5.43d)));
        add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "niels", new Precision(68, 0.43d)));
        add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "niels", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(57.124054, 9.734192), Instant.now(), "niels", new Precision(68, 6.54d)));
    }};

    Route routeOne = new Route(DBAnswer, true, Instant.now(),routeId);
    Route routeTwo = new Route(DBAnswerOne, true, Instant.now(), routeId);
    Route routeThree = new Route(DBAnswerTwo, true, Instant.now(), routeId);
    Route routeFour = new Route(DBAnswerThree, true, Instant.now(), routeId);

    List<RouteMatch> listOfRoutes = new ArrayList<RouteMatch>() {{
        add(new RouteMatch(routeOne, routeTwo, 0.5));
        add(new RouteMatch(routeThree, routeTwo, 0.7));
        add(new RouteMatch(routeFour, routeOne, 0.2));
        add(new RouteMatch(routeThree, routeOne, 0.9));

    }};
    JsonResponseMatchRoute jsonResponseMatchRoute = new JsonResponseMatchRoute(listOfRoutes);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCorrectConstructor() {
        JsonResponseMatchRoute jsonResponseMatchRoute = new JsonResponseMatchRoute(listOfRoutes);
    }

    @Test
    public void testCorrectConstructorEmpty() {
        JsonResponseMatchRoute jsonResponseMatchRoute = new JsonResponseMatchRoute();
    }

    @Test
    public void testGetRouteMatches() throws Exception {
        Assert.assertEquals(jsonResponseMatchRoute.getRouteMatches(), new JsonResponseMatchRoute(listOfRoutes).getRouteMatches());
    }
}