package dk.aau.astep.appserver.model.shared;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.Instant;

/**
 * A token used by the API to respond token and its expiration date.
 */
@XmlRootElement
public class ExpirationAuthenticationToken {
    private String tokenEncoding;
    private Instant expirationDate;

    /**
     * Empty constructor for ExpirationAuthenticationToken used by MOXy
     */
    private ExpirationAuthenticationToken(){}

    /**
     * Constructor to create this
     * @param bytes bytes for the token
     * @param expirationDate expiration date of the token
     */
    public ExpirationAuthenticationToken(byte[] bytes, Instant expirationDate) {
        this.tokenEncoding = encode(bytes);
        this.expirationDate = expirationDate;
    }

    /**
     * Used by the API to response token value
     * @return A string representation of the token value
     */
    @XmlAttribute
    public String getValue() {
        return tokenEncoding;
    }

    /**
     * Used by the API to respond expiration date
     * @return The expiration date
     */
    @XmlAttribute
    public Instant getExpirationDate() {
        return expirationDate;
    }

    /**
     * Encodes bytes into a hexadecimal string
     * @param value Bytes to encode
     * @return A hexadecimal string, e.g. "C661FBE2F69655E2DCC0DDFAA30B94ED"
     */
    private String encode(byte[] value) {
        return DatatypeConverter.printHexBinary(value);
    }
}
