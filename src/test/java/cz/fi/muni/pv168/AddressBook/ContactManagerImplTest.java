package cz.fi.muni.pv168.AddressBook;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ContactManagerImplTest {

    private ContactManagerImpl contactManager;

    @Before
    public void setUp() throws Exception {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby://localhost:1527/Databases/AddressBookDB;user=app;password=passwd");
        ds.setUsername("app");
        ds.setPassword("passwd");
        contactManager = new ContactManagerImpl(ds);
    }

    @Test
    public void testCreateContact() throws Exception {
        Contact contact = new Contact();
        contactManager.createContact(contact);
        assertThat(contact.getContactID(), is(notNullValue()));
        assertThat(contactManager.findAllContacts(), hasItem(contact));
    }

    @Test(expected = NullPointerException.class)
    public void testCreateContactWithNull() throws Exception {
        contactManager.createContact(null);
    }

    @Test(expected = Exception.class)
    public void testSetContactId() throws Exception {
        Contact contact = new Contact();
        contactManager.createContact(contact);
        // Shouldn't allow ID change.
        contact.setContactID(1L);
    }

    @Test
    public void testContactIdDifference() throws Exception {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact();
        contactManager.createContact(contact1);
        contactManager.createContact(contact2);
        assertThat(contact1.getContactID(), not(equalTo(contact2.getContactID())));
    }

    @Test
    public void testUpdateContact() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setAddress("Smetanova 44, 60200 Brno");
        contactManager.createContact(contact);
        Long idBefore = contact.getContactID();
        Collection<Contact> resultNameOk1 = contactManager.findContactByName("John Doe");
        Collection<Contact> resultNameWrong1 = contactManager.findContactByName("James Smith");
        Collection<Contact> resultAddressOk1 = contactManager.findContactByAddress("Smetanova 44, 60200 Brno");
        Collection<Contact> resultAddressWrong1 = contactManager.findContactByAddress("Anderleho 12, 19800 Praha");
        assertThat(resultNameOk1, hasItem(contact));
        assertThat(resultNameWrong1, not(hasItem(contact)));
        assertThat(resultAddressOk1, hasItem(contact));
        assertThat(resultAddressWrong1, not(hasItem(contact)));
        contact.setName("James Smith");
        contact.setAddress("Anderleho 12, 19800 Praha");
        contactManager.updateContact(contact);
        Long idAfter = contact.getContactID();
        Collection<Contact> resultNameWrong2 = contactManager.findContactByName("John Doe");
        Collection<Contact> resultNameOk2 = contactManager.findContactByName("James Smith");
        Collection<Contact> resultAddressWrong2 = contactManager.findContactByAddress("Smetanova 44, 60200 Brno");
        Collection<Contact> resultAddressOk2 = contactManager.findContactByAddress("Anderleho 12, 19800 Praha");
        assertThat(resultNameOk2, hasItem(contact));
        assertThat(resultNameWrong2, not(hasItem(contact)));
        assertThat(resultAddressOk2, hasItem(contact));
        assertThat(resultAddressWrong2, not(hasItem(contact)));
        assertThat(idBefore, equalTo(idAfter));
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateContactWithNull() throws Exception {
        contactManager.updateContact(null);
    }

    @Test
    public void testDeleteContact() throws Exception {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact();
        contactManager.createContact(contact1);
        contactManager.createContact(contact2);
        contactManager.deleteContact(contact1);
        assertThat(contactManager.findAllContacts(), not(hasItem(contact1)));
        assertThat(contactManager.findAllContacts(), hasItem(contact2));
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteContactWithNull() throws Exception {
        contactManager.deleteContact(null);
    }

    @Test
    public void testFindContactById() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contactManager.createContact(contact);
        Contact contactById = contactManager.findContactById(contact.getContactID());
        assertThat(contact, equalTo(contactById));
    }

    @Test(expected = NullPointerException.class)
    public void testFindContactByIdWithNull() throws Exception {
        contactManager.findContactById(null);
    }

    @Test
    public void testFindAllContacts() throws Exception {
        Contact contact1 = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        Contact contact2 = new Contact("Jane Smith", "+420777387456", "jane44@gmail.com");
        Contact contact3 = new Contact("James Bond", "512478007", "jb007@sis.co.uk");
        contactManager.createContact(contact1);
        contactManager.createContact(contact2);
        contactManager.createContact(contact3);
        Collection<Contact> contactList = new ArrayList<>();
        contactList.add(contact1);
        contactList.add(contact2);
        contactList.add(contact3);
        Collection<Contact> result = contactManager.findAllContacts();
        assertThat(contactList, equalTo(result));
    }

    @Test
    public void testFindContactByName() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByName("John Doe");
        Collection<Contact> result2 = contactManager.findContactByName("nobody");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    @Test(expected = NullPointerException.class)
    public void testFindContactByNameWithNull() throws Exception {
        contactManager.findContactByName(null);
    }

    @Test
    public void testFindContactByPhone() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByPhone("+420606542781");
        Collection<Contact> result2 = contactManager.findContactByPhone("000");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    @Test(expected = NullPointerException.class)
    public void testFindContactByPhoneWithNull() throws Exception {
        contactManager.findContactByPhone(null);
    }

    @Test
    public void testAddPhone() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByPhone("+420777387456");
        contact.setNewPhone("+420777387456");
        contactManager.updateContact(contact);
        Collection<Contact> result2 = contactManager.findContactByPhone("+420777387456");
        assertThat(result1, not(hasItem(contact)));
        assertThat(result2, hasItem(contact));
    }

    @Test
    public void testDeletePhone() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setNewPhone("+420777387456");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByPhone("+420777387456");
        contact.deletePhone("+420777387456");
        contactManager.updateContact(contact);
        Collection<Contact> result2 = contactManager.findContactByPhone("+420777387456");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    @Test
    public void testFindContactByFax() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setNewFax("+44-208-1234567");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByFax("+44-208-1234567");
        Collection<Contact> result2 = contactManager.findContactByFax("000");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    @Test(expected = NullPointerException.class)
    public void testFindContactByFaxWithNull() throws Exception {
        contactManager.findContactByFax(null);
    }

    @Test
    public void testAddFax() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setNewFax("+44-208-1234567");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByFax("+35-152-325478");
        contact.setNewFax("+35-152-325478");
        contactManager.updateContact(contact);
        Collection<Contact> result2 = contactManager.findContactByFax("+35-152-325478");
        assertThat(result1, not(hasItem(contact)));
        assertThat(result2, hasItem(contact));
    }

    @Test
    public void testDeleteFax() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setNewFax("+44-208-1234567");
        contact.setNewFax("+35-152-325478");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByFax("+35-152-325478");
        contact.deleteFax("+35-152-325478");
        contactManager.updateContact(contact);
        Collection<Contact> result2 = contactManager.findContactByFax("+35-152-325478");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    @Test
    public void testFindContactByEmail() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByEmail("john.doe@hotmail.com");
        Collection<Contact> result2 = contactManager.findContactByEmail("aaa@bbb.cc");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidEmail() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "aaa");
        contactManager.createContact(contact);
    }

    @Test(expected = NullPointerException.class)
    public void testFindContactByEmailWithNull() throws Exception {
        contactManager.findContactByEmail(null);
    }

    @Test
    public void testAddEmail() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByEmail("jd47@gmail.com");
        contact.setNewEmail("jd47@gmail.com");
        contactManager.updateContact(contact);
        Collection<Contact> result2 = contactManager.findContactByEmail("jd47@gmail.com");
        assertThat(result1, not(hasItem(contact)));
        assertThat(result2, hasItem(contact));
    }

    @Test
    public void testDeleteEmail() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setNewEmail("jd47@gmail.com");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByEmail("jd47@gmail.com");
        contact.deleteEmail("jd47@gmail.com");
        contactManager.updateContact(contact);
        Collection<Contact> result2 = contactManager.findContactByEmail("jd47@gmail.com");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    @Test
    public void testFindContactByAddress() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setAddress("Smetanova 44, 60200 Brno");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByAddress("Smetanova 44, 60200 Brno");
        Collection<Contact> result2 = contactManager.findContactByAddress("nowhere");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    @Test(expected = NullPointerException.class)
    public void testFindContactByAddressWithNull() throws Exception {
        contactManager.findContactByAddress(null);
    }

    @Test
    public void testFindContactByOtherContactType() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setNewOtherContact("Skype", "JohnnyD");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByOtherContactType("Skype", "JohnnyD");
        Collection<Contact> result2 = contactManager.findContactByOtherContactType("aaa", "bbb");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    @Test(expected = NullPointerException.class)
    public void testFindContactByOtherContactTypeWithNull() throws Exception {
        contactManager.findContactByOtherContactType("aaa", null);
        contactManager.findContactByOtherContactType(null, "bbb");
        contactManager.findContactByOtherContactType(null, null);
    }

    @Test
    public void testAddOtherContactType() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setNewOtherContact("Skype", "JohnnyD");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByOtherContactType("ICQ", "342576841");
        contact.setNewOtherContact("ICQ", "342576841");
        contactManager.updateContact(contact);
        Collection<Contact> result2 = contactManager.findContactByOtherContactType("ICQ", "342576841");
        assertThat(result1, not(hasItem(contact)));
        assertThat(result2, hasItem(contact));
    }

    @Test
    public void testDeleteOtherContactType() throws Exception {
        Contact contact = new Contact("John Doe", "+420606542781", "john.doe@hotmail.com");
        contact.setNewOtherContact("Skype", "JohnnyD");
        contact.setNewOtherContact("ICQ", "342576841");
        contactManager.createContact(contact);
        Collection<Contact> result1 = contactManager.findContactByOtherContactType("ICQ", "342576841");
        contact.deleteOtherContact("ICQ", "342576841");
        contactManager.updateContact(contact);
        Collection<Contact> result2 = contactManager.findContactByOtherContactType("ICQ", "342576841");
        assertThat(result1, hasItem(contact));
        assertThat(result2, not(hasItem(contact)));
    }

    //TODO add groupIds tests
}