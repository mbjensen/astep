package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.exception.BusinessException;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;

import javax.xml.bind.annotation.*;
import java.time.Instant;

import java.util.Date;
import java.util.List;

/**
 * Created by carsten on 06/04/2016.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Route implements dk.aau.astep.db.persistent.data.Route<Location>{
    public List<Location> locations;
    public boolean is_stable;
    public Instant timestamp;
    public String username;
    public int id;



    /**
     * Empty constructor for Route used by MOXy
     */
    private Route()  {}

    //TODO create parameter validation

    /**
     * The constructor of a Route.
     * @param locations A list of locations corresponding to the route.
     * @param isStable A flag telling if the route is stable.
     * @param timeStamp A timestamp of when the route was last driven.
     * @param id An id of the route.
     */
    public Route(List<Location> locations, boolean isStable, Instant timeStamp, int id)
    {
        if(locations != null && locations.size() > 1) {
            this.locations = locations;
        }
        else{
            ALogger.log("Route: A route must contain atleast 2 locations", Module.OD, Level.ERROR);
            throw new BusinessException();
        }
        //TODO create parameter validation
        this.is_stable = isStable;
        this.timestamp = timeStamp;
        this.id=id;
        this.username = locations.get(0).getUsername();
    }

    @XmlElement
    public String getUsername() {
        return this.username;
    }

    @XmlElement
    @Override
    public List<Location> getLocations() {
        return this.locations;
    }

    @Override
    public boolean isStable(){
        return this.is_stable;
    }

    @Override
    public Instant getTimestamp() {
        return this.timestamp;
    }

    public void setStable(boolean stable) {
        is_stable = stable;
    }

    @Override
    public int getId() {
        return id;
    }
}
