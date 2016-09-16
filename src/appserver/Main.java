package dk.aau.astep.appserver;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.json.internal.FilteringMoxyJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import io.swagger.jaxrs.config.*;
import org.glassfish.jersey.message.filtering.SelectableEntityFilteringFeature;

import javax.net.ssl.SSLContext;
import javax.ws.rs.ext.ContextResolver;

import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import dk.aau.astep.appserver.restapi.api.CorsResponseFilter;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final URI BASE_URI = getBaseURI();

    public static final boolean PRODUCTION_MODE = false;

    private static URI getBaseURI() {
        if (PRODUCTION_MODE == true) {
            //In production
            return UriBuilder.fromUri("http://astep.cs.aau.dk/api/").port(8080).build();

        } else {
            //In development
            return UriBuilder.fromUri("http://localhost/api/").port(8080).build();
        }
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {

        /*SSLContextConfigurator configurator = new SSLContextConfigurator();
        URL url = configurator.getClass().getResource("/keystore.jks");
        if(url == null) throw new Error("Could not get Keystore!");
        configurator.setKeyStoreFile(url.getFile());
        configurator.setKeyStorePass("testtest");
        configurator.setKeyPass("testtest");
        //configurator.setTrustStoreFile(); use in production mode when we have ssl certificate. TODO
        configurator.setSecurityProtocol("TLS");

        SSLContext context = configurator.createSSLContext();
        SSLEngineConfigurator engineConfigurator = new SSLEngineConfigurator(context);
        engineConfigurator.setWantClientAuth(false);
        engineConfigurator.setClientMode(false);
        engineConfigurator.setNeedClientAuth(false);*/

        // create a resource config that scans for JAX-RS resources and providers
        // in dk.aau.astep package
        final ResourceConfig resourceConfig = new ResourceConfig()
                .packages("dk.aau.astep")
                .property(SelectableEntityFilteringFeature.QUERY_PARAM_NAME, "select")
                .register(SelectableEntityFilteringFeature.class)
                .register(io.swagger.jaxrs.listing.SwaggerSerializers.class)
                .register(io.swagger.jaxrs.listing.ApiListingResource.class)
                .register(CorsResponseFilter.class)
                .register(MoxyJsonFeature.class)
                .register(createMoxyJsonResolver());

        // Setup for Swagger.io - Docomentaion:
        // https://github.com/swagger-api/swagger-core/wiki/Swagger-Core-Jersey-2.X-Project-Setup-1.5
        BeanConfig config = new BeanConfig();
        config.setVersion("1.0.0");
        config.setBasePath("/api");
        config.setResourcePackage("dk.aau.astep.appserver.restapi.resource");
        config.setScan(true);

        final HttpServer grizzlyServer = GrizzlyHttpServerFactory.createHttpServer(
            getBaseURI(),
            resourceConfig,
            false/*,
            engineConfigurator*/);

        try {
            grizzlyServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return grizzlyServer;
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        ////Get directory for swagger.io
        if (PRODUCTION_MODE == true) {
            //Get directory for swagger.io in production
            server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("swagger-ui/"), "/");
        } else {
            //Get directory for swagger.io in development
            server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("src/main/resources/swagger-ui/"), "/");
        }

        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();

        server.shutdown();
    }

    // For documentation see chapter 9.1.2 - MOXy: https://jersey.java.net/documentation/latest/media.html#json.moxy
    public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
        return moxyJsonConfig.resolver();
    }
}

