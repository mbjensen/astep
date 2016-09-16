package dk.aau.astep.appserver.restapi.api;

import dk.aau.astep.appserver.model.shared.Location;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Precision;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParamConvertTest {
    private List<String> names = new ArrayList<String>(){{
        add("bo");
        add("ib");
        add("siv");
    }};
    private List<Location> locations = new ArrayList<Location>(){{
        add(new Location(new Coordinate(12.2d, 32.2d), Instant.parse("2016-05-06T10:44:00.259Z"),
                "bo", new Precision(34.2,12.4)));
        add(new Location(new Coordinate(52.2d, 12.2d), Instant.parse("2010-05-06T10:48:00.259Z"),
                "bo", new Precision(44.2,12.4)));
    }};
    @BeforeClass
    public static void testSetup() {
        // Setup
    }

    @AfterClass
    public static void testCleanup() {
        // Cleanup
    }

    /** usernames **/
    @Test
    public void testUsernamesCorrectFormat(){
        String stringNames = "bo, ib, siv";
        List<String> actual = ParamConvert.usernames(stringNames);

        assertEquals(names, actual);
    }

    @Test
    public void testUsernamesMultipleSpaces(){
        String stringNames = "bo,   ib,    siv";
        List<String> actual = ParamConvert.usernames(stringNames);

        assertEquals(names, actual);
    }
    //TODO these test can be used when the function validates correct.
/*
    @Test(expected = WebApplicationException.class)
    public void testUsernamesNoComma(){
        String stringNames = "bo ib siv";
        List<String> actual = ParamConvert.usernames(stringNames);

        assertEquals(names, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testUsernamesSingleComma(){
        String stringNames = "bo, ib siv";
        List<String> actual = ParamConvert.usernames(stringNames);

        assertEquals(names, actual);
    } */

    /** timeStringToInstant **/
    @Test
    public void testTimeStringToInstantToStringBackToInstant(){
        Instant expected = Instant.now();
        String timeNowString = expected.toString();
        Instant actual = ParamConvert.timeStringToInstant(timeNowString);

        assertEquals(expected, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testTimeStringToInstantWrongDelimiter(){
        String timeNowString = "2016.05-06T10:44:00.259Z"; // correct format: 2016-05-06T10:44:00.259Z
        Instant actual = ParamConvert.timeStringToInstant(timeNowString);
    }
    @Test(expected = WebApplicationException.class)
    public void testTimeStringToInstantNoZ(){
        String timeNowString = "2016.05-06T10:44:00.259"; // correct format: 2016-05-06T10:44:00.259Z
        Instant actual = ParamConvert.timeStringToInstant(timeNowString);
    }
    @Test(expected = WebApplicationException.class)
    public void testTimeStringToInstantNoT(){
        String timeNowString = "2016-05-0610:44:00.259Z"; // correct format: 2016-05-06T10:44:00.259Z
        Instant actual = ParamConvert.timeStringToInstant(timeNowString);
    }
    @Test(expected = WebApplicationException.class)
    public void testTimeStringToInstantIsNull(){
        String timeNowString = null; // correct format: 2016-05-06T10:44:00.259Z
        Instant actual = ParamConvert.timeStringToInstant(timeNowString);
    }

    /** locations **/
    @Test
    public void testLocationsCorrectFormat(){
        List<String> locationsAsStrings = new ArrayList<>();
        locationsAsStrings.add("12.2d;32.2d;34.2;12.4;2016-05-06T10:44:00.259Z");
        locationsAsStrings.add("52.2d;12.2d;44.2;12.4;2010-05-06T10:48:00.259Z");

        List<Location> actual = ParamConvert.locations(locationsAsStrings, "bo");
        assertEquals(locations, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testLocationsCoordinateWrongFormat(){
        List<String> locationsAsStrings = new ArrayList<>();
        locationsAsStrings.add("wer;wer;34.2;12.4;2016-05-06T10:44:00.259Z");
        locationsAsStrings.add("52.2d;12.2d;44.2;12.4;2010-05-06T10:48:00.259Z");

        List<Location> actual = ParamConvert.locations(locationsAsStrings, "bo");
        assertEquals(locations, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testLocationsCoordinateEmpty(){
        List<String> locationsAsStrings = new ArrayList<>();
        locationsAsStrings.add(";;34.2;12.4;2016-05-06T10:44:00.259Z");
        locationsAsStrings.add("52.2d;12.2d;44.2;12.4;2010-05-06T10:48:00.259Z");

        List<Location> actual = ParamConvert.locations(locationsAsStrings, "bo");
        assertEquals(locations, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testLocationsPrecessionWrongFormat(){
        List<String> locationsAsStrings = new ArrayList<>();
        locationsAsStrings.add("12.2d;32.2d;wer;wer;2016-05-06T10:44:00.259Z");
        locationsAsStrings.add("52.2d;12.2d;44.2;12.4;2010-05-06T10:48:00.259Z");

        List<Location> actual = ParamConvert.locations(locationsAsStrings, "bo");
        assertEquals(locations, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testLocationsPrecessionEmpty(){
        List<String> locationsAsStrings = new ArrayList<>();
        locationsAsStrings.add("12.2d;32.2d;56,6;34.7;2016-05-06T10:44:00.259Z");
        locationsAsStrings.add("52.2d;12.2d;;;2010-05-06T10:48:00.259Z");

        List<Location> actual = ParamConvert.locations(locationsAsStrings, "bo");
        assertEquals(locations, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testLocationsTimestampWrongFormat(){
        List<String> locationsAsStrings = new ArrayList<>();
        locationsAsStrings.add("12.2d;32.2d;34.2;12.4;123452323");
        locationsAsStrings.add("52.2d;12.2d;44.2;12.4;2010-05-06T10:48:00.259Z");

        List<Location> actual = ParamConvert.locations(locationsAsStrings, "bo");
        assertEquals(locations, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testLocationsTimestampEmpty(){
        List<String> locationsAsStrings = new ArrayList<>();
        locationsAsStrings.add("12.2d;32.2d;34.2;12.4;2016-05-06T10:44:00.259Z");
        locationsAsStrings.add("52.2d;12.2d;44.2;12.4;");

        List<Location> actual = ParamConvert.locations(locationsAsStrings, "bo");
        assertEquals(locations, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testLocationsEmptyString(){
        List<String> locationsAsStrings = new ArrayList<>();
        locationsAsStrings.add("");
        locationsAsStrings.add("52.2d;12.2d;44.2;12.4;2016-05-06T10:44:00.259Z");

        List<Location> actual = ParamConvert.locations(locationsAsStrings, "bo");
        assertEquals(locations, actual);
    }
    @Test(expected = WebApplicationException.class)
    public void testLocationsIsNull(){
        List<String> locationsAsStrings = null;

        List<Location> actual = ParamConvert.locations(locationsAsStrings, "bo");
        assertEquals(locations, actual);
    }
}

