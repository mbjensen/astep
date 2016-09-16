package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.appserver.business.service.usermanagement.FullUserFactory;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.db.persistent.api.EdgeRequestQueries;
import dk.aau.astep.db.persistent.exception.UserNotFoundException;
import dk.aau.astep.exception.ApiExceptionHandler;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An aSTEP user
 */
@XmlRootElement
public class User {
    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    private String username;

    /**
     * Constructor necessary in order for API to respond this
     */
    private User(){}

    /**
     * Constructor to create a user
     * @param username username of the user
     */
    public User(String username) {
        this.username = username;
    }

    /**
     * Create a user from a full user.
     * Only the username is used.
     * @param fullUser The full user. Only its username is used
     */
    public User(FullUser fullUser) {
        this(fullUser.getUsername());
    }

    /**
     * Get username
     * @return The username
     */
    @XmlAttribute
    public String getUsername() {
        return username;
    }

    /**
     * Finds user in database with same username if it exists
     * @return The found user with all its attributes
     * @throws UserNotFoundException if the user does not exist in the database
     */
    public FullUser getFullUser() throws UserNotFoundException {
        try {
            return baseRepository.<FullUser>userQueries().getUser(username);
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /** Edges **/

    /**
     * Gets an array of the users in-users.
     * @return An array of in-users.
     */
    public User[] getInUsers() {
        List<FullUser> inUserList;

        try {
            inUserList = baseRepository.<FullUser>edgeQueries().getInUsers(this.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
        return fullUserListToUserArray(inUserList);
    }

    /**
     * Gets an array of the users out-users.
     * @return An array of out-users.
     */
    public User[] getOutUsers() {
        List<FullUser> outUserList;

        try {
            outUserList = baseRepository.<FullUser>edgeQueries().getOutUsers(this.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
        return fullUserListToUserArray(outUserList);
    }

    /**
     * Gets the array of users that this user has requested to be its in-users.
     * @return An array of the requested in-users.
     */
    public User[] getMyRequestedInUsers() {
        List<FullUser> inUserRequestList;

        try {
            inUserRequestList = baseRepository.<FullUser>edgeRequestQueries().getToEdgeRequests(this.getUsername(), EdgeRequestQueries.CREATOR.TO);
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
        return fullUserListToUserArray(inUserRequestList);
    }

    /**
     * Gets the array of users that this user has requested to be its out-users.
     * @return An array of the requested out-users.
     */
    public User[] getMyRequestedOutUsers() {
        List<FullUser> outUserRequestList;

        try {
            outUserRequestList = baseRepository.<FullUser>edgeRequestQueries().getFromEdgeRequests(this.getUsername(), EdgeRequestQueries.CREATOR.FROM);
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
        return fullUserListToUserArray(outUserRequestList);
    }

    /**
     * Gets the array of users that have requested to be this user's in-users.
     * @return An array of the users that have requested to be in-users.
     */
    public User[] getOthersRequestedInUsers() {
        List<FullUser> othersInUserRequestList;

        try {
            othersInUserRequestList = baseRepository.<FullUser>edgeRequestQueries().getToEdgeRequests(this.getUsername(), EdgeRequestQueries.CREATOR.FROM);
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
        return fullUserListToUserArray(othersInUserRequestList);
    }

    /**
     * Gets the array of users that have requested to be this user's out-users.
     * @return An array of the users that have requested to be out-users.
     */
    public User[] getOthersRequestedOutUsers() {
        List<FullUser> othersOutUserRequestList;

        try {
            othersOutUserRequestList = baseRepository.<FullUser>edgeRequestQueries().getFromEdgeRequests(this.getUsername(), EdgeRequestQueries.CREATOR.TO);
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
        return fullUserListToUserArray(othersOutUserRequestList);
    }


    /** Groups **/

    /**
     * Get the groups that a user is member of.
     * @return The groups that a user is member of
     */
    public Group[] getGroups() {
        // Get group ids
        List<Integer> groupIds;
        try {
            groupIds = baseRepository.memberQueries().getMemberships(this.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        return Group.getGroupsFromIds(groupIds);
    }


    /** Tools **/

    /**
     * Takes a list of "FullUser" elements, and returns an array of equivalent "User" elements.
     * @param fullUserList The list of "FullUser" elements.
     * @return The equivalent array of "User" elements.
     */
    private User[] fullUserListToUserArray(List<FullUser> fullUserList) {
        int listSize = fullUserList.size();
        User[] userArray = new User[listSize];

        for (int i = 0; i < listSize; i++) {
            userArray[i] = new User(fullUserList.get(i).getUsername());
        }
        return userArray;
    }

    /**
     * Check if this user exists in the database.
     * If this does not exist, then an api exception is thrown
     */
    public void checkExistence() {
        try {
            baseRepository.userQueries().getUser(username);
        } catch (UserNotFoundException e) {
            //If not, throw exception
            throw new ApiExceptionHandler().userNotFound(this);
        } catch (Exception e) {
            //If the call to BaseRepository returns an exception, throw exception
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Transform a list of full users to an array of users.
     * Only the username is stored.
     * @param fullUsers The list of the full users
     * @return The transformed array of users
     */
    public static User[] getUsersFromFullUsers(List<FullUser> fullUsers) {
        User[] users = new User[fullUsers.size()];

        for (int i = 0; i < fullUsers.size(); i++) {
            users[i] = new User(fullUsers.get(i));
        }

        return users;
    }

    /**
     * Get all users that this are allowed to get location information from.
     * The user itself is always included.
     * @return the users that this are allowed to get location information from.
     */
    public List<User> fetchAccessibleUsers(boolean addItself) {
        List<User> users = new ArrayList<>();

        if (addItself) {
            // Add itself
            users.add(this);
        }

        // Add in-users. Assume that you are not your own in-user, since that is not allowed
        users.addAll(Arrays.asList(getInUsers()));

        // Add group members
        Group[] groups = getGroups();
        for (Group group : groups) {
            User[] members = group.getMembers();
            for (User member : members) {
                if (!member.isContainedIn(users)) {
                    users.add(member);
                }
            }
        }

        //return users.toArray(new User[users.size()]);
        return users;
    }

    /**
     * Get if this is contained in a list
     * @param users The list
     * @return true if this is contained in the list, false otherwise
     */
    private boolean isContainedIn(List<User> users) {
        for (User user : users) {
            if (getUsername().equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }
}
