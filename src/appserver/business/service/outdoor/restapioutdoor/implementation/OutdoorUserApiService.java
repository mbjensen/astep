package dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.implementation;

import dk.aau.astep.appserver.business.service.outdoor.helperfunctionality.AvgDistLocation;
import dk.aau.astep.appserver.business.service.outdoor.helperfunctionality.EntityInCircle;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.context.DistanceBetweenTwoGPSLocations;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.context.PointInsidePolygon;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.distancebetweentwogpslocations.Haversine;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.pointInsidepolygon.RayCasting;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IDistanceBetweenTwoGPSLocationsStrategy;
import dk.aau.astep.appserver.business.service.outdoor.restapioutdoor.interfaces.UserApiService;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.DBWrapper;
import dk.aau.astep.appserver.business.service.outdoor.tempdb.Outdoordb;
import dk.aau.astep.appserver.model.outdoor.Circle;
import dk.aau.astep.appserver.model.shared.*;
import dk.aau.astep.appserver.restapi.api.ApiResponseMessage;

import java.time.Duration;
import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * Created by Morten on 07/03/2016.
 */
public class OutdoorUserApiService implements UserApiService {
    DBWrapper db;
    public OutdoorUserApiService(DBWrapper db){
        this.db = db;
    }
    /**
     *
     * @param polyCoordinates List of coordinates representing the corners of the polygon
     * @param usernames Usernames to find within the polygon
     * @return All users within the polygon from usernaems
     */
    @Override
    public Response getUsersInArea(List<Coordinate> polyCoordinates, List<String> usernames) {
        Polygon polly = new Polygon(polyCoordinates);

        List<Location> DBAnswer=db.getNewestLocationsInArea(polly.createBoundingBox(), usernames);
        

        PointInsidePolygon pIP = new PointInsidePolygon(new RayCasting());
        List<Location> insideArea = pIP.pointsInOrOutsidePolygon(DBAnswer, polly, true);

        return Response.ok(new ApiResponseMessage(new JsonResponse(insideArea, null))).build();
    }

    /**
     * A method that find all users within a circle
     * @param center coordinate for the center
     * @param radius radius in kilometers
     * @param usernames List of usernames to find
     * @return All users from usernames within the circle
     */
    @Override
    public Response getUsersInRadius(Coordinate center, Double radius, List<String> usernames) {
        Circle circle = new Circle(center, radius);
        EntityInCircle temp = new EntityInCircle();

        List<Location> DBAnswer=db.getNewestLocationsInArea(circle.circleBoundingBox(), usernames);
        

        List<Location> insideArea = temp.entitiesInOrOutsideCircle(circle, DBAnswer, true, true);

        return Response.ok(new ApiResponseMessage(new JsonResponse(insideArea, null))).build();
    }

    /**
     * A method that return all users outside a area
     * @param polyCoordinate Coordinates forming a polygon
     * @param usernames A list of usernames to test if they are outside area
     * @return List of locations outside area
     */
    @Override
    public Response getUsersOutsideArea(List<Coordinate> polyCoordinate, List<String> usernames) {
        Polygon polly = new Polygon(polyCoordinate);

        List<Location> DBAnswer=db.getNewestLocationsOutsideArea(polly.createBoundingBox(), usernames);

        PointInsidePolygon pIP = new PointInsidePolygon(new RayCasting());
        List<Location> insideArea = pIP.pointsInOrOutsidePolygon(DBAnswer, polly, false);

        return Response.ok(new ApiResponseMessage(new JsonResponse(insideArea, null))).build();
    }

    /**
     * A method to find all users outside og a radius
     * @param center Coordinates to the center of the circle
     * @param radius Radius for the circle
     * @param usernames List of usernaems to look for
     * @return A list of locations outside the radius
     */
    @Override
    public Response getUsersOutsideRadius(Coordinate center, Double radius, List<String> usernames) {
        Circle circle = new Circle(center, radius);
        EntityInCircle temp = new EntityInCircle();
        
        List<Location> DBAnswer=db.getNewestLocationsOutsideArea(circle.circleBoundingBox(), usernames);

        List<Location> insideArea = temp.entitiesInOrOutsideCircle(circle, DBAnswer, false, true);

        return Response.ok(new ApiResponseMessage(new JsonResponse(insideArea, null))).build();
    }

    /**
     * A method to save a location for a user
     * @param coordinate coordinates fot the location
     * @param userName Usernaem for the user saving the location
     * @param precision The precision of the coordinates
     * @param timestamp timestamp for when the location was saved
     * @return Returns a Duration in ISO time, representing how long we recommend there should og before the user save the next location.
     */
    @Override
    public Response postLocation(Coordinate coordinate, String userName, Precision precision, Instant timestamp){
        //Create a location
        Location newLocation = new Location(coordinate, timestamp, userName, precision);

        //Get newest location
        List<String> usernames = new ArrayList<>();
        usernames.add(userName);
        List<Location> oldLocations;
        oldLocations=db.getNewestLocation(usernames);

        //Save new Location
        db.saveLocation(newLocation);
        if(oldLocations !=null && oldLocations.size()!=0) {
            Location oldLocation = oldLocations.get(0);
            //Get distance
            IDistanceBetweenTwoGPSLocationsStrategy distanceStrategy = new Haversine();
            DistanceBetweenTwoGPSLocations distanceObject = new DistanceBetweenTwoGPSLocations(distanceStrategy);
            long distance = (long)distanceObject.getDistance(newLocation.getCoordinate(), oldLocation.getCoordinate());
            //Get time difference
            long hours = Duration.between(oldLocation.getTimestamp(), newLocation.getTimestamp()).toHours();

            //Get the speed
            if(hours != 0){
                long speed = distance/hours;

                if (speed < 9.5) {//Walk speed
                    return Response.ok().entity(new ApiResponseMessage(Duration.ofSeconds(60))).status(201).build();
                } else if(speed<35) {//Cyclist
                    return Response.ok().entity(new ApiResponseMessage(Duration.ofSeconds(15))).status(201).build();
                } else if(speed<60) {
                    return Response.ok().entity(new ApiResponseMessage(Duration.ofSeconds(10))).status(201).build();
                } else {
                    return Response.ok().entity(new ApiResponseMessage(Duration.ofSeconds(5))).status(201).build();
                }
            }
            // the location is saved at the precise same time as previous
            else  {
                return Response.ok().entity(new ApiResponseMessage(Duration.ofSeconds(60))).status(201).build();
            }
        } else {
            //First time the user saves something. Lets get a quick update to see how fast he moves
            Duration duration = Duration.ofSeconds(5);
            return Response.ok().entity(new ApiResponseMessage(duration)).status(201).build();
        }
    }

    /**
     * A method to find avarge distance between users, and return those who are more then avarge+30% away from any other
     * @param usernames List of usernaems to see if to far away from each other
     * @return A list of locations with every user that are to far away
     */
    @Override
    public Response userAvgAwayFromOthers(List<String> usernames){
        //TODO: Changes fake DB data to a actual call to DB, get location from all the users.
        List<Location> usersLocDummyDATA = new ArrayList<>();
        IDistanceBetweenTwoGPSLocationsStrategy distStrat = new Haversine();

        usersLocDummyDATA.add(new Location(new Coordinate(57.037200, 9.911690), Instant.now(), "jens_nielsen", new Precision(68, 5.43f)));
        usersLocDummyDATA.add(new Location(new Coordinate(57.036266, 9.928513), Instant.now(), "carsten_juhl", new Precision(68, 0.43f)));
        usersLocDummyDATA.add(new Location(new Coordinate(57.046539, 9.920616), Instant.now(), "svend_andersen", new Precision(68, 3.23f)));

        AvgDistLocation avgDistLoc = new AvgDistLocation();
        List<Location> isAwayFromGroup = avgDistLoc.entertiesAvgAwayFromGroup(usersLocDummyDATA, distStrat);
        //entertiesAvgAwayFromGroup (coordinates,  distStrat)
        //TODO: Return real data instead of JsonObjectTest
        return Response.ok().entity(new ApiResponseMessage(new JsonResponse(isAwayFromGroup, null))).build();
    }

    /**
     * A to get newest location for a users friends
     * @param usernames
     * @return Newest location for each user sent to the method
     */
    @Override
    public Response getAUsersFriendNameAndLocation(List<String> usernames) {
        List<Location> DBAnswer = db.getNewestLocation(usernames);

        return Response.ok().entity(new ApiResponseMessage(new JsonResponse(DBAnswer, null))).build();
    }

    /**
     * A method to get newest location for each user in the list but only return locations newer then timestamp
     * @param usernames List of users to find location for
     * @param timestamp A timestamp representing the oldest a location must be
     * @return Newest location for each user.
     */
    @Override
    public Response getAUsersFriendNameAndLocationTimestamp(List<String> usernames, Instant timestamp) {
        List<Location> DBAnswer = db.getNewestLocationsFromTime(usernames, timestamp);

        return Response.ok().entity(new ApiResponseMessage((new JsonResponse(DBAnswer, null)))).build();
    }
}

