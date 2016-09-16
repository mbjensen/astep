package dk.aau.astep.appserver.model.shared;

//import dk.aau.astep.appserver.model.shared.CoordinateTest;

import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by Morten on 07/03/2016.
 */

@XmlRootElement
public class JsonResponse {
    public List<Location> locations;
    public List<User> users;

    /**
     * Empty constructor for JsonResponse used by MOXy
     */
    public JsonResponse() {
    }

    public JsonResponse(List<Location> locations, List<User> users) {
        this.locations = locations;
        this.users = users;
    }
}