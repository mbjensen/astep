package dk.aau.astep.appserver.restapi.resource;

//import com.sun.xml.internal.rngom.digested.DDataPattern;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.implementation.OutdoorUserApiService;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.implementation.OutdoorHistoryApiService;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces.UserApiService;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces.GroupApiService;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces.HistoryApiService;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.Outdoordb;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.TempDBWrapper;
import dk.aau.astep.appserver.business.service.usermanagement.AuthenticationServices;
import dk.aau.astep.appserver.model.outdoor.Route;
import dk.aau.astep.appserver.model.outdoor.RouteMatch;
import dk.aau.astep.appserver.model.shared.*;
import dk.aau.astep.appserver.restapi.api.*;

import javax.ws.rs.GET;
import javax.ws.rs.*;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import java.time.Instant;
import java.util.List;

import io.swagger.annotations.*;
import javax.ws.rs.core.Response;


/**
 * Root resource (exposed at "myresource" path)
 */

@Path("locations/outdoor")
@Api(value = "outdoor location based-services", description ="This is the outdoor location based-services resource")
public class OutdoorLocationResource {
    private final TempDBWrapper db = new TempDBWrapper();
    private final UserApiService outdoorUserDelegate =  new OutdoorUserApiService( db);
    private final HistoryApiService outdoorHistoryDelegate = new OutdoorHistoryApiService(db);
    //private final GroupApiService outdoorGroupDelegate = new OutdoorGroupApiService();

    //private final UserManagementService outdoorUserManagementDelegate = new UserManagementService();
    private final AuthenticationServices outdoorAuthenticationServices = new AuthenticationServices();

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a application/json response.
     */

    @GET
    @Path("users")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_FRIENDS, notes = ApiResourceDescription.USER_FRIENDS_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)
    })
    public Response getAllTheUsersFriendsNameAndLocation (
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(false);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        return outdoorUserDelegate.getAUsersFriendNameAndLocation(usernames);
    }

    //TODO: Make description
    @Path("users/{usernames}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_FRIEND, notes = ApiResourceDescription.USER_FRIEND_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)
    })
    public Response GetAFriendsNameAndLocation (
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.USERS, example = ApiResourceExample.USERS, required = true)
            @PathParam("usernames") String usernamesString,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(false);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        // usernames now contains only the elements which are also contained in List from paramConvert.
        usernames.retainAll(ParamConvert.usernames(usernamesString));

        return outdoorUserDelegate.getAUsersFriendNameAndLocation(usernames);
    }

    @Path("users/timestamp")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_FRIENDS_TIMESTAMP, notes = ApiResourceDescription.USER_FRIEND_TIMESTAMP_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)
    })
    public Response GetFriendsNameAndLocationTimestamp (
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value = ApiResourceDescription.ISO_TIME, example = ApiResourceExample.TIMESTAMP, required = true)
            @QueryParam("timestamp") String timestampString,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(false);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        Instant timestamp = ParamConvert.timeStringToInstant(timestampString);

        return outdoorUserDelegate.getAUsersFriendNameAndLocationTimestamp(usernames, timestamp);
    }

    @Path("users/{usernames}/timestamp")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_FRIENDS_TIMESTAMP, notes = ApiResourceDescription.USER_FRIEND_TIMESTAMP_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)
    })
    public Response GetAFriendsNameAndLocationTimestamp (
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value = ApiResourceDescription.USERS, example = ApiResourceExample.USERS, required = true)
            @PathParam("usernames") String usernamesString,
            @ApiParam(value = ApiResourceDescription.ISO_TIME, example = ApiResourceExample.TIMESTAMP, required = true)
            @QueryParam("timestamp") String timestampString,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(false);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        // usernames now contains only the elements which are also contained in List from paramConvert.
        usernames.retainAll(ParamConvert.usernames(usernamesString));

        Instant timestamp = ParamConvert.timeStringToInstant(timestampString);

        return outdoorUserDelegate.getAUsersFriendNameAndLocationTimestamp(usernames, timestamp);
    }

    @Path("users/area")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_AREA, notes = ApiResourceDescription.USER_AREA_NOTE)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
        @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
        @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
        @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)
    })
    public Response GetAllUsersInArea(
        @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
        @HeaderParam("authorization") AuthenticationToken token,
        @ApiParam(value= ApiResourceDescription.POLY_COORDINATE, example = ApiResourceExample.POLY_COORDINATE, required = true)
        @QueryParam("poly_coordinate") List<Coordinate> polyCoordinate,
        @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
        @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(true);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        ParamCheck.checkCoordinates(polyCoordinate);
        return outdoorUserDelegate.getUsersInArea(polyCoordinate, usernames);
    }

    @Path("users/{usernames}/area")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_ID_AREA, notes = ApiResourceDescription.USER_ID_AREA_NOTE, response = Location.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetUserInArea(
            @ApiParam(value= ApiResourceDescription.USERS, example = ApiResourceExample.USERS, required = true)
            @PathParam("usernames") String usernamesString,
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.POLY_COORDINATE, example = ApiResourceExample.POLY_COORDINATE, required = true)
            @QueryParam("poly_coordinate") List<Coordinate> polyCoordinate,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(true);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        // usernames now contains only the elements which are also contained in List from paramConvert.
        usernames.retainAll(ParamConvert.usernames(usernamesString));

        ParamCheck.checkCoordinates(polyCoordinate);
        return outdoorUserDelegate.getUsersInArea(polyCoordinate, usernames);
    }

    @Path("users/radius")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_RADIUS, notes = ApiResourceDescription.USER_RADIUS_NOTE, response = Location.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetAllUsersInRadius(
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.CENTER, example = ApiResourceExample.COORDINATE, required = true)
            @QueryParam("center") Coordinate center,
            @ApiParam(value= ApiResourceDescription.RADIUS, example = ApiResourceExample.DOUBLE, required = true)
            @QueryParam("radius") Double radius,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(true);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        ParamCheck.checkRadius(radius);
        ParamCheck.checkCoordinate(center);
        return outdoorUserDelegate.getUsersInRadius(center, radius, usernames);
    }

    @Path("users/{usernames}/outsidearea")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_ID_OUTSIDE_AREA, notes = ApiResourceDescription.USER_ID_OUTSIDE_AREA_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetUsersOutsideArea(
            @ApiParam(value= ApiResourceDescription.USERS, example = ApiResourceExample.USERS, required = true)
            @PathParam("usernames") String usernamesString,
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.POLY_COORDINATE, example = ApiResourceExample.POLY_COORDINATE, required = true)
            @QueryParam("poly_coordinate") List<Coordinate> polyCoordinate,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(true);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        // usernames now contains only the elements which are also contained in List from paramConvert.
        usernames.retainAll(ParamConvert.usernames(usernamesString));

        ParamCheck.checkCoordinates(polyCoordinate);
        return outdoorUserDelegate.getUsersOutsideArea(polyCoordinate, usernames);
    }

    @Path("users/{usernames}/outsideradius")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_ID_OUTSIDE_RADIUS, notes = ApiResourceDescription.USER_ID_OUTSIDE_RADIUS_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetUsersOutsideRadius(
            @ApiParam(value= ApiResourceDescription.USERS, example = ApiResourceExample.USERS, required = true)
            @PathParam("usernames") String usernamesString,
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.CENTER, example = ApiResourceExample.COORDINATE, required = true)
            @QueryParam("center") Coordinate center,
            @ApiParam(value= ApiResourceDescription.RADIUS, example = ApiResourceExample.DOUBLE, required = true)
            @QueryParam("radius") Double radius,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(false);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        // usernames now contains only the elements which are also contained in List from paramConvert.
        usernames.retainAll(ParamConvert.usernames(usernamesString));

        ParamCheck.checkRadius(radius);
        ParamCheck.checkCoordinate(center);
        return outdoorUserDelegate.getUsersOutsideRadius(center, radius, usernames);
    }

    //TODO: Update Api.Resource.Description
    @Path("users/{usernames}/outsideaverage")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_ID_OUTSIDE_AVERAGE, notes = ApiResourceDescription.USER_ID_OUTSIDE_AVERAGE_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetAllUsersOutsideAverage(
            @ApiParam(value= ApiResourceDescription.USERS, example = ApiResourceExample.USERS, required = true)
            @PathParam("usernames") String usernamesString,
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(true);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        // usernames now contains only the elements which are also contained in List from paramConvert.
        usernames.retainAll(ParamConvert.usernames(usernamesString));

        return outdoorUserDelegate.userAvgAwayFromOthers(usernames);
    }

    @Path("users/{usernames}/radius")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.USER_ID_RADIUS, notes = ApiResourceDescription.USER_ID_RADIUS_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetUsersInRadius(
            @ApiParam(value= ApiResourceDescription.USERS, example = ApiResourceExample.USERS, required = true)
            @PathParam("usernames") String usernamesString,
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.CENTER, example = ApiResourceExample.COORDINATE, required = true)
            @QueryParam("center") Coordinate center,
            @ApiParam(value= ApiResourceDescription.RADIUS, example = ApiResourceExample.DOUBLE, required = true)
            @QueryParam("radius") Double radius,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        List<User> users = outdoorAuthenticationServices.authenticateWithToken(token).fetchAccessibleUsers(false);
        List<String> usernames = ParamConvert.listOfUsersToListOfUsernames(users);

        // usernames now contains only the elements which are also contained in List from paramConvert.
        usernames.retainAll(ParamConvert.usernames(usernamesString));

        ParamCheck.checkRadius(radius);
        return outdoorUserDelegate.getUsersInRadius(center, radius, usernames);
    }

    /*** Incomment this section when User Management have implemented groups
     *   And implement the functionality for groups. ***/
    /*
    @Path("{username}/groups/area")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.GROUP_AREA, notes = ApiResourceDescription.GROUP_AREA_NOTE)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
        @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
        @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetAllGroupMembersLocationInArea(
            @ApiParam(value= ApiResourceDescription.USERNAME, example = ApiResourceExample.USERNAME, required = true)
            @PathParam("username") String username,
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample. TOKEN, required = true)
            @QueryParam("token") String token,
            @ApiParam(value= ApiResourceDescription.POLY_COORDINATE, example = ApiResourceExample.POLY_COORDINATE, required = true)
            @QueryParam("polyCoordinate") List<Coordinate> polyCoordinate) {

        // Call User Management to get permissions of the username and token
        outdoorUserManagementDelegate.authenticateWithToken(username, token);

        ParamCheck.checkIfUsernameIsNull(username);
        ParamCheck.checkCoordinates(polyCoordinate);
        return outdoorGroupDelegate.getAllGroupMembersLocationInArea(polyCoordinate, username);
    }

    @Path("{username}/groups/{groupId}/area")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.GROUP_ID_AREA, notes = ApiResourceDescription.GROUP_ID_AREA_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)
    })
    public Response GetAllGroupMembersLocationInArea(
            @ApiParam(value= ApiResourceDescription.USERNAME, example = ApiResourceExample.USERNAME, required = true)
            @PathParam("username") String username,
            @ApiParam(value= ApiResourceDescription.GROUP_ID, example = ApiResourceExample.GROUP_ID, required = true)
            @PathParam("groupId") String groupId,
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @QueryParam("token") String token,
            @ApiParam(value= ApiResourceDescription.POLY_COORDINATE, example = ApiResourceExample.POLY_COORDINATE, required = true)
            @QueryParam("polyCoordinate") List<Coordinate> polyCoordinate) {

        // Call User Management to get permissions of the username and token
        outdoorUserManagementDelegate.authenticateWithToken(username, token);

        try {
            if (groupId == null) {
                throw new ApiExceptionHandler().groupIdIsEmpty();
            } else if (polyCoordinate.size() < 3) {
                throw new ApiExceptionHandler().needAtLeastXLocations(3);
            }
            return outdoorGroupDelegate.getAllGroupMembersLocationAndNameInArea(polyCoordinate, groupId, username);

        } catch(WebApplicationException exception) {
            System.out.println("Error: " + exception);
            return null;
        }
    }

    @Path("{username}/groups/radius")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.GROUP_RADIUS, notes = ApiResourceDescription.GROUP_RADIUS_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetAllGroupMembersLocationInRadius(
            @ApiParam(value= ApiResourceDescription.USERNAME, example = ApiResourceExample.USERNAME, required = true)
            @PathParam("username") String username,
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @QueryParam("token") String token,
            @ApiParam(value= ApiResourceDescription.CENTER, example = ApiResourceExample.COORDINATE, required = true)
            @QueryParam("center") Coordinate center,
            @ApiParam(value= ApiResourceDescription.RADIUS, example = ApiResourceExample.DOUBLE, required = true)
            @QueryParam("radius") Double radius) {

        // Call User Management to get permissions of the username and token
        outdoorUserManagementDelegate.authenticateWithToken(username, token);

        try {
            if (radius == null) {
               throw new ApiExceptionHandler().radiusIsEmpty();
            }
            return outdoorGroupDelegate.getAllGroupMembersLocationInRadius(center, radius, username);

        } catch(WebApplicationException exception) {
            System.out.println("Error: " + exception);
            return null;
        }
    }

    @Path("{username}/groups/{groudId}/radius")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.GROUP_ID_RADIUS, notes = ApiResourceDescription.GROUP_ID_RADIUS_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetAllGroupMembersLocationInRadius(
            @ApiParam(value= ApiResourceDescription.USERNAME, example = ApiResourceExample.USERNAME, required = true)
            @PathParam("username") String username,
            @ApiParam(value= ApiResourceDescription.GROUP_ID, example = ApiResourceExample.GROUP_ID, required = true)
            @PathParam("groudId") String groupId,
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @QueryParam("token") String token,
            @ApiParam(value= ApiResourceDescription.CENTER, example = ApiResourceExample.COORDINATE, required = true)
            @QueryParam("center") Coordinate center,
            @ApiParam(value= ApiResourceDescription.RADIUS, example = ApiResourceExample.DOUBLE, required = true)
            @QueryParam("radius") Double radius) {

        // Call User Management to get permissions of the username and token
        outdoorUserManagementDelegate.authenticateWithToken(username, token);

        try {
            if (groupId == null ) {
                throw new ApiExceptionHandler().groupIdIsEmpty();
            } else if (radius != null) {
               throw new ApiExceptionHandler().radiusIsEmpty();
            }
            return outdoorGroupDelegate.getAllGroupMembersLocationAndNameInRadius(center, radius, groupId, username);

        } catch(WebApplicationException exception) {
            System.out.println("Error: " + exception);
            return null;
        }
    }
    */

    @Path("histories/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.HISTORIES)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetEntityHistoriy(
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.ISO_TIME, example = ApiResourceExample.TIMESTAMP, required = true)
            @QueryParam("periode_begin") String periodBeginString,
            @ApiParam(value= ApiResourceDescription.ISO_TIME, example = ApiResourceExample.TIMESTAMP2, required = true)
            @QueryParam("periode_end") String periodEndString,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        String username = outdoorAuthenticationServices.authenticateWithToken(token).getUsername();

        Instant periodBeginInstant=ParamConvert.timeStringToInstant(periodBeginString);
        Instant periodEndInstant=ParamConvert.timeStringToInstant(periodEndString);

        ParamCheck.checkTime(periodBeginInstant);
        ParamCheck.checkTime(periodEndInstant);
        ParamCheck.checkIfUsernameIsNull(username);

        return outdoorHistoryDelegate.getAllHistoryDataInTimePeriode(periodBeginInstant, periodEndInstant, username);
    }

    @Path("histories/area")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.HISTORIES_AREA)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetAllEntityHistoriyInArea(
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.POLY_COORDINATE, example = ApiResourceExample.POLY_COORDINATE, required = true)
            @QueryParam("poly_coordinates") List<Coordinate> polyCoordinate,
            @ApiParam(value= ApiResourceDescription.ISO_TIME, example = ApiResourceExample.TIMESTAMP, required = true)
            @QueryParam("periode_begin") String periodBeginString,
            @ApiParam(value= ApiResourceDescription.ISO_TIME, example = ApiResourceExample.TIMESTAMP2, required = true)
            @QueryParam("periode_end") String periodEndString,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        String username = outdoorAuthenticationServices.authenticateWithToken(token).getUsername();

        Instant periodBeginInstant=ParamConvert.timeStringToInstant(periodBeginString);
        Instant periodEndInstant=ParamConvert.timeStringToInstant(periodEndString);

        ParamCheck.checkTime(periodBeginInstant);
        ParamCheck.checkTime(periodEndInstant);
        ParamCheck.checkCoordinates(polyCoordinate);
        ParamCheck.checkIfUsernameIsNull(username);

        return outdoorHistoryDelegate.getAllHistoryDataInAreaInTimePeriod(polyCoordinate, periodBeginInstant, periodEndInstant, username);
    }

    @Path("histories/radius")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.HISTORIES_RADIUS)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response GetAllEntityHistoriyInRadius(
            @ApiParam(value= ApiResourceDescription.TOKEN, example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.CENTER, example = ApiResourceExample.COORDINATE, required = true)
            @QueryParam("center") Coordinate center,
            @ApiParam(value= ApiResourceDescription.RADIUS, example = ApiResourceExample.DOUBLE, required = true)
            @QueryParam("radius") Double radius,
            @ApiParam(value= ApiResourceDescription.ISO_TIME, example = ApiResourceExample.TIMESTAMP, required = true)
            @QueryParam("periode_begin") String periodBeginString,
            @ApiParam(value= ApiResourceDescription.ISO_TIME, example = ApiResourceExample.TIMESTAMP2, required = true)
            @QueryParam("periode_end") String periodEndString,
            @ApiParam(value= ApiResourceDescription.SELECT_BODY, example = ApiResourceExample.SELECT_BODY, required = false)
            @QueryParam("select") String select) {

        // Call User Management to get permissions of the token
        String username = outdoorAuthenticationServices.authenticateWithToken(token).getUsername();

        Instant periodBeginInstant=ParamConvert.timeStringToInstant(periodBeginString);
        Instant periodEndInstant=ParamConvert.timeStringToInstant(periodEndString);

        ParamCheck.checkTime(periodBeginInstant);
        ParamCheck.checkTime(periodEndInstant);
        ParamCheck.checkCoordinate(center);
        ParamCheck.checkRadius(radius);

        return outdoorHistoryDelegate.getAllHistoryDataInRadiusInTimePeriod(center, radius, periodBeginInstant, periodEndInstant, username);
    }

    @Path("locations")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = ApiResourceDescription.POST_LOCATION , notes = ApiResourceDescription.POST_LOCATION_NOTE)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = ApiCodeMessageDescription.CODE_201, response = Location.class),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response PostLocationData(
            @ApiParam(value= ApiResourceDescription.TOKEN,  example = ApiResourceExample.TOKEN, required = true)
            @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(value= ApiResourceDescription.COORDINATE, example = ApiResourceExample.COORDINATE, required = true)
            @QueryParam("coordinate") Coordinate coordinate,
            @ApiParam(value= ApiResourceDescription.PRECISION, example = ApiResourceExample.PRECISION, required = true)
            @QueryParam("precision") Precision precision,
            @ApiParam(value= ApiResourceDescription.ISO_TIME, example = ApiResourceExample.TIMESTAMP, required = true)
            @QueryParam("timestamp") String timestamp) {

        // Call User Management to get permissions of the token
        String username = outdoorAuthenticationServices.authenticateWithToken(token).getUsername();
        System.out.println(username);

        ParamCheck.checkCoordinate(coordinate);
        ParamCheck.checkPrecision(precision);

        Instant timestampInstant=ParamConvert.timeStringToInstant(timestamp);

        return outdoorUserDelegate.postLocation(coordinate, username, precision, timestampInstant);
    }
}
