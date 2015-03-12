package cz.fi.muni.pv168.AddressBook;

import java.util.List;

/**
 * Created by Виктория on 10-Mar-15.
 */
public interface GroupManager {

    /**
     * Stores new group into database. Id for the new group is automatically
     * generated and stored into id attribute.
     *
     * @param group group to be created.
     * @throws IllegalArgumentException when group is null, group has already
     * assigned id, or when group name or contacts list is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    void createGroup(Group group) throws ServiceFailureException;

    /**
     * Updates group in database.
     *
     * @param group updated group to be stored into database.
     * @throws IllegalArgumentException when group is null, or group has null id, or group name is null,
     * or group member list is null, or group name is an empty string.
     * @throws  ServiceFailureException when db operation fails.
     */
    void updateGroup(Group group) throws ServiceFailureException;

    /**
     * Deletes group from database.
     *
     * @param group group to be deleted from db.
     * @throws IllegalArgumentException when group is null, or group has null id, or group has been changed outside updateGroup routine.
     * @throws  ServiceFailureException when db operation fails.
     */
    void deleteGroup(Group group) throws ServiceFailureException;

    /**
     * Returns group with given id.
     *
     * @param id primary key of requested group.
     * @return group with given id or null if such group does not exist.
     * @throws IllegalArgumentException when given id is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    Group findGroupByID(Long id) throws ServiceFailureException;

    /**
     * Returns group with given name.
     *
     * @param name name of requested group.
     * @return group with given name or null if such group does not exist.
     * @throws IllegalArgumentException when given name is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    Group findGroupByName(String name) throws ServiceFailureException;

    /**
     * Returns list of groups to which a given contact belongs.
     *
     * @param contact contact to be found in groups.
     * @return list of groups including given contact or null if the contact does not belong to any group.
     * @throws IllegalArgumentException when given contact is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    List<Group> findGroupByMember(Contact contact) throws ServiceFailureException;


}
