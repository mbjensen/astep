package dk.aau.astep.appserver.business.service.outdoor.tempdb;

import dk.aau.astep.appserver.business.service.usermanagement.FullUserFactory;
import dk.aau.astep.appserver.dataaccess.api.queries.HotLocationQueries;
import dk.aau.astep.appserver.dataaccess.internal.hotloc.db.Mongodb;
import dk.aau.astep.appserver.model.outdoor.Rectangle;
import dk.aau.astep.appserver.model.outdoor.Route;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.db.persistent.api.RouteQueries;
import dk.aau.astep.exception.BusinessException;
import javassist.bytecode.stackmap.BasicBlock;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carsten on 10/05/2016.
 */
// this is a temporary outdoor wrapper for the two databases. 10/05/2016
public class TempDBWrapper extends DBWrapper {
    private HotLocationQueries hotLocationQ;
    private RouteQueries<Coordinate, Location, Route> persistentDB;

    // TODO It shall be changed when the databases are handing over locations to each or they have a joint interface (probably not this semester) 10/05/2016

    public TempDBWrapper(){
        // decide which databases are used for the outdoor componenet.
        this.hotLocationQ = new Mongodb();
        this.persistentDB = new BaseRepository.Builder(new FullUserFactory(), new DBCoordinateFactory(),
                new DBLocationFactory(), new DBRouteFactory()).build().routeQueries();
    }

    /* HotLocationQueries */
    @Override
    public boolean saveLocation(Location location) {
        return hotLocationQ.saveLocation(location);
    }
    @Override
    public boolean saveLocations(List<Location> locations) {
        return hotLocationQ.saveLocations(locations);
    }
    @Override
    public List<Location> getAllLocations(String userId) {
        throw new NotImplementedException();
    }
    @Override
    public Location getNewestLocation(String userId) {
        throw new NotImplementedException();
    }
    @Override
    public List<Location> getNewestLocation(List<String> userIds) {
        return hotLocationQ.getNewestLocation(userIds);
    }
    @Override
    public List<Location> getNewestLocationsFromTime(List<String> userIds, Instant from) {
        return hotLocationQ.getNewestLocationsFromTime(userIds, from);
    }
    @Override
    public List<Location> getNewestLocationsInArea(Polygon area) {
        throw new NotImplementedException();
    }
    @Override
    public List<Location> getNewestLocationsInArea(Rectangle area, List<String> userIds) {
        return hotLocationQ.getNewestLocationsInArea(area, userIds);
    }
    @Override
    public List<Location> getNewestLocationsOutsideArea(Rectangle area, List<String> userIds) {
        return hotLocationQ.getNewestLocationsOutsideArea(area, userIds);
    }
    @Override
    public List<Location> getLocationsAtTimeInterval(String userId, Instant from, Instant to) {
        return hotLocationQ.getLocationsAtTimeInterval(userId, from, to);
    }
    @Override
    public List<Location> getLocationsInAreaAtTimeInterval(Polygon area, Instant from, Instant to) {
        throw new NotImplementedException();
    }
    @Override
    public List<Location> getLocationsInAreaAtTimeInterval(Rectangle area, Instant from, Instant to, String userId) {
        throw new NotImplementedException();
    }
    @Override
    public List<Location> getAllLocationsInRadius(Coordinate coordinate, Double radius) {
        throw new NotImplementedException();
    }
    @Override
    public List<Location> getAllLocationsInRadius(Coordinate coordinate, Double radius, List<String> userIds) {
        throw new NotImplementedException();
    }
    @Override
    public List<Location> getAllLocationsInRadius(Coordinate coordinate, Double radius, Instant from, Instant to) {
        throw new NotImplementedException();
    }
    @Override
    public List<Location> getNewestLocationsOutsideRadius(Coordinate coordinate, Double radius, List<String> userIds) {
        throw new NotImplementedException();
    }



    /* Persisten interface */


    @Override
    public void saveRoute(List locations, String username, Instant timestamp, boolean stable) throws IOException {

        try{
            persistentDB.saveRoute(locations, username, timestamp, stable);
        }
        catch(IOException e){
            throw new BusinessException();
        }

    }


    @Override
    public List<Route> getAllRoutes(List<String> usernames, Instant fromTimestamp, boolean stable) throws IOException {
        try{
            return persistentDB.getAllRoutes(usernames, fromTimestamp, stable);
        }
        catch(IOException e){
            throw new BusinessException();
        }
    }

    @Override
    public boolean updateTimestampOnRoute(Route routeToUpdate, Instant newTime) throws IOException {
        try{
            persistentDB.updateTimestampOnRoute(routeToUpdate, newTime);
            return true;
        }
        catch(IOException e){
            throw new BusinessException();
        }
    }
}
