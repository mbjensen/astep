package dk.aau.astep.appserver.business.service.usermanagement;

import dk.aau.astep.appserver.model.shared.FullUser;
import dk.aau.astep.db.persistent.data.UserFactory;

import java.time.Instant;

/**
 * Factory for FullUser class
 * This is necessary in order to use the persistent database API
 */
public class FullUserFactory implements UserFactory<FullUser> {
    @Override
    public FullUser create(String username, byte[] password, byte[] salt, byte[] token, Instant expirationDate){
        return new FullUser(username, password, salt, token, expirationDate);
    }
}
