package dk.aau.astep.appserver.restapi.api;

public class ApiCodeMessageDescription {
    public static final String CODE_200 = "<b>OK</b><br/>" +
                                          "Standard response for successful HTTP requests.";

    public static final String CODE_201 = "<b>Created</b><br/>" +
            "The request has been fulfilled, resulting in the creation of a new resource.";

    public static final String CODE_400 = "<b>Bad Request</b><br/>" +
                                          "The server cannot or will not process the request due to an apparent client error " +
                                          "(e.g., malformed request syntax, invalid request message framing, or deceptive request routing).";

    public static final String CODE_401 = "<b>Unauthorized</b><br/>" +
                                          "The request was a valid request, but the server is refusing to respond to it due to " +
                                          "authentication has failed.";

    public static final String CODE_404 = "<b>Not Found</b><br/>" +
                                          "The request was a valid request, but a resource mentioned in the request was not found.";

    public static final String CODE_409 = "<b>Conflict</b><br/>" +
                                          "The request could not be processed because of a conflict in the request.";

    public static final String CODE_500 = "<b>Internal Server Error</b><br/>" +
                                          "A generic error message, given when an unexpected condition was encountered and no more " +
                                          "specific message is suitable.";
}
