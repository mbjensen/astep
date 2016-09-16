package dk.aau.astep.appserver.restapi.resource.usermanagement;

import dk.aau.astep.appserver.Main;
import dk.aau.astep.appserver.business.service.usermanagement.FullUserFactory;
import dk.aau.astep.appserver.model.shared.AuthenticationToken;
import dk.aau.astep.appserver.restapi.resource.TestClientBuilder;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.exception.ApiExceptionHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

/**
 * Tests of authentication.
 */
public class AuthenticationTest {
    private static HttpServer server;
    private static WebTarget target;

    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    private final String usernameA = TestHelper.usernameA;
    private final String passwordA = TestHelper.passwordA;
    private String tokenA;

    private final String usernameC = TestHelper.usernameC;
    private final String passwordC = TestHelper.passwordC;

    @BeforeClass
    public static void classSetUp() {
        // Start the server and create the target client
        server = Main.startServer();
        Client c = TestClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    @AfterClass
    public static void classTearDown() {
        server.shutdownNow();
    }

    @Before
    public void setUp() throws Exception {
        tokenA = TestHelper.setUpExistingUser(usernameA, passwordA);

        // Delete charlie if exists
        baseRepository.userQueries().deleteUser(usernameC);
    }

    @After
    public void tearDown() throws Exception {

    }

    /** Password **/

    @Test
    public void unknownUserPw() {
        Response response = target.path("users/" + usernameC + "/token").request().header("authorization", passwordC).get();
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(404, status);
        assertEquals("{\"error_message\":\"The user, " + usernameC + ", does not exist.\"}", message);
    }

    @Test
    public void wrongPw() throws GeneralSecurityException {
        Response response = target.path("users/" + usernameA + "/token").request().header("authorization", "wrong_pass").get();
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(401, status);
        assertEquals("{\"error_message\":\"The password was incorrect.\"}", message);
    }


    /** Token **/

    @Test
    public void wrongToken() {
        final String incorrectToken = "9071CB99D8F514A2813290ADE14E2C1A";

        Response response = target.path("users/groupMemberships").request().header("authorization", incorrectToken).get();
        int status = response.getStatus();
        String message = response.readEntity(String.class);

        assertEquals(401, status);
        assertEquals("{\"error_message\":\"The token was incorrect.\"}", message);
    }

    /**
     * Tests handling of token with wrong letter. Only the letters a, b, c, d, e, and f are allowed
     */
    @Test
    public void tokenWrongLetter() {
        // There is a G in the token
        Response response = target.path("users/groupMemberships").request().header("authorization", "G661FBE2F69655E2DCC0DDFAA30B94ED").get();
        int status = response.getStatus();
        assertEquals(401, status);
    }

    @Test
    public void tokenWrongLength() {
        // Token only half length
        Response response = target.path("users/groupMemberships").request().header("authorization", "6F7A2B5D1BDDF1A8").get();
        int status = response.getStatus();
        assertEquals(401, status);
    }

    @Test
    public void tokenWrongCharacters() {
        Response response = target.path("users/groupMemberships")
                            .request().header("authorization", "#34 <>+ 39434/*-+`´").get();
        int status = response.getStatus();
        assertEquals(401, status);
    }

    @Test
    public void expiredToken() {
        Instant expirationDate = Instant.now().minus(Duration.ofSeconds(1));
        byte[] tokenBytes = new AuthenticationToken(tokenA).getBytes();

        try {
            baseRepository.userQueries().setToken(usernameA, tokenBytes, expirationDate);
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        Response response = target.path("users/groupMemberships").request().header("authorization", tokenA).get();
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(401, status);
        assertEquals("{\"error_message\":\"The token has expired.\"}", message);
    }
}
