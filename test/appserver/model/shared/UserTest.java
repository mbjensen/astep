package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.appserver.business.service.usermanagement.FullUserFactory;
import dk.aau.astep.appserver.restapi.resource.usermanagement.TestHelper;
import dk.aau.astep.db.persistent.api.BaseRepository;
import org.junit.*;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test of the User class
 */
public class UserTest {
    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    private final String usernameA = TestHelper.usernameA;
    private final String passwordA = TestHelper.passwordA;
    private final User userA = new User(usernameA);

    private final String usernameB = TestHelper.usernameB;
    private final String passwordB = TestHelper.passwordB;

    private final String usernameC = TestHelper.usernameC;
    private final String passwordC = TestHelper.passwordC;

    @BeforeClass
    public static void classSetUp() {
    }

    @AfterClass
    public static void classTearDown() {

    }

    @Before
    public void setUp() throws Exception {
        TestHelper.setUpExistingUserNoGroupsNoEdges(usernameA, passwordA);
        TestHelper.setUpExistingUser(usernameB, passwordB);
        TestHelper.setUpExistingUser(usernameC, passwordC);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void AccessUsersSelf() {
        List<User> users = userA.fetchAccessibleUsers(true);
        assertEquals(1, users.size());
        assertEquals(usernameA, users.get(0).getUsername());
    }

    @Test
    public void AccessUsersGroupsOverlap() throws IOException {
        int groupAId = baseRepository.groupQueries().createGroup();
        int groupBId = baseRepository.groupQueries().createGroup();

        baseRepository.memberQueries().createMembership(usernameA, groupAId);
        baseRepository.memberQueries().createMembership(usernameB, groupAId);
        baseRepository.memberQueries().createMembership(usernameC, groupAId);

        baseRepository.memberQueries().createMembership(usernameA, groupBId);
        baseRepository.memberQueries().createMembership(usernameB, groupBId);

        List<User> users = userA.fetchAccessibleUsers(true);

        assertEquals(3, users.size());
    }

    @Test
    public void AccessUsersInEdge() throws IOException {
        baseRepository.edgeQueries().addEdge(usernameA, usernameB);
        List<User> users = userA.fetchAccessibleUsers(true);
        assertEquals(2, users.size());
    }

    @Test
    public void AccessUsersOutEdge() throws IOException {
        baseRepository.edgeQueries().addEdge(usernameB, usernameA);
        List<User> users = userA.fetchAccessibleUsers(true);
        assertEquals(1, users.size());
    }

    @Test
    public void AccessUsersEdgeGroupOverlap() throws IOException {
        int groupAId = baseRepository.groupQueries().createGroup();

        baseRepository.memberQueries().createMembership(usernameA, groupAId);
        baseRepository.memberQueries().createMembership(usernameB, groupAId);

        baseRepository.edgeQueries().addEdge(usernameA, usernameB);

        List<User> users = userA.fetchAccessibleUsers(true);

        assertEquals(2, users.size());
    }
}
