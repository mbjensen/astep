
package dk.aau.astep.appserver.business.service.indoor.AllClient;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;


@Generated("org.jsonschema2pojo")
public class AllClient {

    public Locations Locations;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The Locations
     */
    public Locations getLocations() {
        return Locations;
    }

    /**
     * 
     * @param Locations
     *     The Locations
     */
    public void setLocations(Locations Locations) {
        this.Locations = Locations;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}