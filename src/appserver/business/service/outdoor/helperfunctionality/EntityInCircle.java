package dk.aau.astep.appserver.business.service.outdoor.helperfunctionality;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.context.DistanceBetweenTwoGPSLocations;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Haversine;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Vincenty;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IDistanceBetweenTwoGPSLocationsStrategy;
import dk.aau.astep.appserver.model.outdoor.Circle;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;

import java.util.ArrayList;
import java.util.List;

public class EntityInCircle {
    public EntityInCircle(){};

    /**
     *
     * @param circle The circle area where each locations is checked if inside or outside.
     * @param locations Locations of the users.
     * @param findInside Flag to return users inside or outside. true = inside.
     * @param isAccurate Flag for the precision of the algorithm to be used. True = precise.
     * @return Returns a list of users either inside or outside depending on the findInside flag.
     */
    public List<Location> entitiesInOrOutsideCircle(Circle circle, List<Location> locations,
                                               boolean findInside, boolean isAccurate){
        List<Location> insideLocations=new ArrayList<>();
        for(Location location:locations) {
            if(entityInOrOutsideCircle(circle, location.getCoordinate(), findInside, isAccurate)) {
                insideLocations.add(location);
            }
        }

        return insideLocations;
    }

    /**
     *
     * @param circle The circle area where each location is checked if inside or outside.
     * @param location Location of the user.
     * @param findInside Flag to return users inside or outside. true = inside.
     * @param isAccurate Flag for the precision of the algorithm to be used. True = precise.
     * @return Returns the answer if the user is inside or outside the circle depending on the findInside flag.
     */
    public boolean entityInOrOutsideCircle(Circle circle, Coordinate location,
                                      boolean findInside, boolean isAccurate){
        IDistanceBetweenTwoGPSLocationsStrategy distanceStrategy = isAccurate ? new Vincenty() :
                                                                                new Haversine();

        DistanceBetweenTwoGPSLocations distance = new DistanceBetweenTwoGPSLocations(distanceStrategy);

        return ( distance.getDistance(location, circle.getCenter()) < circle.getRadius() ) ? findInside : !findInside;
    }
}
