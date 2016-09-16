package dk.aau.astep.appserver.restapi.api;

import dk.aau.astep.appserver.model.shared.AuthenticationToken;
import dk.aau.astep.appserver.model.shared.Group;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.exception.ApiExceptionHandler;

/**
 * Can check if required parameters are specified.
 */
public class RequiredParamCheck {
    /**
     * Checks if a string query parameter is specified. If it is not, a WebApplicationException is thrown.
     * @param param The parameter
     * @param parameterName The name of the parameter
     */
    public static void checkQuery(String param, String parameterName) {
        if (param == null) {
            throw new ApiExceptionHandler().missingQueryParameter(parameterName);
        }
    }

    /**
     * Checks if a boolean query parameter is specified. If it is not, a WebApplicationException is thrown.
     * @param param The parameter
     * @param parameterName The name of the parameter
     */
    public static void checkQuery(Boolean param, String parameterName) {
        if (param == null) {
            throw new ApiExceptionHandler().missingQueryParameter(parameterName);
        }
    }

    /**
     * Checks if an enum query parameter is specified. If it is not, a WebApplicationException is thrown.
     * @param param The parameter
     * @param parameterName The name of the parameter
     */
    public static void checkQuery(Enum param, String parameterName) {
        if (param == null) {
            throw new ApiExceptionHandler().missingQueryParameter(parameterName);
        }
    }

    /**
     * Checks if a user query parameter is specified. If it is not, a WebApplicationException is thrown.
     * @param param The parameter
     * @param parameterName The name of the parameter
     */
    public static void checkQuery(User param, String parameterName) {
        if (param == null) {
            throw new ApiExceptionHandler().missingQueryParameter(parameterName);
        }
    }

    /**
     * Checks if a group query parameter is specified. If it is not, a WebApplicationException is thrown.
     * @param param The parameter
     * @param parameterName The name of the parameter
     */
    public static void checkQuery(Group param, String parameterName) {
        if (param == null) {
            throw new ApiExceptionHandler().missingQueryParameter(parameterName);
        }
    }

    /**
     * Checks if a string header is specified. If it is not, a WebApplicationException is thrown.
     * @param value The value of the header
     * @param headerName The name of the header
     */
    public static void checkHeader(String value, String headerName) {
        if (value == null) {
            throw new ApiExceptionHandler().missingHeader(headerName);
        }
    }

    /**
     * Checks if a authenticating token header is specified. If it is not, a WebApplicationException is thrown.
     * @param value The value of the header
     * @param headerName The name of the header
     */
    public static void checkHeader(AuthenticationToken value, String headerName) {
        if (value == null) {
            throw new ApiExceptionHandler().missingHeader(headerName);
        }
    }

    /**
     * Checks if a string request body is specified. If it is not, a WebApplicationException is thrown.
     * @param value The value of the body
     */
    public static void checkBody(String value) {
        if (value.equals("")) {
            throw new ApiExceptionHandler().missingBody();
        }
    }
}
