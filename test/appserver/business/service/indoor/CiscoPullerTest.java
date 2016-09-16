package dk.aau.astep.appserver.business.service.indoor;

import com.sun.net.httpserver.HttpServer;
import dk.aau.astep.appserver.business.service.indoor.AllClient.AllClient;
import dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.CiscoPuller;
import dk.aau.astep.appserver.model.shared.IndoorPackage;
import org.junit.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.staticMethods.ReadJsonToClientList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Anders on 08-03-16.
 */
public class CiscoPullerTest {

    public static String password,
            user ,
            localIP,
            allClientDesc = "{\"Locations\":{\"totalPages\":1,\"currentPage\":1," +
                    "\"pageSize\":80,\"entries\":[{\"macAddress\":\"01:02:03:04:05:06\"," +
                    "\"currentlyTracked\":true,\"confidenceFactor\":10.0," +
                    "\"ipAddress\":[\"01.02.03.456\"],\"ssId\":\"test\",\"band\":" +
                    "\"UNKNOWN\",\"apMacAddress\":\"0a:0b:0c:0d:0e:0f\"," +
                    "\"isGuestUser\":false,\"dot11Status\":\"ASSOCIATED\",\"MapInfo" +
                    "\":{\"mapHierarchyString\":\"DevNetCampus>DevNetBuilding>DevNetZone" +
                    "\",\"floorRefId\":723413320329068590,\"Dimension\":{\"length" +
                    "\":81.9,\"width\":307.0,\"height\":16.5,\"offsetX\":0.0,\"" +
                    "offsetY\":0.0,\"unit\":\"FEET\"}},\"MapCoordinate\":{\"x\"" +
                    ":194.47,\"y\":51.8,\"unit\":\"FEET\"},\"Statistics\":{\"" +
                    "currentServerTime\":\"2016-03-07T09:17:48.631+0000\",\"" +
                    "firstLocatedTime\":\"2016-02-04T23:44:29.634+0000\",\"" +
                    "lastLocatedTime\":\"2016-03-07T09:18:18.121+0000\",\"" +
                    "additionalProperties\":{}},\"additionalProperties\":{}}]}}";
    public static int SizeofConnectionQueue;
    static HttpServer localServer;

    @BeforeClass
    public static void setUp() throws Exception {
        System.out.printf("CiscoPuller tests initiated... \n\n");
        password = "works";
        user = "test";
        localIP = "http://127.0.0.1:8080";
        SizeofConnectionQueue = 1;

        try {
            localServer = HttpServer.create(new InetSocketAddress("127.0.0.1",
                            8080),
                    SizeofConnectionQueue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        localServer.createContext("/api/contextaware/v1/location/clients", httpExchange -> {
            OutputStream os = httpExchange.getResponseBody();
            httpExchange.sendResponseHeaders(200, allClientDesc.length());
            os.write(allClientDesc.getBytes());
            os.close();
        });
        localServer.setExecutor(null);
        localServer.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        localServer.stop(0);
    }

    @Test
    public void testRun() throws Exception {
        // Intentionally not tested
    }

    @Test
    public void testContinuesPuller1() throws Exception {
        CiscoPuller testPuller = new CiscoPuller(localIP, "test", "works");
        assertTrue("Testing 'ContinuesPuller' with a positive integer",
                testPuller.ContinuesPuller(localIP, 1));
        assertFalse("Testing 'ContinuesPuller' with a negative integer",
                testPuller.ContinuesPuller(localIP, -1));
        assertFalse("Testing 'ContinuesPuller' with 0 value integer",
                testPuller.ContinuesPuller(localIP, 0));
    }

    // The value 15000 allows the thread to pull data twice
    // If isAlive returns false, then it means that the thread was unable to pull data
    @Test
    public void testContinuesPuller2() throws Exception {
        CiscoPuller testPuller = new CiscoPuller(localIP, "test", "works");
        Thread runner = new Thread(() -> {
            testPuller.ContinuesPuller(localIP);
        });
        runner.start();
        runner.join(10000);
        assertTrue(runner.isAlive());
    }

    @Test
    public void testSendToDB1() throws  Exception {
        // Actual function is not implemented
        // CiscoDataTransfer.CiscoPuller.SendToDB(java.lang.String)
    }

    @Test
    public void testSendToDB2() throws  Exception {
        // Actual function is not implemented
        // CiscoDataTransfer.CiscoPuller.SendToDB(AllClient.AllClient)
    }
    @Ignore
    @Test
    public void testPreparePackages() throws  Exception {
        CiscoPuller testPuller = new CiscoPuller(localIP, "test", "works");
        AllClient client = ReadJsonToClientList(allClientDesc);
        List<IndoorPackage> data = testPuller.PreparePackages(client);
        assertEquals("Testing 'PreparePackages'",
                "01:02:03:04:05:06", data.get(0).getUsername());
        /*Not done yet*/
    }

}
