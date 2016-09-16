package dk.aau.astep.appserver.business.service.indoor;

import com.sun.net.httpserver.HttpServer;
import dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.staticMethods;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import static dk.aau.astep.appserver.business.service.indoor.CiscoClientTest.LoadOldMSE;
import static dk.aau.astep.appserver.business.service.indoor.CiscoClientTest.SaveMSE;
import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.CiscoClient.threadList;
import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.ServerHandler.ServerHandler.*;
import static org.junit.Assert.*;

/**
 * Created by Anders on 04-04-16.
 */
public class ServerHandlerTest {

    public static HttpServer localServer,
            ciscoserver;
    public static String localIP,
            myIp;
    public static int SizeofConnectionQueue;

    @BeforeClass
    public static void setUp() throws Exception {
        SaveMSE();
        threadList.clear();
        localIP = "127.0.0.1";
        myIp = Inet4Address.getLocalHost().getHostAddress();
        SizeofConnectionQueue = 1;
        try {
            localServer = HttpServer.create(new InetSocketAddress(localIP,
                    8080),1);
            ciscoserver = HttpServer.create(new InetSocketAddress(localIP,
                    8081),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ciscoserver.createContext("/api/contextaware/v1/location/clients/", httpExchange -> {
            httpExchange.sendResponseHeaders(200, "I'm text".length());
            OutputStream os = httpExchange.getResponseBody();
            os.write("I'm text".getBytes());
            os.close();
        });
        localServer.setExecutor(null);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        LoadOldMSE();
        localServer.stop(0);
        ciscoserver.stop(0);
    }

    @Test
    public void testHandleServer() throws Exception {
        //localServer.stop(0);
        HandleServer(localServer);
        localServer.start();
        ciscoserver.start();

        // /online/
        assertEquals("Testing 'HandleServer' on /online/",
                "We are ONLINE!",
                (staticMethods.httpGet("http://" + localIP + ":8080/online/",
                        "test", "works")));

        // /add/
        assertTrue("Testing 'HandleServer' the list should be empty as nothing has been added",
                staticMethods.watchList.isEmpty());
        staticMethods.httpGet("http://" + localIP + ":8080/api/watchlist/add/01:02:03:04:05:06",
                "test", "works");
        assertEquals("Testing 'HandleServer' check if the watchlist contains the new mac address",
                "01:02:03:04:05:06",
                staticMethods.watchList.first());

        // /remove/
        staticMethods.httpGet("http://" + localIP + ":8080/api/watchlist/remove/01:02:03:04:05:06",
                "test", "works");
        assertTrue("Testing 'HandleServer' test if the removed address was removed",
                staticMethods.watchList.isEmpty());

        // /server/
        staticMethods.httpPost("http://" + localIP + ":8080/api/add/server", "test", "works",
                "http://127.0.0.1:8081", "test", "works");
        assertEquals("Testing 'HandleServer' if the server was added",
                Files.readAllLines(Paths.get("MSE")).get(0),
                "http://127.0.0.1:8081 Username:test Password:works");

        staticMethods.httpPost("http://" + localIP + ":8080/api/add/server", "test", "works",
                "http://127.0.0.1:8081", "test", "works");
        assertEquals("Testing 'HandleServer' if a duplicate server gives only one entry",
                1, threadList.size());
    }
}