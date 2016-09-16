package dk.aau.astep.appserver.restapi.resource.usermanagement;

import dk.aau.astep.appserver.Main;
import dk.aau.astep.appserver.business.service.usermanagement.FullUserFactory;
import dk.aau.astep.appserver.business.service.usermanagement.UserServices;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.appserver.restapi.resource.TestClientBuilder;
import dk.aau.astep.db.persistent.api.BaseRepository;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test of the users resource
 */
public class EdgeTest {
    private static HttpServer server;
    private static WebTarget target;

    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    static private final String usernameA = "EdgeUserA", passwordA = "EdgePassA";
    static private final User userA = new User(usernameA);
    static private String tokenA;

    static private final String usernameB = "EdgeUserB", passwordB = "EdgePassB";
    static private final User userB = new User(usernameB);
    static private String tokenB;

    static private final String usernameC = "EdgeUserC", passwordC = "EdgePassC";
    static private final User userC = new User(usernameC);

    static private final Entity<String> emptyEntity = Entity.text("");

    @BeforeClass
    public static void classSetUp() {
        // Start the server and create the target client
        server = Main.startServer();
        Client c = TestClientBuilder.newClient();
        target = c.target(Main.BASE_URI);

        UserServices.createUser(userA, passwordA);
        tokenA = UserServices.getToken(userA).getValue();

        UserServices.createUser(new User(usernameB), passwordB);
        tokenB = UserServices.getToken(userB).getValue();

        target.path("users/inUsers/" + usernameB).request().header("authorization", tokenA).delete();
        target.path("users/inUsers/" + usernameA).request().header("authorization", tokenB).delete();

        target.path("users/outUsers/" + usernameB).request().header("authorization", tokenA).delete();
        target.path("users/outUsers/" + usernameA).request().header("authorization", tokenB).delete();
    }

    @AfterClass
    public static void classTearDown() throws IOException {
        target.path("users/inUsers/" + usernameB).request().header("authorization", tokenA).delete();
        target.path("users/inUsers/" + usernameA).request().header("authorization", tokenB).delete();

        target.path("users/outUsers/" + usernameB).request().header("authorization", tokenA).delete();
        target.path("users/outUsers/" + usernameA).request().header("authorization", tokenB).delete();

        baseRepository.userQueries().deleteUser(usernameA);
        baseRepository.userQueries().deleteUser(usernameB);
        baseRepository.userQueries().deleteUser(usernameC);

        server.shutdownNow();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        target.path("users/inUsers/" + usernameB).request().header("authorization", tokenA).delete();
        target.path("users/inUsers/" + usernameA).request().header("authorization", tokenB).delete();

        target.path("users/outUsers/" + usernameB).request().header("authorization", tokenA).delete();
        target.path("users/outUsers/" + usernameA).request().header("authorization", tokenB).delete();
    }

    @Test
    public void getInUsersAnswer() {
        Response response = target.path("users/inUsers").queryParam("edgeType", "VALID").request().header("authorization", tokenA).get();
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void postInUsersAnswer() throws IOException {
        Response response = target.path("users/inUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void putInUsersAnswer() {
        target.path("users/outUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        Response response = target.path("users/inUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void deleteInUsersAnswer() {
        target.path("users/inUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        Response response = target.path("users/inUsers/" + usernameB).request().header("authorization", tokenA).delete();
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void getOutUsersAnswer() {
        Response response = target.path("users/outUsers").queryParam("edgeType", "VALID").request().header("authorization", tokenA).get();
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void postOutUsersAnswer() {
        Response response = target.path("users/outUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void putOutUsersAnswer() {
        target.path("users/inUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        Response response = target.path("users/outUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void deleteOutUsersAnswer() {
        target.path("users/outUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        Response response = target.path("users/outUsers/" + usernameB).request().header("authorization", tokenA).delete();
        int status = response.getStatus();
        assertEquals(200, status);
    }

    /** InEdge **/
    @Test
    public void invalidInUser() {
        Response response = target.path("users/inUsers").queryParam("specifiedUsername", usernameC).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(404, status);
        assertEquals("{\"error_message\":\"The user, " + usernameC + ", does not exist.\"}", message);
    }

    @Test
    public void inEdgeRequestAlreadyExists() {
        target.path("users/inUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        Response response = target.path("users/inUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(409, status);
        assertEquals("{\"error_message\":\"An edge request between the user, " + usernameA + ", and the user, " + usernameB + ", made by the user, " + usernameA + ", already exists.\"}", message);
    }

    @Test
    public void otherInEdgeRequestAlreadyExists() {
        target.path("users/outUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        Response response = target.path("users/inUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(409, status);
        assertEquals("{\"error_message\":\"An edge request between the user, " + usernameA + ", and the user, " + usernameB + ", made by the user, " + usernameB + ", already exists.\"}", message);
    }

    @Test
    public void inEdgeAlreadyExists() {
        target.path("users/outUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        target.path("users/inUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);
        Response response = target.path("users/inUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(409, status);
        assertEquals("{\"error_message\":\"An edge between the user, " + usernameA + ", and the user, " + usernameB + ", already exists.\"}", message);
    }

    @Test
    public void inEdgeWithNoRequest() {
        Response response = target.path("users/inUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);
        int status = response.getStatus();
        assertEquals(404, status);
    }

    @Test
    public void getValidInEdges() {
        target.path("users/outUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        target.path("users/inUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);

        Response response = target.path("users/inUsers").queryParam("edgeType", "VALID").request().header("authorization", tokenA).get();
        String message = response.readEntity(String.class);
        int status = response.getStatus();
        assertEquals(200, status);
        assertEquals("[{\"username\":\"" + usernameB + "\"}]", message);
    }

    @Test
    public void getInEdgeRequests() {
        target.path("users/inUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        Response response = target.path("users/inUsers").queryParam("edgeType", "MY_REQUESTS").request().header("authorization", tokenA).get();
        String message = response.readEntity(String.class);
        int status = response.getStatus();
        assertEquals(200, status);
        assertEquals("[{\"username\":\"" + usernameB + "\"}]", message);
    }

    @Test
    public void getOthersInEdgeRequests() {
        target.path("users/outUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        Response response = target.path("users/inUsers").queryParam("edgeType", "OTHERS_REQUESTS").request().header("authorization", tokenA).get();
        String message = response.readEntity(String.class);
        int status = response.getStatus();
        assertEquals(200, status);
        assertEquals("[{\"username\":\"" + usernameB + "\"}]", message);
    }

    /** outEdge **/
    @Test
    public void invalidOutUser() {
        Response response = target.path("users/outUsers").queryParam("specifiedUsername", usernameC).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(404, status);
        assertEquals("{\"error_message\":\"The user, " + usernameC + ", does not exist.\"}", message);
    }

    @Test
    public void outEdgeRequestAlreadyExists() {
        target.path("users/outUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        Response response = target.path("users/outUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(409, status);
        assertEquals("{\"error_message\":\"An edge request between the user, " + usernameA + ", and the user, " + usernameB + ", made by the user, " + usernameA + ", already exists.\"}", message);
    }

    @Test
    public void otherOutEdgeRequestAlreadyExists() {
        target.path("users/inUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        Response response = target.path("users/outUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(409, status);
        assertEquals("{\"error_message\":\"An edge request between the user, " + usernameA + ", and the user, " + usernameB + ", made by the user, " + usernameB + ", already exists.\"}", message);
    }

    @Test
    public void outEdgeAlreadyExists() {
        target.path("users/inUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        target.path("users/outUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);
        Response response = target.path("users/outUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        String message = response.readEntity(String.class);
        assertEquals(409, status);
        assertEquals("{\"error_message\":\"An edge between the user, " + usernameA + ", and the user, " + usernameB + ", already exists.\"}", message);
    }

    @Test
    public void outEdgeWithNoRequest() {
        Response response = target.path("users/outUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);
        int status = response.getStatus();
        assertEquals(404, status);
    }

    @Test
    public void getValidOutEdges() {
        target.path("users/inUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        target.path("users/outUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);
        Response response = target.path("users/outUsers").queryParam("edgeType", "VALID").request().header("authorization", tokenA).get();
        String message = response.readEntity(String.class);
        int status = response.getStatus();
        System.out.println(message);
        assertEquals(200, status);
        assertEquals("[{\"username\":\"" + usernameB + "\"}]", message);
    }

    @Test
    public void getOutEdgeRequests() {
        target.path("users/outUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        Response response = target.path("users/outUsers").queryParam("edgeType", "MY_REQUESTS").request().header("authorization", tokenA).get();
        String message = response.readEntity(String.class);
        int status = response.getStatus();
        assertEquals(200, status);
        assertEquals("[{\"username\":\"" + usernameB + "\"}]", message);
    }

    @Test
    public void getOthersOutEdgeRequests() {
        target.path("users/inUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        Response response = target.path("users/outUsers").queryParam("edgeType", "OTHERS_REQUESTS").request().header("authorization", tokenA).get();
        String message = response.readEntity(String.class);
        int status = response.getStatus();
        assertEquals(200, status);
        assertEquals("[{\"username\":\"" + usernameB + "\"}]", message);
    }

    @Test
    public void requestDoubleEdge() {
        Response response = target.path("users/doubleUsers").queryParam("specifiedUsername", usernameB).request().header("authorization", tokenA).post(emptyEntity);
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void validateDoubleEdge() {
        target.path("users/doubleUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        Response response = target.path("users/doubleUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void deleteDoubleEdgeRequest() {
        target.path("users/doubleUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        Response response = target.path("users/doubleUsers/" + usernameB).request().header("authorization", tokenA).delete();
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void deleteDoubleValidEdge() {
        target.path("users/doubleUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenB).post(emptyEntity);
        target.path("users/doubleUsers/" + usernameB).request().header("authorization", tokenA).put(emptyEntity);
        Response response = target.path("users/doubleUsers/" + usernameB).request().header("authorization", tokenA).delete();
        int status = response.getStatus();
        assertEquals(200, status);
    }

    @Test
    public void postUsersEdgeToSelf() {
        Response response = target.path("users/inUsers").queryParam("specifiedUsername", usernameA).request().header("authorization", tokenA).post(emptyEntity);
        String message = response.readEntity(String.class);
        int status = response.getStatus();
        assertEquals(400, status);
        assertEquals("{\"error_message\":\"The user " + usernameA + " is trying to make an edge to itself. This is not allowed.\"}", message);

    }
}