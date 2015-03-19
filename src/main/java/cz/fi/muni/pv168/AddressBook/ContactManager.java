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
    public Collection<Contact> findContactByName(String name);
    public Collection<Contact> findContactByPhone(String phone);
    public Collection<Contact> findContactByFax(String fax);
    public Collection<Contact> findContactByEmail(String email);
    public Collection<Contact> findContactByAddress(String address);
    public Collection<Contact> findContactByOtherContactType(String contactType, String contact);
}
