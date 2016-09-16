package dk.aau.astep.appserver.business.service.outdoor;

import dk.aau.astep.appserver.business.service.outdoor.helperfunctionality.RouteMatcher;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Haversine;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.TempDBWrapper;
import dk.aau.astep.appserver.model.outdoor.Route;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by carsten on 06/05/2016.
 */
public class RouteMatcherTest {
    List<Location> routeLoc = new ArrayList<Location>() {{
        add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "sub", new Precision(68, 5.43d)));
        add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "son", new Precision(68, 0.43d)));
        add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "john", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(57.124054, 9.734192), Instant.now(), "doe", new Precision(68, 6.54d)));
    }};

    Route routeOne = new Route(routeLoc, true, Instant.now(), 01);
    /** constructor **/
    @Test
    public void testConstructorCorrectFormat(){
        RouteMatcher routeMatch = new RouteMatcher(23.4d, 45.4d, 23.4d, 45.4d, new Haversine(), new TempDBWrapper());
    }
    @Test
    public void testConstructorCorrectFormatDistanceWeightIsZero(){
        RouteMatcher routeMatch = new RouteMatcher(0, 45.4d, 23.4d, 45.4d, new Haversine(), new TempDBWrapper());
    }
    @Test
    public void testConstructorCorrectFormatTomeWeightIsZero(){
        RouteMatcher routeMatch = new RouteMatcher(230.3d, 0, 23.4d, 45.4d, new Haversine(), new TempDBWrapper());
    }
    @Test(expected=WebApplicationException.class)
    public void testConstructorNegativeValues(){
        RouteMatcher routeMatch = new RouteMatcher(-456, -89, -98, -65, new Haversine(), new TempDBWrapper());
    }
    @Test(expected=WebApplicationException.class)
    public void testConstructorBothWeightIsZero(){
        RouteMatcher routeMatch = new RouteMatcher(0, 0, 23.4d, 45.4d, new Haversine(), new TempDBWrapper());
    }
    /** timeDistanceAnalyser **/
    @Test
    public void testTimeDistanceAnalyserSameRoute(){
        RouteMatcher routeMatch = new RouteMatcher(0, 23, 23.4d, 45.4d, new Haversine(), new TempDBWrapper());
        double expected = 1;
        double actual = routeMatch.timeDistanceAnalyser(routeOne, routeLoc.get(0), routeLoc.get(routeLoc.size() - 1));
        assertEquals(expected, actual, 0);
    }
    //TODO need more tests

    /** milliSecondsIntoWeekFromUnixTime **/
    @Test
    public void testTimeIsAFriday(){
        RouteMatcher routeMatch = new RouteMatcher(78.1, 45.2, 23.4d, 45.4d, new Haversine(), new TempDBWrapper());
        Instant someFriday = Instant.ofEpochMilli(1462539677000l); // friday 06/05/2016

        Instant mappedFriday = Instant.ofEpochMilli(routeMatch.milliSecondsIntoWeekFromUnixTime(someFriday));
        LocalDate dataActual = LocalDate.ofEpochDay(mappedFriday.getEpochSecond() / (60*60*24)) ;
        DayOfWeek actual = DayOfWeek.from(dataActual);
        DayOfWeek expected = DayOfWeek.FRIDAY;

        assertEquals(expected, actual);
    }
    @Test
    public void testTimeIsToday(){
        RouteMatcher routeMatch = new RouteMatcher(78.1, 45.2, 23.4d, 45.4d, new Haversine(), new TempDBWrapper());
        Instant today = Instant.now(); // friday 06/05/2016

        Instant mappedFriday = Instant.ofEpochMilli(routeMatch.milliSecondsIntoWeekFromUnixTime(today));
        LocalDate dataActual = LocalDate.ofEpochDay(mappedFriday.getEpochSecond() / (60*60*24)) ;
        DayOfWeek actual = DayOfWeek.from(dataActual);

        LocalDate today2 = LocalDate.now();
        DayOfWeek expected = DayOfWeek.from(today2);

        assertEquals(expected, actual);
    }

    /** timeDistanceAnalyser **/
    @Test
    public void testSameRouteScoreEqual1(){
        RouteMatcher routeMatch = new RouteMatcher(78.1, 45.2, 23.4d, 45.4d, new Haversine(), new TempDBWrapper());
        Instant today = Instant.now(); // friday 06/05/2016

        Route route = new Route(routeLoc, true, Instant.now(), 3);
        double actual = routeMatch.timeDistanceAnalyser(route, route.getLocations().get(0),
                route.getLocations().get(route.getLocations().size() - 1));
        double expected = 1;
        assertEquals(expected, actual, 0);
    }

    @Test
    public void testRoutesFarAwayScoreEqual0(){
        RouteMatcher routeMatch = new RouteMatcher(78.1, 45.2, 23.4d, 45.4d, new Haversine(), new TempDBWrapper());
        Instant today = Instant.now(); // friday 06/05/2016
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(new Coordinate(23,11), Instant.now(), "sven", new Precision(24,43)));
        locations.add(new Location(new Coordinate(23,11), Instant.now(), "sven", new Precision(24,43)));

        Route route1 = new Route(routeLoc,true, Instant.now(), 3);

        Route route2 = new Route(locations,true, Instant.now(), 3);
        double actual = routeMatch.timeDistanceAnalyser(route1, route2.getLocations().get(0),
                                                        route2.getLocations().get(route2.getLocations().size() - 1));
        double expected = 0;
        assertEquals(expected, actual, 0);
    }
    /****/
    /****/
    /****/
    /****/
    /****/
}
