package dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.implementation;

import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Haversine;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.DBWrapper;
import dk.aau.astep.appserver.business.service.outdoor.helperfunctionality.EntityInCircle;
import dk.aau.astep.appserver.business.service.outdoor.helperfunctionality.RouteMatcher;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.context.PointInsidePolygon;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.pointInsidepolygon.RayCasting;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces.HistoryApiService;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.TempDBWrapper;
import dk.aau.astep.appserver.model.outdoor.*;
import dk.aau.astep.appserver.model.shared.*;
import dk.aau.astep.appserver.restapi.api.ApiResponseMessage;
import dk.aau.astep.exception.BusinessException;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morten on 07/03/2016.
 */
public class OutdoorHistoryApiService implements HistoryApiService {

    DBWrapper db;
    public OutdoorHistoryApiService(DBWrapper db){
        this.db = db;
    }
    
    /**
     *
     * @param periodBegin Beginning of the time period.
     * @param periodEnd End of the time period.
     * @param userName The username of the user.
     * @return Returns all the location of users both inside a time period, which the userName has permission to see.
     */
    @Override
    public Response getAllHistoryDataInTimePeriode(Instant periodBegin, Instant periodEnd, String userName){        
        List<Location> historieData = db.getLocationsAtTimeInterval(userName, periodBegin, periodEnd);
        
        return Response.ok().entity(new ApiResponseMessage(new JsonResponse(historieData, null))).build();
    }


    /**
     *
     * @param polyCoordinate The corners of the polygon, there need to be atleast 3 distinct coordinates.
     * @param periodBegin Beginning of the time period in UCT miliseconds.
     * @param periodEnd End of the time period in UCT miliseconds.
     * @param userName The username of the user.
     * @return Returns all the location of users both inside the area and a time period, which the userName has permission to see.
     */
    public Response getAllHistoryDataInAreaInTimePeriod(List<Coordinate> polyCoordinate, Instant periodBegin, Instant periodEnd, String userName) {
        Polygon polygon = new Polygon(polyCoordinate);

        List<Location> historieData = db.getLocationsAtTimeInterval(userName, periodBegin, periodEnd);

        PointInsidePolygon pIP = new PointInsidePolygon(new RayCasting());
        List<Location> insideArea = pIP.pointsInOrOutsidePolygon(historieData, polygon, true);

        return Response.ok(new ApiResponseMessage(new JsonResponse(insideArea, null))).build();
    }

    /**
     *
     * @param center Center of the circle (often the users position)
     * @param radius Radius of the circle.
     * @param periodBegin Beginning of the time period.
     * @param periodEnd End of the time period.
     * @param userName The username of the user.
     * @return Returns all the location of users both inside the radius and a time period, which the userName has permission to see them.
     */
    @Override
    public Response getAllHistoryDataInRadiusInTimePeriod(Coordinate center, double radius, Instant periodBegin, Instant periodEnd, String userName)
    {
        Circle circle = new Circle(center, radius);
        EntityInCircle eIC = new EntityInCircle();

        // Database historisk data
        List<Location> historieData = db.getLocationsAtTimeInterval(userName, periodBegin, periodEnd);

        // Only return the locations inside the circle.
        List<Location> insideCircle = eIC.entitiesInOrOutsideCircle(circle, historieData, true, true);

        return Response.ok(new ApiResponseMessage(new JsonResponse(insideCircle, null))).build();
    }

    /**
     *
     * @param routeLocation the locations the route consist of.
     * @param username The username of the user saving the route.
     * @param distanceWeight How much should the distance between the 2 routes count in the ride share algorithm.
     * @param timeWeight How much should the time difference between the 2 routes count in the ride share algorithm.
     * @param largestAcceptableDetourLength The max allowed time difference between to routes in kilometers.
     * @param acceptableTimeDifference The max allowed time difference between to routes in miliseconds.
     * @return Http response code 200 if the route got saved correctly.
     */
    @Override
    public Response postRoute(List<Location> routeLocation, String username, double distanceWeight, double timeWeight, double largestAcceptableDetourLength, double acceptableTimeDifference) {
        RouteMatcher routeMatcher = new RouteMatcher(distanceWeight, timeWeight, largestAcceptableDetourLength, acceptableTimeDifference, new Haversine() , new TempDBWrapper());
        long twoWeeksBackTimeStampe = 1209600000;
        //TODO replace this with the score to match.
        double scoreToMatch = 0.5;
        boolean isStable = false;

        //Get all stableRoutes for the user two weeks back from the database
        List<Route> stableRoutes = routeMatcher.getUsersStableRoutesInPeriode(routeLocation, username, twoWeeksBackTimeStampe);
        if (stableRoutes != null && stableRoutes.size() != 0) {
            //Try to match the route to an already stable route.
            isStable = routeMatcher.routeMatchStable(stableRoutes, routeLocation, scoreToMatch);
        }
        // if the route to save cannot be matched to a already stable route, then check all the users other routes to check
        // for a new stable route.
        if (!isStable) {
            List<String> usernames = new ArrayList<>();
            usernames.add(username);
            List<Route> noneStableRoutes;
            try{
                noneStableRoutes = db.getAllRoutes(usernames, Instant.now().minus(Duration.ofDays(28)), false);
            }
            catch (IOException e){
                ALogger.log("Method postRoute: Persistent database fail: " + e.getMessage(), Module.OD, Level.ERROR);
                throw new BusinessException();
            }

            if(noneStableRoutes != null && noneStableRoutes.size() != 0) {
                routeMatcher.routeMatchRoutes(noneStableRoutes, routeLocation, scoreToMatch);
            } else {
                try {
                    db.saveRoute(routeLocation, routeLocation.get(0).getUsername(), routeLocation.get(0).getTimestamp(), false);
                }
                catch (IOException e){
                    ALogger.log("Method postRoute: Persistent database fail: " + e.getMessage(), Module.OD, Level.ERROR);
                    throw new BusinessException();
                }
            }
        }
        //TODO find out what to send
        return Response.status(201).build();

    }

    /**
     * Using the Ride Share algorithm to found matches between routes.
     * @param userNames The names of users the user have permission to.
     * @param distanceWeight How much should the distance between the 2 routes count in the ride share algorithm.
     * @param timeWeight How much should the time difference between the 2 routes count in the ride share algorithm.
     * @param largestAcceptableDetourLength The max allowed time difference between to routes in kilometers.
     * @param acceptableTimeDifference The max allowed time difference between to routes in miliseconds.
     * @return A response with all the route matches found between the users given by their userNames.
     */
    @Override
    public Response matchRoute(List<String> userNames, double distanceWeight, double timeWeight,
                               double largestAcceptableDetourLength, double acceptableTimeDifference) {
        // The score two routes should get for it to match.
        double scoreToMatch = 0.5;
        double score = -1;
        Route routeToMatch;
        Location startLoc, goalLoc;
        RouteMatcher matcher = new RouteMatcher(distanceWeight, timeWeight, largestAcceptableDetourLength,
                                                acceptableTimeDifference, new Haversine(), new TempDBWrapper());
        List<RouteMatch> matchingRoutes = new ArrayList<>();
        List<Route> routesFromDB;

        // fetch all the routes which the user has permision to.
        try {
            routesFromDB = db.getAllRoutes(userNames, Instant.now().minus(Duration.ofDays(28)), true);
        }
        catch (IOException e){
            ALogger.log("Method matchRoute: Persistent database fail: " + e.getMessage(), Module.OD, Level.ERROR);
            throw new BusinessException();
        }


        // Iterate through all routes from the persisten database both ways, while looking for matches.
        for (int i = 0; i < routesFromDB.size(); i++) {
            for (int j = routesFromDB.size() - 1; j >= 0; j--) {
                // no need to try and match the same routes.
                if(i == j){
                    continue;
                }
                startLoc = routesFromDB.get(i).getLocations().get(0);
                goalLoc = routesFromDB.get(i).getLocations().get(routesFromDB.size());
                routeToMatch = routesFromDB.get(j);

                // get the score between the two routes.
                score = matcher.timeDistanceAnalyser(routeToMatch, startLoc, goalLoc);

                // add a match if the score is greater then 0.5 and the routes are not from the same user.
                if(score > scoreToMatch && routesFromDB.get(i).getUsername() != routeToMatch.getUsername() ){
                    matchingRoutes.add(new RouteMatch(routesFromDB.get(i), routeToMatch, score));
                }
            }
        }
        return Response.ok(new ApiResponseMessage(new JsonResponseMatchRoute(matchingRoutes))).build();
    }
}
