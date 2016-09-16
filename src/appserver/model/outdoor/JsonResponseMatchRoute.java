package dk.aau.astep.appserver.model.outdoor;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.Instant;
import java.util.List;

/**
 * Created by Morten on 14/04/2016.
 */

@XmlRootElement
public class JsonResponseMatchRoute {
    public List<RouteMatch> route_matches;

    /**
     * Empty constructor for JsonResponseMatchRoute used by MOXy
     */
    public JsonResponseMatchRoute() {
    }

    public JsonResponseMatchRoute(List<RouteMatch> route_matches) {
        this.route_matches = route_matches;
    }

    public List<RouteMatch> getRouteMatches() {
        return this.route_matches;
    }
}
