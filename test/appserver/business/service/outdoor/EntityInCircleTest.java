package dk.aau.astep.appserver.business.service.outdoor;

import dk.aau.astep.appserver.business.service.outdoor.helperfunctionality.EntityInCircle;
import dk.aau.astep.appserver.model.outdoor.Circle;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
// The test data is obtained using placemarks form google earth
// circle generator: http://kml4earth.appspot.com/circlegen.html
public class EntityInCircleTest {
    Coordinate center = new Coordinate(0,0);
    double radius = 20;
    Circle circle = new Circle(center, radius);
    double bigRadius = 100;
    Circle bigCircle = new Circle(center, bigRadius);

    Location inside1 = new Location(new Coordinate(0.332317, 0.162791), Instant.now(), "qwe", new Precision(68, 0.1d));
    Location inside2 = new Location(new Coordinate(-0.030849, 0.178917), Instant.now(), "qwe", new Precision(68, 0.1d));
    Location outside1 = new Location(new Coordinate(-0.070861, 1.173619), Instant.now(), "qwe", new Precision(68, 0.1d));
    Location outside2 = new Location(new Coordinate(0.545380, 1.260977), Instant.now(), "qwe", new Precision(68, 0.1d));
    Location outsideCloseToEdge = new Location(new Coordinate(0.460778, -0.773183), Instant.now(), "qwe", new Precision(68, 0.1d));
    Location insideCloseToEdge = new Location(new Coordinate(-0.695367, -0.560959), Instant.now(), "qwe", new Precision(68, 0.1d));




    EntityInCircle eIC = new EntityInCircle();
    /*Test for function: entityInsideCircle*/

    @Test
    public void testSinglePointAtCenter(){
        assertTrue(eIC.entityInOrOutsideCircle(circle, center, true, true));
    }

    @Test
    public void testSinglePointAtEdge(){
        assertTrue(eIC.entityInOrOutsideCircle(circle, new Coordinate(0,radius), false, true));
    }

    @Test
    public void testSinglePointCloseToEdgeInside(){
        assertTrue(eIC.entityInOrOutsideCircle(circle, new Coordinate(0.164042, -0.071620), true, true));
    }

    @Test
    public void testSinglePointCloseToEdgeOutside(){
        assertTrue(eIC.entityInOrOutsideCircle(circle, new Coordinate(0.163887,  -0.077815), false, true));
    }

    @Test
    public void testSinglePointOutsideCircle(){
        assertTrue(eIC.entityInOrOutsideCircle(circle, new Coordinate(0.438916,-0.040827), false, true));
    }

    @Test
    public void testSinglePointInsideCircle(){
        assertTrue(eIC.entityInOrOutsideCircle(circle, new Coordinate(0.034389,-0.061574), true, true));
    }

    @Test
    public void testAllPointInsideVincentCircle(){
        List<Location> allCoord = assignAllCoord();
        boolean isInside = true;
        boolean isAccurate = true;
        List<Location> actual = eIC.entitiesInOrOutsideCircle(bigCircle, allCoord, isInside, isAccurate);
        List<Location> expected = new ArrayList<>();
        expected.add(inside1);
        expected.add(inside2);
        expected.add(insideCloseToEdge);

        assertTrueLocationListEqual(expected, actual);
    }

    @Test
    public void testAllPointOutsideVincentCircle(){
        List<Location> allCoord = assignAllCoord();
        boolean isOutside = false;
        boolean isAccurate = true;
        List<Location> actual = eIC.entitiesInOrOutsideCircle(bigCircle, allCoord, isOutside, isAccurate);
        List<Location> expected = new ArrayList<>();
        expected.add(outside2);
        expected.add(outside1);
        expected.add(outsideCloseToEdge);

        assertTrueLocationListEqual(expected, actual);
    }

    @Test
    public void testAllPointInsideHaversineCircle(){
        List<Location> allCoord = assignAllCoord();
        boolean isInside = true;
        boolean isNotAccurate = false;
        List<Location> actual = eIC.entitiesInOrOutsideCircle(bigCircle, allCoord, isInside, isNotAccurate);
        List<Location> expected = new ArrayList<>();
        expected.add(inside1);
        expected.add(inside2);
        expected.add(insideCloseToEdge);

        assertTrueLocationListEqual(expected, actual);
    }
    @Test
    public void testAllPointOutsideHaversineCircle(){
        List<Location> allCoord = assignAllCoord();
        boolean isOutside = false;
        boolean isNotAccurate = false;
        List<Location> actual = eIC.entitiesInOrOutsideCircle(bigCircle, allCoord, isOutside, isNotAccurate);
        List<Location> expected = new ArrayList<>();
        expected.add(outside2);
        expected.add(outside1);
        expected.add(outsideCloseToEdge);

        assertTrueLocationListEqual(expected, actual);
    }

    @Test
    public void testBoundaryInsideCircle(){
        List<Location> locations = new ArrayList<>();
        locations.add(insideCloseToEdge);
        locations.add(outsideCloseToEdge);

        boolean isInside = true;
        boolean isAccurate = true;
        List<Location> actual = eIC.entitiesInOrOutsideCircle(bigCircle, locations, isInside, isAccurate);
        List<Location> expected = new ArrayList<>();

        expected.add(insideCloseToEdge);

        assertTrueLocationListEqual(expected, actual);
    }

    @Test
    public void testBoundaryOutsideCircle(){
        List<Location> locations = new ArrayList<>();
        locations.add(insideCloseToEdge);
        locations.add(outsideCloseToEdge);

        boolean isOutside = false;
        boolean isAccurate = true;
        List<Location> actual = eIC.entitiesInOrOutsideCircle(bigCircle, locations, isOutside, isAccurate);
        List<Location> expected = new ArrayList<>();

        expected.add(outsideCloseToEdge);

        assertTrueLocationListEqual(expected, actual);
    }

    private void assertTrueLocationListEqual(List<Location> expected, List<Location> actual){
        assertTrue(expected.size() == actual.size());
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    private List<Location> assignAllCoord(){
        List<Location> allCoordinates = new ArrayList<>();
        allCoordinates.add(inside1);
        allCoordinates.add(inside2);
        allCoordinates.add(outside1);
        allCoordinates.add(outside2);
        allCoordinates.add(outsideCloseToEdge);
        allCoordinates.add(insideCloseToEdge);

        return allCoordinates;
    }
}
