package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.appserver.restapi.api.ParamCheck;
import dk.aau.astep.exception.ApiExceptionHandler;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.Instant;

//TODO general clean up this location class..... 25/05/2016
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Location implements dk.aau.astep.db.persistent.data.Location<Coordinate> {
    private Coordinate coordinate;
    private String username;
    private Precision precision;
    private Instant timestamp;

    /**
     * Empty JavaBean constructor used by MOXy
     */
    private Location() {
    }

    public Location(Coordinate coordinate, Instant timestamp, String username, Precision precision) {
        validateAndSave(coordinate, precision, timestamp, username, false);
    }

    // Constructor for parsing REST QueryParam
    @Deprecated // TODO the precision's unit is set as static, this does not work... 25/05/2016
    public Location(String location) {
        double latitude;
        double longitude;
        String username;
        Precision precision;
        Instant timestamp;

        try {
            String part[] = location.split(";");
            System.out.println(location);
            if (part.length != 5) {
                throw new ApiExceptionHandler().locationWrongFormat();
            }
            latitude = Double.parseDouble(part[0].replaceAll("[^\\d.]", ""));
            longitude = Double.parseDouble(part[1].replaceAll("[^\\d.]", ""));
            username = part[2];
            precision = new Precision(0, Float.parseFloat(part[3].replaceAll("[^\\d.]", "")));
            timestamp = Instant.parse(part[4]);
        } catch (Exception e) {
            throw new ApiExceptionHandler().locationWrongFormat();
        }

        validateAndSave(new Coordinate(latitude, longitude, true), precision, timestamp, username, true);
    }

    @XmlElement
    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    @XmlElement
    public String getUsername() {
        return this.username;
    }

    @XmlElement
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @XmlElement
    public Precision getPrecision() {
        return this.precision;
    }

    @Override
    public double getUnit() {
        return this.precision.getUnit();
    }

    @Override
    public double getRadius() {
        return this.precision.getRadius();
    }

    /**
     * A function to validate all input to ensure a valid location
     *
     * @param coordinate   Coordinates fot he location
     * @param precision    The precision of the coordinates
     * @param timestamp    A timestsamp fot then the coordinates was saved
     * @param username     The username of the location
     * @param apiException a boolean say if a api (TRUE) or Business (FALSE) exception should be thrown if a error is found
     */
    private void validateAndSave(Coordinate coordinate, Precision precision, Instant timestamp, String username, boolean
            apiException) {

        try {
            ParamCheck.checkCoordinate(coordinate);
            ParamCheck.checkPrecision(precision);
            //ParamCheck.checkTime(timestamp);
            ParamCheck.checkIfUsernameIsNull(username);
        } catch (ApiExceptionHandler e) {
            if (apiException == true) {
                throw e;
            } else {
                ALogger.log("The location validation went wrong. Exception message: " + e.getMessage(), Module.OD, Level.ERROR);
            }
        }

        this.coordinate = coordinate;
        this.username = username;
        this.timestamp = timestamp;
        this.precision = precision;
    }

    // TODO update the toString and Equal methods, cause they are outdated.... 25/05/2016
    @Deprecated
    @Override
    public String toString() {
        return timestamp + " " + coordinate.toString();
    }

    @Deprecated
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !Location.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        Location location = (Location) obj;
        return this.username.equals(location.getUsername()) && this.timestamp.equals(location.getTimestamp()) &&
                this.coordinate.equals(location.getCoordinate());
    }

}