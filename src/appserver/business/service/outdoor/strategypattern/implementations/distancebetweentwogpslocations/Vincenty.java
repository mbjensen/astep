package dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IDistanceBetweenTwoGPSLocationsStrategy;
import dk.aau.astep.appserver.model.shared.Coordinate;

/*******************************************************************
 *** Algorithm by Chris Veness. Taken from                       ***
 *** http://www.movable-type.co.uk/scripts/latlong-vincenty.html ***
 *** and converted to Java.                                      ***
 *******************************************************************/
public class Vincenty implements IDistanceBetweenTwoGPSLocationsStrategy
{
    /**
     *
     * @param loc1 The first coordinate
     * @param loc2 The second coordiante
     * @return location from one Coordinate to another Coordinate in kilometer
     */
    @Override
    public double getDistance(Coordinate loc1, Coordinate loc2)
    {
        //TODO refactor this so it can actully be read...
        // Declaration of constants for the elipse
        double majorSemiAxe = 6378137;
        double minorSemiAxe = 6356752.314245;
        double flattening = (majorSemiAxe - minorSemiAxe) / majorSemiAxe;

        // Difference in longitude
        double L = loc2.getLongitudeAsRadian() - loc1.getLongitudeAsRadian();

        double U1 = Math.atan((1 - flattening) * Math.tan(loc1.getLatitudeAsRadian()));
        double U2 = Math.atan((1 - flattening) * Math.tan(loc2.getLatitudeAsRadian()));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
        double cosSqAlpha;
        double sinSigma;
        double cos2SigmaM;
        double cosSigma;
        double sigma;

        // First approximation
        double lambda = L, lambdaP, iterLimit = 100;

        // Iterate until change in lambda is negligible
        do
        {
            double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda)
                    * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                    * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            );
            if (sinSigma == 0)
            {
                return 0;
            }

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

            double C = flattening / 16 * cosSqAlpha * (4 + flattening * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * flattening * sinAlpha
                    * (sigma + C * sinSigma
                    * (cos2SigmaM + C * cosSigma
                    * (-1 + 2 * cos2SigmaM * cos2SigmaM)
            )
            );

        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
        {
            return 0;
        }

        double uSq = cosSqAlpha * (majorSemiAxe * majorSemiAxe - minorSemiAxe * minorSemiAxe) / (minorSemiAxe * minorSemiAxe);
        double A = 1 + uSq / 16384
                * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma =
                B * sinSigma
                        * (cos2SigmaM + B / 4
                        * (cosSigma
                        * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                        * (-3 + 4 * sinSigma * sinSigma)
                        * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

        double s = minorSemiAxe * A * (sigma - deltaSigma);

        return s / 1000;
    }
}
