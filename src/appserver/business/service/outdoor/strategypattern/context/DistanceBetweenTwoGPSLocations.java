package dk.aau.astep.appserver.business.service.outdoor.strategypattern.context;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IDistanceBetweenTwoGPSLocationsStrategy;

/**
 * Created by runew on 01-03-2016.
 */
public class DistanceBetweenTwoGPSLocations {
    private IDistanceBetweenTwoGPSLocationsStrategy strategy;

    public DistanceBetweenTwoGPSLocations(IDistanceBetweenTwoGPSLocationsStrategy strategy){
        this.strategy = strategy;
    }

    public double getDistance(Coordinate loc1, Coordinate loc2){
        return strategy.getDistance(loc1, loc2);
    }
}