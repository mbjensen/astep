package dk.aau.astep.appserver.restapi.api;

/**
 * Used for all constant attribute description which occur multiple times, for easier changes.
 */
public class ApiResourceDescription {
    /** Users **/

    public static final String USERNAME = "The username of the user.";

    public static final String SPEC_USERNAME = "The username of the specified user.";

    public static final String AUTH_USERNAME = "The username of the authenticating user.";

    public static final String ADMIN_AUTH_USERNAME = "The username of the authenticating user. The user must be member of the administrating group.";

    public static final String PASSWORD = "The password of the user.";

    public static final String TOKEN = "The access token for the user.";

    public static final String AUTH_TOKEN = "The access token for the authenticating user.";

    public static final String TIMESTAMP = "Only get the new locations after the timestamp";


    /** Groups **/

    public static final String GROUP_ID = "The id of the group.";

    public static final String SPEC_GROUP_ID = "The id of the specified group.";

    public static final String OTHER_GROUP_ID = "The id of the other group.";

    public static final String ADMIN_SPEC_GROUP_ID = "The id of a group that administrate the specified group.";

    public static final String ADMIN_OTHER_GROUP_ID = "The id of a group that administrate the other group.";

    /** Edges **/

    public static final String IN_USER_DESCRIPTION = "An in-user signifies an edge between two users, that allows a one-way sharing of information.\n" +
                                                     "If user A is in-user to user B, user A shares its information with user B.";

    public static final String OUT_USER_DESCRIPTION = "An out-user signifies an edge between two users, that allows a one-way sharing of information.\n" +
                                                      "If user A is out-user to user B, user B shares its information with user A.";

    public static final String DOUBLE_USER_DESCRIPTION = "An double-user is an encapsulation of two edges. These allow two-way sharing of information.\n" +
            "If user A has a double-edge to user B, user B shares information with and takes information from user A.";


    public static final String EDGE_TYPE = "The type of edge to query for. This can be " +
                                           "\"VALID\" for valid edges (i.e. edges requested or validated by both users), " +
                                           "\"MY_REQUESTS\" for invalid edges that are requested by the authenticating user, or " +
                                           "\"OTHERS_REQUESTS\" for invalid edges that are not requested by the authenticating user.";


    /** Mix **/

    public static final String ROUTE_LOCATIONS = "A route must consist of at least two locations. \n\n" +
                                                 "A location consist of: \n\n 1) A coordinate with a latitude and longitude " +
                                                 " \n\n 2) The precision of the coordinate specified by a floating point." +
                                                 "\n\n 3) A timestamp specified in unix time in milliseconds.  ";

    public static final String POLY_COORDINATE = "PolyCoordinates must contain at least three coordinates, " +
                                                "each coordinate correspond to a corner of the polygon." +
                                                "\n\nThe coordinates should be inserted in counterclockwise." +
                                                "\n\nOne coordinate is of the form: latitude;longitude in degrees.";

    public static final String CENTER = "A center coordinate consist of a latitude and longitude.";

    public static final String RADIUS = "Radius of the circle area, specified in kilometers.";

    public static final String COORDINATE = "A coordinate is of the form: latitude;longitude in degrees.";

    public static final String PRECISION = "The precision of the location. The precision is the probability the location " +
            "is inside a cirle of a certain size. The first parameter is the unit of probability and the second is the " +
            "radius of the cirle.";

    public static final String USERS = "One or multiple username('s) of user('s) of interest";

    public static final String UNIX_TIME = "Milliseconds in Unix Time";

    public static final String ISO_TIME = "ISO time on the format: 2009-10-26T04:47:09Z";

    public static final String HISTORIES = "Get user past locations";

    public static final String HISTORIES_AREA = "Get user past locations area";

    public static final String HISTORIES_RADIUS = "Get user past locations radius";

    public static final String POST_LOCATION = "Post user current location";

    public static final String POST_ROUTES = "Post user current route";

    public static final String USER_FRIEND = "Get locations friend";

    public static final String USER_FRIENDS_TIMESTAMP = "Get locations friend timestamp";

    public static final String USER_FRIENDS = "Get all locations friends";

    public static final String USER_AREA = "Get all users area";

    public static final String USER_RADIUS = "Get all users radius";

    public static final String USER_ID_AREA = "Get one/subset user area";

    public static final String USER_ID_RADIUS = "Get one/subset user radius";

    public static final String USER_ID_OUTSIDE_AREA = "Get users outside area";

    public static final String USER_ID_OUTSIDE_RADIUS = "Get users outside radius";

    public static final String USER_ID_OUTSIDE_AVERAGE = "Get users average away.";


    public static final String SELECT_BODY = "Change the Response Body format. " +
            "\n\n <B>This query parameter is optional.</B> When no value is specified for the select query parameter you get the full Json Response" +
            "\n\n You can choose the fields you want returned with the select query parameter for making your API calls more efficient and fast " +
            "\n\n For example, \"?select=body,locations,coordinate,latitude,longitude\"" +
            "\n\n The Json Response consist of the following fields: <B>body, locations, coordinate, latitude, longitude, precision, timestamp, username</B>";

    public static final String SELECT_DATA = "Change the Response Body format. " +
            "\n\n <B>This query parameter is optional.</B> When no value is specified for the select query parameter you get the full Json Response" +
            "\n\n You can choose the fields you want returned with the select query parameter for making your API calls more efficient and fast " +
            "\n\n For example, \"?select=data,route_matches,score,time_match_found\"" +
            "\n\n The Json Response consist of the following fields: <B>data, route_matches, score, time_match_found, route, route_one, route_two, locations, coordinate, latitude, longitude, precision, timestamp, username</B>";


    /*********** Outdoor notes ***********/

    public static final String USER_ID_OUTSIDE_AVERAGE_NOTE = "Get users who is 30% more away then the average of the " +
            "users. Only returns users the user has permission to.";


    public static final String USER_ID_OUTSIDE_AREA_NOTE = "Get users who is outside of the area defined by the polyCoordinate. " +
            "Only returns users the user has permission to.";

    public static final String USER_ID_OUTSIDE_RADIUS_NOTE = "Get users who is outside the radius of a specified circle. " +
            "Only returns users the user has permission to.";

    public static final String USER_FRIEND_NOTE = "Get the name and current location of a specified user or a subset of the users. " +
            "Only returns users the user has permission to.";

    public static final String USER_FRIEND_TIMESTAMP_NOTE = "Get the name and current location of a specified user or a subset of the users if the " +
            "users locations have change since the timestamp. Only returns users the user has permission to.";

    public static final String USER_FRIENDS_NOTE = "Get the names and current locations of the users, which the user has permission to.";

    public static final String USER_AREA_NOTE = "Get a list of names/locations of the users who is " +
            "inside the area. Only returns users the user has permission to.";

    public static final String USER_RADIUS_NOTE = "Get all names/locations of the users inside a circle, given " +
            "a radius and its center which the user has permission to.";

    public static final String USER_ID_AREA_NOTE = "Get a name/location of a user or a set of users in a given area specified by " +
            "the corner coordinates of a polygon (An array of at least three or more coordinates). Only returns users the user has permission to.";

    public static final String USER_ID_RADIUS_NOTE = "Get a name/location of a user or a set of users inside a circle, " +
            "given a radius and its center. Only returns users the user has permission to.";

    public static final String DISTANCE_WEIGHT_NOTE ="The Ride Share algorithms distance weight (d_GAMMA). Must be positive.";

    public static final String TIME_WEIGHT_NOTE ="The Ride Share algorithms time weight (t_GAMMA). Must be positive.";

    public static final String LARGEST_ACCEPTABLE_DETOUR_LENGHT_NOTE ="The Ride Share algorithms modifier for the largest allowed detour (BETA). Must be positive and in kilometers.";

    public static final String ACCEPTABLE_TIME_DIFFERENCE_NOTE ="The Ride Share algorithms modifier for greatest time difference (DELTA). Must be positive and in mili seconds.";

    public static final String POST_LOCATION_NOTE = "Save a users current location to the aSTEP database. ";

    //TODO change this is the post no more uses the Ride Share algorithm.
    public static final String POST_ROUTES_NOTE = "Save a users route. The Ride Share algorithm will be executed to " +
            "check if it is a stable route, and save it as a stable route.";


    public static final String ROUTES_MATCH = "Get all new routes matches 4 weeks old.";

    public static final String ROUTES_MATCH_NOTE = "Get all stable route matches for Ride Share, which are 4 weeks old.";


    /*
    public static final String GROUP_AREA = "NOT IMPLEMENTED - Get all groups area";

    public static final String GROUP_RADIUS = "NOT IMPLEMENTED - Get all groups radius";

    public static final String GROUP_ID_AREA = "NOT IMPLEMENTED - Get one group area";

    public static final String GROUP_ID_RADIUS = "NOT IMPLEMENTED - Get one group radius";

    public static final String GROUP_AREA_NOTE = "NOT IMPLEMENTED - Get current locations of all the users group members of all groups within a given area";

    public static final String GROUP_RADIUS_NOTE = "NOT IMPLEMENTED - Get current locations of all the users group members of all groups within a given radius";

    public static final String GROUP_ID_AREA_NOTE = "NOT IMPLEMENTED - Get current locations of one group's group members within a given area";

    public static final String GROUP_ID_RADIUS_NOTE = "NOT IMPLEMENTED - Get current locations of one group's group members within a given radius";
    */
}

