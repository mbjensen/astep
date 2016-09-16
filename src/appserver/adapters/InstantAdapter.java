package dk.aau.astep.appserver.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;

/**
 * Adapter for serializing Instant to/from XML/JSON
 */
public class InstantAdapter extends XmlAdapter<String, Instant> {

    public Instant unmarshal(String v) throws Exception {
        return Instant.parse(v);
    }

    public String marshal(Instant v) throws Exception {
        return v.toString();
    }

}