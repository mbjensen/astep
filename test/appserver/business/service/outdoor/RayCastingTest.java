package dk.aau.astep.appserver.business.service.outdoor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.pointInsidepolygon.RayCasting;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Coordinate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;


public class RayCastingTest {
    RayCasting rayC = new RayCasting();

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

    @BeforeClass
    public static void testSetup() {
        // Setup
    }

    @AfterClass
    public static void testCleanup() {
        // Cleanup
    }

    @Test
    public void testRayCastingVerySimplePolygonInside(){
        assertTrue(rayC.isInsidePolygon(new Coordinate(31,31),poly1));
    }

    @Test
    public void testRayCastingVerySimplePolygonOutside(){
        assertFalse(rayC.isInsidePolygon(new Coordinate(29,29),poly1));
    }

    @Test
    public void testRayCastingPolygon2Inside(){
        assertTrue(rayC.isInsidePolygon(new Coordinate(1.3,3),poly2));
    }

    @Test
    public void testRayCastingPolygon2Outside(){
        assertFalse(rayC.isInsidePolygon(new Coordinate(2,3),poly2));
    }
    @Test
    public void testRayCastingVerySimplePolygonPointCloseToVerticesOutside(){
        assertFalse(rayC.isInsidePolygon(new Coordinate(29.9999999999999,30),poly1));
    }

    @Test
    public void testRayCastingVerySimplePolygonPointCloseToEdgeInside(){
        assertTrue(rayC.isInsidePolygon(new Coordinate(30.0000000000001,40),poly1));
    }

    // If the vertices is on the edge or a vertic, then we dont know if it is gona be true or false beacause of the imprecision of floating points.
    // Accordingly might be weird to test these........
    @Test
    public void testRayCastingVerySimplePolygonPointOnVertices(){
        assertFalse(rayC.isInsidePolygon(new Coordinate(30,30),poly1));
    }
    @Test
    public void testRayCastingVerySimplePolygonPointOnEdge(){
        assertFalse(rayC.isInsidePolygon(new Coordinate(30,40),poly1));
    }

}
