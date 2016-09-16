package dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.pointInsidepolygon;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IPointInsidePolygonStrategy;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Coordinate;
import java.util.List;

/**
 * Created by David G. Alciatore and Rick Miranda
 * pseudo code from the article "A Winding Number and Point-in-Polygon Algorithm" by David G. Alciatore and Rick Miranda
 * https://www.engr.colostate.edu/~dga/dga/papers/point_in_polygon.pdf
 */

public class WindingNumber implements IPointInsidePolygonStrategy {

    /**
     * The Axis-Crossing method for calculating the windingnumber a polygon winds around the point.
     * Can be used for both simple and non-simple polygons.
     * @param coordToTest The coordiante for testing.
     * @param polygon The area for testing as a polygon
     * @return The answer if the coordinate is inside the polygon. True = inside.
     */
    public boolean isInsidePolygon( Coordinate coordToTest, Polygon polygon)
    {
        List<Coordinate> polyCoord = polygon.getCoordinates();
        int n = polyCoord.size() -1;
        double intersectionX = 0;
        double windingNr = 0;

        Point[] projPoly = new Point[polyCoord.size()];

        // Projection the polygon so the point in question is at coordinate(0,0)
        for (int i = 0; i < n +1 ; i++) {
            projPoly[i] = new Point(polyCoord.get(i).getLatitude() - coordToTest.getLatitude(),
                                    polyCoord.get(i).getLongitude() - coordToTest.getLongitude());
        }
        for (int i = 0; i < n ; i++) {
            Point testi1 = projPoly[i];
            Point testi2 = projPoly[i+1];
            // does the egde cross the x-axis
            if(projPoly[i].getY() * projPoly[i+1].getY() < 0) {
                // find the x value of the intersection.
                intersectionX = projPoly[i].getX() +
                                (projPoly[i].getY() * (projPoly[i + 1].getX() - projPoly[i].getX()) /
                                (projPoly[i].getY() - projPoly[i + 1].getY()));
                //
                // does the edge cross the positive x-axis
                if(intersectionX >= 0) { // TODO talk about if i should use > or >=
                    if(projPoly[i].getY() < 0)
                        windingNr++;
                    else
                        windingNr--;
                }
            }
            // The vertices poly[i] is on the positive / negative x-axis
            else if((projPoly[i].getY() == 0) && (projPoly[i].getX() > 0)) {
                if(projPoly[i+1].getY() > 0)
                    windingNr +=  0.5;
                else
                    windingNr -=  0.5;
            }
            //The vertices poly[i+1] is on the positive / negative x-axis
            else if((projPoly[i+1].getY() == 0) && (projPoly[i+1].getX() > 0)) {
                if(projPoly[i].getY() < 0)
                    windingNr += 0.5;
                else
                    windingNr -= 0.5;
            }
        }
        if (windingNr == 0)
            return false;
        else
            return true;
    }

    // This class is used when projection of the polygon so its center is the coordinate for testing, because lat and
    // long can get values exceeding their range.
    private class Point{
        private double x;
        private double y;
        public Point(double x, double y){
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
