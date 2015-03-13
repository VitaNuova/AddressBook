package cz.fi.muni.pv168.AddressBook;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContactManagerImplTest {

    private ContactManagerImpl contactManager;

    @Before
    public void setUp() throws Exception {
        contactManager = new ContactManagerImpl();
    }

    @Test
    public void testCreateContact() throws Exception {
        Contact contact = new Contact();
        int countBefore = contactManager.findAllContacts().size();
        contactManager.createContact(contact);
        assertThat(contactManager.findAllContacts().size(), is(equalTo(countBefore + 1)));
    }

    @Test(expected = NullPointerException.class)
    public void testCreateContactWithNull() throws Exception {
        contactManager.createContact(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTwoContactsWithSameId() throws Exception {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact();
        contact1.setContactID((long) 1);
        contact2.setContactID((long) 1);
        contactManager.createContact(contact1);
        contactManager.createContact(contact2);
    }

    @Test
    public void testContactCanBeRetrieved() throws Exception {
        Contact contact = new Contact();
        contactManager.createContact(contact);
        assertThat(contactManager.findAllContacts(), hasItem(contact));
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateContactWithNull() throws Exception {
        contactManager.updateContact(null);
    }

    @Test
    public void testDeleteContactWithNull() throws Exception {
        int countBefore = contactManager.findAllContacts().size();
        contactManager.deleteContact(null);
        assertThat(contactManager.findAllContacts().size(), is(equalTo(countBefore)));
    }

    @Test
    public void testDeleteContact() throws Exception {
        Contact contact = new Contact();
        contactManager.createContact(contact);
        int countBefore = contactManager.findAllContacts().size();
        contactManager.deleteContact(contact);
        assertThat(contactManager.findAllContacts().size(), is(equalTo(countBefore - 1)));
    }

    @Test
    public void testFindContactById() throws Exception {

    }

    @Test
    public void testFindAllContacts() throws Exception {

    }

    @Test
    public void testFindContactByName() throws Exception {

    }

    @Test
    public void testFindContactByPhone() throws Exception {

    }

    @Test
    public void testFindContactByFax() throws Exception {

    }

    @Test
    public void testFindContactByEmail() throws Exception {

    }

    @Test
    public void testFindContactByAddress() throws Exception {

    }

    @Test
    public void testFindContactByOtherContactType() throws Exception {

    }
}