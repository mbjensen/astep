package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.exception.BusinessException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.time.*;

/**
 * Created by carsten on 13/04/2016.
 */
public class RouteTest {
    Instant timeNow = Instant.now();
    int routeId = 10;

    List<Location> locationList = new ArrayList<Location>(){{
        add(new Location(new Coordinate(0,0), timeNow, "username", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(0,3), timeNow, "username", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(3,4), timeNow, "username", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(4,0), timeNow, "username", new Precision(68, 3.23d)));
    }};
    Route normalRoute = new Route(locationList, false, timeNow, routeId);

    @BeforeClass
    public static void testSetup() {
        // Setup
    }

    @AfterClass
    public static void testCleanup() {
        // Cleanup
    }

    @Test
    public void testCorrectConstructor(){
        boolean isStable = true;
        Route route = new Route(locationList, isStable, timeNow, routeId);
    }

    @Test
    public void testGetterThroughtConstructor(){
        boolean isStable = true;
        Route route = new Route(locationList, isStable, timeNow, routeId);
        assertEquals(locationList, route.getLocations());
        assertEquals(isStable, route.isStable());
        assertEquals(timeNow, route.getTimestamp());
    }

    @Test(expected=BusinessException.class)
    public void testConstructorOneLocations(){
        List<Location> oneLocatioList = new ArrayList<>();
        oneLocatioList.add(new Location(new Coordinate(4,0), timeNow, "username", new Precision(68, 3.23d)));

        boolean isStable = true;
        Route route = new Route(oneLocatioList, isStable, timeNow, routeId);
    }

    @Test(expected=BusinessException.class)
    public void testConstructorLocationsIsNull(){
        List<Location> oneLocatioList = new ArrayList<>();

        boolean isStable = true;
        Route route = new Route(oneLocatioList, isStable, timeNow, routeId);
    }

    @Test
    public void testsetStableCorrectTrue(){
        boolean expected = true;
        normalRoute.setStable(expected);
        boolean actual = normalRoute.isStable();
        assertTrue(expected == actual);
    }
    @Test
    public void testsetStableCorrectFalse(){
        boolean expected = false;
        normalRoute.setStable(expected);
        boolean actual = normalRoute.isStable();
        assertTrue(expected == actual);
    }

    @Test
    public void testGetUserName(){
        boolean isStable = true;
        String expected = "bo";
        List<Location> locationListNameBo = new ArrayList<Location>(){{
            add(new Location(new Coordinate(0,0), timeNow, expected, new Precision(68, 3.23d)));
            add(new Location(new Coordinate(0,3), timeNow, expected, new Precision(68, 3.23d)));
            add(new Location(new Coordinate(3,4), timeNow, expected, new Precision(68, 3.23d)));
            add(new Location(new Coordinate(4,0), timeNow, expected, new Precision(68, 3.23d)));
        }};
        Route route = new Route(locationListNameBo, isStable, timeNow, routeId);


        assertEquals(expected, route.getUsername());
    }

    @Test
    public void testGetID(){
        boolean isStable = true;
        int expected = 123;
        List<Location> locationListId123 = new ArrayList<Location>(){{
            add(new Location(new Coordinate(0,0), timeNow, "bo", new Precision(68, 3.23d)));
            add(new Location(new Coordinate(0,3), timeNow, "bo", new Precision(68, 3.23d)));
            add(new Location(new Coordinate(3,4), timeNow, "bo", new Precision(68, 3.23d)));
            add(new Location(new Coordinate(4,0), timeNow, "bo", new Precision(68, 3.23d)));
        }};
        Route route = new Route(locationListId123, isStable, timeNow, expected);


        assertEquals(expected, route.getId());
    }


    /*
    * public String getUsername() {
        return this.username;
    }

    @Override
    public int getId() {
        return id;
    }*/

}
