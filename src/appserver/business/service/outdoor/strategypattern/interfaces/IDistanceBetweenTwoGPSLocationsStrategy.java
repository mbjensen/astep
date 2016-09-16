package dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces;

import dk.aau.astep.appserver.model.shared.Coordinate;

/**
 * Created by friea on 01-03-2016.
 */
public interface IDistanceBetweenTwoGPSLocationsStrategy {
    public double getDistance(Coordinate loc1, Coordinate loc2);
}