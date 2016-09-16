package dk.aau.astep.appserver.restapi.resource;

import dk.aau.astep.appserver.business.service.indoor.IndoorDeviceApiService;
import dk.aau.astep.appserver.business.service.usermanagement.AuthenticationServices;
import dk.aau.astep.appserver.model.shared.AuthenticationToken;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@Path("locations/indoor")
@Api(value = "indoor locations", description = "Indoor location resource")
public class IndoorLocationResource {

    private final IndoorDeviceApiService indoorDeviceApiService = new IndoorDeviceApiService();

    private final String authorizedUsername = "admin";

//    private List<Location> exampleData = new ArrayList<Location>() {{
//        add(new Location(new Coordinate(1.2, 2.3), Instant.now(), "12345", new Precision(95d, 1.2d)));
//        add(new Location(new Coordinate(4.2, 5.4), Instant.now(), "12345", new Precision(95d, 1.2d)));
//        add(new Location(new Coordinate(4.2, 4.4), Instant.now(), "12346", new Precision(95d, 1.2d)));
//        add(new Location(new Coordinate(31, 31), Instant.now(), "12347", new Precision(95d, 1.2d)));
//    }};

    private void authenticate(AuthenticationToken token) {
        if (token == null) {
            throw new WebApplicationException("Must supply valid token", 401);
        }
        String username = AuthenticationServices.authenticateWithToken(token).getUsername();
        if (!username.equals(authorizedUsername)) {
            throw new WebApplicationException("Unauthorized user: " + username, 403);
        }
    }

    //// Single Device

    @Path("devices/{deviceId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get the location of a specific device")
    public Location GetDevicePosition(
            @ApiParam(
                    value = "Access token for a specified user",
                    required = true,
                    example = "0EA8CE633D74456DF0BC1E5C73BB1946"
            ) @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(
                    value = "The id of the device",
                    required = true,
                    example = "dev1234"
            ) @PathParam("deviceId") String deviceId) {

        authenticate(token);

        return indoorDeviceApiService.getDevicePosition(deviceId);
    }


    //// Devices in area (rect/poly/circle)

    //  TODO: update examples
    // TODO: specify coordinate structure somewhere more general
    @Path("devices/rectangle")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get locations of devices in a given area specified by a rectangle, within a timeframe of 5 minutes in the past hour.")
    public List<Location> GetAllDevicesInRectangle(
            @ApiParam(
                    value = "Access token for a specified user",
                    required = true,
                    example = "21817EA4D816DC6803E9B3B06F3F80C0"
            ) @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(
                    value = "Specify the upper left corner coordinate of the rectangle\n\n" +
                            "A coordinate consist of a latitude and longitude.",
                    required = true,
                    example = "57.011791;9.990075"
            ) @QueryParam("upperLeft") Coordinate upperLeft,
            @ApiParam(
                    value = "Specify the bottom right corner coordinate of the rectangle\n\n" +
                            "A coordinate consist of a latitude and longitude.",
                    required = true,
                    example = "57.013023;9.991738"
            ) @QueryParam("bottomRight") Coordinate bottomRight,
            @ApiParam(
                    value = "Optional timestamp for receiving previous locations from that time",
                    example = "2016-05-11T13:00:21Z"
            ) @QueryParam("time") String time) {

        authenticate(token);

        if (upperLeft == null) {
            throw new WebApplicationException("must supply upper left corner of rectangle", 422);
        }

        if (bottomRight == null) {
            throw new WebApplicationException("must supply bottom right corner of rectangle", 422);
        }

        // TODO: define rectangle
        if (time != null) {
            try {
                return indoorDeviceApiService.getAllDevicesInRectangle(upperLeft, bottomRight, Instant.parse(time));
            } catch (DateTimeParseException ex) {
                throw new WebApplicationException("invalid timestamp syntax", 400);
            }
        }

        return indoorDeviceApiService.getAllDevicesInRectangle(upperLeft, bottomRight);
    }

    //  TODO: update examples
    @Path("devices/area")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get locations of devices in a given area specified by a polygon, within a timeframe of 5 minutes in the past hour.")
    public List<Location> GetAllDevicesInArea(
            @ApiParam(
                    value = "Access token for a specified user",
                    required = true,
                    example = "0EA8CE633D74456DF0BC1E5C73BB1946"
            ) @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(
                    value = "Specify at least three coordinates of a polygon\n\n" +
                            "A coordinate consist of a latitude and longitude.",
                    required = true,
                    example = "57.013023;9.990075\n" +
                            "57.013023;9.991738\n" +
                            "57.011791;9.990075"
            ) @QueryParam("coordinate") List<Coordinate> coordinates,
            @ApiParam(
                    value = "Optional timestamp for receiving previous locations from that time",
                    example = "2016-05-11T13:00:21Z"
            ) @QueryParam("time") String time) {

        authenticate(token);

        if (coordinates.size() < 3) {
            throw new WebApplicationException("must supply at least 3 coordinates of the polygon", 422);
        }

        if (time != null) {
            try {
                return indoorDeviceApiService.getAllDevicesInArea(coordinates, Instant.parse(time));
            } catch (DateTimeParseException ex) {
                throw new WebApplicationException("invalid timestamp syntax", 400);
            }
        }

        return indoorDeviceApiService.getAllDevicesInArea(coordinates);
    }

    @Path("devices/radius")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get locations of devices within a given radius, within a timeframe of 5 minutes in the past hour.")
    public List<Location> GetAllDevicesInRadius(
            @ApiParam(
                    value = "Access token for a specified user",
                    required = true,
                    example = "0EA8CE633D74456DF0BC1E5C73BB1946"
            ) @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(
                    value = "Center coordinate consisting of latitude and longitude.",
                    required = true,
                    example = "57.013023;9.991738"
            ) @QueryParam("center") Coordinate center,
            @ApiParam(
                    value = "Radius of the circle to get device locations within, specified in kilometres.",
                    required = true,
                    example = "5.34"
            ) @QueryParam("radius") Double radius,
            @ApiParam(
                    value = "Optional timestamp for receiving previous locations from that time",
                    example = "2016-05-11T13:00:21Z"
            ) @QueryParam("time") String time) {

        authenticate(token);

        if (center == null) {
            throw new WebApplicationException("must supply center coordinates of circle", 422);
        }

        if (radius == null) {
            throw new WebApplicationException("must supply radius of the circle", 422);
        }

        if (time != null) {
            try {
                return indoorDeviceApiService.getAllDevicesInCircle(center, radius, Instant.parse(time));
            } catch (DateTimeParseException ex) {
                throw new WebApplicationException("invalid timestamp syntax", 400);
            }
        }

        return indoorDeviceApiService.getAllDevicesInCircle(center, radius);
    }


    // TODO: groups have not been implemented

    @Path("groups/area")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get locations of groups in a given area specified by a polygon of coordinates", notes = "NOT IMPLEMENTED")
    public Response GetAllGroupMembersInArea(
            @ApiParam(
                    value = "Access token for a specified user",
                    required = true,
                    example = "0EA8CE633D74456DF0BC1E5C73BB1946"
            ) @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(
                    value = "Must specify at least three coordinates\n\nA coordinate consist of a latitude and longitude.",
                    example = "76.4532544;86.3245473\n84.3487124;75.9674423"
            ) @QueryParam("coordinate") List<Coordinate> coordinates) {

        return Response.status(418).build();
    }

    @Path("groups/{groupId}/area")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get locations of groups in a given area specified by a polygon of coordinates", notes = "NOT IMPLEMENTED")
    public Response GetAllGroupMembersInArea(
            @ApiParam(
                    value = "The unique id of the group",
                    required = true,
                    example = "group1"
            ) @QueryParam("groupId") String groupId,
            @ApiParam(
                    value = "Access token for a specified user",
                    required = true,
                    example = "0EA8CE633D74456DF0BC1E5C73BB1946"
            ) @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(
                    value = "Must specify at least three coordinates\n\nA coordinate consist of a latitude and longitude.",
                    example = "76.4532544;86.3245473\n84.3487124;75.9674423"
            ) @QueryParam("coordinate") List<Coordinate> coordinates) {

        return Response.status(418).build();
    }

    @Path("group/radius")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get locations of groups within a given radius", notes = "NOT IMPLEMENTED")
    public Response GetAllGroupsInRadius(
            @ApiParam(
                    value = "Access token for a specified user",
                    required = true,
                    example = "0EA8CE633D74456DF0BC1E5C73BB1946"
            ) @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(
                    value = "A center coordinate consist of a latitude and longitude.",
                    example = "43.7676757;34.1343216"
            ) @QueryParam("center") Coordinate center,
            @ApiParam(
                    value = "Radius of circle to get devices location within, specified in meters.",
                    example = "5.34"
            ) @QueryParam("radius") Double radius) {

        return Response.status(418).build();
    }

    @Path("group/{groupId}/radius")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get locations of a group within a given radius", notes = "NOT IMPLEMENTED")
    public Response GetGroupInRadius(
            @ApiParam(
                    value = "The unique id of the group",
                    required = true,
                    example = "dev1"
            ) @QueryParam("groupId") String groupId,
            @ApiParam(
                    value = "Access token for a specified user",
                    required = true,
                    example = "0EA8CE633D74456DF0BC1E5C73BB1946"
            ) @HeaderParam("authorization") AuthenticationToken token,
            @ApiParam(
                    value = "A center coordinate consist of a latitude and longitude.",
                    example = "43.7676757;34.1343216"
            ) @QueryParam("center") Coordinate center,
            @ApiParam(
                    value = "Radius of circle to get devices location within, specified in meters.",
                    example = "5.34"
            ) @QueryParam("radius") Double radius) {

        return Response.status(418).build();
    }

}