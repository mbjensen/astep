package dk.aau.astep.appserver.business.service.indoor;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;

import java.time.*;
import java.util.Random;

public class DataGenerator {

    public DataGenerator(){}

    //Generates a Location instance using user and time parameters and random coordinates and precision
    public Location randomUserLoc(String userName, Instant time){

        //Generates random coordinates within a square of NORK
        //Square coordinates(UpRight: 57.018355, 9.991419; UpLeft: 57.018377, 9.988803;DoRight: 57.016624, 9.991356;DoLeft: 57.016623, 9.989112)
        float xCoord = randomFloatInRange(57.016624f, 57.018355f);
        float yCoord = randomFloatInRange(9.988803f, 9.991419f);
        Coordinate coord = new Coordinate(xCoord, yCoord);

        Double precision = (double)randomIntInRange(10, 35);

        Location loc = new Location(coord, time, userName, new Precision(95, precision));

        return loc;
    }

    private float randomFloatInRange(float rangeMin, float rangeMax){
        Random r = new Random();
        float randomValue = rangeMin + (rangeMax - rangeMin) * r.nextFloat();

        return randomValue;
    }

    private int randomIntInRange(int rangeMin, int rangeMax){
        Random rand = new Random();
        int randomNum = rand.nextInt((rangeMax - rangeMin) + 1) + rangeMin;

        return randomNum;
    }
}
