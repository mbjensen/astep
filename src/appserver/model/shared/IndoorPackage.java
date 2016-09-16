package dk.aau.astep.appserver.model.shared;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.time.*;

/**
 * Created by Anders on 30-03-16.
 */
public class IndoorPackage extends Location{
    boolean currentlyTracked,
            isGuestUser;
    float confidenceFactor;
    List<String> ipAddress;
    String macAddress,
            ssId,
            band,
            apMacAddress,
            dot11Status;
    String mapName,
            floorRefId;
    float width,
            height,
            offsetX,
            offsetY;
    String offsetUnit;
    float mapX,
            mapY;
    String mapUnit;
    String currentServerTime,
            firstLocatedTime,
            lastLocatedTime;

    public  IndoorPackage(Coordinate coordinate, Instant timeStamp,
                          String username, Precision precision,
                          boolean currentlyTracked, float confidenceFactor,
                          List<String> ipAddress, String ssId, String band,
                          String apMacAddress,  String dot11Status,
                          boolean isGuestUser, String mapName,
                          String floorRefId, float width, float height,
                          float offsetX, float offsetY, String offsetUnit,
                          float mapX, float mapY, String mapUnit,
                          String currentServerTime, String firstLocatedTime,
                          String lastLocatedTime) {

        super(coordinate, timeStamp, username, precision);

        this.currentlyTracked = currentlyTracked;
        this.confidenceFactor = confidenceFactor;
        this.ipAddress = ipAddress;
        this.macAddress = apMacAddress;
        this.ssId = ssId;
        this.band = band;
        this.apMacAddress = apMacAddress;
        this.dot11Status = dot11Status;
        this.isGuestUser = isGuestUser;
        this.mapName = mapName;
        this.floorRefId = floorRefId;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetUnit = offsetUnit;
        this.mapX = mapX;
        this.mapY = mapY;
        this.mapUnit = mapUnit;
        this.currentServerTime = currentServerTime;
        this.firstLocatedTime = firstLocatedTime;
        this.lastLocatedTime = lastLocatedTime;
    }
}
