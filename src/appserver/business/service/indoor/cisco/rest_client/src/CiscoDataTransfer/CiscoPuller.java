package dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer;

import dk.aau.astep.appserver.business.service.indoor.AllClient.AllClient;
import dk.aau.astep.appserver.business.service.indoor.AllClient.Entry;
import dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CertificateHandler.CertificateHandler;
import dk.aau.astep.appserver.model.shared.Coordinate;
import dk.aau.astep.appserver.model.shared.IndoorPackage;
import dk.aau.astep.appserver.model.shared.Precision;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.staticMethods.CollectAllClients;

/**
 * Created by Anders on 22-02-16.
 */
public class CiscoPuller extends Thread {
    private boolean retry = true;
    public String get_ip() {
        return _ip;
    }

    private String _ip;
    private String _username;
    private String _password;
    public CiscoPuller(String ip, String username, String password) {
        _ip = ip;
        _username = username;
        _password = password;
    }

    @Override public void run() {
        new CertificateHandler();
        while(retry) {
            ContinuesPuller(_ip);
        }
        System.out.println("Thread " + _ip + " failed ");
    }

    /**
     * Pulls data from the Cisco server. If numberOfPulls is null, the server will pull until stopped
     * @param ip The URL used to connect to Cisco
     * @param numberOfPulls amount of pulls made from Cisco
     * @return true if successful else false
     */
    public Boolean ContinuesPuller(String ip, int numberOfPulls)  {
        if (numberOfPulls < 1){
            System.out.println("Invalid input for ContinuesPuller. numberOfPulls = " +
                    numberOfPulls +
                    "\n Must be above 0");
            return false;
        }
        Boolean pulledUntilStop = true;
        for (; numberOfPulls > 0; numberOfPulls--) {
            pulledUntilStop = ContinuesPullerHelper(ip);
            if (!pulledUntilStop) return  false;
        }
        return pulledUntilStop;
    }
    
    /**
     * Method that calls {@link CiscoPuller#ContinuesPullerHelper(String)}
     * @param ip the URL used to connect to Cisco
     * @return false when done collecting data
     */
    public Boolean ContinuesPuller(String ip)  {
        while (ContinuesPullerHelper(ip));
        return false;
    }

    /**
     * Method that makes the call to collect data from Cisco
     * @param ip The URL from where we get the data.
     * @return true if data was extracted from Cisco and false if not
     */
    public Boolean ContinuesPullerHelper (String ip){
        long time = System.currentTimeMillis();
        // Pull data via httpGEt
        String data = null;
        try {
            data = CollectAllClients(_username, _password, ip);
        } catch (IOException e) {
            e.printStackTrace();
            failedConnection();
            return false;
        }

        if (!data.isEmpty()) {
            System.out.println("Pulled Data at " + System.currentTimeMillis() + " on " + _ip);
            SendToDB(data);
            try {
                sleep(time + 30000 - System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
        return true;
    }

    /**
     * To be implemented
     * @param data the data that should be send to the database
     * @return true if it went well false if not
     */
    public Boolean SendToDB (String data) {
        try {
            // some send function
            return true;
        } catch ( Exception e/*Some error*/) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * To be implemented
     * @param data the data that should be send to the database
     * @return true if it went well false if not
     */
    public Boolean SendToDB (AllClient data) {
        try {
            // some send function
            return true;
        } catch ( Exception e/*Some error*/) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method that handles when connection fails. If it fails it will tell and try to re-establish
     * connection for up to 48 attempts over 12 hours
     */
    private void failedConnection(){
        System.out.println("WARNING: CANNOT CONNECT TO " + _ip);
        System.out.println("We will try to reconnect for the next 12 hours");
        int tries = 1;
        long timeMilis;
        boolean attemptSucceeded = false;
        int timeintervalSec = 600;

        while(!attemptSucceeded){
            timeMilis = System.currentTimeMillis();
            if(tries < 36){
                try {
                    sleep(timeMilis + timeintervalSec * 1000 - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // After 48 tries, the interval extended
            else if(tries < 48){
                timeintervalSec = 1800;
                try {
                    sleep(timeMilis + timeintervalSec * 1000 - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(tries==48){
                retry = false;
                return;
            }
            try {
                CollectAllClients(_username, _password,_ip);
            } catch (IOException e) {
                tries++;
                System.out.println("Tested " + _ip +
                        " for the " + tries + ". time. Connection failed.");
                continue;
            }
            attemptSucceeded = true;
            System.out.println("Connected to " + _ip + "." +
                    " Data will now be pulled at regular intervals.");
        }
    }

    public List<IndoorPackage> PreparePackages(AllClient clients){
        List<IndoorPackage> data = new ArrayList<>();

        for (Entry e: clients.getLocations().getEntries()) {
            Coordinate cordinate = new Coordinate(0/*Lat*/,0/*Lon*/);
            Instant timestamp = Instant.parse(e.getStatistics().getCurrentServerTime());
            data.add(new IndoorPackage(cordinate, timestamp,
                    e.getMacAddress(), new Precision(95, e.getConfidenceFactor().doubleValue()),
                    e.getCurrentlyTracked(), e.getConfidenceFactor().floatValue(), e.getIpAddress(),
                    e.getSsId(), e.getBand(), e.getApMacAddress(), e.getDot11Status(),
                    e.getIsGuestUser(), e.getMapInfo().getMapHierarchyString(),
                    e.getMapInfo().getFloorRefId(), e.getMapInfo().getDimension().getWidth().floatValue(),
                    e.getMapInfo().getDimension().getHeight().floatValue(),
                    e.getMapInfo().getDimension().getOffsetX().floatValue(),
                    e.getMapInfo().getDimension().getOffsetY().floatValue(),
                    e.getMapInfo().getDimension().getUnit(), e.getMapCoordinate().getX().floatValue(),
                    e.getMapCoordinate().getY().floatValue(), e.getMapCoordinate().getUnit(),
                    e.getStatistics().getCurrentServerTime(),
                    e.getStatistics().getFirstLocatedTime(),
                    e.getStatistics().getLastLocatedTime()));
        }
        return data;
    }

    private LocalDateTime ConvertToTime(String time) {
        String[] split = time.split("[\\.T]");
        String date = split[0] + " " + split[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(date, formatter);
    }
}
