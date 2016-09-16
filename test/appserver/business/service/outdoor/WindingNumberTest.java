package dk.aau.astep.appserver.business.service.outdoor;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.context.PointInsidePolygon;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.pointInsidepolygon.RayCasting;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.pointInsidepolygon.WindingNumber;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Precision;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// TODO: I am migrating this to the PolygonTest class using Polygon.contains method - mathias.
// This might not be needed anymore

/**
 * Created by carsten on 18/04/2016.
 */
public class WindingNumberTest {
    PointInsidePolygon pip = new PointInsidePolygon(new WindingNumber());
    Polygon poly1 = new Polygon(new ArrayList<Coordinate>(){{
        add(new Coordinate(30,30));
        add(new Coordinate(30,50));
        add(new Coordinate(50,40));
    }});

    Polygon poly2 = new Polygon(new ArrayList<Coordinate>(){{
        add(new Coordinate(1,1));
        add(new Coordinate(3,1));
        add(new Coordinate(4,2));
        add(new Coordinate(4,4));
        add(new Coordinate(2,2));
        add(new Coordinate(1,4));
    }});

    @Test
    public void testRayCastingMultiplePointsSomeInsideSomeOutside(){
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(new Coordinate(30,30), Instant.now(), "id", new Precision(68, 5d)));
        //inside these two below
        locations.add(new Location(new Coordinate(40,40), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(45,40), Instant.now(), "id", new Precision(68, 5d)));

        locations.add(new Location(new Coordinate(60,30), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(70,20), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(80,10), Instant.now(), "id", new Precision(68, 5d)));

        List<Location> actualLocs = pip.pointsInOrOutsidePolygon(locations, poly1, true);
        List<Coordinate> actual = new ArrayList<>();

        for (Location loc: actualLocs ) {
            actual.add(loc.getCoordinate());
        }
        List<Coordinate> expected = new ArrayList<>();
        expected.add(new Coordinate(40,40));
        expected.add(new Coordinate(45,40));

        // only checks the coordinates in the lists of locations.
        assertTrue("Either too few or too many coordinates were detected inside polygon.",
                expected.size() == actual.size());
        assertTrue("The expected list does not contain all the element that is inside.", expected.containsAll(actual));
        // this last one might not be needed
        assertTrue("The list of points inside the polygon does not contain all the elements that the expected list does", actual.containsAll(expected));
    }

    @Test
    public void testRayCastingMultiplePointsOnlyOutside(){
        List<Location> locations = new ArrayList<>();
        List<Coordinate> expected = new ArrayList<>();
        List<Coordinate> actual = new ArrayList<>();

        locations.add(new Location(new Coordinate(30,30), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(60,30), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(70,20), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(80,10), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(0,0), Instant.now(), "id", new Precision(68, 5d)));

        for (Location loc: locations ) {
            expected.add(loc.getCoordinate());
        }

        // testing the function
        List<Location> actualLocs = pip.pointsInOrOutsidePolygon(locations, poly1, false);


        for (Location loc: actualLocs ) {
            actual.add(loc.getCoordinate());
        }

        // only checks the coordinates in the lists of locations.
        assertTrue(actual.equals(expected));
    }

    @Test
    public void testRayCastingMultiplePointsOnlyInside(){
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(new Coordinate(1.5,1.5), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(2.5,1.5), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(3,2), Instant.now(), "id", new Precision(68, 5d)));
        locations.add(new Location(new Coordinate(3.2,2.5), Instant.now(), "id", new Precision(68, 5d)));

        // testing the function
        List<Location> actualLocs = pip.pointsInOrOutsidePolygon(locations, poly2, true);
        List<Coordinate> actual = new ArrayList<>();

        for (Location loc: actualLocs ) {
            actual.add(loc.getCoordinate());
        }

        List<Coordinate> expected = new ArrayList<>();
        expected.add(new Coordinate(1.5,1.5));
        expected.add(new Coordinate(2.5,1.5));
        expected.add(new Coordinate(3,2));
        expected.add(new Coordinate(3.2,2.5));

        // only checks the coordinates in the lists of locations.
        assertTrue((expected.size() == actual.size()) &&
                (expected.containsAll(actual) && actual.containsAll(expected)));
    }

    WindingNumber windingNr = new WindingNumber();

    @BeforeClass
    public static void testSetup() {
        // Setup
    }

    @AfterClass
    public static void testCleanup() {
        // Cleanup
    }

    @Test
    public void testWindingNrVerySimplePolygonOutside(){
        assertFalse(windingNr.isInsidePolygon(new Coordinate(29,29),poly1));
    }

    @Test
    public void testWindingNrPolygon2Inside(){
        assertTrue(windingNr.isInsidePolygon(new Coordinate(1.3,3),poly2));
    }

    @Test
    public void testWindingNrPolygon2Outside(){
        assertFalse(windingNr.isInsidePolygon(new Coordinate(2,3),poly2));
    }

    @Test
    public void testWindingNrVerySimplePolygonPointCloseToEdgeInside(){
        assertTrue(windingNr.isInsidePolygon(new Coordinate(30.0000000000001,40),poly1));
    }

    @Test
    public void testWindingNrVerySimplePolygonPointOnVertices(){
        assertFalse(windingNr.isInsidePolygon(new Coordinate(30,30),poly1));
    }
}
