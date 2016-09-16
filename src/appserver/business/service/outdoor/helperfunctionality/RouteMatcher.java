package dk.aau.astep.appserver.business.service.outdoor.helperfunctionality;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.DBWrapper;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IDistanceBetweenTwoGPSLocationsStrategy;
import dk.aau.astep.appserver.model.outdoor.Route;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.exception.BusinessException;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RouteMatcher {
    //TODO these constants need to be fixed.
    private final double DISTANCE_WEIGHT; // be the modifier for distance                             "d_GAMMA"
    private final double TIME_WEIGHT; // be the modifier for time                                     "t_GAMMA"
    private final double LARGEST_ACCEPTABLE_DETOUR_LENGHT;                        //                  "BETA"
    private final double ACCEPTABLE_TIME_DIFFERENCE; // be the acceptable time difference in |R > 0    "DELTA"
    private final double DISTANCE_TO_TIME = 3600000; // Hour to milli seconds - translation from distance to time in |R > 0      "GAMMA"
    private IDistanceBetweenTwoGPSLocationsStrategy dist;
    private DBWrapper db;

    /**
     *
     * @param distanceWeight
     * @param timeWeight
     * @param largestAcceptableDetourLength
     * @param acceptableTimeDifference
     * @param dist The strategy for calculating distance between two coordinates.
     */
    public RouteMatcher(double distanceWeight, double timeWeight, double largestAcceptableDetourLength,
                        double acceptableTimeDifference, IDistanceBetweenTwoGPSLocationsStrategy dist, DBWrapper db){
        if(distanceWeight < 0 || timeWeight < 0 || largestAcceptableDetourLength < 0 || acceptableTimeDifference < 0){
            ALogger.log("RouteMatcher constructor: its parameter may not be negative.", Module.OD, Level.ERROR);
            throw new BusinessException();
        }
        if(distanceWeight == 0 && timeWeight == 0){
            ALogger.log("RouteMatcher constructor: Both weight must not be 0 at the same time", Module.OD, Level.ERROR);
            throw new BusinessException();
        }
        this.DISTANCE_WEIGHT = distanceWeight;
        this.TIME_WEIGHT = timeWeight;
        this.LARGEST_ACCEPTABLE_DETOUR_LENGHT=largestAcceptableDetourLength;
        this.ACCEPTABLE_TIME_DIFFERENCE = acceptableTimeDifference;
        this.dist = dist;
        this.db = db;
    }

    /**
     * Calculates a score of the spatial-temporal match between two routes.
     * @param route
     * @param start
     * @param goal
     * @return  Returns a score of the two routes between 1 and 0, where the closer too 1 the better the score. Return 0 if either the temporal or spatial score is negative.
     */
    public double timeDistanceAnalyser(Route route, Location start, Location goal) {
        double distanceScore = distanceAnalyser(route, start, goal);
        double timeScore = timeAnalyser(route, start, goal);
        if (distanceScore > 0 && timeScore > 0) {
            return (distanceScore * DISTANCE_WEIGHT + timeScore * TIME_WEIGHT) / (DISTANCE_WEIGHT + TIME_WEIGHT);
        } else {
            return 0;
        }
    }


    /**
     * A function to give a score for how well a routes match pickup and set off point when looking at distance.
     * The score can not be above 1. Between 1 and 0 is acceptable match. below 0 is a bad match.
     * @param route a route to which the two locations should be compared to
     * @param start the start location for the route
     * @param goal the end location for the route
     * @return      Returns a value between x and x describing the distance score. 1 is perfect, and below 0 is not good enough
     */

    private double distanceAnalyser(Route route, Location start, Location goal) {
        // Find the closests points from the route to the start and end.
        Location closestPointStart = closestPoint(route, start.getCoordinate());
        Location closestPointGoal = closestPoint(route, goal.getCoordinate());

        double pickupDetourDist = dist.getDistance(closestPointStart.getCoordinate(), start.getCoordinate());
        double setOffDetourDist = dist.getDistance(closestPointGoal.getCoordinate(), goal.getCoordinate());
        return 1 - ((pickupDetourDist + setOffDetourDist) / LARGEST_ACCEPTABLE_DETOUR_LENGHT);
    }

    /**
     * Will find the location in a route that are closest to a give Coordinate.
     * @param route A route in which a location will be found
     * @param targetCoord The coordinate for which each location in the route will be compared to
     * @return      The Location in the route that are closest to the given Coordinate
     */
    private Location closestPoint(Route route, Coordinate targetCoord){
        List<Location> routeLoc = route.getLocations();
        int closestIndex = 0;
        double closestDist = dist.getDistance(routeLoc.get(0).getCoordinate(), targetCoord);

        // Find the closest point to the targetCoord on the route.
        for (int i = 1; i < routeLoc.size() ; i++) {
            double tempDist = dist.getDistance(routeLoc.get(i).getCoordinate(), targetCoord);

            if(closestDist > tempDist){
                closestDist = tempDist;
                closestIndex = i;
            }
        }
        return routeLoc.get(closestIndex);
    }

    /**
     *
     * @param route
     * @param start
     * @param goal
     * @return
     */
    private double timeAnalyser(Route route, Location start, Location goal) {
        Location closestPointStart = closestPoint(route, start.getCoordinate());
        Location closestPointGoal = closestPoint(route, goal.getCoordinate());

        //Find the total detour for the route to get to start and goal.
        double distStart = dist.getDistance(closestPointStart.getCoordinate(), start.getCoordinate());
        double distGoal = dist.getDistance(closestPointStart.getCoordinate(), start.getCoordinate());



        double timeDiffStart = Math.abs(timeDifference(start.getTimestamp(), closestPointStart.getTimestamp())
                                        + (distGoal * DISTANCE_TO_TIME));
        double timeDiffGoal = Math.abs(timeDifference(closestPointGoal.getTimestamp(), goal.getTimestamp())
                                       + (distStart * DISTANCE_TO_TIME));

        //Find the largest time for one of the routes, using largest time different to be pessimistic.
        double greatestTimeDiff = Math.max(timeDiffStart, timeDiffGoal);

        return 1 - (greatestTimeDiff / ACCEPTABLE_TIME_DIFFERENCE);
    }

    private double timeDifference(Instant startTimeStamp, Instant endTimeStamp){
        long milliSecondsInWeekStart = milliSecondsIntoWeekFromUnixTime(startTimeStamp);
        long milliSecondsInWeekStartEnd = milliSecondsIntoWeekFromUnixTime(endTimeStamp);

        return milliSecondsInWeekStart - milliSecondsInWeekStartEnd;
    }


    /**
     *
     * @param timestamp The timestamp to be converted to ms in the given week.
     * @return Returns the milisecond the timestamp has been in the corrent week.
     */
    public long milliSecondsIntoWeekFromUnixTime(Instant timestamp) {
        long milliSecondsInAWeek = 604800000;
        long milliSecondsIntoWeek = timestamp.toEpochMilli() % milliSecondsInAWeek;

        return milliSecondsIntoWeek;
    }

    /**
     *
     * @param routeLocation Contains the locations of a route
     * @param username The username of the user
     * @param timePeriode The time periode specified in milliseconds
     * @return Return the users stable routes in a specified timeperiode ex. two weeks
     */
    public List<Route> getUsersStableRoutesInPeriode(List<Location> routeLocation, String username, long timePeriode) {
        Instant timestampOfRoute = routeLocation.get(0).getTimestamp();
        long timeOfStableRoutesTwoWeeksBack = timestampOfRoute.toEpochMilli() - timePeriode;

        List<String> usernames = new ArrayList<>();
        usernames.add(username);
        try {
            return db.getAllRoutes(usernames, Instant.now().minus(Duration.ofDays(28)), true);
        }
        catch (IOException e) {
            ALogger.log("Method getUsersStableRoutesInPeriod: Persistent database fail: " + e.getMessage(), Module.OD, Level.ERROR);
            throw new BusinessException();
        }
    }

    /**
     *
     * @param stableRoutes the stable routes used for matching with the single route.
     * @param routeLocationsToCheck the single route used to match with all the stable route.
     * @param scoreToMatch If a route matches it must have a score greater then this value.
     * @return returns true if the route matches a stabile route, and saves the route in the DB, else false.
     */
    public boolean routeMatchStable(List<Route> stableRoutes, List<Location> routeLocationsToCheck, double scoreToMatch) {

        /*RouteMatcher matcher = new RouteMatcher(this.DISTANCE_WEIGHT, this.TIME_WEIGHT, this.LARGEST_ACCEPTABLE_DETOUR_LENGHT,
                                                this.ACCEPTABLE_TIME_DIFFERENCE, new Haversine());*/
        double score;
        Route routeToMatch;

        //TODO validate if the lists are null
        Location startLoc = routeLocationsToCheck.get(0);
        Location goalLoc = routeLocationsToCheck.get(routeLocationsToCheck.size() - 1);

        for (int i = 0; i < stableRoutes.size(); i++) {
            routeToMatch = stableRoutes.get(i);
            score = this.timeDistanceAnalyser(routeToMatch, startLoc, goalLoc);

            if(score > scoreToMatch){
                //The new route match another stable route

                //Save locations
                db.saveLocations(routeLocationsToCheck);
                //update the timestamp of the stable route.
                try {
                    db.updateTimestampOnRoute(routeToMatch, routeLocationsToCheck.get(0).getTimestamp());
                }
                catch (IOException e){
                    ALogger.log("Method routeMatchStable: Persistent database fail: " + e.getMessage(), Module.OD, Level.ERROR);
                    throw new BusinessException();
                }
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param routes The non stable routes the single route is trying to be matched with.
     * @param routeLocationsToCheck The single route used to match all the other routes.
     * @param scoreToMatch If a route matches it must have a score greater then this value.
     * @return returns true if the single route matches enough of the other routes to make it stable.
     */
    public boolean routeMatchRoutes(List<Route> routes, List<Location> routeLocationsToCheck, double scoreToMatch) {
        boolean isStable = false;
        double score;
        Route routeToMatch;
        Location startLoc = routeLocationsToCheck.get(0);;
        Location goalLoc = routeLocationsToCheck.get(routeLocationsToCheck.size() - 1);
        List<Route> listMatches = new ArrayList<>();

        // need 3 routes to make a stable route.
        int stableCount = 2;
        int matchCount = 0;

        if (routes != null) {
            for (int i = 0; i < routes.size(); i++) {
                routeToMatch = routes.get(i);
                score = this.timeDistanceAnalyser(routeToMatch, startLoc, goalLoc);
                if (score > scoreToMatch) {
                    matchCount++;
                    listMatches.add(routeToMatch);;
                }
            }
            if (matchCount >= stableCount) {
                isStable = true;
            }
        }
        Location tempLoc = routeLocationsToCheck.get(0);
        try {
            db.saveRoute(routeLocationsToCheck, tempLoc.getUsername(), tempLoc.getTimestamp(), isStable);
            return isStable;
        }
        catch(IOException e){
            ALogger.log("Method routeMatchRoutes: Persistent database fail: " + e.getMessage(), Module.OD, Level.ERROR);
            throw new BusinessException();
        }

    }
}
