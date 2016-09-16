package dk.aau.astep.appserver.business.service.usermanagement;

import dk.aau.astep.appserver.model.shared.ExpirationAuthenticationToken;
import dk.aau.astep.appserver.model.shared.FullUser;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.db.persistent.exception.UserExistsException;
import dk.aau.astep.db.persistent.exception.UserNotFoundException;
import dk.aau.astep.exception.ApiExceptionHandler;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.Period;

/**
 * User management services for users and their attributes
 */
public class UserServices {
    public final static int TOKEN_EXPIRATION_DAYS = 30;
    public final static int TOKEN_BYTES_LENGTH = 16;

    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    /**
     * Creates a new user in the database with the specified username and password, and a generated salt
     * @param user The user that is being created, the username of this user type is used
     * @param password The password for the created user
     **/
    public static void createUser(User user, String password)
    {
        // Creates salt and hashes the password
        byte[] salt = PasswordHashing.createSalt();
        byte[] hashedPassword = PasswordHashing.hashPassword(password, salt);

        // Runs the createUser database query and throws an internal server error if the query throws an exception
        try {
            baseRepository.userQueries().createUser(user.getUsername(), hashedPassword, salt);
        } catch (UserExistsException e) {
            // If the user being created already exists throw a user does already exist exception
            throw new ApiExceptionHandler().userAlreadyExist(user.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Invalidates the old token and issues a new token for the user.
     * @param user The user that the tokens should be issued for. The user should be authenticated beforehand
     * @return the new token being issued as an Authentication token
     */
    public static ExpirationAuthenticationToken issueToken(User user) {
        byte[] newToken = new byte[TOKEN_BYTES_LENGTH];
        Instant expirationDate = Instant.now().plus(Period.ofDays(TOKEN_EXPIRATION_DAYS));

        // Creates a random, unique token
        SecureRandom random = new SecureRandom();
        do {
            random.nextBytes(newToken);
        } while (tokenExists(newToken));

        // Runs the setToken query and throws a internalServerError exception if it fails
        try {
            baseRepository.userQueries().setToken(user.getUsername(), newToken, expirationDate);
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        return new ExpirationAuthenticationToken(newToken, expirationDate);
    }

    /**
     * Gets the token of the user specified, issues a new token if the previous token is invalid or has expired
     * @param user The user the token is gotten from
     * @return the token gotten for the user or the new token issued
     */
    public static ExpirationAuthenticationToken getToken(User user) {
        FullUser fullUser = user.getFullUser();
        byte[] token = fullUser.getToken();
        Instant expirationDate = fullUser.getExpirationDate();

        // Check if the user has a token and if that token is not expired
        if (token != null && expirationDate.isAfter(Instant.now())) {
            // return the token
            return new ExpirationAuthenticationToken(token, expirationDate);
        } else {
            // issue new token
            return issueToken(user);
        }
    }

    /**
     * Changes the password of the specified user to the new password specified
     * @param user The user that the password is being changed for
     * @param newPassword The new password for the user
     */
    public static void changePassword(User user, String newPassword) {

        FullUser fullUser = user.getFullUser();

        // Get the salt from the user and hash the new password
        byte[] salt = fullUser.getSalt();
        byte[] hashedPassword = PasswordHashing.hashPassword(newPassword, salt);

        // Runs the setPassword database query with the username and the new hashed password as parameters,
        // throws an internal server error if the query throws an exception **/
        try {
            baseRepository.userQueries().setPassword(user.getUsername(), hashedPassword);
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }


    /** Help methods **/

    private static boolean tokenExists(byte[] tokenBytes) {
        try {
            baseRepository.userQueries().getUser(tokenBytes);
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        } catch (UserNotFoundException e) {
            return false;
        }

        return true;
    }
}
