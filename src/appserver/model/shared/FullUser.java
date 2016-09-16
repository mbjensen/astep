package dk.aau.astep.appserver.model.shared;

import java.time.Instant;


/**
 * A user that also has password, salt, token and an expiration date for that token.
 */
public class FullUser extends User implements dk.aau.astep.db.persistent.data.User{
    private byte[] password;
    private byte[] salt;
    private byte[] token;
    private Instant expirationDate;

    /**
     * Constructor for creation of a full user
     * @param username Username of the user
     * @param password Hashed password of the user
     * @param salt Salt for hashing the user's passwords
     * @param token Token of the user. This is allowed to be null if the user is new.
     * @param expirationDate Expiration date of the current token
     */
    public FullUser(String username, byte[] password, byte[] salt, byte[] token, Instant expirationDate) {
        super(username);

        this.password = password.clone();
        this.salt = salt.clone();
        this.token = (token == null ? null : token.clone());
        this.expirationDate = expirationDate;
    }

    /**
     * Get the hashed password of the user
     * @return A copy of the hashed password
     */
    public byte[] getPassword() { return password.clone(); }

    /**
     * Get the salt for hashing the user's password
     * @return A copy of the salt
     */
    public byte[] getSalt() {
        return salt.clone();
    }

    /**
     * Get the token of the user
     * @return A copy of the token
     */
    public byte[] getToken() {
        return (token == null ? null : token.clone());
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }
}
