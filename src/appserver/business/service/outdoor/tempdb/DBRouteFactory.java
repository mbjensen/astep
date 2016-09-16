package dk.aau.astep.appserver.business.service.outdoor.tempdb;

import dk.aau.astep.appserver.model.outdoor.Route;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.db.persistent.data.RouteFactory;

import java.time.Instant;
import java.util.List;

/**
 * Created by carsten on 10/05/2016.
 */
// this class is used for the persistent to build a Route
public class DBRouteFactory implements RouteFactory<Location, Route> {

    @Override
    public Route create(List<Location> route, boolean isStable, Instant timestamp, String username, int id) {
        return new Route(route, isStable, timestamp, id);
    }
}
