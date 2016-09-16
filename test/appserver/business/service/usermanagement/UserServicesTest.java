package dk.aau.astep.appserver.business.service.usermanagement;

import dk.aau.astep.appserver.model.shared.ExpirationAuthenticationToken;
import dk.aau.astep.appserver.model.shared.FullUser;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.appserver.restapi.resource.usermanagement.TestHelper;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.exception.ApiExceptionHandler;
import org.apache.hadoop.hbase.shaded.org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Test of the users resource
 */
public class UserServicesTest {
    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    private final String usernameA = TestHelper.usernameA;
    private final String passwordA = TestHelper.passwordA;
    private final User userA = new User(usernameA);

    private final String usernameB = TestHelper.usernameB;
    private final String passwordB = TestHelper.passwordB;
    private final User userB = new User(usernameB);

    @Before
    public void setUp() throws Exception {
        // Create alice
        baseRepository.userQueries().deleteUser(usernameA); // delete if exists beforehand
        UserServices.createUser(new User(usernameA), passwordA);
        UserServices.issueToken(new User(usernameA));

        // Delete bob if created
        baseRepository.userQueries().deleteUser(usernameB); // delete if exists beforehand
    }

    @After
    public void tearDown() throws Exception {
    }

    /** Basic UM services: CreateUser, GetToken, IssueToken and ChangePassword **/

    @Test
    public void createUser() {
        UserServices.createUser(userB, passwordB);

        FullUser userBTest = userB.getFullUser();

        assertEquals(userB.getUsername(), userBTest.getUsername());
        Assert.assertArrayEquals(PasswordHashing.hashPassword(passwordB, userBTest.getSalt()), userBTest.getPassword());
    }

    @Test
    public void changePassword() {
        UserServices.changePassword(userA, passwordB);

        FullUser userATest = userA.getFullUser();

        Assert.assertArrayEquals(PasswordHashing.hashPassword(passwordB, userATest.getSalt()), userATest.getPassword());
    }

    @Test
    public void issueToken() {
        ExpirationAuthenticationToken token = UserServices.issueToken(userA);

        FullUser userATest = userA.getFullUser();

        Assert.assertArrayEquals(DatatypeConverter.parseHexBinary(token.getValue()), userATest.getToken());
    }

    @Test
    /** Tests if the date assigned to tokens issued is correct **/
    public void issueTokenDate() {
        ExpirationAuthenticationToken token = UserServices.issueToken(userA);

        Instant plus30days = Instant.now().plus(Period.ofDays(UserServices.TOKEN_EXPIRATION_DAYS));

        // Is within 5 minutes of plus 30 days
        assertTrue(token.getExpirationDate().isAfter(plus30days.minus(Duration.ofMinutes(5))));
        assertTrue(token.getExpirationDate().isBefore(plus30days.plus(Duration.ofMinutes(5))));
    }

    @Test
    public void getToken() {
        ExpirationAuthenticationToken token = UserServices.issueToken(userA);

        ExpirationAuthenticationToken tokenTest = UserServices.getToken(userA);

        assertEquals(token.getValue(), tokenTest.getValue());
        assertEquals(token.getExpirationDate().toString(), tokenTest.getExpirationDate().toString());
    }

    /** Tests if a new token is created when getToken is called and the token the user have is expired**/
    @Test
    public void getExpiredToken() {

        byte[] token = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(token);

        try {
            baseRepository.userQueries().setToken(usernameA, token, Instant.now().minus(Period.ofDays(1)));
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        ExpirationAuthenticationToken tokenTest = UserServices.getToken(userA);

        assertFalse(Arrays.equals(token, DatatypeConverter.parseHexBinary(tokenTest.getValue())));
    }


    /** Tests if a new token is created when getToken is called and the user does not have a token**/
    @Test
    public void getInvalidToken() {
        UserServices.createUser(userB, passwordB);

        FullUser userBBeforeGetToken = userB.getFullUser();
        ExpirationAuthenticationToken tokenTest = UserServices.getToken(userB);

        assertFalse(Arrays.equals(userBBeforeGetToken.getToken(), DatatypeConverter.parseHexBinary(tokenTest.getValue())));
    }
}