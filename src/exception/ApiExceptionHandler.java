package dk.aau.astep.exception;

import dk.aau.astep.appserver.model.shared.Group;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.appserver.restapi.api.ApiResponseMessage;
import dk.aau.astep.appserver.restapi.api.ApiResourceExample;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.json.Json;
import javax.swing.text.html.parser.Entity;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;

public class ApiExceptionHandler extends WebApplicationException {
    public ApiExceptionHandler() {
    }
    // region status 400

    public WebApplicationException needAtLeastXLocations(int i) {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("Need at least "+i+" locations."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException needExactlyXLocations(int i) {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("Exactly " + i + " locations are needed for this request."))
                .type(MediaType.APPLICATION_JSON).build());
    }

	public WebApplicationException locationOutOfBounds() {
       return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("A location's latitude or longitude is out of bounds."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException deviceIdIsEmpty() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("deviceId is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException radiusIsEmpty() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("radius is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException radiusIsNegative() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("radius must be greater than 0."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException groupIdIsEmpty() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("groupId is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException usernamesIsEmpty() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("usernames is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException coordinateWrongFormat() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("The coordinate is wrongly formatted. Example of " +
                "a correct format: 23.54765;12.769843"))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException locationWrongFormat() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("The location is wrongly formatted. Example of " +
                "a correct format: 57.004963;9.852982;3.23;68;2016-04-07T13:14:52Z"))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException unixTimeIsEmpty() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("dateTime is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException timestampIsEmpty() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("timestamp is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException timestampWrongFormat() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("Timestamp is of wrong format"))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException unixTimeIsInTheFuture() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("unixTime cannot be set to a time in the future."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException precisionIsEmpty() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("precision is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException precisionIsOfWrongFormat() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("Precision is of wrong format"))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException precisionRadiusIsEmpty() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("precision's radius is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException precisionRadiusisNegative() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("precision's radius must be a positive value."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException precisionUnitIsEmpty() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("precision's unit is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException precisionUnitisNegative() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("precision's unit must be a positive value."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException missingQueryParameter(String parameterName) {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("The " + parameterName +
                        " query parameter was not specified. It is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException missingHeader(String headerName) {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("The " + headerName +
                        " header was not specified. It is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException missingBody() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("The request body was not specified. It is mandatory."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException tokenWrongFormat() {
        return new WebApplicationException(Response.status(401)
                                           .entity(new ApiResponseMessage("The token is wrongly formatted"))
                                           .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException groupIdWrongFormat() {
        return new WebApplicationException(Response.status(400)
                                           .entity(new ApiResponseMessage("A group id is in a wrong format. " +
                                                                          "A group id must be an integer between –2147483648 and 2147483647 (both included)."))
                                           .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException userEdgeToItself(User user) {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("The user " + user.getUsername() +
                        " is trying to make an edge to itself. This is not allowed."))
                .type(MediaType.APPLICATION_JSON).build());
    }


    public WebApplicationException distanceWeightOutOfBounds() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("distance_weight must be above 0"))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException timeWeightOutOfBounds() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("time_weight must be above 0"))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException largestAcceptableDetourLengthOutOfBounds() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("largest_acceptable_detour_length must be above 0"))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException acceptableTimeDifferenceOutOfBounds() {
        return new WebApplicationException(Response.status(400)
                .entity(new ApiResponseMessage("acceptable_time_difference must be above 0"))
                .type(MediaType.APPLICATION_JSON).build());
    }

    // endregion

    // region status 401
    public WebApplicationException incorrectCredentials(String credentials) {
        return new WebApplicationException(Response.status(401)
                .entity(new ApiResponseMessage("The " + credentials + " was incorrect."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException tokenExpired() {
        return new WebApplicationException(Response.status(401)
                .entity(new ApiResponseMessage("The token has expired."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException noGroupAdministration(Group noAdmin, Group otherGroup) {
        return new WebApplicationException(Response.status(401)
                .entity(new ApiResponseMessage("The group with id " + noAdmin.getId() +
                        " does not administrate the group with id " +
                        otherGroup.getId()))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException noAdminMembership(Group adminGroup, User noMember) {
        return new WebApplicationException(Response.status(401)
                                           .entity(new ApiResponseMessage("The " + noMember.getUsername() + " user is not member of the the group with id " + adminGroup.getId() + "."))
                                           .type(MediaType.APPLICATION_JSON).build());
    }
    // endregion



    // region status 404

    public WebApplicationException userNotFound(User user) {
        return new WebApplicationException(Response.status(404)
                .entity(new ApiResponseMessage("The user, " + user.getUsername() + ", does not exist."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException invitationNotFound(User user, Group group) {
        return new WebApplicationException(Response.status(404)
                                           .entity(new ApiResponseMessage("The " + user.getUsername() + " user does not have an invitation for the group " + group.getId() + "."))
                                           .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException requestNotFound(User user, User otherUser) {
        return new WebApplicationException(Response.status(404)
                                           .entity(new ApiResponseMessage("An edge request between user " + user.getUsername() + " and user " + otherUser.getUsername() + " does not exist."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException noAdministration(Group administrator, Group administrated) {
        return new WebApplicationException(Response.status(404)
                                           .entity(new ApiResponseMessage("The group with id " + administrator.getId() + " does not administrate the group with id " + administrated.getId() + "."))
                                           .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException groupNotFound(Group group) {
        return new WebApplicationException(Response.status(404)
                                           .entity(new ApiResponseMessage("The group with id " + group.getId() + " was not found."))
                                           .type(MediaType.APPLICATION_JSON).build());
    }

    // endregion


    // region status 409

    public WebApplicationException userAlreadyExist(String username) {
        return new WebApplicationException(Response.status(409)
                .entity(new ApiResponseMessage("The user, " + username + ", already exists."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException edgeRequestAlreadyExist(User user, User otherUser) {
        return new WebApplicationException(Response.status(409)
                .entity(new ApiResponseMessage("An edge request between the user, " + user.getUsername() +
                        ", and the user, " + otherUser.getUsername() +
                        ", made by the user, " + user.getUsername() +
                        ", already exists."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException otherEdgeRequestAlreadyExist(User user, User otherUser) {
        return new WebApplicationException(Response.status(409)
                .entity(new ApiResponseMessage("An edge request between the user, " + user.getUsername() +
                        ", and the user, " + otherUser.getUsername() +
                        ", made by the user, " + otherUser.getUsername() +
                        ", already exists."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException edgeAlreadyExist(User user, User otherUser) {
        return new WebApplicationException(Response.status(409)
                .entity(new ApiResponseMessage("An edge between the user, " + user.getUsername() +
                        ", and the user, " + otherUser.getUsername() +
                        ", already exists."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException noOtherAdministrator(Group specifiedGroup, Group otherGroup) {
        return new WebApplicationException(Response.status(409)
                                           .entity(new ApiResponseMessage("The group with id " + otherGroup.getId() + " is only administrated by the group with id  " + specifiedGroup.getId() + "."))
                                           .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException alreadyAdmin(Group specifiedGroup, Group otherGroup) {
        return new WebApplicationException(Response.status(409)
                                           .entity(new ApiResponseMessage("The group with id " + otherGroup.getId() + " does already administrate the group with id  " + specifiedGroup.getId() + "."))
                                           .type(MediaType.APPLICATION_JSON).build());
    }

    public WebApplicationException inviteAlreadyExist(User user, Group group) {
        return new WebApplicationException(Response.status(409)
                                           .entity(new ApiResponseMessage("The " + user.getUsername() + " is already invited to the group with id " + group.getId() + "."))
                                           .type(MediaType.APPLICATION_JSON).build());
    }

    // endregion

    // region status 500

    public WebApplicationException internalServerError() {
        return new WebApplicationException(Response.status(500)
                .entity(new ApiResponseMessage("Internal Server Error."))
                .type(MediaType.APPLICATION_JSON).build());
    }

    // endregion
}
