package dk.aau.astep.appserver.restapi.resource;

import com.mysql.fabric.xmlrpc.base.Param;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.implementation.OutdoorHistoryApiService;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces.HistoryApiService;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.Outdoordb;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.TempDBWrapper;
import dk.aau.astep.appserver.business.service.usermanagement.AuthenticationServices;
import dk.aau.astep.appserver.model.outdoor.Route;
import dk.aau.astep.appserver.model.outdoor.RouteMatch;
import dk.aau.astep.appserver.model.shared.AuthenticationToken;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.appserver.restapi.api.*;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Morten on 06/05/2016.
 */

@Path("routes/outdoor")
@Api(value = "outdoor location based-services", description ="This is the outdoor location based-services resource")
public class OutdoorRouteResource {
    private final HistoryApiService outdoorHistoryDelegate = new OutdoorHistoryApiService(new TempDBWrapper());
    //private final UserManagementService outdoorUserManagementDelegate = new UserManagementService();

    private final AuthenticationServices outdoorAuthenticationServices = new AuthenticationServices();

    @Path("routes")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.POST_ROUTES, notes = ApiResourceDescription.POST_ROUTES_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = ApiCodeMessageDescription.CODE_201, response = Route.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response postRoute(
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            //TODO remember to check description and example for ROUTE_LOCATION
            @ApiParam(value= ApiResourceDescription.ROUTE_LOCATIONS, example = ApiResourceExample.ROUTE_LOCATION , required = true)
            @QueryParam("route") List<String> locationsString,
            @ApiParam(value= ApiResourceDescription.DISTANCE_WEIGHT_NOTE, example = ApiResourceExample.DISTANCE_WEIGHT, required = true)
            @QueryParam("distance_weight") double distanceWeight,
            @ApiParam(value= ApiResourceDescription.TIME_WEIGHT_NOTE, example = ApiResourceExample.TIME_WEIGHT, required = true)
            @QueryParam("time_weight") double timeWeight,
            @ApiParam(value= ApiResourceDescription.LARGEST_ACCEPTABLE_DETOUR_LENGHT_NOTE, example = ApiResourceExample.LARGEST_ACCEPTABLE_DETOUR_LENGHT, required = true)
            @QueryParam("largest_acceptable_detour_length") double largestAcceptableDetourLength,
            @ApiParam(value= ApiResourceDescription.ACCEPTABLE_TIME_DIFFERENCE_NOTE, example = ApiResourceExample.ACCEPTABLE_TIME_DIFFERENCE, required = true)
            @QueryParam("acceptable_time_difference") double acceptableTimeDifference) {


        // Call User Management to get permissions of the token
        String username = outdoorAuthenticationServices.authenticateWithToken(token).getUsername();
        List<Location> locations = ParamConvert.locations(locationsString, username);

        ParamCheck.checkDistanceWeight(distanceWeight);
        ParamCheck.checkTimeWeight(timeWeight);
        ParamCheck.checkLargestAcceptableDetourLength(largestAcceptableDetourLength);
        ParamCheck.checkacceptableTimeDifference(acceptableTimeDifference);

        return outdoorHistoryDelegate.postRoute(locations, username, distanceWeight, timeWeight, largestAcceptableDetourLength, acceptableTimeDifference);
    }

    @Path("routes/match")
    @GET
    @ApiOperation(value = ApiResourceDescription.ROUTES_MATCH, notes = ApiResourceDescription.ROUTES_MATCH_NOTE)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = RouteMatch.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getMatchingRoutes(
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.DISTANCE_WEIGHT_NOTE, example = ApiResourceExample.DISTANCE_WEIGHT, required = true)
            @QueryParam("distance_weight") double distanceWeight,
            @ApiParam(value= ApiResourceDescription.TIME_WEIGHT_NOTE, example = ApiResourceExample.TIME_WEIGHT, required = true)
            @QueryParam("time_weight") double timeWeight,
            @ApiParam(value= ApiResourceDescription.LARGEST_ACCEPTABLE_DETOUR_LENGHT_NOTE, example = ApiResourceExample.LARGEST_ACCEPTABLE_DETOUR_LENGHT, required = true)
            @QueryParam("largest_acceptable_detour_length") double largestAcceptableDetourLength,
            @ApiParam(value= ApiResourceDescription.ACCEPTABLE_TIME_DIFFERENCE_NOTE, example = ApiResourceExample.ACCEPTABLE_TIME_DIFFERENCE, required = true)
            @QueryParam("acceptable_time_difference") double acceptableTimeDifference,
            @ApiParam(value= ApiResourceDescription.SELECT_DATA, example = ApiResourceExample.SELECT_DATA, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(true);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        // Validate parameters.
        ParamCheck.checkIfUsernamesIsNull(usernames);
        ParamCheck.checkDistanceWeight(distanceWeight);
        ParamCheck.checkTimeWeight(timeWeight);
        ParamCheck.checkLargestAcceptableDetourLength(largestAcceptableDetourLength);
        ParamCheck.checkacceptableTimeDifference(acceptableTimeDifference);

        //TODO: Validate the 4 new parameters

        return outdoorHistoryDelegate.matchRoute(usernames, distanceWeight, timeWeight, largestAcceptableDetourLength, acceptableTimeDifference);
    }
}
