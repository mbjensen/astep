package dk.aau.astep.appserver.business.service.outdoor;


import dk.aau.astep.appserver.business.service.outdoor.helperfunctionality.AvgDistLocation;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Haversine;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.exception.BusinessException;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class AvgDistLocationTest {
    AvgDistLocation avgDistLoc = new AvgDistLocation();
    Instant timeNow = Instant.now();
    List<Location> locationsCloseToEachOther = new ArrayList<Location>(){{
        add(new Location(new Coordinate(0,0), timeNow, "Ulla", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(0,1), timeNow, "Ulla", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(1,1), timeNow, "Ulla", new Precision(68, 3.23d)));
        add(new Location(new Coordinate(1,0), timeNow, "Ulla", new Precision(68, 3.23d)));
    }};

    @BeforeClass
    public static void testSetup() {

    }

    @AfterClass
    public static void testCleanup() {

    }

    @Test
    public void testAllUserCloseToEachOtherListShouldBeEmpty() {
        List<Location> actual = avgDistLoc.entertiesAvgAwayFromGroup(
                                    locationsCloseToEachOther, new Haversine());
        List<Location> expected = new ArrayList<>();
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testOneAwayFormTheOtherUsers() {

        List<Location> locations = locationsCloseToEachOther;
        Location userFarAway = new Location(new Coordinate(5,5), timeNow, "Ulla", new Precision(68, 3.23d));
        locations.add(userFarAway);

        List<Location> actual = avgDistLoc.entertiesAvgAwayFromGroup(locations, new Haversine());
        List<Location> expected = new ArrayList<>();
        expected.add(userFarAway);

        Assert.assertEquals(actual, expected);
    }

    @Test(expected = BusinessException.class)
    public void testLocationIsNull() {
        List<Location> nullList = null;
        avgDistLoc.entertiesAvgAwayFromGroup(nullList, new Haversine());
    }

    @Test(expected = BusinessException.class)
    public void testLocationOnlyOneElement() {
        List<Location> oneElementList = new ArrayList<>();
        oneElementList.add(new Location(new Coordinate(0,0), timeNow, "Ulla", new Precision(68, 3.23d)));
        List<Location> actual = avgDistLoc.entertiesAvgAwayFromGroup(oneElementList, new Haversine());
    }
}
