package dk.aau.astep.appserver.dataaccess.internal.hotloc.db;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import dk.aau.astep.appserver.dataaccess.api.queries.HotLocationQueries;
import dk.aau.astep.appserver.model.outdoor.Rectangle;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.exception.BusinessException;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;
import org.bson.BSONObject;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

public class Mongodb implements HotLocationQueries {
    private static final String mongoConnectionString   = "mongodb://172.19.0.243:27019/";
    private static final String databaseName            = "astepmongodb";
    private static final String collectionName          = "hotlocations";

    private static MongoClient                          mongoClient;
    private MongoDatabase                               database;
    private MongoCollection<Document>                   collection;

    /***
     * Constructor which uses the default database (astepmongodb) and collection (hotlocations)
     */
    public Mongodb() {
        try {
            mongoClient    = connect();
            database       = mongoClient.getDatabase(databaseName);
            if(!database.listCollections().iterator().hasNext()){
                ALogger.log("No MongoDB collection was found.", Module.DB, Level.ERROR);
                throw new MongoException("Does not have any collections");
            }
            collection     = database.getCollection(collectionName);
        } catch (MongoException e) {
            ALogger.log("Mongodb connection error: " + e.getMessage(), Module.DB, Level.ERROR);
            throw new BusinessException();
        }
    }

    /***
     * Constructor. Enables use of different database and collection
     * @param dbName
     * @param collectionName
     */
    public Mongodb(String dbName, String collectionName) {
        try {
            mongoClient    = connect();
            database       = mongoClient.getDatabase(dbName);
            if(!database.listCollections().iterator().hasNext()){
              throw new MongoException("Does not have any collections");
            }
            collection     = database.getCollection(collectionName);
        } catch (MongoException e) {
            ALogger.log("Mongodb connection error: " + e.getMessage(), Module.DB, Level.ERROR);
            throw new BusinessException();
        }
    }

    /***
     * Get working connection to the MongoDB database
     * @return MongoClient object with a connection to the database
     */
    public MongoClient                  getConnection() { return mongoClient; }

    /***
     * Get the MongoDB database
     * @return MongoDatabase object representing the aSTEP MongoDB database
     */
    public MongoDatabase                getDatabase()   { return database; }

    /***
     * Get the MongoDB collection containing the hot location data
     * @return MongoCollection<Document> object representing the hot location data collection
     */
    public MongoCollection<Document>    getCollection() { return collection; }

    /***
     * Use to reconnect to the default aSTEP MongoDB database
     * @return MongoClient object with a connection to the database
     */
    public MongoClient                  reconnect()     { return connect(); }

    /***
     * Write a single location to the database
     * @param location A single instance of the Location class
     * @return boolean value indicating whether the write was successful or not
     */
    @Override
    public boolean saveLocation(Location location) {
        try {
            Document locationDoc = new Document()
                                    .append("createdAt", new Date()) // Used for TTL index
                                    .append("timestamp", new Date().from(location.getTimestamp()))
                                    .append("userId", location.getUsername())
                                    .append("location", new Document()
                                        .append("type", "Point")
                                        .append("coordinates",
                                            asList(location.getCoordinate().getLongitude(), location.getCoordinate().getLatitude()))
                                    )
                                    .append("precision", new Document()
                                        .append("radius", location.getPrecision().getRadius())
                                        .append("unit", location.getPrecision().getUnit())
                                    );
            collection.insertOne(locationDoc);
            return true;
        } catch (MongoException e) {
            ALogger.log("Mongodb, write error: " + e.getMessage(), Module.DB, Level.ERROR);
        }

        return false;
    }

    /***
     * Write multiple locations to the database.
     * @param locations A list of Location objects
     * @return boolean value indicating whether the bulk write was successful or not.
     * Will return false if a single write fails
     */
    @Override
    public boolean saveLocations(List<Location> locations) {
        List<WriteModel<Document>> locationDocs = new ArrayList<>();

        for (Location loc : locations) {
            locationDocs.add(new InsertOneModel<Document>(
                                new Document()
                                    .append("createdAt", new Date()) // Used for TTL index
                                    .append("timestamp", new Date().from(loc.getTimestamp()))
                                    .append("userId", loc.getUsername())
                                    .append("location", new Document()
                                        .append("type", "Point")
                                        .append("coordinates",
                                            asList(loc.getCoordinate().getLongitude(), loc.getCoordinate().getLatitude()))
                                    )
                                    .append("precision", new Document()
                                        .append("radius", loc.getPrecision().getRadius())
                                        .append("unit", loc.getPrecision().getUnit())
                                    )
                                )
                            );
        }

        BulkWriteOptions bulkWriteOptions = new BulkWriteOptions();
        // Unordered bulk writing should increase performance when the collection is sharded
        bulkWriteOptions.ordered(false);

        try {
            collection.bulkWrite(locationDocs, bulkWriteOptions);
            return true;
        } catch (BulkWriteException e) {
            ALogger.log("Mongodb, bulk write error. Bulk writes which failed: " + e.getWriteErrors().toString() + " Error message: " + e.getMessage(), Module.DB, Level.ERROR);
        }

        return false;
    }

    /***
     * Get all locations for a specific user
     * @param userId The user id as a String
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getAllLocations(String userId) {
        List<Location> locations  = new ArrayList<>();

        // -1 in the sort modifier indicates descending order
        FindIterable<Document> iterable = collection.find(new Document("userId", userId)).sort(new Document("timestamp", -1));

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document)));
        }

        return locations;
    }

    /***
     * Get the newest location of a specific user
     * @param userId The user id as a String
     * @return A Location object. Will return null if nothing is found
     */
    @Override
    public Location getNewestLocation(String userId) {
        FindIterable<Document> iterable = collection.find(new Document("userId", userId)).sort(new Document("timestamp", -1)).limit(1);
        Document foundLoc               = iterable.iterator().tryNext();

        if (foundLoc.isEmpty()) { return null; }

        return parseLocation(foundLoc);
    }

    /***
     * Get the newest location for multiple users
     * @param userIds A list of Strings representing user ids
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getNewestLocation(List<String> userIds) {
        List<Location> locations  = new ArrayList<>();

        Document query      = new Document("userId", new Document("$in", userIds));
        Document sort       = getSortingFunction();

        Document matchDoc   = new Document("$match", query);
        Document sortDoc    = new Document("$sort", sort);
        Document groupDoc   = new Document("$group", getGroupingFunction());

        AggregateIterable<Document> iterable = collection.aggregate(asList(matchDoc, sortDoc, groupDoc));

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document, true)));
        }

        return locations;
    }

    /***
     * Get the newest location for multiple user from a specific point in time and onward
     * @param userIds A list of Strings representing user ids
     * @param from An Instant representing a point in time. Only locations at that time or later will be returned
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getNewestLocationsFromTime(List<String> userIds, Instant from) {
        List<Location> locations = new ArrayList<>();

        Document query      = new Document()
                                .append("userId", new Document("$in", userIds))
                                .append("timestamp", new Document("$gte", new Date().from(from)));

        Document sort       = getSortingFunction();

        Document matchDoc   = new Document("$match", query);
        Document sortDoc    = new Document("$sort", sort);
        Document groupDoc   = new Document("$group", getGroupingFunction());

        AggregateIterable<Document> iterable = collection.aggregate(asList(matchDoc, sortDoc, groupDoc));


        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document, true)));
        }

        return locations;
    }

    /***
     * Get the newest location for each user within a polygon area
     * @param area A Polygon object
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getNewestLocationsInArea(Polygon area) {
        List<Location> locations = new ArrayList<>();

        Document query      = new Document("location", getPolygonQuery(area));
        Document sort       = getSortingFunction();

        Document matchDoc   = new Document("$match", query);
        Document sortDoc    = new Document("$sort", sort);
        Document groupDoc   = new Document("$group", getGroupingFunction());

        AggregateIterable<Document> iterable = collection.aggregate(asList(matchDoc, sortDoc, groupDoc));


        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document, true)));
        }

        return locations;
    }

    /***
     * Get the newest location for specific users if and only if the newest location is within the rectangle area
     * @param area A Rectangle object
     * @param userIds A list of Strings representing user ids
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getNewestLocationsInArea(Rectangle area, List<String> userIds) {
        List<Location> locations = new ArrayList<>();

        Document userQuery      = new Document()
                                    .append("userId", new Document("$in", userIds));

        Document geoQuery       = new Document()
                                    .append("location", getRectangleQuery(area));

        Document sort           = getSortingFunction();
        Document userMatchDoc   = new Document("$match", userQuery);
        Document geoMatchDoc    = new Document("$match", geoQuery);

        Document sortDoc        = new Document("$sort", sort);
        Document groupDoc       = new Document("$group", getGroupingFunction());

        AggregateIterable<Document> iterable = collection.aggregate(asList(userMatchDoc, sortDoc, groupDoc, geoMatchDoc));

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document, true)));
        }

        return locations;
    }

    /***
     * Get the newest location for specific users if and only if the newest location is outside the rectangle area
     * @param area A Rectangle object
     * @param userIds A list of Strings representing user ids
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getNewestLocationsOutsideArea(Rectangle area, List<String> userIds) {
        List<Location> locations = new ArrayList<>();

        Document query          = new Document()
                                    .append("userId", new Document("$in", userIds));

        Document geoQuery       = new Document()
                                    .append("location", new Document()
                                        .append("$not", getRectangleQuery(area))
                                    );

        Document sort           = getSortingFunction();
        Document userMatchDoc   = new Document("$match", query);
        Document geoMatchDoc    = new Document("$match", geoQuery);
        Document sortDoc        = new Document("$sort", sort);
        Document groupDoc       = new Document("$group", getGroupingFunction());

        AggregateIterable<Document> iterable = collection.aggregate(asList(userMatchDoc, sortDoc, groupDoc, geoMatchDoc));


        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document, true)));
        }

        return locations;
    }

    /***
     * Get all locations for a user within a specific time interval
     * @param userId The user id as a String
     * @param from An Instant representing a point in time. Only locations at that time or later will be returned
     * @param to An Instant representing a point in time. Only locations at that time or earlier will be returned
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getLocationsAtTimeInterval(String userId, Instant from, Instant to) {
        List<Location> locations = new ArrayList<>();

        Document query = new Document()
                            .append("userId", userId)
                            .append("timestamp", new Document()
                                .append("$gte", new Date().from(from))
                                .append("$lte", new Date().from(to))
                            );

        FindIterable<Document> iterable = collection.find(query);

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document)));
        }
        return locations;
    }

    /***
     * Get all locations for all users within a polygon area within a specific time interval
     * @param area A Polygon object
     * @param from An Instant representing a point in time. Only locations at that time or later will be returned
     * @param to An Instant representing a point in time. Only locations at that time or earlier will be returned
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getLocationsInAreaAtTimeInterval(Polygon area, Instant from, Instant to) {
        List<Location> locations  = new ArrayList<>();

        Document query = new Document()
                            .append("location", getPolygonQuery(area))
                            .append("timestamp", new Document()
                                .append("$gte", new Date().from(from))
                                .append("$lte", new Date().from(to))
                            );

        FindIterable<Document> iterable = collection.find(query).sort(new Document("userId", 1));

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document)));
        }

        return locations;
    }

    /***
     * Get all locations for a specific user within a rectangle area and a defined time interval
     * @param area A Rectangle object
     * @param from An Instant representing a point in time. Only locations at that time or later will be returned
     * @param to An Instant representing a point in time. Only locations at that time or earlier will be returned
     * @param userId The user id as a String
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getLocationsInAreaAtTimeInterval(Rectangle area, Instant from, Instant to, String userId) {
        List<Location> locations = new ArrayList<>();

        Document query = new Document()
                            .append("location", getRectangleQuery(area))
                            .append("timestamp", new Document()
                                .append("$gte", new Date().from(from))
                                .append("$lte", new Date().from(to))
                            );

        FindIterable<Document> iterable = collection.find(query).sort(new Document("userId", 1));

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document)));
        }

        return locations;
    }

    /***
     * Get all locations within a circular area
     * @param coordinate A Coordinate object representing the center of the circle
     * @param radius A Double representing the radius of the spherical area in kilometers
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getAllLocationsInRadius(Coordinate coordinate, Double radius) {
        List<Location> locations    = new ArrayList<>();
        // Mongodb expects the radius in miles, hence the conversion
        Double radiusInMiles        = convertFromKmToMiles(radius);

        Document query = new Document()
                            .append("location", new Document()
                                .append("$geoWithin", new Document()
                                    .append("$centerSphere",
                                        asList(asList(coordinate.getLongitude(), coordinate.getLatitude()), radiusInMiles / 3963.2)
                                    )
                                )
                            );

        FindIterable<Document> iterable = collection.find(query);

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document)));
        }

        return locations;
    }

    /***
     * Get all locations within a circular area for specific users
     * @param coordinate A Coordinate object representing the center of the circle
     * @param radius A Double representing the radius of the circular area in kilometers
     * @param userIds A list of Strings representing user ids
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getAllLocationsInRadius(Coordinate coordinate, Double radius, List<String> userIds) {
        List<Location> locations    = new ArrayList<>();
        Double radiusInMiles        = convertFromKmToMiles(radius);

        Document query = new Document()
                            .append("userId", new Document("$in", userIds))
                            .append("location", new Document()
                                .append("$geoWithin", new Document()
                                    .append("$centerSphere",
                                        asList(asList(coordinate.getLongitude(), coordinate.getLatitude()), radiusInMiles / 3963.2)
                                    )
                                )
                            );

        FindIterable<Document> iterable = collection.find(query).sort(new Document("userId", 1));

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document)));
        }

        return locations;
    }

    /***
     * Get all locations within a circular area in a specific time interval
     * @param coordinate A Coordinate object representing the center of the circle
     * @param radius A Double representing the radius of the circular area in kilometers
     * @param from An Instant representing a point in time. Only locations at that time or later will be returned
     * @param to An Instant representing a point in time. Only locations at that time or earlier will be returned
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getAllLocationsInRadius(Coordinate coordinate, Double radius, Instant from, Instant to) {
        List<Location> locations    = new ArrayList<>();
        Double radiusInMiles        = convertFromKmToMiles(radius);

        Document query = new Document()
                            .append("location", new Document()
                                .append("$geoWithin", new Document()
                                    .append("$centerSphere",
                                        asList(asList(coordinate.getLongitude(), coordinate.getLatitude()), radiusInMiles / 3963.2)
                                    )
                                )
                            )
                            .append("timestamp", new Document()
                                .append("$gte", new Date().from(from))
                                .append("$lte", new Date().from(to))
                            );

        FindIterable<Document> iterable = collection.find(query);

        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document)));
        }

        return locations;
    }

    /***
     * Get the newest locations NOT in the defined circular area for specific users
     * @param coordinate A Coordinate object representing the center of the circle
     * @param radius A Double representing the radius of the circular area in kilometers
     * @param userIds A list of Strings representing user ids
     * @return A list of Location objects. If the query returns nothing an empty list will be returned
     */
    @Override
    public List<Location> getNewestLocationsOutsideRadius(Coordinate coordinate, Double radius, List<String> userIds) {
        List<Location> locations    = new ArrayList<>();
        Double radiusInMiles        = convertFromKmToMiles(radius);

        Document query = new Document()
                            .append("userId", new Document("$in", userIds))
                            .append("location", new Document()
                                .append("$not", new Document()
                                    .append("$geoWithin", new Document()
                                        .append("$centerSphere",
                                            asList(asList(coordinate.getLongitude(), coordinate.getLatitude()), radiusInMiles / 3963.2)
                                        )
                                    )
                                )
                            );

        Document sort       = getSortingFunction();

        Document matchDoc   = new Document("$match", query);
        Document sortDoc    = new Document("$sort", sort);
        Document groupDoc   = new Document("$group", getGroupingFunction());

        AggregateIterable<Document> iterable = collection.aggregate(asList(matchDoc, sortDoc, groupDoc));


        if (iterable.iterator().hasNext()) {
            iterable.forEach((Block<Document>) document -> locations.add(parseLocation(document, true)));
        }

        return locations;
    }

    /***
     * Connects to the database server based on the static server IP
     * @return A MongoClient object with a connection to the database server
     */
    private MongoClient connect() {
        if (mongoClient == null) {
            return new MongoClient(new MongoClientURI(this.mongoConnectionString));
        }
        return mongoClient;
    }

    /***
     * Helper function to parse location data which is when the data is not a result of a map / reduce query
     * @param locationDoc A locations as a BSON Document which must parsed to a Location object
     * @return A Location object
     */
    private Location parseLocation(Document locationDoc) { return parseLocation(locationDoc, false, false); }

    /***
     * Helper function to parse location data which is when the data is not a result of a map / reduce query
     * @param locationDoc A locations as a BSON Document which must parsed to a Location object
     * @param isAggregationDocument Boolean value indicating whether the location to be parsed is a result of a aggregation query
     * @return A Location object
     */
    private Location parseLocation(Document locationDoc, Boolean isAggregationDocument) { return parseLocation(locationDoc, false, isAggregationDocument); }

    /***
     * Parses the BSON Document returned from a query to a Location object
     * @param locationDoc BSON Document representing location data
     * @param isMapReduceDocument Boolean value indicating whether the location data is a result of a map / reduce query
     * @param isAggregationDocument Boolean value indicating whether the location data is a result of a aggregation query
     * @return A Location object
     */
    private Location parseLocation(Document locationDoc, Boolean isMapReduceDocument, Boolean isAggregationDocument) {
        ArrayList<Double> coordinates;
        Precision precision;
        Double precisionRadius;
        Double precisionUnit;
        Date timestamp;
        String userId;

        if (isMapReduceDocument && !isAggregationDocument){
            coordinates     = ((ArrayList<Double>)((Document)((Document)locationDoc.get("value")).get("location")).get("coordinates"));
            timestamp       = (Date)((Document)locationDoc.get("value")).get("timestamp");
            userId          = ((Document)locationDoc.get("value")).get("userId").toString();
            precisionRadius = ((Document)((Document)locationDoc.get("value")).get("precision")).getDouble("radius");
            precisionUnit   = ((Document)((Document)locationDoc.get("value")).get("precision")).getDouble("unit");

        } else if (isAggregationDocument && !isMapReduceDocument) {
            coordinates     = ((ArrayList<Double>)((Document)locationDoc.get("location")).get("coordinates"));
            timestamp       = (Date)locationDoc.get("timestamp");
            userId          = locationDoc.get("_id").toString();
            precisionRadius = ((Document)locationDoc.get("precision")).getDouble("radius");
            precisionUnit   = ((Document)locationDoc.get("precision")).getDouble("unit");

        } else if (isMapReduceDocument && isAggregationDocument) {
            ALogger.log("Mongodb. A result cannot be both a map / reduce and aggregation result.", Module.DB, Level.ERROR);
            throw new BusinessException();
        } else {
            coordinates     = ((ArrayList<Double>)((Document)locationDoc.get("location")).get("coordinates"));
            timestamp       = (Date)locationDoc.get("timestamp");
            userId          = locationDoc.get("userId").toString();
            precisionRadius = ((Document)locationDoc.get("precision")).getDouble("radius");
            precisionUnit   = ((Document)locationDoc.get("precision")).getDouble("unit");
        }

        Double longitude    = coordinates.get(0);
        Double latitude     = coordinates.get(1);

        if(precisionUnit != null && precisionRadius != null) {
            precision = new Precision(precisionUnit, precisionRadius);
        } else {
            ALogger.log("The Precision Object contains null values.", Module.DB, Level.ERROR);
            throw new BusinessException();
        }

        Instant instantTimestamp    = timestamp.toInstant();
        Coordinate coordinate       = new Coordinate(latitude, longitude);

        return new Location(coordinate, instantTimestamp, userId, precision);
    }

    /***
     * Helper function for creating the BSON Document to be used for querying for locations within a polygon
     * @param area A Polygon object
     * @return BSON Document representing the polygon query
     */
    private Document getPolygonQuery(Polygon area) {
        List<Coordinate> coordinates = area.getCoordinates();
        List coordinatePairs = new ArrayList<>();

        for (Coordinate coordinate : coordinates) {
            Double coordinatePair[] = new Double[]{coordinate.getLongitude(), coordinate.getLatitude()};
            coordinatePairs.add(asList(coordinatePair));
        }

        // Add the first element as the last to ensure the loop is closed
        coordinatePairs.add(coordinatePairs.get(0));

        return new Document()
                .append("$geoWithin", new Document()
                    .append("$geometry", new Document()
                        .append("type", "Polygon")
                        .append("coordinates",
                            asList(coordinatePairs)
                        )
                    )
                );
    }

    /***
     * Helper function for creating the BSON Document to be used for querying for locations within a rectangle
     * @param area A Rectangle object
     * @return BSON Document representing the rectangle query
     */
    private Document getRectangleQuery(Rectangle area) {
        Coordinate maxXYCoordinate = area.getMaxXYCoord();
        Coordinate minXYCoordinate = area.getMinXYCoord();

        Double maxLon = maxXYCoordinate.getLongitude();
        Double maxLat = maxXYCoordinate.getLatitude();
        Double minLon = minXYCoordinate.getLongitude();
        Double minLat = minXYCoordinate.getLatitude();

        return new Document()
                .append("$geoWithin", new Document()
                    .append("$geometry", new Document()
                        .append("type", "Polygon")
                        .append("coordinates",
                            asList(
                                asList(
                                    asList(minLon, maxLat),
                                    asList(maxLon, maxLat),
                                    asList(maxLon, minLat),
                                    asList(minLon, minLat),
                                    asList(minLon, maxLat)
                                )
                            )
                        )
                    )
                );
    }

    /***
     * Helper function for the sorting stage of aggregation queries
     * @return BSON Document containing the sorting function
     */
    private Document getSortingFunction() {
        return new Document()
                .append("userId", 1)
                .append("timestamp", -1);
    }

    /***
     * Helper function for the group stage of aggregation queries
     * @return BSON Document containing the grouping function
     */
    private Document getGroupingFunction() {
        return new Document()
                .append("_id", "$userId")
                .append("timestamp", new Document("$first", "$timestamp"))
                .append("location", new Document("$first", "$location"))
                .append("precision", new Document("$first", "$precision"));
    }

    /*
    /***
     * Mapping function used in the map / reduce queries
     * @return A mapping function as a String. Represents a Javascript function

    private String getMappingFunction() { return "function() {" + "emit(this.userId, this);" + "}"; }

    /***
     * Reduce function used in the map / reduce queries
     * @return A reduce function as a String. Represents a Javascript function

    private String getReduceFunction() {
        return "function reduce(userId, docs) {" +
                    "var mostRecent = new Date(0);" +
                    "var mostRecentDoc = null;" +
                    "docs.forEach(function(doc) {" +
                        "if (doc.timestamp > mostRecent) {" +
                            "mostRecent = doc.timestamp;" +
                            "mostRecentDoc = doc;" +
                        "}" +
                    "})" +
                    ";return mostRecentDoc;" +
                "}";
    }
    */

    /***
     * Convert a distance from kilometers to miles
     * @param distance A distance as a Double in kilometers which must be converted
     * @return A distance as a Double in miles
     */
    private Double convertFromKmToMiles(Double distance) { return distance / 1.609344; }
}