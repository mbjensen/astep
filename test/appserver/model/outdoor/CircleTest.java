package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.exception.BusinessException;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CircleTest {
    Coordinate center = null;
    double radius = 0;
    Circle circle = null;
    Rectangle circleBoundingBox = null;

    @Before
    public void setUp() {
        center = new Coordinate(57.004963,9.852982);
        radius = 5.32;
        circle = new Circle(center, radius);

        Coordinate rectCoordinate1 = new Coordinate(56.9451581132064,9.743160839515525);
        Coordinate rectCoordinate2 = new Coordinate(57.0647678867936, 9.962803160484478);
        circleBoundingBox = new Rectangle(rectCoordinate1, rectCoordinate2);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCircleBoundingBoxMaxXY() {
        assertTrue(circleBoundingBox.getMaxXYCoord().equals(circle.circleBoundingBox().getMaxXYCoord()));
    }

    @Test
    public void testCircleBoundingBoxMinXY() {
        assertTrue(circleBoundingBox.getMinXYCoord().equals(circle.circleBoundingBox().getMinXYCoord()));
    }

    @Test
    public void testGetCenter() {
        Assert.assertEquals(center, circle.getCenter());
    }

    @Test
    public void testGetRadius() {
        Assert.assertEquals(radius, circle.getRadius());
    }

    @Test(expected=BusinessException.class)
    public void testRadiusIsZero(){
        new Circle(new Coordinate(2,3), 0);
    }

    @Test(expected=BusinessException.class)
    public void testRadiusIsNegative(){
        new Circle(new Coordinate(2,3), -0.00004);
    }

    @Test (expected=BusinessException.class)
    public void testWrongCenterCoordinate(){
        new Circle(new Coordinate(181,181), 2);
    }

    @Test
    public void testCircleBoundingBox(){
        Circle centeredCircle = new Circle(new Coordinate(0,0), 5);
        Rectangle centeredCircleBoundingBox = centeredCircle.circleBoundingBox();

        Coordinate minXY = centeredCircleBoundingBox.getMinXYCoord();
        Coordinate maxXY = centeredCircleBoundingBox.getMaxXYCoord();

        Assert.assertEquals("Expected the absolute values of a circles's maximum " +
                "and minimum latitude to be equal.",
                Math.abs(minXY.getLatitude()), Math.abs(maxXY.getLatitude()));
        Assert.assertEquals("Expected the absolute values of a circles's maximum " +
                "and minimum longitude to be equal.",
                Math.abs(minXY.getLongitude()), Math.abs(maxXY.getLongitude()));
    }
}