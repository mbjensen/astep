package dk.aau.astep.appserver.business.service.outdoor;

import static org.junit.Assert.assertEquals;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Vincenty;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.context.DistanceBetweenTwoGPSLocations;
import dk.aau.astep.appserver.model.shared.Coordinate;
import org.junit.Test;

public class VincentyTest {
    DistanceBetweenTwoGPSLocations vincenty = new DistanceBetweenTwoGPSLocations(new Vincenty());
    Coordinate locUni = new Coordinate(57.012389,9.990891);
    Coordinate locSkallerupvej = new Coordinate(57.039372,10.008090);
    Coordinate locBerlin = new Coordinate(52.468821,13.423917);
    Coordinate locAustralia = new Coordinate(-24.017921,134.979820);

    @Test
    public void testHaversineDistanceFromAAUToSkallerupvej(){
        assertEquals(3.18, vincenty.getDistance(locUni, locSkallerupvej), 1);
    }

    @Test
    public void testHaversineDistanceFromAAUToBerlin(){
        assertEquals(551.83, vincenty.getDistance(locUni, locBerlin), 1);
    }

    @Test
    public void testHaversineDistanceFromAAUToAustralia(){
        assertEquals(14315., vincenty.getDistance(locUni, locAustralia), 1);
    }
}
