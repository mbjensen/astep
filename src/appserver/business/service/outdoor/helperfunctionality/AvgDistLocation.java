package dk.aau.astep.appserver.business.service.outdoor.helperfunctionality;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IDistanceBetweenTwoGPSLocationsStrategy;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.exception.BusinessException;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carsten on 22/03/2016.
 */
public class AvgDistLocation {
    /**
     *
     * @param locations of the users in the group. Must be atleast two locations.
     * @param distStrat The Distance strategy to be used.
     * @return Returns a list of location corresponding to the users which is 30% more away then the average distance to each member in the group.
     */
    public List<Location> entertiesAvgAwayFromGroup (List<Location> locations, IDistanceBetweenTwoGPSLocationsStrategy distStrat)
    {
        if(locations == null ){
            ALogger.log("entertiesAvgAwayFromGroup: locations must not be null", Module.OD, Level.ERROR);
            throw new BusinessException();
        }
        //TODO: We can not throw an exception if location is less the 2. Perhaps there is not data in the database
        else if(locations.size() < 2){
            ALogger.log("entertiesAvgAwayFromGroup: locations must have size greater then 1, size: " + locations.size()
                        , Module.OD, Level.ERROR);
            throw new BusinessException();
        }
        int listSize = locations.size();
        double coordinateTotalSum = 0, totalSum = 0, avgSum = 0, toFarAway = 0;
        List<Double> totalDist = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            // This inner loop calculate the total distance sum from a single coordinate to all the other coordinates.
            for (int j = 0; j < listSize; j++) {
                if(j == i) {
                    continue;
                }
                coordinateTotalSum += distStrat.getDistance(locations.get(i).getCoordinate(), locations.get(j).getCoordinate());
            }
            totalDist.add(coordinateTotalSum);
            totalSum += coordinateTotalSum;
            coordinateTotalSum = 0;
        }

        avgSum = totalSum / listSize;
        // A user is to far away if its total distance is more then 30% higher then the avg distance.
        toFarAway = avgSum * 1.3;
        // Return all the coordinates that is to far away from the group.
        return coordinatesToFarAway(totalDist, locations, toFarAway);
    }

    private List<Location> coordinatesToFarAway(List<Double> distToCheck, List<Location> locations, double toFarAway){

        if(distToCheck == null || locations == null){
            ALogger.log("distToCheck or coordinate must not be null in func: coordinatesToFarAway", Module.OD, Level.ERROR);
            throw new BusinessException();
        }

        List<Location> avgAwayFromGroup = new ArrayList<>();
        // Finds all the coordinates that is to far away from the group.
        for (int i = 0; i < distToCheck.size(); i++) {
            if(distToCheck.get(i) > toFarAway){
                avgAwayFromGroup.add(locations.get(i));
            }
        }
        return avgAwayFromGroup;
    }
}
