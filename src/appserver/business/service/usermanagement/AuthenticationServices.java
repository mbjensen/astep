package dk.aau.astep.appserver.business.service.usermanagement;

import dk.aau.astep.appserver.model.shared.AuthenticationToken;
import dk.aau.astep.appserver.model.shared.FullUser;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.db.persistent.exception.UserNotFoundException;
import dk.aau.astep.exception.ApiExceptionHandler;

import java.time.Instant;
import java.util.Arrays;

/**
 * Services for authentication
 */
public class AuthenticationServices {
    /**
     * Authenticates a user with its password. If the user cannot be authenticated, an ApiExceptionHandler is thrown.
     * @param user user
     * @param password user's password
     */
    public static void authenticateWithPassword(User user, String password) {
        // Get user from database if it exists
        FullUser fullUser;
        try {
            fullUser = user.getFullUser();
        } catch (UserNotFoundException e) {
            throw new ApiExceptionHandler().userNotFound(user);
        }

        // Hash passwords
        byte[] salt = fullUser.getSalt();
        byte[] hashedPassword = PasswordHashing.hashPassword(password, salt);

        // Check if hashed passwords match
        if (!Arrays.equals(hashedPassword, fullUser.getPassword())) {
            throw new ApiExceptionHandler().incorrectCredentials("password");
        }
    }

    /**
     * Authenticates a user with its token.
     * If the user cannot be authenticated, an ApiExceptionHandler is thrown.
     * @param token user's token
     * @return The user that is authenticated
     */
    public static User authenticateWithToken(AuthenticationToken token) {
        // Get user from database if it exists
        FullUser fullUser;
        try {
            fullUser = token.getFullUser();
        } catch (UserNotFoundException e) {
            throw new ApiExceptionHandler().incorrectCredentials("token");
        }

        // Check if token is expired
        if (fullUser.getExpirationDate().isBefore(Instant.now())) {
            throw new ApiExceptionHandler().tokenExpired();
        }

        // Return a user object with only a username for security reasons
        return new User(fullUser);
    }
}
