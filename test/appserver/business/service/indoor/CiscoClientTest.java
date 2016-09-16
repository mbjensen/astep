package dk.aau.astep.appserver.business.service.indoor;

import com.sun.net.httpserver.HttpServer;
import dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.CiscoClient;
import dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.CiscoPuller;
import dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.staticMethods;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.CiscoClient.*;
import static org.junit.Assert.*;

/**
 * Created by Anders on 08-03-16.
 */
public class CiscoClientTest {

    public static HttpServer localServer;
    public static int SizeofConnectionQueue;
    public static String localIP,
            myIp,
            clientDesc = "{\"WirelessClientLocation\":{\"macAddress\":\"" +
            "00:00:2a:01:00:0a\",\"currentlyTracked\":true,\"confidenceFactor" +
            "\":56.0,\"ipAddress\":[\"10.10.20.169\"],\"ssId\":\"test\",\"band" +
            "\":\"UNKNOWN\",\"apMacAddress\":\"00:2b:01:00:03:00\",\"isGuestUser" +
            "\":false,\"dot11Status\":\"ASSOCIATED\",\"MapInfo\":{\"" +
            "mapHierarchyString\":\"DevNetCampus>DevNetBuilding>DevNetZone\",\"" +
            "floorRefId\":723413320329068590,\"Dimension\":{\"length\":81.9,\"" +
            "width\":307.0,\"height\":16.5,\"offsetX\":0.0,\"offsetY\":0.0,\"" +
            "unit\":\"FEET\"}},\"MapCoordinate\":{\"x\":120.43,\"y\":40.03,\"unit" +
            "\":\"FEET\"},\"Statistics\":{\"currentServerTime\":\"" +
            "2016-03-09T11:12:15.259+0000\",\"firstLocatedTime\":\"" +
            "2016-02-04T23:44:29.635+0000\",\"lastLocatedTime\":\"" +
            "2016-03-09T11:12:09.963+0000\"}}}";

    @BeforeClass
    public static void setUp() throws Exception {
        SaveMSE();
        threadList.clear();
        System.out.printf("CiscoClient tests initiated... \n\n");
        localIP = "127.0.0.1";
        myIp = Inet4Address.getLocalHost().getHostAddress();
        SizeofConnectionQueue = 1;
        try {
            localServer = HttpServer.create(new InetSocketAddress(localIP,
                            8080),
                    1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        localServer.createContext("/api/contextaware/v1/location/clients/12.34.56.780", httpExchange -> {
            httpExchange.sendResponseHeaders(200, clientDesc.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(clientDesc.getBytes());
            os.close();
        });
        localServer.setExecutor(null);
        localServer.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        localServer.stop(0);
        LoadOldMSE();
    }
    @After
    public void afterEach() {
        ippw.clear();
        ipList.clear();
    }

    @Test
    public void testMain() throws Exception {

    }

    @Test
    public void testAddIP() throws NoSuchElementException {
        assertTrue("Testing 'AddIP' trying to add a correct ip entry",
                AddIP("https://64.103.26.61", "user1", "pw1"));
        assertEquals("Testing 'AddIP' trying to find an IP from the IP list",
                "https://64.103.26.61", CiscoClient.ipList.first());
        assertEquals("Testing 'AddIP' find the right user and pw form an IP",
                "user1 pw1", CiscoClient.ippw.get("https://64.103.26.61"));
        assertTrue("Testing 'AddIP' trying to add a correct ip entry",
                AddIP("https://10.200.30.40", "user2", "pw2"));
        assertEquals("Testing 'AddIP' find the right user and pw form an IP",
                "user2 pw2", CiscoClient.ippw.get("https://10.200.30.40"));

        // Invalid inputs
        assertFalse("Testing 'AddIP' trying to add invalid ip entry(nan)",
                AddIP("ip", "", ""));
        assertEquals("Testing 'AddIP' checking if the list were incorrectly updated ",
                null, CiscoClient.ippw.get("ip"));
        assertFalse("Testing 'AddIP' trying to add invalid ip entry(empty)",
                AddIP("", "", ""));
        assertFalse("Testing 'AddIP' trying to add invalid ip entry(above 255)",
                AddIP("https://256.0.0.0", "", ""));
        assertFalse("Testing 'AddIP' trying to add invalid ip entry(comma)",
                AddIP("https://0,0,0,0", "", ""));
    }

    @Test
    public void testWriteToFile() throws Exception {
        AddIP("https://01.234.56.78", "username", "password");
        AddIP("https://255.255.255.255", "myName", "myPassword");
        assertTrue("Testing 'WriteToFile' write two lines", WriteToFile());
        assertEquals("Testing 'WriteToFile'",
                "https://01.234.56.78 Username:username Password:password",
                Files.readAllLines(Paths.get("MSE")).get(0));
        assertEquals("Testing 'WriteToFile'",
                "https://255.255.255.255 Username:myName Password:myPassword",
                Files.readAllLines(Paths.get("MSE")).get(1));
    }

    @Test
    public void testDuplicateInThreadlist() throws  Exception {
        CiscoPuller puller = new CiscoPuller("ip", "user", "password");
        threadList.add(puller);
        assertTrue("Testing 'DuplicateInThreadlist'", DuplicateInThreadlist(puller));
        puller.join(0);
    }

    protected static void SaveMSE() throws IOException {
        if(Files.exists(Paths.get("MSE"))) {
            Files.deleteIfExists(Paths.get("MSE.OLD"));
            Files.copy(Paths.get("MSE"), Paths.get("MSE.OLD"));
            Files.write(Paths.get("MSE"), "".getBytes());
        }
        else {
            Files.createFile(Paths.get("MSE"));
        }
    }

    protected static void LoadOldMSE() throws IOException {
        if(Files.exists(Paths.get("MSE.OLD"))) {
            Files.delete(Paths.get("MSE"));
            Files.copy(Paths.get("MSE.OLD"), Paths.get("MSE"));
            Files.delete(Paths.get("MSE.OLD"));
        }
    }
}
