package dk.aau.astep.appserver.business.service.outdoor.tempdb;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.db.persistent.data.LocationFactory;


import java.time.Instant;

/**
 * Created by carsten on 10/05/2016.
 */
// this class is used for the persistent to build a Location
public class DBLocationFactory implements LocationFactory<Coordinate,Location> {

    @Override
    public Location create(Coordinate coordinate, Instant timestamp, String username, double unit, double radius) {
        return new Location(coordinate, timestamp, username, new Precision(unit, radius));
    }
}

