package dk.aau.astep.appserver.business.service.outdoor.tempdb;

import dk.aau.astep.appserver.dataaccess.api.queries.HotLocationQueries;
import dk.aau.astep.appserver.model.outdoor.Route;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.db.persistent.api.RouteQueries;

/**
 * Created by carsten on 10/05/2016.
 */
public abstract class DBWrapper implements HotLocationQueries, RouteQueries<Coordinate, Location, Route> {
}
