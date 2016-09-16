package dk.aau.astep.appserver.business.service.outdoor.restimpl;

import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.implementation.OutdoorUserApiService;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces.UserApiService;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.Outdoordb;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.TempDBWrapper;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Precision;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OutdoorUserApiServiceTest {
    final UserApiService outdoorUserDelegate  =  new OutdoorUserApiService(new TempDBWrapper());
    Coordinate center = new Coordinate(57.038507,9.919930);
    double radius = 5.32;
    Coordinate coordinate = new Coordinate(57.038507,9.919930);
    Precision precision = new Precision(68, 5.32d);
    Instant timestamp = Instant.ofEpochMilli(1360033892010l);
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

    @Before
    public void setUp(){

    }

    @After
    public void tearDown(){

    }

    public void testGetSpecificUsersInArea(){
        assertEquals(true, outdoorUserDelegate.getUsersInArea(polyCoordinates, usernames).hasEntity());
    }

    @Test
    public void testGetSpecificUsersInRadius(){
        assertEquals(true, outdoorUserDelegate.getUsersInRadius(center, radius, usernames).hasEntity());
    }

    @Test
    public void testGetUsersOutsideArea(){
        assertEquals(true, outdoorUserDelegate.getUsersOutsideArea(polyCoordinates, usernames).hasEntity());
    }

    @Test
    public void testGetUsersOutsideRadius(){
        assertEquals(true, outdoorUserDelegate.getUsersOutsideRadius(center, radius, usernames).hasEntity());
    }

    @Ignore
    @Test
    public void testPostLocation(){
        assertEquals(true, outdoorUserDelegate.postLocation(coordinate, username, precision, timestamp).hasEntity());
    }

    @Test
    public void testUserAvgAwayFromOthers(){
        assertEquals(true, outdoorUserDelegate.userAvgAwayFromOthers(usernames).hasEntity());
    }

    @Test
    public void testGetAUsersFriendNameAndLocation(){
        assertEquals(true, outdoorUserDelegate.getAUsersFriendNameAndLocation(usernames).hasEntity());
    }
}