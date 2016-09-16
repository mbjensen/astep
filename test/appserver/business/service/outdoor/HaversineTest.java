package dk.aau.astep.appserver.business.service.outdoor;

import static org.junit.Assert.assertEquals;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Haversine;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.context.DistanceBetweenTwoGPSLocations;
import dk.aau.astep.appserver.model.shared.Coordinate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class HaversineTest {
    DistanceBetweenTwoGPSLocations haversine = new DistanceBetweenTwoGPSLocations(new Haversine());
    Coordinate locUni = new Coordinate(57.012389,9.990891);
    Coordinate locSkallerupvej = new Coordinate(57.039372,10.008090);
    Coordinate locBerlin = new Coordinate(52.468821,13.423917);
    Coordinate locAustralia = new Coordinate(-24.017921,134.979820);

    @BeforeClass
    public static void testSetup() {
        // Setup
    }

    @AfterClass
    public static void testCleanup() {
        // Cleanup
    }

    @Test
    public void testHaversineDistanceFromAAUToSkallerupvej(){
        assertEquals(3.18, haversine.getDistance(locUni, locSkallerupvej), 0.02);
    }

    @Test
    public void testHaversineDistanceFromAAUToBerlin(){
        assertEquals(551.83, haversine.getDistance(locUni, locBerlin), 0.9);
    }

    @Test
    public void testHaversineDistanceFromAAUToAustralia(){
        assertEquals(14315., haversine.getDistance(locUni, locAustralia), 17);
    }
}
