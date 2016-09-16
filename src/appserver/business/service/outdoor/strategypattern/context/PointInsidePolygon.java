package dk.aau.astep.appserver.business.service.outdoor.strategypattern.context;


import dk.aau.astep.appserver.business.service.outdoor.strategypattern.interfaces.IPointInsidePolygonStrategy;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;

import java.util.ArrayList;
import java.util.List;

public class PointInsidePolygon {
    private IPointInsidePolygonStrategy strategy;

    public PointInsidePolygon(IPointInsidePolygonStrategy strategy){
        this.strategy = strategy;
    }

    /**
     *
     * @param point The point to check if it is inside the polygon
     * @param polygon The polygon
     * @param isInside Boolean flag which determine if the algorithm checks if the point is inside or outside.
     * @return if isInside is true the function returns true when the point is inside the polygon, if isInside is false then it return true if the point is outside the function.
     */
    public boolean isPointInsidePolygon(Coordinate point, Polygon polygon, boolean isInside){
        if(isInside)
            return this.strategy.isInsidePolygon(point, polygon);
        else
            return !this.strategy.isInsidePolygon(point, polygon);
    }

    /**
     *
     * @param locationsToTest A list of points to check if it is inside the polygon
     * @param polygon The polygon
     * @param isInside Boolean flag which determine if the algorithm checks if the point is inside or outside.
     * @return If isInside is true then it returns all locations which is inside, if isInside is false then it returns all locations which is false.
     */
    public List<Location> pointsInOrOutsidePolygon(List<Location> locationsToTest, Polygon polygon, boolean isInside) {
        // lat is used as x and long is used as y.
        List<Location> locationsInside = new ArrayList<Location>();
        int j = 0;

        for (Location l : locationsToTest) {
            // Foreach location check if it is inside the polygon.
            if (this.isPointInsidePolygon(locationsToTest.get(j).getCoordinate(), polygon, isInside)) {
                locationsInside.add(locationsToTest.get(j));
            }
            j++;
        }
        return locationsInside;
    }
}
