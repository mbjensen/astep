package dk.aau.astep.appserver.business.service.usermanagement;

import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PasswordHashingTest {
    private final String password = "1234";
    private final String salt = "BD44CEBA09A3FBE9A9595FD7957B1309464752F270E8F713144F6DA04BFA6111";
    
    @Test
    public void hashTwoEqualPasswordsDifferentSalt() {
        byte[] saltA = PasswordHashing.createSalt();
        byte[] saltB = PasswordHashing.createSalt();
        
        byte[] hashedPasswordSaltA = PasswordHashing.hashPassword(password, saltA);
        byte[] hashedPasswordSaltB = PasswordHashing.hashPassword(password, saltB);
        
        assertNotEquals(DatatypeConverter.printHexBinary(hashedPasswordSaltA), DatatypeConverter.printHexBinary(hashedPasswordSaltB));
    }
    
    @Test
    public void hashTwoEqualPasswordsSameSalt() {
        byte[] hashedPasswordA = PasswordHashing.hashPassword(password, DatatypeConverter.parseHexBinary(salt));
        byte[] hashedPasswordB = PasswordHashing.hashPassword(password, DatatypeConverter.parseHexBinary(salt));
        
        assertEquals(DatatypeConverter.printHexBinary(hashedPasswordA), DatatypeConverter.printHexBinary(hashedPasswordB));
    }
}
