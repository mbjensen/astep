package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.exception.BusinessException;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;

import javax.xml.bind.annotation.*;

import java.time.Instant;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class RouteMatch {
    private double score;
    private Instant time_match_found;
    private Route route_one, route_two;

    /**
     * Empty constructor for RouteMatch used by MOXy
     */
    private RouteMatch() {}

    public RouteMatch(Route route_one, Route route_two, double score){
        if(route_one == null ){
            ALogger.log("RouteMatch: route1 must not be null", Module.OD, Level.ERROR);
            throw new BusinessException();
        }
        else if(route_two == null){
            ALogger.log("RouteMatch: route2 must not be null", Module.OD, Level.ERROR);
            throw new BusinessException();
        }
        this.time_match_found = Instant.now();
        this.route_one = route_one;
        this.route_two = route_two;
        this.score = score;

    }

    @XmlElement
    public double getScore() {
        return score;
    }

    @XmlElement(name="time_match_found")
    public float getTime_match_found() {
        return this.time_match_found.toEpochMilli();
    }

    @XmlElement
    public Route getRoute_one() {
        return route_one;
    }

    @XmlElement
    public Route getRoute_two() {
        return route_two;
    }

    public Instant getTimeMatchFoundInstant() {
        return time_match_found;
    }
}
