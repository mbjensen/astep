package dk.aau.astep.appserver.business.service.outdoor.tempdb;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.db.persistent.data.CoordinateFactory;

/**
 * Created by carsten on 11/05/2016.
 */
// this class is used for the persistent to build a Coordinate
public class DBCoordinateFactory implements CoordinateFactory<Coordinate> {
    @Override
    public Coordinate create(double latitude, double longitude) {
        return new Coordinate(latitude, longitude);
    }
}
