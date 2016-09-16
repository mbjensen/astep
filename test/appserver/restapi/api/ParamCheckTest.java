package dk.aau.astep.appserver.restapi.api;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Precision;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ParamCheckTest {
    @BeforeClass
    public static void testSetup() {
        // Setup
    }

    @AfterClass
    public static void testCleanup() {
        // Cleanup
    }
    /** checkLatitude **/
    @Test(expected=WebApplicationException.class)
    public void testCheckLatitudeIs91ThrowsException(){
        ParamCheck.checkLatitude(91);
    }
    @Test
    public void testCheckLatitudeIs90(){
        ParamCheck.checkLatitude(90);
    }
    @Test
    public void testCheckLatitudeIs89(){
        ParamCheck.checkLatitude(89);
    }
    @Test
    public void testCheckLatitudeIs0(){
        ParamCheck.checkLatitude(0);
    }
    @Test
    public void testCheckLatitudeIsMinus89(){
        ParamCheck.checkLatitude(-89);
    }
    @Test
    public void testCheckLatitudeIsMinus90(){
        ParamCheck.checkLatitude(-90);
    }
    @Test(expected=WebApplicationException.class)
    public void testCheckLatitudeIsMinus91ThrowsException(){
        ParamCheck.checkLatitude(-91);
    }

    /** checkLogitude **/
    @Test(expected=WebApplicationException.class)
    public void testCheckLongitudeIs181ThrowsException(){
        ParamCheck.checkLatitude(181);
    }
    @Test
    public void testCheckLongitudeIs180(){
        ParamCheck.checkLongitude(180);
    }
    @Test
    public void testCheckLongitudeIs179(){
        ParamCheck.checkLongitude(179);
    }
    @Test
    public void testCheckLongitudeIs0(){
        ParamCheck.checkLatitude(0);
    }
    @Test
    public void testCheckLongitudeIsMinus179(){
        ParamCheck.checkLongitude(-179);
    }
    @Test
    public void testCheckLongitudeIsMinus180(){
        ParamCheck.checkLongitude(-180);
    }
    @Test(expected=WebApplicationException.class)
    public void testCheckLongitudeIs181Minus181ThrowsException(){
        ParamCheck.checkLongitude(-181);
    }

    /** checkCoordiante **/
    // the latitude and longitude function is already been tested above.
    @Test(expected=WebApplicationException.class)
    public void testCheckCoordinateIsNullThrowException(){
        ParamCheck.checkCoordinate(null);
    }
    @Test
    public void testCheckCoordinateCorrectFormat(){
        ParamCheck.checkCoordinate(new Coordinate(23.3d, 90.6d));
    }


    /** checkCoordinates **/
    @Test(expected=WebApplicationException.class)
    public void testCheckCoordiantesCountIs2ThrowException(){
        List<Coordinate> list = new ArrayList<>();
        list.add(new Coordinate(54.1, 78));
        list.add(new Coordinate(23,12.3));

        ParamCheck.checkCoordinates(list);
    }
    @Test(expected=WebApplicationException.class)
    public void testCheckCoordiantesOneCoordinateWrongFormatThrowsException(){
        List<Coordinate> list = new ArrayList<>();
        list.add(new Coordinate(54.6, 78));
        list.add(new Coordinate(23,12.2));
        list.add(new Coordinate(-99.0, 182));

        ParamCheck.checkCoordinates(list);
    }
    @Test
    public void testCheckCoordiantesCorrectFormat(){
        List<Coordinate> list = new ArrayList<>();
        list.add(new Coordinate(54, 78.8));
        list.add(new Coordinate(23,12));
        list.add(new Coordinate(-9, 18.2));
        list.add(new Coordinate(-34, 43));

        ParamCheck.checkCoordinates(list);
    }

    /** checkRadius **/
    @Test(expected=WebApplicationException.class)
    public void testCheckRadiusIsNullThrowsException(){ ParamCheck.checkRadius(null);}
    @Test(expected=WebApplicationException.class)
    public void testCheckRadiusIsminus1ThrowsException(){ ParamCheck.checkRadius(-1.0);}
    @Test
    public void testCheckRadiusCloseToZero(){ ParamCheck.checkRadius(0.01);}
    @Test
    public void testCheckRadiusIsNormalRadius(){ ParamCheck.checkRadius(20.5);}

    /** checkTime **/
    @Test(expected=WebApplicationException.class)
    public void testCheckTimeIsNullThrowsException(){ ParamCheck.checkTime(null);}
    @Test(expected=WebApplicationException.class)
    public void testCheckTimeIsInTheFuture(){
        Instant time = Instant.now();
        Instant future = time.plusSeconds(12323);
        ParamCheck.checkTime(future);
    }
    @Test
    public void testCheckTimeCorrectFormat(){
        ParamCheck.checkTime(Instant.now());
    }

    /** checkUsername **/
    @Test(expected=WebApplicationException.class)
    public void testCheckUsernameIsNullThrowException(){ ParamCheck.checkIfUsernameIsNull(null);}
    @Test
    public void testCheckUsernameCorrectFormat(){ ParamCheck.checkIfUsernameIsNull("JohnDoe");}

    /**checkIfUsernamesIsNull**/
    @Test(expected=WebApplicationException.class)
    public void testCheckUsernamesIsNullThrowException(){ ParamCheck.checkIfUsernameIsNull(null);}
    @Test(expected=WebApplicationException.class)
    public void testCheckUsernamesOneIsNullThrowException(){
        List<String> usernames = new ArrayList<>();
        usernames.add("JohnDoe");
        usernames.add("bo");
        usernames.add(null);
        usernames.add("else");

        ParamCheck.checkIfUsernamesIsNull(usernames);
    }
    @Test
    public void testCheckUsernamesCorrectFormat(){
        List<String> usernames = new ArrayList<>();
        usernames.add("JohnDoe");
        usernames.add("bo");
        usernames.add("else");

        ParamCheck.checkIfUsernamesIsNull(usernames);}

    /**checkPrecision**/
    @Test(expected=WebApplicationException.class)
    public void testCheckPrecisionIsNullThrowException(){ ParamCheck.checkPrecision(null); }
    @Test(expected=WebApplicationException.class)
    public void testCheckPrecisionNegativeUnitThrowException(){
        Precision precision = new Precision(-0.1, 3.4d);
        ParamCheck.checkPrecision(precision);
    }
    @Test(expected=WebApplicationException.class)
    public void testCheckPrecisionNegativeRadiusThrowException(){
        Precision precision = new Precision(23.1d, -0.1);
        ParamCheck.checkPrecision(precision);
    }
    @Test(expected=WebApplicationException.class)
    public void testCheckPrecisionBothZeroThrowException(){
        Precision precision = new Precision(0.0, 0.0);
        ParamCheck.checkPrecision(precision);
    }
    @Test(expected=WebApplicationException.class)
    public void testCheckPrecisionRadiusZeroThrowException(){
        Precision precision = new Precision(4.2, 0.0);
        ParamCheck.checkPrecision(precision);
    }
    @Test(expected=WebApplicationException.class)
    public void testCheckPrecisionUnitZeroThrowException(){
        Precision precision = new Precision(0.0, 1.4);
        ParamCheck.checkPrecision(precision);
    }
    @Test
    public void testCheckPrecisionBothCloseToZero(){
        Precision precision = new Precision(0.1, 0.1);
        ParamCheck.checkPrecision(precision);
    }
    @Test
    public void testCheckPrecisionNormalFormat(){
        Precision precision = new Precision(68, 0.52);
        ParamCheck.checkPrecision(precision);
    }
}
