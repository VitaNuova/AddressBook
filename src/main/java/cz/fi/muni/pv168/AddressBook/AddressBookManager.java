package cz.fi.muni.pv168.AddressBook;

import java.util.List;

/**
 * Created by Виктория on 10-Mar-15.
 */
public interface AddressBookManager {
    /**
     * Lists contacts of person.
     * @throws  ServiceFailureException when db operation fails.
     * @return list of person's contacts or null if the person doesn't have any contacts in the address book.
     */
    public List<Contact> listContactsByPerson() throws ServiceFailureException;

    /**
     * Lists groups that person has created in the address book.
     * @throws  ServiceFailureException when db operation fails.
     * @return list of groups in person's address book, or null if there are no groups in the address book.
     */
    public List<Group> listGroupsByPerson()  throws ServiceFailureException;

    /**
     * Lists contacts belonging to a particular group in the address book.
     * @param groupName name of the group for which the contacts will be listed.
     * @throws java.lang.IllegalArgumentException if groupName is null.
     * @throws  ServiceFailureException when db operation fails.
     * @return list of contacts belonging to the group, or null if such group does not exist.
     */
    public List<Contact> listContactsByGroup(String groupName)  throws ServiceFailureException;



 }
