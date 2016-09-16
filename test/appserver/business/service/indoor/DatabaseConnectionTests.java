package dk.aau.astep.appserver.business.service.indoor;

import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.Location;
import dk.aau.astep.appserver.model.shared.Precision;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import java.time.Instant;

// TODO: Consider removing this class when we have made test for the URIs
public class DatabaseConnectionTests {

    //private static Influx database = new Influx();
    private static String testDeviceId = "dev1234";
    private static Coordinate coord = new Coordinate(3.4, 4.5);
    private static Precision precision = new Precision(95, 23.432d);
    private static Location fetchedLoc;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Code executed before the first test method
        Location loc = new Location(coord, Instant.now(), "IndoorUser", precision);
        /*
        database.connect();

        database.publishLocationOfId(testDeviceId, loc);
        fetchedLoc = database.getLocationOfId(testDeviceId);
        */
    }

    @Before
    public void setUp() throws Exception {
        // Code executed before each test
        /*
        Boolean connetionStatus = database.isConnected();
        if ( connetionStatus = false){
            System.out.println("No connection");
        } else { System.out.println("Connection is active");}
        */
    }
    /*
    @Test
    public void testObjectIsLocationType() {
        assertThat("Returned object should be of type Location", fetchedLoc, instanceOf(Location.class));
    }

    @Test
    public void testExpectedEntityId() {
        assertEquals("The fetched location does not have the expected userId", testDeviceId, fetchedLoc.getUsername());
    }

    @Test
    public void testExpectedLatitude() {
        assertEquals("Mismatching latitudes", coord.getLatitude(), fetchedLoc.getCoordinate().getLatitude(), 0.001);
    }

    @Test
    public void testExpectedLongitude() {
        assertEquals("Mismatching longitudes", coord.getLongitude(), fetchedLoc.getCoordinate().getLongitude(), 0.001);
    }
    */
    @Ignore @Test
    public void testExpectedPrecision() {
        assertEquals("Mismatching precisions", precision, new Precision(95, 0.001d));
    }

    @After
    public void tearDown() throws Exception {
        // Code executed after each test
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        // Code executed after the last test method
        //database.disconnect();
    }
}
