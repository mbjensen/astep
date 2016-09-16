package dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Precision;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.time.Instant;

/**
 * Created by Morten on 07/03/2016.
 */
public interface UserApiService {

    Response getUsersInArea(List<Coordinate> polyCoordinates, List<String> usernames);

    Response getUsersInRadius(Coordinate center, Double radius, List<String> usernames);

    Response getUsersOutsideArea(List<Coordinate> polyCoordinates, List<String> usernames);

    Response getUsersOutsideRadius(Coordinate center, Double radius, List<String> usernames);

    Response postLocation(Coordinate coordinate, String userName, Precision precision, Instant timestamp);

    Response userAvgAwayFromOthers(List<String> usernames);

    Response getAUsersFriendNameAndLocation(List<String> usernames);

    Response getAUsersFriendNameAndLocationTimestamp(List<String> usernames, Instant timestamp);
}
