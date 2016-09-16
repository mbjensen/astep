package dk.aau.astep.appserver.business.service.outdoor.tempdb;

import dk.aau.astep.appserver.model.outdoor.Route;
import dk.aau.astep.appserver.model.outdoor.RouteMatch;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import dk.aau.astep.db.persistent.api.RouteQueries;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morten on 11/05/2016.
 */
public class PerformanceTestDataRoutes implements RouteQueries<Coordinate, Location, Route> {

    List<Location> DBAnswer = new ArrayList<Location>() {{
        double j = 0.000001;

        for (int i = 0; i < 200; i++) {
            add(new Location(new Coordinate(57.037200, 9.911690 + j), Instant.now(), "svend", new Precision(68, 5.43f + j)));
            j += 0.000001;
        }
    }};

    List<Location> DBAnswerOne = new ArrayList<Location>() {{
        double j = 0.000001;

        for (int i = 0; i < 200; i++) {
            add(new Location(new Coordinate(57.037200, 9.911690 + j), Instant.now(), "svend", new Precision(68, 5.43f + j)));
            j += 0.000001;
        }
    }};

    List<Location> DBAnswerTwo = new ArrayList<Location>() {{
        double j = 0.000001;

        for (int i = 0; i < 200; i++) {
            add(new Location(new Coordinate(57.037200, 9.911690 + j), Instant.now(), "svend", new Precision(68, 5.43f + j)));
            j += 0.000001;
        }
    }};

    List<Location> DBAnswerThree = new ArrayList<Location>() {{
        double j = 0.000001;

        for (int i = 0; i < 200; i++) {
            add(new Location(new Coordinate(57.037200, 9.911690 + j), Instant.now(), "svend", new Precision(68, 5.43f + j)));
            j += 0.000001;
        }
    }};

    // Dummy data.
    private Route routeOne = new Route(DBAnswer, true, Instant.now(), 2);
    private Route routeTwo = new Route(DBAnswerOne, true, Instant.now(), 3);
    private Route routeThree = new Route(DBAnswerTwo, true, Instant.now(), 4);
    private Route routeFour = new Route(DBAnswerThree, true, Instant.now(), 5);

    private List<Route> routeData = new ArrayList<Route>(){{
        add(routeOne);
        add(routeTwo);
        add(routeThree);
        add(routeFour);
    }};

    RouteMatch match1 = new RouteMatch(routeOne, routeTwo, 0.5);
    RouteMatch match2 = new RouteMatch(routeThree, routeTwo, 0.7);
    RouteMatch match3 = new RouteMatch(routeFour, routeOne, 0.2);
    RouteMatch match4 = new RouteMatch(routeThree, routeOne, 0.9);

    private List<RouteMatch> matchData = new ArrayList<RouteMatch>(){{
        add(match1);
        add(match2);
        add(match3);
        add(match4);
    }};


    public PerformanceTestDataRoutes() {

    }

    @Override
    public void saveRoute(List<Location> list, String s, Instant instant, boolean b) throws IOException {

    }

    @Override
    public List<Route> getAllRoutes(List<String> usernames, Instant timestamp, boolean stable) {
        return routeData;
    }

    @Override
    public boolean updateTimestampOnRoute(Route routeToUpdate, Instant newTime) {
        return false;
    }
}
