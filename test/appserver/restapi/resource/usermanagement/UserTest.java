package dk.aau.astep.appserver.restapi.resource.usermanagement;

import dk.aau.astep.appserver.Main;
import dk.aau.astep.appserver.restapi.resource.TestClientBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Test of the basic user management services from the API perspective
 */
public class UserTest {
    private static HttpServer server;
    private static WebTarget target;

    private final String usernameA = TestHelper.usernameA;
    private final String passwordA = TestHelper.passwordA;
    private String tokenA;

    private final String usernameC = TestHelper.usernameC;
    private final String passwordC = TestHelper.passwordC;

    private final Entity<String> emptyEntity = Entity.text("");

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
        TestHelper.setUpNonExistingUser(usernameC);
    }

    @After
    public void tearDown() throws Exception {

    }

    /** Correct responses for API UM calls **/

    @Test
    public void createUserAnswer() {
        Response response = target.path("users").queryParam("username", usernameC).request().post(Entity.text(passwordC));
        TestHelper.assertResponseStatus(response, 200);
    }

    @Test
    public void createUserFailAnswer() {
        Response response = target.path("users").queryParam("username", usernameC).request().post(Entity.text(passwordC));
        TestHelper.assertResponseStatus(response, 200);

        response = target.path("users").queryParam("username", usernameC).request().post(Entity.text(passwordC));
        TestHelper.assertResponseStatus(response, 409);
    }
    @Test
    public void issueTokenAnswer() {
        Response response = target.path("users/" + usernameA + "/token").request().header("authorization", passwordA).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 200);
    }

    @Test
    public void getTokenAnswer() {
        Response response = target.path("users/" + usernameA + "/token").request().header("authorization", passwordA).get();
        TestHelper.assertResponseStatus(response, 200);
    }

    @Test
    public void changePassAnswer() {
        Response response = target.path("users/" + usernameA + "/password").request().header("authorization", passwordA).put(Entity.text("new_password"));
        TestHelper.assertResponseStatus(response, 200);
    }


    /** Mandatory parameters **/

    @Test
    public void missingUserQueryParam() {
        Response response = target.path("users").request().post(Entity.text(passwordA));
        TestHelper.assertResponseStatus(response, 400);
        String message = response.readEntity(String.class);
        assertEquals("{\"error_message\":\"The username query parameter was not specified. It is mandatory.\"}", message);
    }

    @Test
    public void MissingEnumQueryParam() {
        Response response = target.path("users/inUsers").request().header("authorization", tokenA).get();
        TestHelper.assertResponseStatus(response, 400);
        String message = response.readEntity(String.class);
        assertEquals("{\"error_message\":\"The edgeType query parameter was not specified. It is mandatory.\"}", message);
    }

    @Test
    public void MissingHeaderPassword() {
        Response response = target.path("users/" + usernameA + "/token").request().get();
        TestHelper.assertResponseStatus(response, 400);
        String message = response.readEntity(String.class);
        assertEquals("{\"error_message\":\"The authorization header was not specified. It is mandatory.\"}", message);
    }

    @Test
    public void MissingHeaderToken() {
        Response response = target.path("users/groupMemberships").request().get();
        TestHelper.assertResponseStatus(response, 400);
        String message = response.readEntity(String.class);
        assertEquals("{\"error_message\":\"The authorization header was not specified. It is mandatory.\"}", message);
    }

    @Test
    public void MissingStringBody() {
        Response response = target.path("users").queryParam("username", usernameA).request().post(emptyEntity);
        TestHelper.assertResponseStatus(response, 400);
        String message = response.readEntity(String.class);
        assertEquals("{\"error_message\":\"The request body was not specified. It is mandatory.\"}", message);
    }
}