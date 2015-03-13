package cz.fi.muni.pv168.AddressBook;

import java.util.Collection;

/**
 * Created by Виктория on 12-Mar-15.
 */
public interface ContactManager {

    public void createContact(Contact contact);
    public void updateContact(Contact contact);
    public void deleteContact(Contact contact);
    public Contact findContactById(Long id);
    public Collection<Contact> findAllContacts();
    public Contact findContactByName(String name);
    public Contact findContactByPhone(String phone);
    public Contact findContactByFax(String fax);
    public Contact findContactByEmail(String email);
    public Contact findContactByAddress(String address);
    public Contact findContactByOtherContactType(String contactType, String contact);
}
