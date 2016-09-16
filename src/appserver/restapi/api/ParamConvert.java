package dk.aau.astep.appserver.restapi.api;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.exception.ApiExceptionHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by friea on 14-04-2016.
 */
public class ParamConvert {
    /**
     * A method that converts a single string with userNames to a list of userNames
     * @param usernamesString a string containing multiple userNames separated by a ','
     * @return      a list containing all the userNames
     */
    public static List<String> usernames(String usernamesString) {
        //TODO check for correct format.
        return Arrays.asList(usernamesString.replaceAll("\\s","").split(","));
    }

    /**
     * A method to convert a string of ISO 8601 format to Instant
     * @param timeString a string of the iso 8601 time format
     * @return A Instant class of the given time.
     */
    public static Instant timeStringToInstant(String timeString) {
        Instant instant;
        try {
            instant = Instant.parse(timeString);
        }catch (Exception e) {
            throw new ApiExceptionHandler().timestampWrongFormat();
        }
        ParamCheck.checkTime(instant);
        return instant;
    }

    public static List<String> listOfUsersToListOfUsernames(List<User> users) {
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }

    /**
     * A method to create a list of Location from a string of locations without a username and a username
     * @param locationsString a string containing everything in a Location but username seperated by ';'
     * @param username a userName as string to be used for every location
     * @return      a list of Locations
     */
    public static List<Location> locations(List<String> locationsString, String username) {
        List<Location> returnData=new ArrayList<>();
        if(locationsString != null){
            for(String string:locationsString) {
                double latitude;
                double longitude;
                Precision precision;
                Instant timestamp;

                try {
                    String part[] = string.split(";");
                    if(part.length != 5){
                        throw new ApiExceptionHandler().locationWrongFormat();
                    }
                    latitude = Double.parseDouble(part[0].replaceAll("[^\\d.]", ""));
                    longitude = Double.parseDouble(part[1].replaceAll("[^\\d.]", ""));
                    precision = new Precision(Double.parseDouble(part[2].replaceAll("[^\\d.]", "")),
                                              Double.parseDouble(part[3].replaceAll("[^\\d.]", "")));
                    timestamp = Instant.parse(part[4]);
                } catch (Exception e) {
                    throw new ApiExceptionHandler().locationWrongFormat();
                }

                //Validate data
                ParamCheck.checkLatitude(latitude);
                ParamCheck.checkLongitude(longitude);
                ParamCheck.checkPrecision(precision);
                ParamCheck.checkTime(timestamp);
                returnData.add(new Location(new Coordinate(latitude, longitude), timestamp, username, precision));
            }
        }
        else {
            throw new ApiExceptionHandler().locationWrongFormat();
        }

        return returnData;
    }
}
