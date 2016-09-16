package dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.ServerHandler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.CiscoPuller;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.CiscoClient.*;
import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.CiscoClient.threadList;
import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.staticMethods.*;

/**
 * Created by Anders on 04-04-16.
 */
public class ServerHandler {
    /**
     * Method adding addresses to the watchList and obfuscating them and adds Cisco servers to the system
     * @throws IOException through HttpServer
     */
    public static void HandleServer(HttpServer server) throws IOException {
        System.out.println("Running server on; " + server.getAddress() );

        server.createContext("/online", httpExchange -> {
            System.out.println("Received request from " +
                    httpExchange.getRemoteAddress().getAddress());
            String response = "We are ONLINE!";
            System.out.println("Response: " + response);
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(Charset.forName("UTF-8")));
            os.close();
        });
        // Adds a mac address to the watchlist
        // This address is not obfuscated until it is removed from the watchlist.
        server.createContext("/api/watchlist/add/", httpExchange -> {
            if (!VerifyConnection(httpExchange)){
                return;
            }
            // The address specified in the URL will be saved to the watchlist
            DiffMatchPatch diff = new DiffMatchPatch();
            LinkedList<DiffMatchPatch.Diff> diffLinkedList =
                    diff.diffMain(httpExchange.getRequestURI().getPath(),
                            httpExchange.getHttpContext().getPath());

            // Handle special case for Cisco AAU:
            httpGet("http://172.18.37.71:8080/api/watchlist/add/" +
                    diffLinkedList.peekLast().text, "test", "works");

            String response = "Added " + diffLinkedList.peekLast().text +
                    " to watchlist.\nGo to " + server.getAddress() +
                    "/api/watchlist/remove/" + diffLinkedList.peekLast().text +
                    " to remove the address from the watchlist.";
            AddMacAddressToWatchList(diffLinkedList.peekLast().text);
            System.out.println("Response: " + response);
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(Charset.forName("UTF-8")));
            os.close();
        });

        // Removes a mac address from the watchlist. After this it will be obfuscated.
        server.createContext("/api/watchlist/remove/", httpExchange -> {
            if (!VerifyConnection(httpExchange)){
                return;
            }
            DiffMatchPatch diff = new DiffMatchPatch();
            LinkedList<DiffMatchPatch.Diff> diffLinkedList = diff.diffMain(httpExchange.getRequestURI().getPath(),
                    httpExchange.getHttpContext().getPath());

            httpGet("http://172.18.37.71:8080/api/watchlist/remove/" +
                    diffLinkedList.peekLast().text, "test", "works");
            String response = "Removed " + diffLinkedList.peekLast().text +
                    " from watchlist.\nGo to " + server.getAddress() +
                    "/api/watchlist/add/" + diffLinkedList.peekLast().text +
                    " to add an address to the watchlist.";
            RemoveMacAddressToWatchList(diffLinkedList.peekLast().text);
            System.out.println("Response: " + response);
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(Charset.forName("UTF-8")));
            os.close();
        });

        // Add a Cisco server(IP) to the system
        server.createContext("/api/add/server", httpExchange -> {
            // The server can not be added if it is offline
            if (!VerifyConnection(httpExchange)){
                return;
            }
            String response = "Received request to add server with ip ";
            if(VerifyHeaders(httpExchange)){
                Headers headers = httpExchange.getRequestHeaders();
                List<String> username = headers.get("Username");
                List<String> passphrase = headers.get("Password");
                List<String> url = headers.get("url");
                response = response + url.get(0);
                if(validIP(url.get(0)) && IsServerOnline(url.get(0))){
                    CiscoPuller puller = new CiscoPuller(url.get(0), username.get(0), passphrase.get(0));
                    if (DuplicateInThreadlist(puller)) {
                        System.out.println("ciscoPuller with address "
                                + puller.get_ip() +
                                " was not added as it already exists");
                        try {
                            puller.join(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        AddIP(url.get(0), username.get(0), passphrase.get(0));
                        threadList.add(puller);
                        WriteToFile();
                    }
                    for (CiscoPuller thread : threadList) {
                        if (!thread.isAlive()) {
                            thread.start();
                            System.out.println("ciscoPuller started on ip: " + thread.get_ip());
                        }
                    }
                }
                else{
                    response = response + "\nInvalid IP. " +
                            "Either the server is down or the form is not correct. " +
                            "Please make sure it is of the form http(s)://127.0.0.1(:port)";
                }
            }
            else{
                response = response + " Request denied.";
            }
            System.out.println("Response: " + response);
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes(Charset.forName("UTF-8")));
            os.close();
        });
    }
}
