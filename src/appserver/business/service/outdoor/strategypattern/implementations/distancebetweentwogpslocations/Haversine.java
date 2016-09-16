package dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IDistanceBetweenTwoGPSLocationsStrategy;
import dk.aau.astep.appserver.model.shared.Coordinate;

/***********************************************************
 *** Algorithm by Chris Veness. Taken from               ***
 *** http://www.movable-type.co.uk/scripts/latlong.html  ***
 *** and converted to Java.                              ***
 ***********************************************************/

public class Haversine implements IDistanceBetweenTwoGPSLocationsStrategy
{

    /**
     * Algo from http://www.movable-type.co.uk/scripts/latlong.html
     * @param loc1 first coordinate
     * @param loc2 second coordinate
     * @return the distance between the two coordinates in kilometer.
     */
    @Override
    public double getDistance(Coordinate loc1, Coordinate loc2)
    {
        //Setting the radius of the earth.
        int earthsRadius = 6371;

        // Load latitude and longitude into variables.
        double lat1Radians = loc1.getLatitudeAsRadian();
        double lat2Radians = loc2.getLatitudeAsRadian();

        // Calculate delta of the latitude and longitude.
        double deltaLat = lat1Radians - lat2Radians;
        double deltaLon = loc1.getLongitudeAsRadian() - loc2.getLongitudeAsRadian();


        // Three calculation steps for calculating the variable a
        double step1 = Math.pow(Math.sin(deltaLat / 2), 2);
        double step2 = Math.pow(Math.sin(deltaLon  / 2), 2);
        double step3 = Math.cos(lat1Radians) * Math.cos(lat2Radians);

        // A is the square of half the chord length between the points.
        double a = step1 + step2 * step3;

        // Calculate the square root of the variable a
        double squareRootOfA = Math.sqrt(a);

        // Calculate the square root of one minus the value a
        double squareRootOfAMinusOne = Math.sqrt(1 - a);

        // C is the angular distance in radians, and a is the square of half the chord length between the points.
        double c = 2 * Math.atan2(squareRootOfA, squareRootOfAMinusOne);

        // Calculate the distance in kilometre.
        double distance = earthsRadius * c;

        return distance;
    }
}
