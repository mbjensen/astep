package dk.aau.astep.appserver.restapi.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import dk.aau.astep.logger.ALogger;
import dk.aau.astep.logger.Module;
import org.apache.logging.log4j.Level;

/**
 * Resources for logging services
 */
@Path("errors")
@Api(value = "errors", description ="This is the logger resource")
public class LoggerResource {
    @POST
    @ApiOperation(value = "Log error", notes = "Log an error.")
    public Response logError(@ApiParam(value= "Message to log", required = true) @QueryParam("message") String message) {
        if (message == null) {
            throw new WebApplicationException(400);
        }

        ALogger.log(message, Module.APP, Level.ERROR);

        return Response.status(200).build();
    }
}
