package dk.aau.astep.appserver.dataaccess.api.queries;

import com.mongodb.MongoClient;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.outdoor.Rectangle;

import java.time.Instant;
import java.util.List;

public interface HotLocationQueries {

    boolean         saveLocation                        (Location location);

    boolean         saveLocations                       (List<Location> locations);

    List<Location>  getAllLocations                     (String userId);

    Location        getNewestLocation                   (String userId);

    List<Location>  getNewestLocation                   (List<String> userIds);

    List<Location>  getNewestLocationsFromTime          (List<String> userIds, Instant from);

    List<Location>  getNewestLocationsInArea            (Polygon area);

    List<Location>  getNewestLocationsInArea            (Rectangle area, List<String> userIds);

    List<Location>  getNewestLocationsOutsideArea       (Rectangle area, List<String> userIds);

    List<Location>  getLocationsAtTimeInterval          (String userId, Instant from, Instant to);

    List<Location>  getLocationsInAreaAtTimeInterval    (Polygon area, Instant from, Instant to);

    List<Location>  getLocationsInAreaAtTimeInterval    (Rectangle area, Instant from, Instant to, String userId);

    List<Location>  getAllLocationsInRadius             (Coordinate coordinate, Double radius);

    List<Location>  getAllLocationsInRadius             (Coordinate coordinate, Double radius, List<String> userIds);

    List<Location>  getAllLocationsInRadius             (Coordinate coordinate, Double radius, Instant from, Instant to);

    List<Location>  getNewestLocationsOutsideRadius     (Coordinate coordinate, Double radius, List<String> userIds);
}

