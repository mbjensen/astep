package dk.aau.astep.appserver.restapi.resource;

import dk.aau.astep.appserver.business.service.usermanagement.AuthenticationServices;
import dk.aau.astep.appserver.business.service.usermanagement.EdgeServices;
import dk.aau.astep.appserver.business.service.usermanagement.GroupServices;
import dk.aau.astep.appserver.business.service.usermanagement.UserServices;
import dk.aau.astep.appserver.model.shared.AuthenticationToken;
import dk.aau.astep.appserver.model.shared.Group;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.appserver.restapi.api.ApiCodeMessageDescription;
import dk.aau.astep.appserver.restapi.api.ApiResourceDescription;
import dk.aau.astep.appserver.restapi.api.ApiResourceExample;
import dk.aau.astep.appserver.restapi.api.RequiredParamCheck;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resources for user management services.
 */
@Path("")
@Api(value = "user management")
public class UserResource {
    @POST
    @Path("users")
    @ApiOperation(value = "Create user", notes = "If the username is free, create user with specified username and password and default privacy settings.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response createUser(@ApiParam(value = ApiResourceDescription.USERNAME, required = true) @QueryParam("username") User user,
                               @ApiParam(value = ApiResourceDescription.PASSWORD, required = true) String password) {
        RequiredParamCheck.checkQuery(user, "username");
        RequiredParamCheck.checkBody(password);

        UserServices.createUser(user, password);

        return Response.status(200).build();
    }

    @GET
    @Path("users/{username}/token")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get token", notes = "Gets a valid token for the user. " +
                                               "If the current token is invalid, a new token is issued. " +
                                               "A new token is valid for " + UserServices.TOKEN_EXPIRATION_DAYS + " days or until another token is issued." +
                                               ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.EXPIRATION_TOKEN + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getToken(@ApiParam(value = ApiResourceDescription.USERNAME, required = true) @PathParam("username") User user,
                             @ApiParam(value = ApiResourceDescription.PASSWORD, required = true) @HeaderParam("authorization") String password) {
        RequiredParamCheck.checkHeader(password, "authorization");

        AuthenticationServices.authenticateWithPassword(user, password);

        return Response.ok(UserServices.getToken(user)).build();
    }

    @POST
    @Path("users/{username}/token")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Issue token", notes = "Invalidates previous token for a user and gets a new valid token for that user. " +
                                                 "The new token is valid for " + UserServices.TOKEN_EXPIRATION_DAYS + " days or until another token is issued." +
                                                 ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.EXPIRATION_TOKEN + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response issueToken(@ApiParam(value = ApiResourceDescription.USERNAME, required = true) @PathParam("username") User user,
                               @ApiParam(value = ApiResourceDescription.PASSWORD, required = true) @HeaderParam("authorization") String password) {
        RequiredParamCheck.checkHeader(password, "authorization");

        AuthenticationServices.authenticateWithPassword(user, password);

        return Response.ok(UserServices.issueToken(user)).build();
    }

    @PUT
    @Path("users/{username}/password")
    @ApiOperation(value = "Change password", notes = "Change a user's password.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response ChangePassword(@ApiParam(value = ApiResourceDescription.USERNAME, required = true) @PathParam("username") User user,
                                   @ApiParam(value = "The existing password of the user", required = true) @HeaderParam("authorization") String oldPassword,
                                   @ApiParam(value = "The desired password of the user", required = true) String newPassword) {
        RequiredParamCheck.checkHeader(oldPassword, "authorization");
        RequiredParamCheck.checkBody(newPassword);

        AuthenticationServices.authenticateWithPassword(user, oldPassword);

        UserServices.changePassword(user, newPassword);

        return Response.status(200).build();
    }

    @GET
    @Path("users/username")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get username", notes = "Gets the username of a user. " +
                                                  ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.USER + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getUsername(@ApiParam(value = ApiResourceDescription.TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        return Response.ok(user).build();
    }


    /** Edges **/
    public enum EdgeType {VALID, MY_REQUESTS, OTHERS_REQUESTS}

    @GET
    @Path("users/inUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get in-users", notes = "Get in-users to the authenticating user, that have an specified type of edge. " +
                                                  ApiResourceDescription.IN_USER_DESCRIPTION +
                                                  ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.USER_ARRAY + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getInEdges(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                               @ApiParam(value = ApiResourceDescription.EDGE_TYPE, required = true) @QueryParam("edgeType") EdgeType edgeType) {
        RequiredParamCheck.checkHeader(token, "authorization");

        RequiredParamCheck.checkQuery(edgeType, "edgeType");

        User user = AuthenticationServices.authenticateWithToken(token);

        User[] resultUsers;
        if (edgeType.equals(EdgeType.VALID)) {
            resultUsers = EdgeServices.getValidInUsers(user);
        } else if (edgeType.equals(EdgeType.MY_REQUESTS)) {
            resultUsers = EdgeServices.getMyRequestedInUsers(user);
        } else if (edgeType.equals(EdgeType.OTHERS_REQUESTS)) {
            resultUsers = EdgeServices.getOthersRequestedInUsers(user);
        } else {
            throw new RuntimeException("Unreachable");
        }

        return Response.ok(resultUsers).build();
    }

    @POST
    @Path("users/inUsers")
    @ApiOperation(value = "Request in-user", notes = "Request a specified user to be an in-user of the authenticating user. The edge between them will be valid once the specified user validates it. " +
                                                     ApiResourceDescription.IN_USER_DESCRIPTION)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response requestInEdge(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                  @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @QueryParam("specifiedUsername") User specifiedUser) {
        RequiredParamCheck.checkHeader(token, "authorization");

        RequiredParamCheck.checkQuery(specifiedUser, "specifiedUsername");

        User user = AuthenticationServices.authenticateWithToken(token);

        EdgeServices.requestInUser(user, specifiedUser);

        return Response.ok().build();
    }

    @PUT
    @Path("users/inUsers/{specifiedUsername}")
    @ApiOperation(value = "Validate in-user", notes = "Validate a specified user to be an in-user to the authenticating user. The edge must be requested by the specified user. " +
                                                      ApiResourceDescription.IN_USER_DESCRIPTION)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response validateInEdge(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                   @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @PathParam("specifiedUsername") User specifiedUser) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        EdgeServices.validateInUser(user, specifiedUser);

        return Response.ok().build();
    }

    @DELETE
    @Path("users/inUsers/{specifiedUsername}")
    @ApiOperation(value = "Delete in-user", notes = "Delete the specified user as a requested or valid in-user to the authenticating user. " +
                                                    ApiResourceDescription.IN_USER_DESCRIPTION)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response deleteInEdge(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                 @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @PathParam("specifiedUsername") User specifiedUser) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        EdgeServices.deleteInUser(user, specifiedUser);

        return Response.ok().build();
    }

    @GET
    @Path("users/outUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get out-users", notes = "Get out-users to the authenticating user, that have an specified type of edge. " +
                                                   ApiResourceDescription.OUT_USER_DESCRIPTION +
                                                   ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.USER_ARRAY + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getOutEdges(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                @ApiParam(value = ApiResourceDescription.EDGE_TYPE, required = true) @QueryParam("edgeType") EdgeType edgeType) {
        RequiredParamCheck.checkHeader(token, "authorization");

        RequiredParamCheck.checkQuery(edgeType, "edgeType");

        User user = AuthenticationServices.authenticateWithToken(token);

        User[] resultUsers;
        if (edgeType.equals(EdgeType.VALID)) {
            resultUsers = EdgeServices.getValidOutUsers(user);
        } else if (edgeType.equals(EdgeType.MY_REQUESTS)) {
            resultUsers = EdgeServices.getMyRequestedOutUsers(user);
        } else if (edgeType.equals(EdgeType.OTHERS_REQUESTS)) {
            resultUsers = EdgeServices.getOthersRequestedOutUsers(user);
        } else {
            throw new RuntimeException("Unreachable");
        }

        return Response.ok(resultUsers).build();
    }

    @POST
    @Path("users/outUsers")
    @ApiOperation(value = "Request out-user", notes = "Request a specified user to be an out-user of the authenticating user. The edge between them will be valid once the specified user validates it. " +
                                                      ApiResourceDescription.OUT_USER_DESCRIPTION)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response requestOutEdge(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                   @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @QueryParam("specifiedUsername") User specifiedUser) {
        RequiredParamCheck.checkHeader(token, "authorization");
        RequiredParamCheck.checkQuery(specifiedUser, "specifiedUsername");

        User user = AuthenticationServices.authenticateWithToken(token);

        EdgeServices.requestOutUser(user, specifiedUser);

        return Response.ok().build();
    }

    @PUT
    @Path("users/outUsers/{specifiedUsername}")
    @ApiOperation(value = "Validate out-user", notes = "Validate a specified user to be an out-user to the authenticating user. The edge must be requested by the specified user. " +
                                                       ApiResourceDescription.OUT_USER_DESCRIPTION)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response validateOutEdge(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                    @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @PathParam("specifiedUsername") User specifiedUser) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        EdgeServices.validateOutUser(user, specifiedUser);

        return Response.ok().build();
    }

    @DELETE
    @Path("users/outUsers/{specifiedUsername}")
    @ApiOperation(value = "Delete out-user", notes = "Delete the specified user as a requested or valid out-user to the authenticating user. " +
                                                     ApiResourceDescription.OUT_USER_DESCRIPTION)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response deleteOutEdge(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                  @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @PathParam("specifiedUsername") User specifiedUser) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        EdgeServices.deleteOutUser(user, specifiedUser);

        return Response.ok().build();
    }

    @POST
    @Path("users/doubleUsers")
    @ApiOperation(value = "Request double-user", notes = "Request a specified user to be both an in- and out-user of the authenticating user. The edges between them will be valid once the specified user validates them. " +
            ApiResourceDescription.DOUBLE_USER_DESCRIPTION)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response requestDoubleEdge(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                      @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @QueryParam("specifiedUsername") User specifiedUser) {
        RequiredParamCheck.checkHeader(token, "authorization");
        RequiredParamCheck.checkQuery(specifiedUser, "specifiedUsername");

        User user = AuthenticationServices.authenticateWithToken(token);

        EdgeServices.requestDoubleUser(user, specifiedUser);

        return Response.ok().build();
    }

    @PUT
    @Path("users/doubleUsers/{specifiedUsername}")
    @ApiOperation(value = "Validate out-user", notes = "Validate a specified user to be an double-user to the authenticating user. The double-edge must be requested by the specified user. " +
            ApiResourceDescription.DOUBLE_USER_DESCRIPTION)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
            @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response validateDoubleEdge(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                    @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @PathParam("specifiedUsername") User specifiedUser) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        EdgeServices.validateDoubleUser(user, specifiedUser);

        return Response.ok().build();
    }

    @DELETE
    @Path("users/doubleUsers/{specifiedUsername}")
    @ApiOperation(value = "Delete double-user", notes = "Delete the specified double-user as a requested or valid double-user to the authenticating user. " +
            ApiResourceDescription.DOUBLE_USER_DESCRIPTION)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
            @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
            @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
            @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
            @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
            @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response deleteDoubleEdge(@ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token,
                                  @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @PathParam("specifiedUsername") User specifiedUser) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        EdgeServices.deleteDoubleUser(user, specifiedUser);

        return Response.ok().build();
    }

    /** Groups **/

    @GET
    @Path("users/groupMemberships")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get groups that a user is member of", notes = "Get the groups that a user is member of. " +
                                                                         "Only the user is authorised for this service. " +
                                                                         ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.GROUP_ARRAY + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getGroups(@ApiParam(value = ApiResourceDescription.TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        return Response.ok(GroupServices.getGroups(user)).build();
    }

    @GET
    @Path("users/groupInvitations")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get groups that a user is invited to", notes = "Get the groups that a user is invited to. " +
                                                                                   "Only the user is authorised for this service. " +
                                                                                   ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.GROUP_ARRAY + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getInvitationGroups(@ApiParam(value = ApiResourceDescription.TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        return Response.ok(GroupServices.getInvitationGroups(user)).build();
    }

    @DELETE
    @Path("users/groupInvitations/{groupId}")
    @ApiOperation(value = "Decline group invitation", notes = "Delete a group invitation. " +
                                                              "Only the user is authorised for this service.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response declineGroupInvitation(@ApiParam(value = ApiResourceDescription.GROUP_ID, required = true) @PathParam("groupId") Group group,
                                           @ApiParam(value = ApiResourceDescription.TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");
        RequiredParamCheck.checkQuery(group, "groupId");

        User user = AuthenticationServices.authenticateWithToken(token);

        GroupServices.declineInvitation(user, group);

        return Response.ok().build();
    }

    @POST
    @Path("users/groupMemberships")
    @ApiOperation(value = "Join group", notes = "Make a user join a group. " +
                                                "The user must have a group invitation beforehand. " +
                                                "The invitation will be deleted. " +
                                                "Only the user is authorised for this service.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response joinGroup(@ApiParam(value = ApiResourceDescription.GROUP_ID, required = true) @QueryParam("groupId") Group group,
                              @ApiParam(value = ApiResourceDescription.TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");
        RequiredParamCheck.checkQuery(group, "groupId");

        User user = AuthenticationServices.authenticateWithToken(token);

        GroupServices.joinGroup(user, group);

        return Response.ok().build();
    }

    @DELETE
    @Path("users/groupMemberships/{groupId}")
    @ApiOperation(value = "Leave group", notes = "Make a user leave a group. " +
                                                 "Only the user is authorised for this service.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response leaveGroup(@ApiParam(value = ApiResourceDescription.GROUP_ID, required = true) @PathParam("groupId") Group group,
                               @ApiParam(value = ApiResourceDescription.TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");
        RequiredParamCheck.checkQuery(group, "groupId");

        User user = AuthenticationServices.authenticateWithToken(token);

        GroupServices.leaveGroup(user, group);

        return Response.ok().build();
    }

    @POST
    @Path("groups")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create group", notes = "Create a group. " +
                                                  "Add the user to the group. " +
                                                  "Set up the group to administrate itself." +
                                                  "The group is responded<br/>" +
                                                  "Only the user is authorised for this service." +
                                                  ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.GROUP + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response create(@ApiParam(value = ApiResourceDescription.TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        return Response.ok(GroupServices.createAndSetupGroup(user)).build();
    }

    @GET
    @Path("groups/{groupId}/members")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get members of a group", notes = "Get the users that are members of a specified group. " +
                                                            "Only members of the specified group and members of groups that administrate the specified group are authorised for this service." +
                                                            ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.USER_ARRAY + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getMembers(@ApiParam(value = ApiResourceDescription.SPEC_GROUP_ID, required = true) @PathParam("groupId") Group group,
                               @ApiParam(value = ApiResourceDescription.ADMIN_SPEC_GROUP_ID, required = false) @QueryParam("adminGroupId") Group adminGroup,
                               @ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        return Response.ok(GroupServices.getMembers(user, group, adminGroup)).build();
    }

    @GET
    @Path("groups/{groupId}/invited")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get invited users", notes = "Get the users that are invited to a specified group. " +
                                                       "Only members of the specified group and members of groups that administrate the specified group are authorised for this service." +
                                                       ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.USER_ARRAY + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getInvitedUsers(@ApiParam(value = ApiResourceDescription.SPEC_GROUP_ID, required = true) @PathParam("groupId") Group group,
                                    @ApiParam(value = ApiResourceDescription.ADMIN_SPEC_GROUP_ID, required = false) @QueryParam("adminGroupId") Group adminGroup,
                                    @ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        return Response.ok(GroupServices.getInvitedMembers(group, user, adminGroup)).build();
    }

    @DELETE
    @Path("groups/{groupId}/invited/{specifiedUsername}")
    @ApiOperation(value = "Revoke group invitation", notes = "Make a specified user no longer invited to a specified group. " +
                                                                               "Only members of groups that administrate the specified group are authorised for this service.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response revokeInvitation(@ApiParam(value = ApiResourceDescription.SPEC_GROUP_ID, required = true) @PathParam("groupId") Group specifiedGroup,
                                     @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @PathParam("specifiedUsername") User specifiedUser,
                                     @ApiParam(value = ApiResourceDescription.ADMIN_SPEC_GROUP_ID, required = true) @QueryParam("adminGroupId") Group adminGroup,
                                     @ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkQuery(specifiedUser, "username");
        RequiredParamCheck.checkQuery(adminGroup, "adminGroupId");
        RequiredParamCheck.checkHeader(token, "authorization");

        User adminUser = AuthenticationServices.authenticateWithToken(token);

        GroupServices.deleteInvitationAsAdmin(specifiedUser, specifiedGroup, adminUser, adminGroup);

        return Response.ok().build();
    }

    @POST
    @Path("groups/{groupId}/invited")
    @ApiOperation(value = "Invite user to group", notes = "Invite a specified user to a specified group. " +
                                                          "Only members of groups that administrate the specified group are authorised for this service.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response invite(@ApiParam(value = ApiResourceDescription.SPEC_GROUP_ID, required = true) @PathParam("groupId") Group specifiedGroup,
                           @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @QueryParam("specifiedUsername") User specifiedUser,
                           @ApiParam(value = ApiResourceDescription.ADMIN_SPEC_GROUP_ID, required = true) @QueryParam("adminGroupId") Group adminGroup,
                           @ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");
        RequiredParamCheck.checkQuery(specifiedUser, "specifiedUsername");
        RequiredParamCheck.checkQuery(adminGroup, "adminGroupId");

        User adminUser = AuthenticationServices.authenticateWithToken(token);

        GroupServices.inviteIfAdmin(specifiedUser, specifiedGroup, adminUser, adminGroup);

        return Response.ok().build();
    }

    @DELETE
    @Path("groups/{groupId}/members/{specifiedUsername}")
    @ApiOperation(value = "Remove user from group", notes = "Remove a specified user from a specified group. " +
                                                            "Only members of groups that administrate the specified group are authorised for this service.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response removeMember(@ApiParam(value = ApiResourceDescription.SPEC_GROUP_ID, required = true) @PathParam("groupId") Group specifiedGroup,
                                 @ApiParam(value = ApiResourceDescription.SPEC_USERNAME, required = true) @PathParam("specifiedUsername") User specifiedUser,
                                 @ApiParam(value = ApiResourceDescription.ADMIN_SPEC_GROUP_ID, required = true) @QueryParam("adminGroupId") Group adminGroup,
                                 @ApiParam(value = ApiResourceDescription.TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");
        RequiredParamCheck.checkQuery(adminGroup, "adminGroupId");

        User adminUser = AuthenticationServices.authenticateWithToken(token);

        GroupServices.removeUserFromGroup(specifiedGroup, adminGroup, specifiedUser, adminUser);

        return Response.ok().build();
    }

    /** Administration of groups **/

    @GET
    @Path("groups/{groupId}/administratedGroups")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get administrated groups", notes = "Get the groups that a specified group administrates. " +
                                                              "Only members of the specified group and members of groups that administrate the specified group are authorised for this service. " +
                                                              "If the user should be authorised as a member of an administrating group, then \"adminGroupId\" must be supplied." +
                                                              ApiResourceExample.EXAMPLE_RESULT_HEADER + "<p>" + ApiResourceExample.GROUP_ARRAY + "</p>")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response getAdministratedGroups(@ApiParam(value = ApiResourceDescription.SPEC_GROUP_ID, required = true) @PathParam("groupId") Group group,
                                           @ApiParam(value = ApiResourceDescription.ADMIN_SPEC_GROUP_ID, required = false) @QueryParam("adminGroupId") Group adminGroup,
                                           @ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        Group[] result = GroupServices.getAdmins(group, user, adminGroup);

        return Response.ok(result).build();
    }

    @POST
    @Path("groups/{groupId}/administratedGroups")
    @ApiOperation(value = "Add administration", notes = "Make a specified group an administrator of another group. " +
                                                        "Only users that are both member of a group that administrate the specified group and member of a group that administrate the other group are authorised for this service.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response makeAdmin(@ApiParam(value = ApiResourceDescription.SPEC_GROUP_ID, required = true) @PathParam("groupId") Group specifiedGroup,
                              @ApiParam(value = ApiResourceDescription.OTHER_GROUP_ID, required = true) @QueryParam("otherGroupId") Group otherGroup,
                              @ApiParam(value = ApiResourceDescription.ADMIN_SPEC_GROUP_ID, required = true) @QueryParam("adminSpecifiedGroupId") Group adminSpecifiedGroup,
                              @ApiParam(value = ApiResourceDescription.ADMIN_OTHER_GROUP_ID, required = true) @QueryParam("adminOtherGroupId") Group adminOtherGroup,
                              @ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkQuery(otherGroup, "otherGroupId");
        RequiredParamCheck.checkQuery(adminSpecifiedGroup, "adminGroupId");
        RequiredParamCheck.checkQuery(adminOtherGroup, "adminGroupId");
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        GroupServices.makeAdmin(specifiedGroup, otherGroup, user, adminSpecifiedGroup, adminOtherGroup);

        return Response.ok().build();
    }

    @DELETE
    @Path("groups/{groupId}/administratedGroups/{otherGroupId}")
    @ApiOperation(value = "Remove administration", notes = "Make a specified group no longer an administrator of another group. " +
                                                           "Only members of groups that administrate the specified group are authorised for this service. " +
                                                           "The other group must be administrated by at least one more group other than the specified group.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = ApiCodeMessageDescription.CODE_200),
                           @ApiResponse(code = 400, message = ApiCodeMessageDescription.CODE_400),
                           @ApiResponse(code = 401, message = ApiCodeMessageDescription.CODE_401),
                           @ApiResponse(code = 404, message = ApiCodeMessageDescription.CODE_404),
                           @ApiResponse(code = 409, message = ApiCodeMessageDescription.CODE_409),
                           @ApiResponse(code = 500, message = ApiCodeMessageDescription.CODE_500)})
    public Response removeAdmin(@ApiParam(value = ApiResourceDescription.SPEC_GROUP_ID, required = true) @PathParam("groupId") Group specifiedGroup,
                                @ApiParam(value = ApiResourceDescription.OTHER_GROUP_ID, required = true) @PathParam("otherGroupId") Group otherGroup,
                                @ApiParam(value = ApiResourceDescription.ADMIN_SPEC_GROUP_ID, required = true) @QueryParam("adminGroupId") Group adminGroup,
                                @ApiParam(value = ApiResourceDescription.AUTH_TOKEN, required = true) @HeaderParam("authorization") AuthenticationToken token) {
        RequiredParamCheck.checkQuery(otherGroup, "otherGroupId");
        RequiredParamCheck.checkQuery(adminGroup, "adminGroupId");
        RequiredParamCheck.checkHeader(token, "authorization");

        User user = AuthenticationServices.authenticateWithToken(token);

        GroupServices.removeAdministration(specifiedGroup, otherGroup, user, adminGroup);

        return Response.ok().build();
    }
}