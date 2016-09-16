package dk.aau.astep.appserver.restapi.api;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.exception.ApiExceptionHandler;

import java.time.Instant;
import java.util.List;

/**
 * Created by friea on 24-03-2016.
 */
public class ParamCheck {

    /**
     *
     * @param latitude
     */
    public static void checkLatitude(double latitude) {
        if(latitude>90 || latitude < -90) {
            throw new ApiExceptionHandler().locationOutOfBounds();
        }
    }

    public static void checkLongitude(double longitude) {
        if(longitude>180 || longitude < -180) {
            throw new ApiExceptionHandler().locationOutOfBounds();
        }
    }

    public static void checkCoordinate(Coordinate coordinate) {
        if(coordinate == null) {
            throw new ApiExceptionHandler().coordinateWrongFormat();
        }
        ParamCheck.checkLatitude(coordinate.getLatitude());
        ParamCheck.checkLongitude(coordinate.getLongitude());
    }

    public static void checkCoordinates(List<Coordinate> coordinates) {
        if (coordinates.size() < 3) {
            throw new ApiExceptionHandler().needAtLeastXLocations(3);
        }
        for(Coordinate coordinate : coordinates) {
            ParamCheck.checkCoordinate(coordinate);
        }
    }
/*
    public static void checkLocations(List<Location> locations){
        if(locations.size() < 2){
            throw new ApiExceptionHandler().needAtLeastXLocations(2);
        }
        for(Location loc : locations) {
            ParamCheck.checkCoordinate(loc.getCoordinate());
            ParamCheck.checkPrecision(loc.getPrecision());
            ParamCheck.checkIfUsernameIsNull(loc.getUsername());
        }
    } */

    public static void checkRadius(Double radius) {
        if (radius == null) {
            throw new ApiExceptionHandler().radiusIsEmpty();
        } else if(radius<=0) {
            throw new ApiExceptionHandler().radiusIsNegative();
        }
    }

    public static void checkTime(Instant timestamp) {
        if (timestamp == null) {
            throw new ApiExceptionHandler().timestampIsEmpty();
        } else if (timestamp.isAfter(Instant.now())) {
            throw new ApiExceptionHandler().unixTimeIsInTheFuture();
        }
    }

    public static void checkIfUsernameIsNull(String username) {
        if (username == null) {
            throw new ApiExceptionHandler().usernamesIsEmpty();
        }
    }

    public static void checkIfUsernamesIsNull(List<String> usernames) {
        if(usernames == null) {
            throw new ApiExceptionHandler().usernamesIsEmpty();
        }
        for(String username : usernames) {
            ParamCheck.checkIfUsernameIsNull(username);
        }
    }

    public static void checkPrecision(Precision precision) {
        if(precision == null){
            throw new ApiExceptionHandler().precisionIsEmpty();
        } else if(precision.getRadius() < 0) {
            throw new ApiExceptionHandler().precisionRadiusisNegative();
        } else if (precision.getRadius() == 0.0d) {
            throw new ApiExceptionHandler().precisionRadiusIsEmpty();
        } else if(precision.getUnit() < 0) {
            throw new ApiExceptionHandler().precisionUnitisNegative();
        } else if (precision.getUnit() == 0.0d) {
            throw new ApiExceptionHandler().precisionUnitIsEmpty();
        }
    }

    public static void checkDistanceWeight(double distanceWeight) {
        if(distanceWeight<=0) {
            throw new ApiExceptionHandler().distanceWeightOutOfBounds();
        }
    }

    public static void checkTimeWeight(double distanceWeight) {
        if(distanceWeight<=0) {
            throw new ApiExceptionHandler().timeWeightOutOfBounds();
        }
    }

    public static void checkLargestAcceptableDetourLength(double distanceWeight) {
        if(distanceWeight<=0) {
            throw new ApiExceptionHandler().largestAcceptableDetourLengthOutOfBounds();
        }
    }
    public static void checkacceptableTimeDifference(double distanceWeight) {
        if(distanceWeight<=0) {
            throw new ApiExceptionHandler().acceptableTimeDifferenceOutOfBounds();
        }
    }
}