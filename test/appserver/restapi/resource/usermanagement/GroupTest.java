package dk.aau.astep.appserver.restapi.resource.usermanagement;

import com.google.gson.Gson;
import dk.aau.astep.appserver.Main;
import dk.aau.astep.appserver.business.service.usermanagement.FullUserFactory;
import dk.aau.astep.appserver.model.shared.Group;
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
 * Test of the basic user management services from the API perspective
 */
public class GroupTest {
    private static HttpServer server;
    private static WebTarget target;

    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    private final String adminName = TestHelper.usernameA;
    private final String adminPw = TestHelper.passwordA;
    private String adminToken;
    private final User adminUser = new User(adminName);

    private final String customerName = TestHelper.usernameB;
    private final String customerPw = TestHelper.passwordB;
    private String customerToken;
    private final User customerUser = new User(customerName);

    private final String outsiderName = TestHelper.usernameC;
    private final String outsiderPw = TestHelper.passwordC;
    private String outsiderToken;

    private int customerGroupId;
    private int adminGroupId;

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

    /**
     * Admins administrate themselves and customers
     * Outsider user is not in any group
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // Set up users
        adminToken = TestHelper.setUpExistingUserNoGroupsNoInvitations(adminName, adminPw);
        customerToken = TestHelper.setUpExistingUserNoGroupsNoInvitations(customerName, customerPw);
        outsiderToken = TestHelper.setUpExistingUserNoGroupsNoInvitations(outsiderName, outsiderPw);

        // Set up groups
        customerGroupId = baseRepository.groupQueries().createGroup();
        adminGroupId = baseRepository.groupQueries().createGroup();

        // Add users to groups
        baseRepository.memberQueries().createMembership(customerUser.getUsername(), customerGroupId);
        baseRepository.memberQueries().createMembership(adminUser.getUsername(), adminGroupId);

        // Set up administrations
        baseRepository.groupQueries().createAdmin(adminGroupId, adminGroupId);
        baseRepository.groupQueries().createAdmin(adminGroupId, customerGroupId);
    }

    @After
    public void tearDown() throws Exception {

    }


    /** Group id wrong format **/

    @Test
    public void groupIdNotNumber() {
        Response response = target.path("groups/15sdf3/members").request()
                            .header("authorization", customerToken).get();
        TestHelper.assertResponseStatus(response, 400);
    }

    @Test
    public void groupIdMaxValue() {
        // 2147483647 is the maximum value of int
        Response response = target.path("groups/2147483647/members").request()
                            .header("authorization", customerToken).get();
        TestHelper.assertResponseStatus(response, 404); // group not exist
    }

    @Test
    public void groupIdTooHigh() {
        Response response = target.path("groups/2147483648/members").request()
                            .header("authorization", customerToken).get();
        TestHelper.assertResponseStatus(response, 400);
    }


    /** Get groups **/

    @Test
    public void getGroupsAnswer() {
        Response response = target.path("users/groupMemberships").request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 1);
    }

    @Test
    public void getNoGroupsAnswer() {
        Response response = target.path("users/groupMemberships").request().header("authorization", outsiderToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 0);
    }


    /** Invites **/

    @Test
    public void deleteInviteAsUser() {
        // Check that user has 0 invitations
        Response response = target.path("users/groupInvitations").request().header("authorization", outsiderToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 0);

        // Admin invites outsider to customers
        response = target.path("groups/" + customerGroupId + "/invited").queryParam("specifiedUsername", outsiderName)
                   .queryParam("adminGroupId", adminGroupId).request()
                   .header("authorization", adminToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 200);

        // Check that user has 1 invitation
        response = target.path("users/groupInvitations").request().header("authorization", outsiderToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 1);

        // Outsider deletes invite
        response = target.path("users/groupInvitations/" + customerGroupId).request().header("authorization", outsiderToken).delete();
        TestHelper.assertResponseStatus(response, 200);

        // Check that user has 0 invitations
        response = target.path("users/groupInvitations").request().header("authorization", outsiderToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 0);
    }

    @Test
    public void deleteInviteAsAdmin() {
        // Check that customer group are inviting 0 users
        Response response = target.path("groups/" + customerGroupId + "/invited")
                            .queryParam("adminGroupId", adminGroupId).request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 0);

        // Admin invites outsider to customers
        response = target.path("groups/" + customerGroupId + "/invited").queryParam("specifiedUsername", outsiderName)
                   .queryParam("adminGroupId", adminGroupId).request()
                   .header("authorization", adminToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 200);

        // Check that customer group are inviting 1 user
        response = target.path("groups/" + customerGroupId + "/invited")
                   .queryParam("adminGroupId", adminGroupId).request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 1);

        // Admin revokes invitation
        response = target.path("groups/" + customerGroupId + "/invited/" + outsiderName)
                   .queryParam("adminGroupId", adminGroupId).request()
                   .header("authorization", adminToken).delete();
        TestHelper.assertResponseStatus(response, 200);

        // Check that customer group are inviting 0 users
        response = target.path("groups/" + customerGroupId + "/invited")
                            .queryParam("adminGroupId", adminGroupId).request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 0);
    }


    /** Join group **/

    @Test
    public void joinGroup() throws IOException {
        // Invite outsider to customer group
        baseRepository.inviteQueries().createInvite(customerGroupId, outsiderName);

        Response response = target.path("groups/" + customerGroupId + "/members")
                                  .queryParam("adminGroupId", customerGroupId).request().header("authorization", customerToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 1);
        
        response = target.path("users/groupMemberships").queryParam("groupId", customerGroupId)
                         .request().header("authorization", outsiderToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 200);
        
        response = target.path("groups/" + customerGroupId + "/members")
                         .queryParam("adminGroupId", customerGroupId).request().header("authorization", customerToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 2);
    }
    
    /** Try to join a group without an invite to that group **/
    
    @Test 
    public void tryJoinGroupWithoutInvite() {
        Response response = target.path("groups/" + adminGroupId + "/groupMemberships")
                .queryParam("adminGroupId", adminGroupId).request().header("authorization", customerToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 404);
    }

    /** Get administrations **/

    @Test
    public void getAdminsAnswer() {
        Response response = target.path("groups/" + adminGroupId + "/administratedGroups")
                            .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 2);
    }

    @Test
    public void get0AdminsAnswer() {
        Response response = target.path("groups/" + customerGroupId + "/administratedGroups")
                            .request().header("authorization", customerToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 0);
    }

    @Test
    public void get0AdminsAsAdminAnswer() {
        Response response = target.path("groups/" + customerGroupId + "/administratedGroups")
                            .queryParam("adminGroupId", adminGroupId)
                            .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 0);
    }

    /**
     * Try to get admins of a group that you are not, even if you are not allowed
     */
    @Test
    public void getAdminsDenyNonAdmin1() {
        Response response = target.path("groups/" + adminGroupId + "/administratedGroups")
                            .request().header("authorization", customerToken).get();
        TestHelper.assertResponseStatus(response, 401);
    }

    /**
     * Try to get admins of a group that you are not, even if you are not allowed
     */
    @Test
    public void getAdminsDenyNonAdmin2() {
        Response response = target.path("groups/" + adminGroupId + "/administratedGroups")
                            .queryParam("adminGroupId", customerGroupId)
                            .request().header("authorization", customerToken).get();
        TestHelper.assertResponseStatus(response, 401);
    }


    /** Add admin **/

    @Test
    public void createGroupAndTryAddAdminTwice() {
        // Create group
        Response response = target.path("groups").request()
                            .header("authorization", adminToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 200);
        Gson gs = new Gson();
        String message = response.readEntity(String.class);
        Group group = gs.fromJson(message, Group.class);
        int newGroupId = group.getId();

        // Assert that the admin group administrates 2 groups
        response = target.path("groups/" + adminGroupId + "/administratedGroups")
                   .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 2);

        // Add admin from adminGroup to newGroup
        response = target.path("groups/" + adminGroupId + "/administratedGroups").queryParam("otherGroupId", newGroupId)
                   .queryParam("adminSpecifiedGroupId", adminGroupId).queryParam("adminOtherGroupId", newGroupId)
                   .request().header("authorization", adminToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 200);

        // Assert that the admin group administrates 3 groups
        response = target.path("groups/" + adminGroupId + "/administratedGroups")
                   .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 3);

        // Try to add admin from adminGroup to newGroup again
        response = target.path("groups/" + adminGroupId + "/administratedGroups")
                   .queryParam("otherGroupId", newGroupId)
                   .queryParam("adminSpecifiedGroupId", adminGroupId).queryParam("adminOtherGroupId", newGroupId)
                   .request().header("authorization", adminToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 409);
    }


    /** Get users **/

    @Test
    public void getMembersAsUsers() {
        Response response = target.path("groups/" + customerGroupId + "/members")
                            .request().header("authorization", customerToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 1);
    }

    @Test
    public void getMembersAsAdmin() {
        Response response = target.path("groups/" + customerGroupId + "/members")
                            .queryParam("adminGroupId", adminGroupId)
                            .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 1);
    }


    /** Remove user as admin **/

    @Test
    public void removeUserAsAdmin() throws IOException {
        int num = baseRepository.memberQueries().getMembers(customerGroupId).size();
        assertEquals(1, num);

        Response response = target.path("groups/" + customerGroupId + "/members/" + customerName)
                            .queryParam("adminGroupId", adminGroupId)
                            .request().header("authorization", adminToken).delete();
        TestHelper.assertResponseStatus(response, 200);

        num = baseRepository.memberQueries().getMembers(customerGroupId).size();
        assertEquals(0, num);
    }


    /** Leave group **/

    @Test
    public void leaveGroup() throws IOException {
        int num = baseRepository.memberQueries().getMembers(customerGroupId).size();
        assertEquals(1, num);

        Response response = target.path("users/groupMemberships/" + customerGroupId).request()
                            .header("authorization", customerToken).delete();
        TestHelper.assertResponseStatus(response, 200);

        num = baseRepository.memberQueries().getMembers(customerGroupId).size();
        assertEquals(0, num);
    }


    /** Remove admin **/

    @Test
    public void removeAdmin() throws IOException {
        // Assert that the admin group administrates 2 groups
        Response response = target.path("groups/" + adminGroupId + "/administratedGroups")
                            .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 2);

        // Make the customer group administrate itself
       baseRepository.groupQueries().createAdmin(customerGroupId, customerGroupId);

        // Remove admin from admin group to customer group
        response = target.path("groups/" + adminGroupId + "/administratedGroups/" + customerGroupId)
                   .queryParam("adminGroupId", adminGroupId).request()
                   .header("authorization", adminToken).delete();
        TestHelper.assertResponseStatus(response, 200);

        // Assert that the admin group still administrates 2 group
        response = target.path("groups/" + adminGroupId + "/administratedGroups")
                   .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 1);
    }

    @Test
    public void tryRemoveNonExistingAdmin() throws IOException {
        // Make the admin group no longer administrate the customer group
        baseRepository.groupQueries().deleteAdmin(adminGroupId, customerGroupId);

        // Assert that the admin group administrates 1 group
        Response response = target.path("groups/" + adminGroupId + "/administratedGroups")
                            .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 1);

        // Try to remove admin from admin group to customer group
        response = target.path("groups/" + adminGroupId + "/administratedGroups/" + customerGroupId)
                   .queryParam("adminGroupId", adminGroupId).request()
                   .header("authorization", adminToken).delete();
        TestHelper.assertResponseStatus(response, 404);
    }

    /**
     * Try to remove a admin in the scenario where the other group has no other admins.
     * Such a request should not be allowed.
     */
    @Test
    public void tryRemoveAdminWithNoOtherAdmin() {
        // Assert that the admin group administrates 2 groups
        Response response = target.path("groups/" + adminGroupId + "/administratedGroups")
                   .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 2);

        // Remove admin from admin group to customer group
        response = target.path("groups/" + adminGroupId + "/administratedGroups/" + customerGroupId)
                   .queryParam("adminGroupId", adminGroupId).request()
                   .header("authorization", adminToken).delete();
        TestHelper.assertResponseStatus(response, 409);

        // Assert that the admin group still administrates 2 group
        response = target.path("groups/" + adminGroupId + "/administratedGroups")
                   .request().header("authorization", adminToken).get();
        TestHelper.assert200ResponseWithJsonArrayLength(response, 2);
    }

    /**
     * Not found
     */
    @Test
    public void inviteNotFound() {
        // Outsider try to join customer group without invite
        Response response = target.path("users/groupMemberships").queryParam("groupId", customerGroupId)
                            .request().header("authorization", outsiderToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 404);
    }

    @Test
    public void invitedAlready() {
        // Invite outsider
        Response response = target.path("groups/" + customerGroupId + "/invited")
                            .queryParam("specifiedUsername", outsiderName)
                            .queryParam("adminGroupId", adminGroupId)
                            .request().header("authorization", adminToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 200);

        // Invite again
        response = target.path("groups/" + customerGroupId + "/invited")
                   .queryParam("specifiedUsername", outsiderName)
                   .queryParam("adminGroupId", adminGroupId)
                   .request().header("authorization", adminToken).post(emptyEntity);
        TestHelper.assertResponseStatus(response, 409);
    }
}