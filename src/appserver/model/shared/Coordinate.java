package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.exception.ApiExceptionHandler;
import dk.aau.astep.exception.BusinessException;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Coordinate implements dk.aau.astep.db.persistent.data.Coordinate {
    private Double latitude;
    private Double longitude;

    /**
     * Empty JavaBean constructor used by MOXy
     */
    private Coordinate() { }

    public Coordinate(double latitude, double longitude) {
        validateAndSave(latitude, longitude, false);
    }

    public Coordinate(double latitude, double longitude, boolean apiException) {
        validateAndSave(latitude, longitude, apiException);
    }

    /**
     * Constructor for parsing rest QueryParam
     *
     * @param coordinate
     */
    public Coordinate(String coordinate) {
        double latitude;
        double longitude;
        try {
            String part[] = coordinate.split(";");
            if (part.length != 2) {
                throw new ApiExceptionHandler().coordinateWrongFormat();
            }
            latitude = Double.parseDouble(part[0].replaceAll("[^\\d.]", ""));
            longitude = Double.parseDouble(part[1].replaceAll("[^\\d.]", ""));

        } catch (NumberFormatException e) {
            throw new ApiExceptionHandler().coordinateWrongFormat();
        }
        validateAndSave(latitude, longitude, true);
    }

    private void validateAndSave(double latitude, double longitude, boolean apiException) {
        if (latitude > 90 || latitude < -90) {
            if (apiException) {
                throw new ApiExceptionHandler().locationOutOfBounds();
            } else {
                ALogger.log("Latitude is out of bound", Module.OD, Level.ERROR);
                throw new BusinessException();
            }

        } else if (longitude > 180 || longitude < -180) {
            if (apiException) {
                throw new ApiExceptionHandler().locationOutOfBounds();
            } else {
                ALogger.log("Longitude is out of bound", Module.OD, Level.ERROR);
                throw new BusinessException();
            }
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @XmlElement
    public double getLatitude() {
        return latitude;
    }

    @XmlElement
    public double getLongitude() {
        return longitude;
    }

    public double getLongitudeAsRadian() {
        return Math.toRadians(longitude);
    }

    public double getLatitudeAsRadian() {
        return Math.toRadians(latitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !Coordinate.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        Coordinate coordinate = (Coordinate) obj;
        return this.latitude.equals(coordinate.latitude) && this.longitude.equals(coordinate.longitude);
    }

    @Override
    public String toString() {
        return "(" + Double.toString(getLatitude()) + "," + Double.toString(getLongitude()) + ")";
    }

}
