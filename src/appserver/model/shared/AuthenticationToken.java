package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.appserver.business.service.usermanagement.FullUserFactory;
import dk.aau.astep.appserver.business.service.usermanagement.UserServices;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.db.persistent.exception.UserNotFoundException;
import dk.aau.astep.exception.ApiExceptionHandler;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * Token used for the API for authentication
 */
public class AuthenticationToken {
    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    private byte[] value;

    /**
     * Constructor for API.
     * @param value A string encoding of token bytes
     */
    public AuthenticationToken(String value) {
        try {
            this.value = decode(value); // Throws IllegalArgumentException if cannot decode

            if (this.value.length != UserServices.TOKEN_BYTES_LENGTH) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) { // catch wrong format
            throw new ApiExceptionHandler().tokenWrongFormat();
        }
    }

    /**
     * Gets a copy of the bytes
     * @return Copy of the bytes
     */
    public byte[] getBytes() {
        return value.clone();
    }

    /**
     * Decodes a hexadecimal string into bytes
     * @param value Hexadecimal string to decode, e.g. "C661FBE2F69655E2DCC0DDFAA30B94ED"
     * @return The bytes that are decoded
     */
    private byte[] decode(String value) {
        return DatatypeConverter.parseHexBinary(value);
    }

    /**
     * Finds user in database with same token if it exists
     * @return The user found
     * @throws UserNotFoundException if no user has this token in the database
     */
    public FullUser getFullUser() throws UserNotFoundException {
        try {
            return baseRepository.<FullUser>userQueries().getUser(getBytes());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }
}
