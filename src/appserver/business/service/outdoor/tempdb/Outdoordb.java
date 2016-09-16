package dk.aau.astep.appserver.business.service.outdoor.tempdb;

import com.mongodb.MongoClient;
import dk.aau.astep.appserver.dataaccess.api.queries.HotLocationQueries;
import dk.aau.astep.appserver.model.outdoor.Rectangle;
import dk.aau.astep.appserver.model.outdoor.Route;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.exception.BusinessException;
import org.mockito.internal.matchers.Not;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

//Connection to a Temporary MySQL database
public class Outdoordb extends DBWrapper {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://62.107.80.214/astep";//Private server

    //  Database credentials
    static final String USER = "astep";
    static final String PASS = "astep";

    Statement stmt=null;

    Connection conn;
    public Outdoordb() {
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            this.conn = DriverManager.getConnection(this.DB_URL,this.USER,this.PASS);
        }
        catch(Exception e){
            System.out.println("Exception is " + e.getMessage());
        }
    }

    private long saveLocationTemp(Location location) {
        try {
            stmt = this.conn.createStatement();

            String sql = "INSERT INTO location (latitude, longitude, prec, prec2, username, timestamps) " +
                    " VALUES ("+
                    location.getCoordinate().getLatitude()+", " +
                    location.getCoordinate().getLongitude()+", " +
                    location.getPrecision().getRadius()+", " +
                    location.getPrecision().getUnit()+", '" +
                    location.getUsername()+"','" +
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.ofInstant(location.getTimestamp(), ZoneId.of("Z")))+"');";
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }
        return 0;
    }

    /*
    @Override
    public void saveRoute(List<Location> list, String s, Instant instant, boolean b) throws IOException {

    }*/
    /** Routes **/
    @Override
    public void saveRoute(List<Location> locations, String username, Instant timestamp, boolean stable) {
        //Save route
        long routeId=0;
        try {
            stmt = this.conn.createStatement();

            String sql = "INSERT INTO routes (timestamps, stable, username) " +
                    " VALUES ('"+
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.ofInstant(locations.get(0).getTimestamp(), ZoneId.of("Z")))+"', " +
                    stable+", '" +
                    locations.get(0).getUsername()+"');";
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    routeId= generatedKeys.getLong(1);
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }
        for(Location location: locations) {
            long locationId = saveLocationTemp(location);
            saverouteRelation(locationId, routeId);
        }
    }

    private void saverouteRelation(long locationId, long routeId) {
        //Save route
        try {
            stmt = this.conn.createStatement();

            String sql = "INSERT INTO routerelations (routeId, locationId) " +
                    " VALUES ("+
                    routeId+", " +
                    locationId+")";
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }
    }

    @Override
    public List<Route> getAllRoutes(List<String> usernames, Instant timestamp, boolean stable) {
        List<Route> routes = new ArrayList<>();
        Location location=null;

        try {
            //Start by getting all the routes
            stmt = this.conn.createStatement();
            String sql;
            sql="SELECT id, stable, DATE_FORMAT(timestamps, '%Y-%m-%dT%TZ') AS timestamps FROM `routes` WHERE timestamps>'"+timestamp.toString()+"' AND (";
            for (String user : usernames) {
                sql=sql+"username='"+user+"' OR ";
            }
            if(stable) {
                sql=sql+" 1=2) AND stable=TRUE;";
            }else {
                sql=sql+" 1=2) AND stable=FALSE;";
            }
            ResultSet result = stmt.executeQuery(sql);

            List<Boolean> stableValues = new ArrayList<>();
            List<Integer> idValues = new ArrayList();
            List<String> timestampsValues = new ArrayList();
            while (result.next()) {
                stableValues.add(result.getBoolean("stable"));
                idValues.add(result.getInt("id"));
                timestampsValues.add(result.getString("timestamps"));
            }
            for(int i=0;i<stableValues.size();i++) {
                List<Location> locations = new ArrayList<>();
                //For each route lets get the locations
                sql="SELECT location.latitude, location.longitude, location.username, location.prec2, location.prec, DATE_FORMAT(location.timestamps, '%Y-%m-%dT%TZ') AS timestamps " +
                        "FROM `location` JOIN routerelations ON routerelations.locationId=location.id WHERE routerelations.routeId="+idValues.get(i)+";";

                ResultSet resultLocatins = stmt.executeQuery(sql);
                while(resultLocatins.next()){
                    location = new Location(new Coordinate(resultLocatins.getDouble("latitude"), resultLocatins.getDouble("longitude")),
                            Instant.parse(resultLocatins.getString("timestamps")), resultLocatins.getString("username"), new Precision(resultLocatins.getFloat("prec2"), resultLocatins.getFloat("prec")));
                    locations.add(location);
                }
                //Save the route
                routes.add(new Route(locations, stableValues.get(i), Instant.parse(timestampsValues.get(i)), idValues.get(i)));
            }
        }catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }
        return routes;
    }

    @Override
    public boolean updateTimestampOnRoute(Route routeToUpdate, Instant newTime) {
        try {
            stmt = this.conn.createStatement();
            String sql = "UPDATE routes " +
                    "SET timestamps='"+newTime.toString()+
                    "' WHERE id="+routeToUpdate.id+";";
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }
        return false;
    }

    /*Methods from the interface*/
    @Override
    public boolean saveLocation(Location location) {
        if(saveLocationTemp(location)!= 0 ){
            return true;
        }
        else {
            return false;
        }
    }
    @Override
    public boolean saveLocations(List<Location> locations) {
        for(Location location: locations) {
            if(saveLocationTemp(location) == 0){
                return false;
            }
        }
        return true;
    }
    @Override
    public List<Location> getNewestLocationsInArea(Rectangle boundingBox, List<String> usernames) {
        Coordinate minXYBound = boundingBox.getMinXYCoord();
        Coordinate maxXYBound = boundingBox.getMaxXYCoord();
        List<Location> returnData = new ArrayList<Location>();
        try {
            stmt = this.conn.createStatement();
            //TODO: May only get friends and himself
            String sql = "SELECT latitude, longitude, prec, prec2, username, MAX(DATE_FORMAT(timestamps, '%Y-%m-%dT%TZ')) AS timestamps" +
                    " FROM location" +
                    " WHERE";
            for (String user : usernames) {
                sql=sql+" username='"+user+"' OR";
            }
            sql=sql+" 1=2" +
                    " AND latitude>" + minXYBound.getLatitude() + " AND latitude<" + maxXYBound.getLatitude() +
                    " AND longitude>" + minXYBound.getLongitude() + " AND longitude<" + maxXYBound.getLongitude()
                    + " GROUP BY username ORDER BY timestamps DESC;";
            ResultSet result = stmt.executeQuery(sql);

            while (result.next()) {
                returnData.add(new Location(new Coordinate(result.getDouble("latitude"), result.getDouble("longitude")),
                        Instant.parse(result.getString("timestamps")), result.getString("username"), new Precision(result.getFloat("prec2"), result.getFloat("prec"))));
            }
        } catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }
        return returnData;
    }
    @Override
    public List<Location> getNewestLocationsOutsideArea(Rectangle boundingBox, List<String> usernames) {
        Coordinate minXYBound = boundingBox.getMinXYCoord();
        Coordinate maxXYBound = boundingBox.getMaxXYCoord();
        List<Location> returnData = new ArrayList<Location>();
        try {
            stmt = this.conn.createStatement();
            //TODO: May only get friends and himself
            String sql = "SELECT latitude, longitude, prec, prec2, username, MAX(DATE_FORMAT(timestamps, '%Y-%m-%dT%TZ')) AS timestamps" +
                    " FROM location" +
                    " WHERE";
            for (String user : usernames) {
                sql=sql+" username='"+user+"' OR";
            }
            sql=sql+" 1=2" +
                    " AND latitude<" + minXYBound.getLatitude() + " AND latitude>" + maxXYBound.getLatitude() +
                    " AND longitude<" + minXYBound.getLongitude() + " AND longitude>" + maxXYBound.getLongitude()
                    + " GROUP BY username ORDER BY timestamps DESC;";
            ResultSet result = stmt.executeQuery(sql);

            while (result.next()) {
                returnData.add(new Location(new Coordinate(result.getDouble("latitude"), result.getDouble("longitude")),
                        Instant.parse(result.getString("timestamps")), result.getString("username"), new Precision(result.getFloat("prec2"), result.getFloat("prec"))));
            }
        } catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }
        return returnData;
    }

    @Override
    public List<Location> getNewestLocationsFromTime(List<String> usernames, Instant timestamp) {
        List<Location> returnData = new ArrayList<Location>();
        try {
            stmt = this.conn.createStatement();
            String sql = "SELECT latitude, longitude, prec, prec2, username, MAX(DATE_FORMAT(timestamps, '%Y-%m-%dT%TZ')) AS timestamps" +
                    " FROM location" +
                    " WHERE (";
            for (String user : usernames) {
                sql=sql+" username='"+user+"' OR";
            }
            sql=sql+" 1=2" +
                    ") AND timestamps>'"+timestamp.toString()+"' GROUP BY username ORDER BY timestamps DESC;";
            ResultSet result = stmt.executeQuery(sql);

            while (result.next()) {
                returnData.add(new Location(new Coordinate(result.getDouble("latitude"), result.getDouble("longitude")),
                        Instant.parse(result.getString("timestamps")), result.getString("username"), new Precision(result.getFloat("prec2"), result.getFloat("prec"))));
            }
        } catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }

        return returnData;
    }
    @Override
    public List<Location> getLocationsAtTimeInterval(String username, Instant periodStart, Instant periodEnd) {
        List<Location> returnData=new ArrayList<Location>();
        try {
            stmt = this.conn.createStatement();
            String sql = "SELECT latitude, longitude, prec, prec2, username, DATE_FORMAT(timestamps, '%Y-%m-%dT%TZ') AS timestamps " +
                    "FROM location " +
                    "WHERE  username='"+username+"' AND timestamps<'"+periodEnd.toString()+"' AND timestamps>'"+periodStart.toString()+"';";
            ResultSet result = stmt.executeQuery(sql);

            while (result.next()) {
                returnData.add(new Location(new Coordinate(result.getDouble("latitude"), result.getDouble("longitude")),
                        Instant.parse(result.getString("timestamps")), result.getString("username"), new Precision(result.getFloat("prec2"), result.getFloat("prec"))));
            }
        }
        catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }
        return returnData;
    }

    @Override
    public List<Location> getNewestLocation(List<String> usernames) {
        List<Location> returnData = new ArrayList<Location>();
        try {
            stmt = this.conn.createStatement();
            String sql = "SELECT latitude, longitude, prec, prec2, username, MAX(DATE_FORMAT(timestamps, '%Y-%m-%dT%TZ')) AS timestamps" +
                    " FROM location" +
                    " WHERE";
            for (String user : usernames) {
                sql=sql+" username='"+user+"' OR";
            }
            sql=sql+" 1=2" +
                    " GROUP BY username ORDER BY timestamps DESC;";
            ResultSet result = stmt.executeQuery(sql);

            while (result.next()) {
                returnData.add(new Location(new Coordinate(result.getDouble("latitude"), result.getDouble("longitude")),
                        Instant.parse(result.getString("timestamps")), result.getString("username"), new Precision(result.getFloat("prec2"), result.getFloat("prec"))));
            }
        } catch (SQLException e) {
            System.out.println("Exception is " + e.getMessage());
        }
        return returnData;
    }

    @Override
    public List<Location> getLocationsInAreaAtTimeInterval(Rectangle area, Instant from, Instant to, String userId){throw new NotImplementedException();}
    @Override
    public List<Location> getAllLocations(String userId){throw new NotImplementedException();}
    @Override
    public Location getNewestLocation (String userId){throw new NotImplementedException();}
    @Override
    public List<Location> getNewestLocationsInArea (Polygon area) {throw new NotImplementedException();}
    @Override
    public List<Location> getLocationsInAreaAtTimeInterval(Polygon area, Instant from, Instant to) {throw new NotImplementedException();}
    @Override
    public List<Location> getAllLocationsInRadius (Coordinate coordinate, Double radius){throw new NotImplementedException();}
    @Override
    public List<Location> getAllLocationsInRadius (Coordinate coordinate, Double radius, List<String> userIds){throw new NotImplementedException();}
    @Override
    public List<Location> getAllLocationsInRadius (Coordinate coordinate, Double radius, Instant from, Instant to){throw new NotImplementedException();}
    @Override
    public List<Location> getNewestLocationsOutsideRadius (Coordinate coordinate, Double radius, List<String> userIds){throw new NotImplementedException();}


}