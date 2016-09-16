package dk.aau.astep.appserver.business.service.outdoor;

import dk.aau.astep.appserver.business.service.outdoor.effFarming.FarmingArea;
import dk.aau.astep.appserver.business.service.outdoor.effFarming.Node;
import dk.aau.astep.appserver.business.service.outdoor.strategypattern.implementations.pointInsidepolygon.RayCasting;
import dk.aau.astep.appserver.model.shared.Polygon;
import dk.aau.astep.appserver.model.shared.Coordinate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zobair on 13-04-2016.
 */
public class EffFarming {

    private FarmingArea area;

    @Test
    public void testEffFarming() throws Exception {

        ArrayList<Coordinate> currarea = new ArrayList<>();

        //Trapez expanding to the left
        /*
        Coordinate temp0 = new Coordinate(57.01337970848041, 9.994735233485699);

        Coordinate temp1 = new Coordinate(57.017807106012924, 9.994735233485699);
        currarea.add(1, temp1);

        Coordinate temp2 = new Coordinate(57.01701278325412, 10.003232471644878);
        currarea.add(2, temp2);


        Coordinate temp3 = new Coordinate(57.0145128920773, 10.003103725612164);
        currarea.add(3, temp3);
        */
        //
        //Trapez expanding to the right
        Coordinate temp0 = new Coordinate(57.01337970848041, 9.994735233485699);


        Coordinate temp1 = new Coordinate(57.017807106012924, 9.995529167354107);


        Coordinate temp2 = new Coordinate(57.01957090911099, 10.004562847316265);



        Coordinate temp3 = new Coordinate(57.0115343779843, 10.003946274518967);

        //

        //Perfect square
        /*
        Coordinate temp0 = new Coordinate(57.01337970848041, 9.994735233485699);

        Coordinate temp1 = new Coordinate(57.017807106012924, 9.994735233485699);


        Coordinate temp2 = new Coordinate(57.017807106012924, 10.003946274518967);


        Coordinate temp3 = new Coordinate(57.01337970848041, 10.003946274518967);
        */
        //
        currarea.add(0, temp0);
        currarea.add(1, temp1);
        currarea.add(2, temp2);
        currarea.add(3, temp3);



        area = new FarmingArea(currarea, 0.0002);


        Polygon p = new Polygon(currarea);
        List<Node> g = area.getGraph();
        RayCasting ray = new RayCasting();
        for(Node n : g) {
            if(!ray.isInsidePolygon(n.getCenterPoint(), p))
                assert(false) ;
        }
        assert (true);
    }
}
