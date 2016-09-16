package dk.aau.astep.appserver.model.shared;

import dk.aau.astep.appserver.business.service.usermanagement.FullUserFactory;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.exception.ApiExceptionHandler;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.List;

/**
 * An aSTEP group
 */
@XmlRootElement
public class Group {
    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    private int id;

    /**
     * Empty constructor for Group used by MOXy
     */
    public Group(){}

    /**
     * Constructor to create a group object
     * @param id The id of the group
     */
    public Group(int id) {
        this.id = id;
    }

    /**
     * Constructor necessary in order for the API to create this
     * @param id The id of the group
     */
    public Group(String id) {
        try {
            this.id = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new ApiExceptionHandler().groupIdWrongFormat();
        }
    }

    /**
     * Get username
     * @return The username
     */
    @XmlAttribute
    public int getId() {
        return id;
    }

    public static Group[] getGroupsFromIds(List<Integer> ids) {
        Group[] groups = new Group[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            groups[i] = new Group(ids.get(i));
        }
        return groups;
    }

    /**
     * Check if a group exists in the database.
     */
    public void checkExistence() {
        try {
            if (!baseRepository.groupQueries().doesGroupExists(getId())) {
                throw new ApiExceptionHandler().groupNotFound(this);
            }
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Check if this administrates another group.
     * If not, an unauthorized api exception is thrown.
     * @param otherGroup The other group
     */
    public void checkAdministrationAuth(Group otherGroup) {
        if (!doesAdministrate(otherGroup)) {
            throw new ApiExceptionHandler().noGroupAdministration(this, otherGroup);
        }
    }

    /**
     * Gets if this administrates another group
     * @param otherGroup The other group
     * @return true if this administrates the other group, otherwise false
     */
    public boolean doesAdministrate(Group otherGroup) {
        List<Integer> groupIdsThatThisAdministrates;
        try {
            groupIdsThatThisAdministrates = baseRepository.groupQueries().getAdminsOf(getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        return groupIdsThatThisAdministrates.contains(otherGroup.getId());
    }

    /**
     * Check if a user is member of this.
     * If not, an unauthorized api exception is thrown.
     * @param user The user to check for
     */
    public void checkMembershipAuth(User user) {
        List<Integer> ids;
        try {
            ids = baseRepository.memberQueries().getMemberships(user.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        if (!ids.contains(getId())) {
            throw new ApiExceptionHandler().noAdminMembership(this, user);
        }
    }

    /**
     * Make this administrate a specific group
     * @param otherGroup The group that this should administrate. It can also be this.
     */
    public void addAdministration(Group otherGroup) {
        try {
            baseRepository.groupQueries().createAdmin(getId(), otherGroup.getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Adds a user to a this
     * @param user The user to add
     */
    public void addMember(User user) {
        try {
            baseRepository.memberQueries().createMembership(user.getUsername(), getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }
    
    /**
     * Checks if a user is invited to the group.
     * If not, a not found api exception is thrown.
     * @param user The user to check for
     */
    public void checkInvitedToGroup(User user) {
        if (!isInviting(user)) {
            throw new ApiExceptionHandler().invitationNotFound(user, this);
        }
    }

    /**
     * Check that a user is not invited to this group.
     * If the user is invited, then an api exception is thrown.
     * @param user The user to check for
     */
    public void checkNoInvitation(User user) {
        if (isInviting(user)) {
            throw new ApiExceptionHandler().inviteAlreadyExist(user, this);
        }
    }

    /**
     * Get if a user is invited to this group.
     * @param user The user to check for
     * @return true if the user is invited, otherwise false
     */
    private boolean isInviting(User user) {
        List<Integer> groupsIds;

        try {
            groupsIds = baseRepository.<FullUser>inviteQueries().getInvitation(user.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        return groupsIds.contains(getId());
    }
    
    public User[] getMembers() {
        List<FullUser> fullUserMembers;
        int i = 0;

        try {
            fullUserMembers = baseRepository.<FullUser>memberQueries().getMembers(getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
        
        User[] members = new User[fullUserMembers.size()];
        
        for (User user : fullUserMembers) {
            members[i] = user;
            i++;
        }
        return members;
    }
}
