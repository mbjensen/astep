package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Coordinate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PolygonTest {
    List<Coordinate> polyCor = new ArrayList<Coordinate>(){{
        add(new Coordinate(0,0));
        add(new Coordinate(0,3));
        add(new Coordinate(3,4));
        add(new Coordinate(4,0));
    }};

    Polygon polyNormal = new Polygon(polyCor);
    @BeforeClass
    public static void testSetup() {
        // Setup
    }

    @AfterClass
    public static void testCleanup() {
        // Cleanup
    }

    @Test
    public void testPolygonSquareMinBound(){
        assertTrue(new Coordinate(0,0).equals(polyNormal.createBoundingBox().getMinXYCoord()));
    }

    @Test
    public void testPolygonSquareMaxBound(){
        assertTrue(new Coordinate(4,4).equals(polyNormal.createBoundingBox().getMaxXYCoord()));
    }

    @Test(expected=WebApplicationException.class)
    public void testPolygonVerticesOverlap(){
        List<Coordinate> polyCorners = new ArrayList<Coordinate>(){{
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
        }};
        Polygon poly = new Polygon(polyCorners);
    }

    @Test(expected=WebApplicationException.class)
    public void testPolygonVerticesCount1(){
        List<Coordinate> polyCorners = new ArrayList<Coordinate>(){{
            add(new Coordinate(0,0));
        }};
        Polygon poly = new Polygon(polyCorners);
    }

    @Test(expected=WebApplicationException.class)
    public void testPolygonEmpty(){
        List<Coordinate> polyCorners = new ArrayList<Coordinate>();
        Polygon poly = new Polygon(polyCorners);
    }
    @Test(expected=WebApplicationException.class)
    public void testPolygonOnlySamVertices(){
        List<Coordinate> polyCorners = new ArrayList<Coordinate>(){{
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
            add(new Coordinate(0,0));
        }};
        Polygon poly = new Polygon(polyCorners);
    }

}
