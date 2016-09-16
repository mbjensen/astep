package dk.aau.astep.appserver.business.service.outdoor.effFarming;

import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Coordinate;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Zobair on 12-04-2016.
 */
public abstract class Area {
    public ArrayList<Coordinate> getArea() {
        return area;
    }
    public Polygon getPolyArea()  {return polyArea;}

    private LinkedList<Coordinate> sortedArea;
    private ArrayList<Coordinate> area;
    private Polygon polyArea;

    private double xDimension = 0;
    private double yDiemnsion = 0;

    /**
     * Initializes a new instance of the Area class. And chekcs if valid area
     * (ie. has at least four points and is not null) otherwise throws Illegal argument exception
     * @param area a list of at least four elements
     */
    public Area(ArrayList<Coordinate> area) {
        if(area == null)
            throw new IllegalArgumentException("Area list cannot be null");
        if(area.contains(null))
            throw new IllegalArgumentException("Area list cannot contain a null value");
        if(area.size() < 4)
            throw new IllegalArgumentException("Area list must contain at least four elements");
        this.area = area;
        polyArea = new Polygon(area);
        //TODO implement/fix findShapeOfArea();
    }

    /**
     * Defines the shape of the area!
     */
    protected void findShapeOfArea() {
        //TODO test
        //start by choosing a point
        AbstractMap.SimpleEntry<Coordinate, Coordinate> diagonalPoints = findMidPoint();
        if(diagonalPoints.equals(null))
            throw new NullPointerException("Didn't succesfully find a diagonalpoint!");
        Coordinate initialPoint = diagonalPoints.getKey();
        Coordinate oppositePoint = diagonalPoints.getValue();
        ArrayList<Coordinate> tempList = area;
        area.remove(initialPoint);
        area.remove(oppositePoint);
        sortedArea.add(initialPoint);
        //choose point that is NOT opposite
        sortedArea.add(tempList.get(0));
        sortedArea.add(oppositePoint);
        //choose last point
        sortedArea.add(tempList.get(tempList.size()-1));

    }

    /**
     * @return Key value pair of two diagonal points
     */
    private AbstractMap.SimpleEntry<Coordinate, Coordinate> findMidPoint() {
        //TODO test
        ArrayList<Coordinate> midPoints = new ArrayList<>();
        for(int i = 0; i < area.size(); i++) {
            Coordinate currI = area.get(i);
            for(int y = 0; y < area.size(); y++) {
                if(!area.get(y).equals(currI)) {
                    Coordinate midPoint = findMidPointBetweenTwoPoints(currI, area.get(y));
                    if(midPoints.contains(midPoint))
                        return new AbstractMap.SimpleEntry<Coordinate, Coordinate>(currI, area.get(y));

                    midPoints.add(midPoint);
                }
            }
        }
        return null;
    }

    /**
     * Finds a point midway between point X and Y
     * @param pointX Coordinate
     * @param pointY Coordinate
     * @return the mid point
     */
    private Coordinate findMidPointBetweenTwoPoints(Coordinate pointX, Coordinate pointY) {
        //TODO tests
        //We find vector between point x and y and divide it in half, then apply that vector to point x and we have the mid point between x and y
        Coordinate vector = new Coordinate((pointX.getLatitude()-pointY.getLatitude())/2, (pointX.getLongitude()-pointY.getLongitude())/2);
        return new Coordinate(pointX.getLatitude()+vector.getLatitude(), pointX.getLongitude()+vector.getLongitude());
    }
}
