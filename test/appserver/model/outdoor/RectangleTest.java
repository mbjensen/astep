package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.appserver.model.shared.Coordinate;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by friea on 18-04-2016.
 */
public class RectangleTest {
    Rectangle rectangle = new Rectangle(new Coordinate(57.004963,9.852982), new Coordinate(56.999354,9.990997));

    @Test
    public void testGetMinXYCoord() throws Exception {
        assertEquals(new Coordinate(57.004963,9.852982), rectangle.getMinXYCoord());
    }

    @Test
    public void testGetMaxXYCoord() throws Exception {
        assertEquals(new Coordinate(56.999354,9.990997), rectangle.getMaxXYCoord());
    }
}