package dk.aau.astep.appserver.business.service.usermanagement;

import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.db.persistent.api.EdgeRequestQueries;
import dk.aau.astep.exception.ApiExceptionHandler;

import java.io.IOException;

/**
 * Edge services.
 */
public class EdgeServices {
    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    /**
    /** In-edge services **/

    /**
     * Gets an array of users that are valid in-users.
     * @param user is the user whose valid in-users are returned.
     * @return An array of users.
     */
    public static User[] getValidInUsers(User user) {
        return user.getInUsers();
    }

    /**
     * Gets an array of the users that are requested as in-users.
     * @param user is the user whose requested in-users are returned.
     * @return An array of users.
     */
    public static User[] getMyRequestedInUsers(User user) {
        return user.getMyRequestedInUsers();
    }

    /**
     * Gets an array of users that has requested the be in-users, to the user.
     * @param user is the user that the returned users, have requested to be in-users to.
     * @return An array of users that have requested to be in-users.
     */
    public static User[] getOthersRequestedInUsers(User user) {
        return user.getOthersRequestedInUsers();
    }

    /**
     * Makes a request for an user to be an in-user.
     * @param user The user that sends the request.
     * @param otherUser The user requested to be a in-user.
     */
    public static void requestInUser(User user, User otherUser) {

        //Check if "otherUser" is in the database. "User" is not checked since it is already verified
        otherUser.checkExistence();

        //Call method to check is the edge request can be made
        checkIfEdgeRequestPossible(getMyRequestedInUsers(user), getOthersRequestedInUsers(user),
                                   getValidInUsers(user), user, otherUser);

        //Create the edge request
        try {
            baseRepository.edgeRequestQueries().createRequest(user.getUsername(), otherUser.getUsername(), EdgeRequestQueries.CREATOR.TO);
        } catch (IOException e) {
            //If the call to BaseRepository returns an exception, throw exception
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Validates a request from another user to be an in-user.
     * @param user The user that validates the request.
     * @param otherUser The user that requests to be an in-user.
     */
    public static void validateInUser(User user, User otherUser) {
        checkIfRequestEdgeExists(user.getOthersRequestedInUsers(), user, otherUser);

        //If a matching request exists, validate it
        try {
            //Make a valid edge
            baseRepository.edgeQueries().addEdge(user.getUsername(), otherUser.getUsername());
            //Delete edge request
            deleteRequestedInUser(user, otherUser);
        } catch (IOException e) {
            //If the call to BaseRepository returns an exception, throw exception
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Deletes a requested in-edge between the two users.
     * @param user The user whose in-edge request it is.
     * @param otherUser The user that is requested to be an in-user.
     */
    private static void deleteRequestedInUser(User user, User otherUser) {

        try {
            baseRepository.edgeRequestQueries().deleteRequest(user.getUsername(), otherUser.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Deletes an valid or requested in-edge between the two users.
     * @param user The user in the "to" end of the in-edge.
     * @param otherUser The user in the "from" end of the in-edge.
     */
    public static void deleteInUser(User user, User otherUser) {

        try {
            baseRepository.edgeRequestQueries().deleteRequest(user.getUsername(), otherUser.getUsername());
            baseRepository.edgeQueries().deleteEdge(user.getUsername(), otherUser.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }


    /** Out-edge services **/

    /**
     * Gets an array of users that are valid out-users.
     * @param user is the user whose valid out-users are returned.
     * @return An array of users.
     */
    public static User[] getValidOutUsers(User user) {
        return user.getOutUsers();
    }

    /**
     * Gets an array of the users that are requested as out-users.
     * @param user is the user whose requested out-users are returned.
     * @return An array of users.
     */
    public static User[] getMyRequestedOutUsers(User user) {
        return user.getMyRequestedOutUsers();
    }

    /**
     * Gets an array of users that has requested the be out-users, to the user.
     * @param user is the user that the returned users, have requested to be out-users to.
     * @return An array of users that have requested to be out-users.
     */
    public static User[] getOthersRequestedOutUsers(User user) {
        return user.getOthersRequestedOutUsers();
    }

    /**
     * Makes a request for an user to be an out-user.
     * @param user The user that sends the request.
     * @param otherUser The user requested to be an out-user.
     */
    public static void requestOutUser(User user, User otherUser) {

        //Check if "otherUser" is in the database. "User" is not checked since it is already verified
        otherUser.checkExistence();

        //Call method to check is the edge request can be made
        checkIfEdgeRequestPossible(getMyRequestedOutUsers(user), getOthersRequestedOutUsers(user),
                                   getValidOutUsers(user), user, otherUser);

        //Create the edge request
        try {
            baseRepository.edgeRequestQueries().createRequest(otherUser.getUsername(), user.getUsername(), EdgeRequestQueries.CREATOR.FROM);
        } catch (IOException e) {
            //If the call to BaseRepository returns an exception, throw exception
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Validates a request from another user to be an out-user.
     * @param user The user that validates the request.
     * @param otherUser The user that requests to be an out-user.
     */
    public static void validateOutUser(User user, User otherUser) {
        checkIfRequestEdgeExists(user.getOthersRequestedOutUsers(), user, otherUser);

        try {
            //Make a valid edge
            baseRepository.edgeQueries().addEdge(otherUser.getUsername(), user.getUsername());
            //Delete edge request
            deleteRequestedOutUser(user, otherUser);
        } catch (IOException e) {
            //If the call to BaseRepository returns an exception, throw exception
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Deletes a requested out-edge between the two users.
     * @param user The user whose out-edge request it is.
     * @param otherUser The user that is requested to be an out-user.
     */
    private static void deleteRequestedOutUser(User user, User otherUser) {

        try {
            baseRepository.edgeRequestQueries().deleteRequest(otherUser.getUsername(), user.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Deletes an out-edge between the two users.
     * @param user The user in the "from" end of the out-edge.
     * @param otherUser The user in the "to" end of the out-edge.
     */
    public static void deleteOutUser(User user, User otherUser) {

        try {
            baseRepository.edgeRequestQueries().deleteRequest(otherUser.getUsername(), user.getUsername());
            baseRepository.edgeQueries().deleteEdge(otherUser.getUsername(), user.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /** Double-edge services **/

    /**
     * Makes a request for an user to be an out-user and a in-user.
     * @param user The user that sends the request.
     * @param otherUser The user requested to be an out-user and an in-user.
     */
    public static void requestDoubleUser(User user, User otherUser){

        //Check if "otherUser" is in the database. "User" is not checked since it is already verified
        otherUser.checkExistence();

        //Call method to check is the double-edge requests can be made
        checkIfEdgeRequestPossible(getMyRequestedOutUsers(user), getOthersRequestedOutUsers(user),
                                   getValidOutUsers(user), user, otherUser);
        checkIfEdgeRequestPossible(getMyRequestedInUsers(user), getOthersRequestedInUsers(user),
                                   getValidInUsers(user), user, otherUser);

        //Create the double-edge request
        try {
            baseRepository.edgeRequestQueries().createRequest(user.getUsername(), otherUser.getUsername(), EdgeRequestQueries.CREATOR.TO);
            baseRepository.edgeRequestQueries().createRequest(otherUser.getUsername(), user.getUsername(), EdgeRequestQueries.CREATOR.FROM);
        } catch (IOException e) {
            //If the calls to BaseRepository returns an exception, throw exception
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Validates a request from another user to be an out-user and an in-user.
     * @param user The user that validates the request.
     * @param otherUser The user that requests to be an out-user and an in-user.
     */
    public static void validateDoubleUser(User user, User otherUser){

        //Check is two matching requests already exists
        checkIfRequestEdgeExists(user.getOthersRequestedInUsers(), user, otherUser);
        checkIfRequestEdgeExists(user.getOthersRequestedOutUsers(), user, otherUser);

        try {
            //Make a valid edges
            baseRepository.edgeQueries().addEdge(user.getUsername(), otherUser.getUsername());
            baseRepository.edgeQueries().addEdge(otherUser.getUsername(), user.getUsername());
            //Delete edge requests
            deleteRequestedOutUser(user, otherUser);
            deleteRequestedInUser(otherUser, user);
        } catch (IOException e) {
            //If the call to BaseRepository returns an exception, throw exception
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Deletes an out-edge and an in-edge between the two users.
     * @param user One of the users.
     * @param otherUser Another one of the users.
     */
    public static void deleteDoubleUser(User user, User otherUser){
        try {
            baseRepository.edgeRequestQueries().deleteRequest(user.getUsername(), otherUser.getUsername());
            baseRepository.edgeRequestQueries().deleteRequest(otherUser.getUsername(), user.getUsername());
            baseRepository.edgeQueries().deleteEdge(user.getUsername(), otherUser.getUsername());
            baseRepository.edgeQueries().deleteEdge(otherUser.getUsername(), user.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /** Tools **/

    /**
     * Checks if an edge request is possible.
     * @param requestedUsers The list of edge requests by "user".
     * @param otherUserRequests The list of requests to "user".
     * @param edges The list of edges connected to "user".
     * @param user The user that wants to make the request.
     * @param otherUser The user the "user" wants to made an edge request to.
     */
    private static void checkIfEdgeRequestPossible(User[] requestedUsers, User[] otherUserRequests, User[] edges, User user, User otherUser){

        //Check if "user" is the same user as "otherUser".
        if(user.getUsername().equals(otherUser.getUsername())){
            throw new ApiExceptionHandler().userEdgeToItself(user);
        }

        //Check if the request already exists
        for(User u : requestedUsers) {
            if(u.getUsername().equals(otherUser.getUsername())) {
                //If an identical request exists, throw exception
                throw new ApiExceptionHandler().edgeRequestAlreadyExist(user, otherUser);
            }
        }

        //Check if a identical request, from another user, already exists.
        for(User u : otherUserRequests) {
            if(u.getUsername().equals(otherUser.getUsername())) {
                //If an identical request exists, throw exception
                throw new ApiExceptionHandler().otherEdgeRequestAlreadyExist(user, otherUser);
            }
        }

        //Check if a valid version of the edge already exists
        for(User u : edges) {
            if(u.getUsername().equals(otherUser.getUsername())) {
                //If an valid request already exist, throw exception
                throw new ApiExceptionHandler().edgeAlreadyExist(user, otherUser);
            }
        }
    }

    /***
     * Checks if there exists a request matching the name of "otherUser".
     * @param othersRequestedUserList a list of requests that "otherUser" has made to "User".
     * @param user The user that there must be a matching request to.
     * @param otherUser The user that must have made a matching request.
     */
    private static void checkIfRequestEdgeExists(User[] othersRequestedUserList, User user, User otherUser){
        //Mark if a matching request if found
        boolean match = false;

        //Checks if a matching request exists
        for(User u : othersRequestedUserList) {
            if(u.getUsername().equals(otherUser.getUsername())) match = true;
        }
        //If not, throw exception
        if(!match) throw new ApiExceptionHandler().requestNotFound(user, otherUser);
    }
}