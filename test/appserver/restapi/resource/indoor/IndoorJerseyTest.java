package dk.aau.astep.appserver.restapi.resource.indoor;

import dk.aau.astep.appserver.Main;
import dk.aau.astep.appserver.business.service.usermanagement.UserServices;
import dk.aau.astep.appserver.model.shared.ExpirationAuthenticationToken;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.appserver.restapi.resource.IndoorLocationResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;

import javax.ws.rs.core.Application;
import java.net.URI;

public abstract class IndoorJerseyTest extends JerseyTest {
    protected static final String indoorResourcePath = "locations/indoor/";

    private ExpirationAuthenticationToken token = UserServices.getToken(new User("admin"));

    protected String getToken() {
        return token.getValue();
    }

    /**
     * Enables test traffic logging (inbound and outbound headers) as well as dumping the HTTP message entity as part of the traffic logging
     * Returns a ResourceConfig of the test application that only contains the IndoorLocationResource resource class
     */
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        return new ResourceConfig(IndoorLocationResource.class);
    }

    /**
     * Sets the base URI of the test application to Main.Base_URI
     */
    @Override
    protected URI getBaseUri() {
        return Main.BASE_URI;
    }
}
