package dk.aau.astep.exception;

import dk.aau.astep.appserver.restapi.api.ApiResponseMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class BusinessException extends WebApplicationException {
    public BusinessException() {
        super(Response.status(500).entity(new ApiResponseMessage("Something went wrong")).type(MediaType.APPLICATION_JSON).build());
    }
}