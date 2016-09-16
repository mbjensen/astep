package dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.pointInsidepolygon;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IPointInsidePolygonStrategy;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Coordinate;

import java.util.List;

/**
 * Created by Darel Rex Finley
 * Point-In-Polygon Algorithm — Determining Whether A Point Is Inside A Complex Polygon
 * http://alienryderflex.com/polygon/
 */

public class RayCasting implements IPointInsidePolygonStrategy {

    /**
     * Also called Crossing-test, even odd test.
     * Should only be used for simple-polygons
     * @param point The point to check if it is inside the polygon
     * @param polygon The polygon
     * @return True if the point is inside the polygon, else false. (if the point is on the poly edge it can return either true or false)
     */
    public boolean isInsidePolygon(Coordinate point, Polygon polygon)
    {
        double polyX, polyY;
        double pointX = point.getLatitude();
        double pointY = point.getLongitude();
        List<Coordinate> poly = polygon.getCoordinates();
        int count = poly.size();
        int j = count -1;
        boolean isInside = false;
        Coordinate prePoint;

        for (int i = 0; i < count; i++) {
            polyX = poly.get(i).getLatitude();
            polyY = poly.get(i).getLongitude();
            prePoint = poly.get(j);
            // Check if the ray is passing throught an edge.
            if( polyY < pointY && prePoint.getLongitude() >= pointY ||
                prePoint.getLongitude() < pointY && polyY >= pointY ) {

                if( polyX + (pointY - polyY) / (prePoint.getLongitude() - polyY) *
                    (prePoint.getLatitude() - polyX) < pointX ) {
                    // odd number of passing = true.
                    isInside = !isInside;
                }
            }
            j = i;
        }
        return isInside;
    }
}
