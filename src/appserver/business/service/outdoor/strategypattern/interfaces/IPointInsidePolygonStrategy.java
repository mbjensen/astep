package dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces;

import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Coordinate;

public interface IPointInsidePolygonStrategy {
    public boolean isInsidePolygon(Coordinate point, Polygon polygon);
}
