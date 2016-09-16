@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(type = Instant.class, value = InstantAdapter.class)
})
package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.appserver.adapters.InstantAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.time.Instant;

/**
 * This file registers an adapter for marshalling/unmarshalling
 * the java.time.Instant type to and from JSON,
 * since this is not yet natively part of the JAXB specification.
 */
