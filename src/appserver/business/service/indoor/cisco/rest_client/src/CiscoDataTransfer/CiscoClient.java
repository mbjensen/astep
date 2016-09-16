package dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer;

import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.util.*;

import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.CiscoDataTransfer.staticMethods.*;
import static dk.aau.astep.appserver.business.service.indoor.cisco.rest_client.src.ServerHandler.ServerHandler.*;

/**
 * Created by Anders on 22-02-16.
 */
public class CiscoClient {
    public static TreeSet<String>  ipList = new TreeSet<>();
    public static List<CiscoPuller> threadList = new ArrayList<>();
    public static HashMap<String, String> ippw = new HashMap<>();
    static private HttpServer server;

    static private String myIp;
    static {
        try {
            myIp = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
           System.out.println("Unknown Host Address");
           e.printStackTrace();
        }
    }

    public static void main(String[] argv) throws IOException {
      if(Files.exists(FileSystems.getDefault().getPath("MSE"))){
          String line = null;
          FileReader fileReader = new FileReader("MSE");
          BufferedReader bufferedReader = new BufferedReader(fileReader);
          while((line = bufferedReader.readLine()) != null) {
              String[] split = line.split("\\s+");
              if (validIP(split[0]) && identifyUser(split[1])!=null &&
                      identifyPW(split[2])!= null) {
                  AddIP(split[0],identifyUser(split[1]), identifyPW(split[2]));
              }
          }
      }
        else {
          Files.createFile(FileSystems.getDefault().getPath("MSE"));
      }
        // Read the Main arguments to add one or more new connections
        // A connection consists of 3 arguments
        int i = 0;
        while(argv.length > i+2){
            if (validIP(argv[i]) &&
                    identifyUser(argv[i+1])!= null &&
                    identifyPW(argv[i+2])!= null) {
                AddIP(argv[i],identifyUser(argv[i+1]), identifyPW(argv[i+2]));
            }
            i+=3;
        }
        // Save the new connections from the while loop
        if(!WriteToFile()){
          System.out.println("Failed to write to file.");
      }

      // ADD IPS TO THREADLIST.
      for (String ip : ipList) {
          String user = ippw.get(ip);
          String split[] = user.split(" ");
          threadList.add(new CiscoPuller(ip, split[0], split[1]));
      }

        server = HttpServer.create(new InetSocketAddress(myIp, 8080), 1);
        HandleServer(server);

        // RUN EACH THREAD.
        for (CiscoPuller thread : threadList) {
          thread.start();
          System.out.println("ciscoPuller started on ip: " + thread.get_ip());
        }

        server.start();
        System.in.read();
  }

    /**
     * Method to update the list of watched addresses
     * @param ip the url to be added to list
     * @param user the username to access the url
     * @param pw the corresponding password
     * @return true if successful false if not
     */
    public static boolean AddIP(String ip, String user, String pw){
        if (!validIP(ip)) {
            System.out.println("Invalid ip: " + ip);
            return false;
        }
        ipList.add(ip);
        ippw.put(ip, user +" " + pw);
        return true;
    }

    /**
     * Method that save newly added connections
     * @return true if successful false if not
     */
    public static boolean WriteToFile(){
        String writeString = "";
        for (String ip: ipList) {
            String user = ippw.get(ip);
            String split[] = user.split(" ");
            writeString = writeString + (ip + " Username:" + split[0] +
                    " Password:" + split[1] +"\n");
        }
        try {
            Files.write(Paths.get("MSE"), writeString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to print to file!");
            return false;
        }
        return true;
    }

    /**
     * checks for duplicates in the threadList
     * @param puller an instance of Ciscopuller containing threadList {@link CiscoPuller}
     * @return true if new address is a duplicate and false if it is not
     */
    public static boolean DuplicateInThreadlist(CiscoPuller puller) {
        for(CiscoPuller thread : threadList){
            if (Objects.equals(thread.get_ip(), puller.get_ip())) {
                return  true;
            }
        }
        return false;
    }
}
