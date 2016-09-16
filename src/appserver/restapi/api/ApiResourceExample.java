package dk.aau.astep.appserver.restapi.api;

import dk.aau.astep.appserver.model.shared.Coordinate;
import java.util.List;
import java.util.ArrayList;

/**
 * Examples of resources.
 */
public class ApiResourceExample {
    public static final String TOKEN = "4D03F5B2F89F5977167AB61BFBB05DD4";

    public static final String EXPIRATION_TOKEN = "{\"expirationDate\":\"2016-05-20T17:06:00.641Z\",\"value\":\"C661FBE2F69655E2DCC0DDFAA30B94ED\"}";

    public static final String USERNAME = "outdoor";

    public static final String USER = "{\"username\":\"alice\"}";

    public static final String USER_ARRAY = "[{\"username\":\"alice\"},{\"username\":\"bob\"},{\"username\":\"charlie\"}]";

    public static final String GROUP = "{\"id\":1240483276}";

    public static final String GROUP_ARRAY = "[{\"id\":1240483276},{\"id\":-343129918},{\"id\":1425362660}]";

    public static final String COORDINATE = "57.038507;9.919930";

    public static final String POLY_COORDINATE = "57.004963;9.852982\n" +
            "56.999354;9.990997\n" +
            "57.105413;10.050049\n" +
            "57.124054;9.734192";

    public static final String ROUTE_LOCATION = "57.004963;9.852982;3.23;68.0;2016-04-13T13:53:09Z\n" +
            "56.999354;9.990997;3.9;68.0;2016-04-13T13:53:09Z\n" +
            "57.105413;10.050049;3.5;68.0;2016-04-13T13:53:09Z\n" +
            "57.124054;9.734192;3.23;68.0;2016-04-13T13:53:09Z";

    public static final String DOUBLE = "5.32";

    public static final String PRECISION = "5.32;68";

    public static final String USERS = "alex_holder," +
            "alex_fisker," +
            "carsten_hansen";

    public static final String GROUP_ID = "org.yourcompany.awesomeapp";

    public static final String DISTANCE_WEIGHT = "1"; // be the modifier for distance                             "d_GAMMA"

    public static final String TIME_WEIGHT = "1"; // be the modifier for time                                     "t_GAMMA"

    public static final String LARGEST_ACCEPTABLE_DETOUR_LENGHT = "15";                        //                  "BETA"

    public static final String ACCEPTABLE_TIME_DIFFERENCE = "1800000"; //30min, it is the acceptable time difference in |R > 0    "DELTA"

    public static final String UNIX_TIME = "1360033892010";

    public static final String UNIX_TIME2 = "1460034892010";

    public static final String TIMESTAMP = "2015-10-26T04:47:09Z";

    public static final String TIMESTAMP2 = "2016-05-06T18:47:09Z";

    public static final String SELECT_BODY = "body,locations,coordinate,latitude,longitude,precision,unit,radius,timestamp,username,error_message";

    public static final String SELECT_DATA = "data,route_matches,score,time_match_found,route,route_one,route_two," +
            "locations,coordinate,latitude,longitude,precision,unit,radius,timestamp,username,error_message";

    public static final String EXAMPLE_RESULT_HEADER = "<h4>Example Result</h4>" ;
}
