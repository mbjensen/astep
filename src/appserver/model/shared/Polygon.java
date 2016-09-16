package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.appserver.model.outdoor.Rectangle;
import dk.aau.astep.exception.BusinessException;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;

import java.util.List;

public class Polygon {
    private final List<Coordinate> vertices;

    /**
     * Constructor of the polygon class.
     *
     * @param vertices A list of coordinates, which must atleast have three different elements.
     */
    public Polygon(List<Coordinate> vertices) {
        if (vertices == null || vertices.size() < 3) {
            ALogger.log("Polygon: need to have at least 3 coordinates", Module.OD, Level.ERROR);
            throw new BusinessException();
        }

        // Ensure that all polygon coordinates are unique (no duplicates).
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                if (vertices.get(i).equals(vertices.get(j))) {
                    ALogger.log("Polygon must contain unique coordinates", Module.OD, Level.ERROR);
                    throw new BusinessException();
                }
            }
        }

        this.vertices = vertices;
        //this.vertices = new ArrayList<Coordinate>(vertices);
    }

    /**
     * @return returns the bounding box of the polygon as a rectangle.
     */
    public Rectangle createBoundingBox() {
        // lat is used as x and long is used as y.
        double minLat = this.vertices.get(0).getLatitude();
        double minLong = this.vertices.get(0).getLongitude();
        double maxLat = minLat;
        double maxLong = minLong;

        // Find the points min/max latitude and longitude
        for (int i = 1; i < this.vertices.size(); i++) {
            minLat = Math.min(this.vertices.get(i).getLatitude(), minLat);
            maxLat = Math.max(this.vertices.get(i).getLatitude(), maxLat);
            minLong = Math.min(this.vertices.get(i).getLongitude(), minLong);
            maxLong = Math.max(this.vertices.get(i).getLongitude(), maxLong);
        }
        return new Rectangle(new Coordinate(minLat, minLong), new Coordinate(maxLat, maxLong));
    }

    public List<Coordinate> getCoordinates() {
        return vertices;
    }

    /**
     * Return true if the given coordinate is contained inside the boundary.
     * See: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
     * https://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
     *
     * @param test The coordinate to check
     * @return true if the point is inside the boundary, false otherwise
     */
    public boolean contains(Coordinate test) {
        boolean result = false;
        for (int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
            double iy = vertices.get(i).getLongitude();
            double ix = vertices.get(i).getLatitude();
            double jy = vertices.get(j).getLongitude();
            double jx = vertices.get(j).getLatitude();

            // check point on vertex
            if (test.getLatitude() == ix && test.getLongitude() == iy) return true;

            // check within boundary
            if ((iy > test.getLongitude()) != (jy > test.getLongitude()) &&
                    (test.getLatitude() < (jx - ix) * (test.getLongitude() - iy) / (jy - iy) + ix)) {
                result = !result;
            }
        }
        return result;
    }

}
