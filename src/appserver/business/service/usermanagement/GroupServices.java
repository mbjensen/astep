package dk.aau.astep.appserver.business.service.usermanagement;

import dk.aau.astep.appserver.model.shared.FullUser;
import dk.aau.astep.appserver.model.shared.Group;
import dk.aau.astep.appserver.model.shared.User;
import dk.aau.astep.db.persistent.api.BaseRepository;
import dk.aau.astep.exception.ApiExceptionHandler;

import java.io.IOException;
import java.util.List;

/**
 * A collection of user management group services
 */
public class GroupServices {
    private static BaseRepository baseRepository = new BaseRepository.Builder(new FullUserFactory()).build();

    /**
     * Get the groups that a user is member of.
     * The user should already be authenticated.
     * @param user The user
     * @return The groups that a user is member of
     */
    public static Group[] getGroups(User user) {
        return user.getGroups();
    }

    /**
     * Get the groups that a user is invited to.
     * The user should already be authenticated.
     * @param user The user
     * @return The groups that a user is invited to
     */
    public static Group[] getInvitationGroups(User user) {
        List<Integer> groupIds;
        try {
            groupIds = baseRepository.inviteQueries().getInvitation(user.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        return Group.getGroupsFromIds(groupIds);
    }

    /**
     * Get the users that are invited to a specified group.
     * @param group The specified group
     * @param user A beforehand authenticated user. This user most be in the specified group of the admin group
     * @param adminGroup If the authenticating user is in the admin group, this should be that admin group. Otherwise this should be null
     * @return The users that are invited to the specified group
     */
    public static User[] getInvitedMembers(Group group, User user, Group adminGroup) {
        if (adminGroup == null) {
            // Check that the user is in the specified group
            group.checkMembershipAuth(user);
        } else {
            // Check that the admin group administrates the specified group and that the user is in the admin group
            adminGroup.checkMembershipAuth(user);
            adminGroup.checkAdministrationAuth(group);
        }

        // Get invites
        List<FullUser> userList;
        try {
            userList = baseRepository.<FullUser>inviteQueries().getInvitedMembers(group.getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        return User.getUsersFromFullUsers(userList);
    }

    /**
     * Decline group invitation.
     * It is assumed that the user is already authenticated.
     * @param user The user that declines the invitation
     * @param group The groups with the invitation to delete
     */
    public static void declineInvitation(User user, Group group) {
        group.checkExistence();
        group.checkInvitedToGroup(user);

        try {
            baseRepository.inviteQueries().deleteInvite(group.getId(), user.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Delete an invitation if adminUser is admin
     * @param specifiedUser The user that is invited
     * @param specifiedGroup The group that invited the user
     * @param adminUser A user that claims to be member of the admin group. Must be authenticated beforehand
     * @param adminGroup A group that is claimed to be administrating the specified group
     */
    public static void deleteInvitationAsAdmin(User specifiedUser, Group specifiedGroup, User adminUser, Group adminGroup) {
        // Check existence of users and groups
        specifiedGroup.checkExistence();
        specifiedUser.checkExistence();
        adminGroup.checkExistence();

        // Check administration
        adminGroup.checkAdministrationAuth(specifiedGroup);

        // Check membership of admin user
        adminGroup.checkMembershipAuth(adminUser);

        // Check that the invitation exists
        specifiedGroup.checkInvitedToGroup(specifiedUser);

        // Delete invitation
        try {
            baseRepository.inviteQueries().deleteInvite(specifiedGroup.getId(), specifiedUser.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Invite a specified user to a specified group if the authenticated user is admin.
     * It is assumed that the admin user is authenticated.
     * @param specifiedUser The user to invite
     * @param specifiedGroup The group to invite the specified user to
     * @param adminGroup The group that should administrate the specified group
     * @param adminUser The user that should be member of the admin group
     */
    public static void inviteIfAdmin(User specifiedUser, Group specifiedGroup, User adminUser, Group adminGroup) {
        // Check existence of users and groups
        specifiedGroup.checkExistence();
        specifiedUser.checkExistence();
        adminGroup.checkExistence();

        // Check administration
        adminGroup.checkAdministrationAuth(specifiedGroup);

        // Check membership of admin user
        adminGroup.checkMembershipAuth(adminUser);

        // Check that specified user is no already invited
        specifiedGroup.checkNoInvitation(specifiedUser);

        // Invite
        try {
            baseRepository.inviteQueries().createInvite(specifiedGroup.getId(), specifiedUser.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }


    /** Administration **/

    /**
     * Get the groups that a specified group administrates
     * @param specifiedGroup The specified group
     * @param user The authenticating user. It must have been authenticated beforehand.
     * @param adminGroup A group that administrates the specified group. This may be null
     * @return The group that the specified group administrates
     */
    public static Group[] getAdmins(Group specifiedGroup, User user, Group adminGroup) {
        specifiedGroup.checkExistence();

        if (adminGroup == null) {
            // Check that the user is in the specified group
            specifiedGroup.checkMembershipAuth(user);
        } else {
            // Check that the admin group exists,
            // that the user is in that group,
            // and that the admin group administrates the specified group
            adminGroup.checkExistence();
            adminGroup.checkMembershipAuth(user);
            adminGroup.checkAdministrationAuth(specifiedGroup);
        }

        List<Integer> ids;
        try {
            ids = baseRepository.groupQueries().getAdminsOf(specifiedGroup.getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        return Group.getGroupsFromIds(ids);
    }

    /**
     * Make a specified group administrate another group.
     * @param specifiedGroup The specified group
     * @param otherGroup The group that the specified group should administrate
     * @param user A beforehand authenticated user in the admin group
     * @param adminSpecifiedGroup A group that administrates the specified group
     * @param adminOtherGroup A group that administrates the other group
     */
    public static void makeAdmin(Group specifiedGroup, Group otherGroup, User user, Group adminSpecifiedGroup, Group adminOtherGroup) {
        // Check existence of groups
        specifiedGroup.checkExistence();
        otherGroup.checkExistence();
        adminSpecifiedGroup.checkExistence();
        adminOtherGroup.checkExistence();

        // Check that the user is in the admin groups
        adminSpecifiedGroup.checkMembershipAuth(user);
        adminOtherGroup.checkMembershipAuth(user);

        // Check that the admin groups administrates the two main groups
        adminSpecifiedGroup.checkAdministrationAuth(specifiedGroup);
        adminOtherGroup.checkAdministrationAuth(otherGroup);

        // Check that the specified group does not administrate the other group already
        if (specifiedGroup.doesAdministrate(otherGroup)) {
            throw new ApiExceptionHandler().alreadyAdmin(specifiedGroup, otherGroup);
        }

        // Make admin
        try {
            baseRepository.groupQueries().createAdmin(specifiedGroup.getId(), otherGroup.getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Make a specified group no longer an administrator of another group.
     * @param specifiedGroup The specified group
     * @param otherGroup The group that the specified group should no longer administrate
     * @param user A beforehand authenticated user in the admin group
     * @param adminGroup A group that administrates the specified group and the other group
     */
    public static void removeAdministration(Group specifiedGroup, Group otherGroup, User user, Group adminGroup) {
        // Check existence of groups
        specifiedGroup.checkExistence();
        otherGroup.checkExistence();
        adminGroup.checkExistence();

        // Check that the user is in the admin group
        adminGroup.checkMembershipAuth(user);

        // Check that the admin group administrates the specified group
        adminGroup.checkAdministrationAuth(specifiedGroup);

        // Get all admins of the other group
        List<Integer> othersAdminsIds;
        try {
            othersAdminsIds = baseRepository.groupQueries().getAdmins(otherGroup.getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        // Check that the other group is administrated by the specified group
        // Then check that the other group has other admins than the specified group
        // This is to be sure that all groups are administrated
        if (!othersAdminsIds.contains(specifiedGroup.getId())) {
            throw new ApiExceptionHandler().noAdministration(specifiedGroup, otherGroup);
        } else if (othersAdminsIds.size() == 1) {
            throw new ApiExceptionHandler().noOtherAdministrator(specifiedGroup, otherGroup);
        }

        // Remove admin
        try {
            baseRepository.groupQueries().deleteAdmin(specifiedGroup.getId(), otherGroup.getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }

    /**
     * Create a group
     * Add the user to the group.
     * Set up the group to administrate itself.
     * @return The group created
     */
    public static Group createAndSetupGroup(User user) {
        // Create group in db
        int id;
        try {
            id = baseRepository.groupQueries().createGroup();
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }

        Group group = new Group(id);

        group.addMember(user);
        group.addAdministration(group);

        return group;
    }
    
    /**
     * Check if group exists and there is an invitation to the group
     * Join the group
     * Delete the invitation
     * @param user The user
     * @param group The Group
     */
    public static void joinGroup(User user, Group group) {
        group.checkExistence();
        group.checkInvitedToGroup(user);

        try {
            baseRepository.memberQueries().createMembership(user.getUsername(), group.getId());
            baseRepository.inviteQueries().deleteInvite(group.getId(), user.getUsername());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }
    
    /**
     * Check if group exists
     * Check membership of the user in either the group or an admin group
     * Gets the members of a group
     * @param group The group
     * @return members The members
     */
    public static User[] getMembers(User user, Group group, Group adminGroup) {
        group.checkExistence();
        
        if (adminGroup == null) {
            group.checkMembershipAuth(user);
        } else {
            adminGroup.checkExistence();
            adminGroup.checkMembershipAuth(user);
        }
                
        return group.getMembers();
    }
    
    /**
     * Checks existence of groups and users
     * Checks if specified user is a member of the specified group
     * Checks if the admin group administrates the specified group
     * Checks if the admin user is a member of the admin group
     * Removes the specified user from the specified group
     * @param specifiedGroup The specified group
     * @param adminGroup The admin group
     * @param specifiedUser The user to be removed
     * @param adminUser The admin user to remove the specified user
     */
    public static void removeUserFromGroup(Group specifiedGroup, Group adminGroup, User specifiedUser, User adminUser) {
        specifiedGroup.checkExistence();
        adminGroup.checkExistence();
        specifiedUser.checkExistence();
        specifiedGroup.checkMembershipAuth(specifiedUser);
        adminGroup.checkAdministrationAuth(specifiedGroup);        
        adminGroup.checkMembershipAuth(adminUser);
        
        try {
            baseRepository.memberQueries().deleteMembership(specifiedUser.getFullUser().getUsername(), specifiedGroup.getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }
    
    /**
     * Checks if the user and group exist
     * Checks if the user is a member of the group
     * Deletes the membership of the group for the user
     * @param user The user
     * @param group The group
     */
    public static void leaveGroup(User user, Group group) {
        user.checkExistence();
        group.checkExistence();
        group.checkMembershipAuth(user);
        
        try {
            baseRepository.memberQueries().deleteMembership(user.getUsername(), group.getId());
        } catch (IOException e) {
            throw new ApiExceptionHandler().internalServerError();
        }
    }
}
