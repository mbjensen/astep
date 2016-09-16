package dk.aau.astep.appserver.business.service.outdoor.effFarming;

import dk.aau.astep.appserver.model.shared.Coordinate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

//for debug
import java.time.*;
import java.io.FileOutputStream;
import java.io.File;

/**
 * Created by Zobair on 12-04-2016.
 */
public class FarmingArea extends Area {

    //Length of x and y axis
    private double yDimension;
    private double xDimension;
    private double harvesterWidth = 0;

    private int numberOfXNodes = 10;

    private ArrayList<Obstacle> obstacles;


    public List<Node> getGraph() {
        return graph;
    }

    private List<Node> graph; //TODO decide on list implementation to use
    /**
     * Initializes a new instance of the FarmingArea class, with obstacles defined.
     *
     * @param area
     */
    public FarmingArea(ArrayList<Coordinate> area, ArrayList<Obstacle> ob, double harvesterWidth) {
        super(area);
        obstacles = ob;
        this.harvesterWidth = harvesterWidth;
    }

    /**
     * Initializes a new instance of the FarmingArea class.
     *
     * @param area
     */
    public FarmingArea(ArrayList<Coordinate> area, double harvesterWidth) {
        super(area);
        this.harvesterWidth = harvesterWidth;
        //constructGraph();
        graph = new ArrayList<>(); //** Or whatever implementation to choose
        xDimension = area.get(0).getLongitude() - area.get(3).getLongitude();
        constructGraphSquare(new ArrayList<>(area));
        //printGraphToCSVFile();
    }

    private void constructGraphSquare(ArrayList<Coordinate> areaP) {

        //Base case
        fillRow(areaP);
        //

        //move bottom edge up (ie point0 -> point3)
        //determine number of nodes on point0->point1 and point3->point2
        double vPoint0ToPoint1Y = areaP.get(1).getLatitude() - areaP.get(0).getLatitude();
        double vPoint3ToPoint2Y = areaP.get(2).getLatitude() - areaP.get(3).getLatitude();
        double numberOfNodes0 = vPoint0ToPoint1Y/harvesterWidth;
        double numberOfNodes3 = vPoint3ToPoint2Y/harvesterWidth;
        //
        Coordinate newPoint0 = movePointUpOnVector(areaP.get(0), areaP.get(1), numberOfNodes0);
        Coordinate newPoint3 = movePointUpOnVector(areaP.get(3), areaP.get(2), numberOfNodes3);
        //

        //decide whether to lower number of nodes for this row
        //??
        //

        //speciale cases
        //CASE: point0 latitude + harvest width is higher than point1 AND theres still space between point3 and point2
        if((newPoint0.getLatitude()+harvesterWidth > areaP.get(1).getLatitude()) && !(newPoint3.getLatitude() >= areaP.get(2).getLatitude())) {
            if(newPoint0.getLatitude() > newPoint3.getLatitude()+harvesterWidth) { //if point0 is above point3 we want to move along the point0->point3 axis
                newPoint0 = movePointUpOnVector(areaP.get(0), areaP.get(3), numberOfXNodes);;
            }else {
                Coordinate temp = movePointUpOnVector(areaP.get(1), areaP.get(2), numberOfXNodes);
                //if((temp.getLatitude()-harvesterWidth)
                areaP.remove(1); areaP.add(1, temp);
                newPoint0 = new Coordinate(temp.getLatitude() - (harvesterWidth), temp.getLongitude()); //if point0+harvestwidth we want to fill in the node that can just barerly fit ie.point1-0.5*harvestwidth
            }
        }
        //
        //Recursive
        //decide whether we are done or not
        boolean iterate = true;
        if(newPoint0.getLatitude() >= areaP.get(1).getLatitude() && newPoint3.getLatitude() >= areaP.get(2).getLatitude()) {
            iterate = false;
        }
        //

        //CASE: point3 latitude + harvest width is higher than point2
        if(newPoint3.getLatitude()+harvesterWidth > areaP.get(2).getLatitude()) {
            newPoint3 = new Coordinate(areaP.get(2).getLatitude()-harvesterWidth, newPoint3.getLongitude());
        }


        //replace the new points
        areaP.remove(0);
        areaP.add(0, newPoint0);

        areaP.remove(3);
        areaP.add(3, newPoint3);
        //
        if(iterate)
            constructGraphSquare(areaP);
    }

    private void fillRow(ArrayList<Coordinate> areaP) {
        Coordinate anchor = areaP.get(0);
        //Vector between point0 and point3 (ie. bottom edge of square)
        double moveVectorX = (areaP.get(3).getLongitude()-areaP.get(0).getLongitude())/(double)numberOfXNodes;
        double moveVectorY = (areaP.get(3).getLatitude()-areaP.get(0).getLatitude())/(double)numberOfXNodes;
        //
        double nodeWidth = Math.abs(moveVectorX);
        double relativeX = nodeWidth*0.5f;
        double relativeY = harvesterWidth*0.5f;

        //while we are atleast one node to the left of the bottom right point
        while (anchor.getLongitude()+relativeX < areaP.get(3).getLongitude()) {
            //add node to graph
            graph.add(new Node(new Coordinate(anchor.getLatitude()+relativeY, anchor.getLongitude()+relativeX)));
            //move anchor on moveVector
            anchor = new Coordinate(anchor.getLatitude()+moveVectorY, anchor.getLongitude()+moveVectorX);
            //

        }

    }

    //Helper function for constructGraph
    //returns pointX moved along vector pointX->PointY by stepSize
    private Coordinate movePointUpOnVector(Coordinate pointX, Coordinate pointY, double stepSize) {
        double vectorY = (pointY.getLatitude() - pointX.getLatitude())/stepSize;
        double vectorX = (pointY.getLongitude() - pointX.getLongitude())/stepSize;

        return new Coordinate(pointX.getLatitude()+vectorY, pointX.getLongitude()+vectorX);
    }

    //TODO DEBUG REMOVE
    //also delete java.io.* import
    //Use http://www.convertcsv.com/csv-to-kml.htm to convert to kml fields;
    //Name:1,lat:2,long:3,description:4
    private void printGraphToCSVFile()
    {
        int point = 0;
        Instant c = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        File f = new File("/" + c.toString().replaceAll(":", "-") + ".txt");

        try {
            f.createNewFile();
            FileOutputStream o = new FileOutputStream(f.getAbsolutePath());
            //define area
            for (int i = 0; i < getArea().size(); i++) {
                String csvLine = //"name, lat, long, desc(graphPoint)
                        "AREA," +
                                Double.toString(getArea().get(i).getLatitude()) + "," +
                                Double.toString(getArea().get(i).getLongitude()) + "," +
                                Integer.toString(i) + "\n";
                o.write(csvLine.getBytes());
            }
            //define all graph nodes
            for (int i = 0; i < graph.size(); i++) {
                String csvLine = //"name, lat, long, desc(graphPoint)
                        "node," +
                                Double.toString(graph.get(i).getCenterPoint().getLatitude()) + "," +
                                Double.toString(graph.get(i).getCenterPoint().getLongitude()) + "," +
                                Integer.toString(i) + "\n";
                o.write(csvLine.getBytes());

            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        } catch (IOException e1) {
            e1.getMessage();
        }
    }
}
