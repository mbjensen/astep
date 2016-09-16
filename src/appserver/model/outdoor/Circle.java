package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.exception.BusinessException;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;

/**
 * Created by carsten on 25/03/2016.
 */
public class Circle {
    // radius in km
    private double radius;
    private Coordinate center;

    public Circle(Coordinate center, double radius){
        if(radius <= 0){
            ALogger.log("Radius of a circle must be greater than 0, radius: "
                    + radius, Module.OD, Level.ERROR);
            throw new BusinessException();
        }
        this.radius = radius;
        this.center = center;
    }

    /**
     * Does not handle request if the pole or the 180 median is within the circle.
     * Also this algorithm assumes the earth is a full sphere
     * @return returns the boundingBox as a rectangle. If the radius of the circle is bigger then 1000km, the box might cut the circle.
     */
    public Rectangle circleBoundingBox()
    {
        // The math behind this algorith is from:
        // http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates#RefBronstein
        // assuming the earth is a nice round sphere, this earth radius is in km.
        int earthRadius = 6371;

        double latRad = Math.toRadians(this.center.getLatitude());
        double longRad = Math.toRadians(this.center.getLongitude());
        // Rather cover larger areal (25%) then cutting the circle.
        double dist = this.radius + (this.radius  / 4);
        double angularDist, latMin, latMax, deltaLong, longMin, longMax;

        // angular distance on a great circle of the earth
        angularDist = dist / earthRadius;
        // when traversing the great circle a distance equal to the angle: angularDist is equal to travel a distance of dist km.
        latMin = latRad - angularDist;
        latMax = latRad + angularDist;

        deltaLong =  Math.asin(Math.sin(angularDist)/Math.cos(latRad));

        longMin = longRad - deltaLong;
        longMax = longRad + deltaLong;

        return new Rectangle(new Coordinate(Math.toDegrees(latMin), Math.toDegrees(longMin)),
                             new Coordinate(Math.toDegrees(latMax), Math.toDegrees(longMax)));
    }

    public Coordinate getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }
}
