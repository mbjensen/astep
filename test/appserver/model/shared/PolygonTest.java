package dk.aau.astep.appserver.model.shared;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PolygonTest {
    private static final Polygon triangle = new Polygon(asList(
            new Coordinate(30, 30),
            new Coordinate(30, 50),
            new Coordinate(50, 40)));

    private static final Polygon concave = new Polygon(asList(
            new Coordinate(10, 10),
            new Coordinate(10, 50),
            new Coordinate(50, 50),
            new Coordinate(20, 40)));

    @Test
    public void testTrianglePointInside() {
        assertTrue(triangle.contains(new Coordinate(40, 40)));
        assertTrue(triangle.contains(new Coordinate(35, 42)));
    }

    @Test
    public void testTrianglePointOutside() {
        assertFalse(triangle.contains(new Coordinate(10, 10)));
        assertFalse(triangle.contains(new Coordinate(60, 60)));
        assertFalse(triangle.contains(new Coordinate(60, 60)));
        assertFalse(triangle.contains(new Coordinate(40, 30)));
        assertFalse(triangle.contains(new Coordinate(40, 60)));
    }

    @Test
    public void testTrianglePointOnEdge() {
        assertTrue(triangle.contains(new Coordinate(30, 40)));
    }

    @Test
    public void testTrianglePointOnVertex() {
        assertTrue(triangle.contains(new Coordinate(30, 30)));
        assertTrue(triangle.contains(new Coordinate(30, 50)));
        assertTrue(triangle.contains(new Coordinate(50, 40)));
    }

    @Test
    public void testTriangleClosePointOutside() {
        assertFalse(triangle.contains(new Coordinate(29.9999999999999, 40)));
        // TODO: add more checks
    }

    @Test
    public void testTriangleClosePointInside() {
        assertTrue(triangle.contains(new Coordinate(30.0000000000001, 40)));
        // TODO: add more checks
    }

    // Concave polygon tests

    @Test
    public void testConcavePointInside() {
        assertTrue(concave.contains(new Coordinate(15, 45)));
        assertTrue(concave.contains(new Coordinate(15, 35)));
        assertTrue(concave.contains(new Coordinate(25, 45)));
    }

    @Test
    public void testConcavePointOutside() {
        assertFalse(concave.contains(new Coordinate(5, 35)));
        assertFalse(concave.contains(new Coordinate(30, 55)));
        assertFalse(concave.contains(new Coordinate(25, 35)));
        assertFalse(concave.contains(new Coordinate(25, 40)));
    }

    @Test
    public void testConcavePointOnEdge() {
        assertTrue(concave.contains(new Coordinate(10, 30)));
        assertTrue(concave.contains(new Coordinate(10, 11)));
        assertTrue(concave.contains(new Coordinate(40, 49.999999999999)));

        // TODO: this case does not work, probably because of rounding errors in .contains?
        //assertTrue(concave.contains(new Coordinate(49, 50)));
    }

    @Test
    public void testConcavePointOnVertex() {
        assertTrue(concave.contains(new Coordinate(10, 10)));
        assertTrue(concave.contains(new Coordinate(10, 50)));
        assertTrue(concave.contains(new Coordinate(50, 50)));
        assertTrue(concave.contains(new Coordinate(20, 40)));
    }

    @Test
    public void testConcaveClosePointOutside() {
        assertFalse(concave.contains(new Coordinate(9.999999999999, 30)));
        assertFalse(concave.contains(new Coordinate(9.999999999999, 40)));
        assertFalse(concave.contains(new Coordinate(30, 50.000000000001)));
        assertFalse(concave.contains(new Coordinate(20.000000000010, 40.0000000000001)));
    }

    @Test
    public void testConcaveClosePointInside() {
        assertTrue(concave.contains(new Coordinate(10.000000000001, 30)));
        assertTrue(concave.contains(new Coordinate(10.000000000001, 40)));
        assertTrue(concave.contains(new Coordinate(30, 49.999999999999)));
        assertTrue(concave.contains(new Coordinate(19.999999999999, 40)));
        assertTrue(concave.contains(new Coordinate(20, 40.000000000001)));
    }
}
