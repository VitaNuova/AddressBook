package cz.fi.muni.pv168.AddressBook;

import javax.sql.DataSource;
import java.util.Collection;

/**
 * Created by Виктория on 12-Mar-15.
 */
public class ContactManagerImpl implements ContactManager {

    private final DataSource dataSource;

    public ContactManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createContact(Contact contact) {

    }

    public void updateContact(Contact contact) {

    }

    public void deleteContact(Contact contact) {

    }

    public Contact findContactById(Long id) {
        return null;
    }


    public Collection<Contact> findAllContacts() {
        return null;
    }

    public Collection<Contact> findContactByName(String name) {
        return null;
    }

    public Collection<Contact> findContactByPhone(String phone) {
        return null;
    }

    public Collection<Contact> findContactByFax(String fax) {
        return null;
    }

    public Collection<Contact> findContactByEmail(String email) {
        return null;
    }

    public Collection<Contact> findContactByAddress(String address) {
        return null;
    }

    public Collection<Contact> findContactByOtherContactType(String contactType, String contact) {
        return null;
    }
}
