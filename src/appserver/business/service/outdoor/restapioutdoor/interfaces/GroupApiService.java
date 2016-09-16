package dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces;

import javax.ws.rs.core.Response;
import java.util.List;

import dk.aau.astep.appserver.model.shared.Coordinate;

/**
 * Created by Morten on 07/03/2016.
 */
public interface GroupApiService {

    Response getAllGroupMembersLocationInArea(List<Coordinate> polyCoordinate, String username);

    Response getAllGroupMembersLocationInRadius(Coordinate center, double radius, String username);

    Response getAllGroupMembersLocationAndNameInArea(List<Coordinate> polyCoordinate, String groupId, String username);

    Response getAllGroupMembersLocationAndNameInRadius(Coordinate center, double radius, String groupId, String username);
}
