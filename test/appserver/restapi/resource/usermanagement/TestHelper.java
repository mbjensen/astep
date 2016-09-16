package dk.aau.astep.appserver.restapi.resource.usermanagement;

import com.google.gson.Gson;
import dk.aau.astep.appserver.business.service.usermanagement.FullUserFactory;
import dk.aau.astep.appserver.business.service.usermanagement.PasswordHashing;
import dk.aau.astep.appserver.model.shared.ExpirationAuthenticationToken;
import dk.aau.astep.appserver.model.shared.FullUser;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.db.persistent.api.EdgeRequestQueries;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.Period;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Used for helping set up tests
 */
public class TestHelper {
    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    public static final String usernameA = "alice", passwordA = "a_pass";
    public static final String usernameB = "bob", passwordB = "b_pass";
    public static final String usernameC = "charlie", passwordC = "c_pass";

    /**
     * Delete a user, then creates it again, then issues a new token for the user
     * @param username Username of the user
     * @param password Password of the user
     * @return A String that represents the token of the user
     * @throws IOException If a remote or network exception occurred
     */
    public static String setUpExistingUser(String username, String password) throws IOException {
        // delete if exists beforehand
        baseRepository.userQueries().deleteUser(username);

        byte[] salt = PasswordHashing.createSalt();
        byte[] hashedPassword = PasswordHashing.hashPassword(password, salt);

        // Create user in DB
        baseRepository.userQueries().createUser(username, hashedPassword, salt);

        // Create token bytes
        byte[] newToken = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(newToken);

        // Create expiration date
        Instant expirationDate = Instant.now().plus(Period.ofDays(30));

        // Set token
        baseRepository.userQueries().setToken(username, newToken, expirationDate);

        return new ExpirationAuthenticationToken(newToken, expirationDate).getValue();
    }

    /**
     * Deletes a user is if exists
     * @param username Username of the user
     * @throws IOException if IO fails
     */
    public static void setUpNonExistingUser(String username) throws IOException {
        // delete if exists beforehand
        baseRepository.userQueries().deleteUser(username);
    }

    /**
     * Sets up an existing user that is no in any groups and is not invited to any.
     * @param username Username of the user
     * @param password Password of the user
     * @return Token for the user
     * @throws IOException if IO fails
     */
    public static String setUpExistingUserNoGroupsNoInvitations(String username, String password) throws IOException {
        String token = setUpExistingUser(username, password);

        // Get user's groups and delete the users memberships in those groups
        List<Integer> ids = baseRepository.memberQueries().getMemberships(username);
        for (int id : ids) {
            baseRepository.memberQueries().deleteMembership(username, id);
        }

        // Get user's invites and delete those invites
        ids = baseRepository.inviteQueries().getInvitation(username);
        for (int id : ids) {
            baseRepository.inviteQueries().deleteInvite(id, username);
        }

        return token;
    }

    /**
     * Set up an existing user that is in no groups,
     * is not invited to any groups,
     * and has no edges (neither valid or requested) going to or from any user).
     * @param username Desired username of the user
     * @param password Desired password of the user
     * @return The token of the user
     */
    public static String setUpExistingUserNoGroupsNoEdges(String username, String password) throws IOException {
        String token = setUpExistingUser(username, password);

        removeEdgesFromToUser(username);
        leaveGroupsDeleteInvites(username);

        return token;
    }

    /**
     * Remove all valid and requested edges from and to a user.
     * @param username Username of the user
     */
    private static void removeEdgesFromToUser(String username) throws IOException {
        // Remove edge edges from in-users
        List<FullUser> users = baseRepository.<FullUser>edgeRequestQueries().getToEdgeRequests(username, EdgeRequestQueries.CREATOR.FROM);
        for (FullUser user : users) {
            baseRepository.edgeRequestQueries().deleteRequest(username, user.getUsername());
        }

        users = baseRepository.<FullUser>edgeRequestQueries().getToEdgeRequests(username, EdgeRequestQueries.CREATOR.TO);
        for (FullUser user : users) {
            baseRepository.edgeRequestQueries().deleteRequest(username, user.getUsername());
        }

        users = baseRepository.<FullUser>edgeQueries().getInUsers(username);
        for (FullUser user : users) {
            baseRepository.edgeQueries().deleteEdge(username, user.getUsername());
        }

        // Remove edges to out-users
        users = baseRepository.<FullUser>edgeRequestQueries().getFromEdgeRequests(username, EdgeRequestQueries.CREATOR.FROM);
        for (FullUser user : users) {
            baseRepository.edgeRequestQueries().deleteRequest(user.getUsername(), username);
        }

        users = baseRepository.<FullUser>edgeRequestQueries().getFromEdgeRequests(username, EdgeRequestQueries.CREATOR.TO);
        for (FullUser user : users) {
            baseRepository.edgeRequestQueries().deleteRequest(user.getUsername(), username);
        }

        users = baseRepository.<FullUser>edgeQueries().getOutUsers(username);
        for (FullUser user : users) {
            baseRepository.edgeQueries().deleteEdge(user.getUsername(), username);
        }
    }

    /**
     * Delete memberships and invites for a user.
     * @param username username of the user
     * @throws IOException if IO fails
     */
    private static void leaveGroupsDeleteInvites(String username) throws IOException {
        // Get user's groups and delete the users memberships in those groups
        List<Integer> ids = baseRepository.memberQueries().getMemberships(username);
        for (int id : ids) {
            baseRepository.memberQueries().deleteMembership(username, id);
        }

        // Delete memberships
        for (int id : ids) {
            baseRepository.memberQueries().deleteMembership(username, id);
        }
    }

    /**
     * Assert that a response has a status code of 200 and a body that is a JSON array with a certain length
     * @param response Response to assert
     * @param expectedLength Expected length of the JSON array
     */
    public static void assert200ResponseWithJsonArrayLength(Response response, int expectedLength) {
        assertResponseStatus(response, 200);
        Gson gs = new Gson();
        String message = response.readEntity(String.class);
        Collection collection = gs.fromJson(message, Collection.class);
        assertEquals(expectedLength, collection.size());
    }

    /**
     * Assert that a response has a certain status code.
     * @param response Response to assert
     * @param exceptedStatus Expected status code
     */
    public static void assertResponseStatus(Response response, int exceptedStatus) {
        int status = response.getStatus();
        assertEquals(exceptedStatus, status);
    }
}
