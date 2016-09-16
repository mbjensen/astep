package dk.aau.astep.appserver.business.service.outdoor.tempdb;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Haversine;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IDistanceBetweenTwoGPSLocationsStrategy;
import dk.aau.astep.appserver.dataaccess.api.queries.HotLocationQueries;
import dk.aau.astep.appserver.model.outdoor.Rectangle;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Precision;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morten on 11/05/2016.
 */
public class PerformanceTestDataLocations implements HotLocationQueries {

    private List<Location> locationData = new ArrayList<>();

    public PerformanceTestDataLocations() {
        locationData.add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "jens_nielsen", new Precision(68, 5.43f)));
        locationData.add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "carsten_juhl", new Precision(68, 0.43f)));
        locationData.add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend_andersen", new Precision(68, 10.23f)));
        locationData.add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "jens_nielsen", new Precision(68, 9.43f)));
        locationData.add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "carsten_juhl", new Precision(68, 12.43f)));
        locationData.add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend_andersen", new Precision(68, 54.23f)));
        locationData.add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "jens_nielsen", new Precision(68, 3.43f)));
        locationData.add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "carsten_juhl", new Precision(68, 14.43f)));
        locationData.add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend_andersen", new Precision(68, 18.23f)));
        locationData.add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend_andersen", new Precision(68, 2.23f)));
        locationData.add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "carsten_juhl", new Precision(68, 14.43f)));
        locationData.add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend_andersen", new Precision(68, 18.23f)));
        locationData.add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend_andersen", new Precision(68, 2.23f)));
        locationData.add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "carsten_juhl", new Precision(68, 14.43f)));
        locationData.add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend_andersen", new Precision(68, 18.23f)));
        locationData.add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend_andersen", new Precision(68, 2.23f)));
    }


    @Override
    public boolean saveLocation(Location location) {
        return true;
    }

    @Override
    public boolean saveLocations(List<Location> locations) {
        return true;
    }

    @Override
    public Location getNewestLocation(String userId) {

        Location locationData = new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "jens_nielsen", new Precision(68, 5.43f));

        return locationData;
    }

    @Override
    public List<Location> getNewestLocation(List<String> userIds) {

        return locationData;
    }

    @Override
    public List<Location> getNewestLocationsFromTime(List<String> userIds, Instant from) {

        return locationData;
    }

    @Override
    public List<Location> getNewestLocationsInArea(Polygon area) {

        return locationData;
    }

    @Override
    public List<Location> getNewestLocationsInArea(Rectangle area, List<String> userIds) {

        return locationData;
    }

    @Override
    public List<Location> getNewestLocationsOutsideArea(Rectangle area, List<String> userIds) {

        return locationData;
    }

    @Override
    public List<Location> getLocationsAtTimeInterval(String userId, Instant from, Instant to) {
        int i = 2;
        return locationData;
    }

    @Override
    public List<Location> getLocationsInAreaAtTimeInterval(Polygon area, Instant from, Instant to) {

        return locationData;
    }

    @Override
    public List<Location> getLocationsInAreaAtTimeInterval(Rectangle area, Instant from, Instant to, String userId) {

        return locationData;
    }

    @Override
    public List<Location> getAllLocationsInRadius(Coordinate coordinate, Double radius) {

        return locationData;
    }

    @Override
    public List<Location> getAllLocationsInRadius(Coordinate coordinate, Double radius, List<String> userIds) {

        return locationData;
    }

    @Override
    public List<Location> getAllLocationsInRadius(Coordinate coordinate, Double radius, Instant from, Instant to) {

        return locationData;
    }

    @Override
    public List<Location> getNewestLocationsOutsideRadius(Coordinate coordinate, Double radius, List<String> userIds) {

        return locationData;
    }

    @Override
    public List<Location> getAllLocations(String username) {
        return locationData;
    }
}
