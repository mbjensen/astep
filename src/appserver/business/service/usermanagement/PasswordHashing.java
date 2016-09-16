package dk.aau.astep.appserver.business.service.usermanagement;

import dk.aau.astep.exception.ApiExceptionHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Used to hash passwords given a salt and create a new salt
 */

public class PasswordHashing {
	private static final String HASH_ALGORITHM = "SHA-256";
	private static final int SALT_SIZE = 32;
	
	public static byte[] hashPassword(String password, byte[] salt) {
        MessageDigest messageDigest;

		try {
			messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new ApiExceptionHandler().internalServerError();
		}

        messageDigest.reset();
        messageDigest.update(salt);
        return messageDigest.digest(password.getBytes());
	}
	
	public static byte[] createSalt() {
		byte[] salt = new byte[SALT_SIZE];
        new SecureRandom().nextBytes(salt);
		return salt;
	}
}