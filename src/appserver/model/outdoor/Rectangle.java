package dk.aau.astep.appserver.model.outdoor;

import dk.aau.astep.appserver.model.shared.Coordinate;

/**
 * Created by carsten on 13/04/2016.
 */
public class Rectangle {
    private Coordinate minXYCoord, maxXYCoord;

    public Rectangle(Coordinate minXYCoord, Coordinate maxXYCoord){
        this.minXYCoord = minXYCoord;
        this.maxXYCoord = maxXYCoord;
    }

    /**
     *
     * @return Returns a coordiante with the minimum lat and long of the rectangle.
     */
    public Coordinate getMinXYCoord() {
        return minXYCoord;
    }

    /**
     *
     * @return Returns a coordiante with the maximum lat and long of the rectangle.
     */
    public Coordinate getMaxXYCoord() {
        return maxXYCoord;
    }
}
