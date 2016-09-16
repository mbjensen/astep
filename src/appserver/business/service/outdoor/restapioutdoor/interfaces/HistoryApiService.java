package dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import io.swagger.models.auth.In;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

/**
 * Created by Morten on 07/03/2016.
 */
public interface HistoryApiService {

    //TODO These shall be deleted if there is none app using them
    //Response getAllHistoryDataAtTime(String time, String userName);

    //Response getAllHistoryDataInAreaAtTime(List<Coordinate> polyCoordinate, String time, String userName);

    Response getAllHistoryDataInTimePeriode(Instant periodBegin, Instant periodEnd, String userName);

    Response getAllHistoryDataInAreaInTimePeriod(List<Coordinate> polyCoordinate, Instant periodBegin, Instant periodEnd, String userName);

    Response getAllHistoryDataInRadiusInTimePeriod(Coordinate center, double radius, Instant periodBegin, Instant periodEnd, String userName);

    Response postRoute(List<Location> routeLocation, String userName, double distanceWeight, double timeWeight, double largestAcceptableDetourLength, double acceptableTimeDifference);

    Response matchRoute(List<String> userName, double distanceWeight, double timeWeight, double largestAcceptableDetourLength, double acceptableTimeDifference);
}
