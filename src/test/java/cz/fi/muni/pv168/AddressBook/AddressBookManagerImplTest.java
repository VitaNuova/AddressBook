package cz.fi.muni.pv168.AddressBook;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.tools.jar.Main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class AddressBookManagerImplTest {
    private AddressBookManagerImpl addressBookManager;
    private ContactManagerImpl contactManager;
    private GroupManagerImpl groupManager;

    @Before
    public void setUp() throws SQLException, IOException {
        Properties config = new Properties();
        config.load(Main.class.getResourceAsStream("/dbSetup.properties"));
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(config.getProperty("jdbc.url"));
        ds.setUsername(config.getProperty("jdbc.user"));
        ds.setPassword(config.getProperty("jdbc.password"));
        addressBookManager = new AddressBookManagerImpl(ds);
        contactManager = new ContactManagerImpl(ds);
        groupManager = new GroupManagerImpl(ds);
    }


    @Test
    //don't forget to override equals() in Contact class, otherwise ArrayList.contains() will not work properly!!!
    public void testListContactsByPerson() {
        List<Contact> retrievedContacts = addressBookManager.listContactsByPerson();
        assertNull(retrievedContacts);

        Contact contact1 = new Contact("Mary");
        Contact contact2 = new Contact("John");
        Contact contact3 = new Contact("Paul");
        contactManager.createContact(contact1);
        contactManager.createContact(contact2);

        retrievedContacts = addressBookManager.listContactsByPerson();
        assertNotNull(retrievedContacts);
        assertTrue(retrievedContacts.contains(contact1));
        assertTrue(retrievedContacts.contains(contact2));
        assertFalse(retrievedContacts.contains(contact3));

        try {
            retrievedContacts.get(2);
            fail();
        }
        catch(IndexOutOfBoundsException ex) {
            //OK
        }
    }

    @Test
    public void testListGroupsByPerson() {
        List<String> groupList = addressBookManager.listGroupsByPerson();
        assertNull(groupList);

        List<Long> memberList1 = new ArrayList<>();
        memberList1.add(1l);
        memberList1.add(2l);

        List<Long> memberList2 = new ArrayList<>();
        memberList2.add(3l);
        memberList2.add(4l);

        Group group1 = newGroup("Family", memberList1);
        Group group2 = newGroup("Friends", memberList2);

        groupManager.createGroup(group1);
        groupList = addressBookManager.listGroupsByPerson();
        assertNotNull(groupList);
        assertEquals("Family", groupList.get(0));
        assertTrue(groupList.contains("Family"));
        assertFalse(groupList.contains("Friends"));

        try {
            groupList.get(1);
            fail();
        }
        catch (IndexOutOfBoundsException ex) {
            //OK
        }
    }

    @Test
    public void testListContactsByGroup() {
        List<Long> contacts = new ArrayList<>();
        Contact contact = newContact("John", "Prague");
        contactManager.createContact(contact);
        contacts.add(contact.getContactID());
        Group group = newGroup("Family", contacts);
        groupManager.createGroup(group);

        Contact contactNotInGroup = newContact("Susane", "Munchen");
        contactManager.createContact(contactNotInGroup);

        List<Long> contacts2 = new ArrayList<>();
        Group group2 = newGroup("Friends", contacts2);
        groupManager.createGroup(group2);

        List<Contact> retrievedContacts = addressBookManager.listContactsByGroup(group2);
        assertNull(retrievedContacts);

        retrievedContacts = addressBookManager.listContactsByGroup(group);
        
        List<Long> retrievedContactsID = new ArrayList<>();
        for(Contact cont : retrievedContacts) {
            retrievedContactsID.add(cont.getContactID());
        }

        assertNotNull(retrievedContacts);
        assertEquals(retrievedContactsID, contacts);
        assertTrue(retrievedContacts.contains(contact));
        assertFalse(retrievedContacts.contains(contactNotInGroup));

        try {
            retrievedContacts.get(1);
            fail();
        }
        catch(IndexOutOfBoundsException ex) {
            //OK
        }

        try {
            addressBookManager.listContactsByGroup(null);
            fail();
        }
        catch(IllegalArgumentException illArg) {
            //OK
        }
    }

    @After
    public void deleteDataFromDB() throws IOException {
        Properties config = new Properties();
        config.load(Main.class.getResourceAsStream("/dbSetup.properties"));
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(config.getProperty("jdbc.url"));
        ds.setUsername(config.getProperty("jdbc.user"));
        ds.setPassword(config.getProperty("jdbc.password"));
        try (Connection con = ds.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("delete from groups")) {
                st.executeUpdate();
            }
            try (PreparedStatement st1 = con.prepareStatement("delete from phone")) {
                st1.executeUpdate();
            }
            try (PreparedStatement st1 = con.prepareStatement("delete from fax")) {
                st1.executeUpdate();
            }
            try (PreparedStatement st1 = con.prepareStatement("delete from email")) {
                st1.executeUpdate();
            }
            try (PreparedStatement st1 = con.prepareStatement("delete from other_contact")) {
                st1.executeUpdate();
            }
            try (PreparedStatement st1 = con.prepareStatement("delete from group_id")) {
                st1.executeUpdate();
            }
            try (PreparedStatement st1 = con.prepareStatement("delete from contact")) {
                st1.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Database delete failed", ex);
        }
    }

    private static Contact newContact(String name, String address) {
        Contact contact = new Contact(name);
        contact.setName(name);
        contact.setAddress(address);
        contact.setNewPhone("774889809");
        contact.setNewEmail("me@this.cz");
        contact.setNewFax("773449809");
        contact.setNewOtherContact("Skype", "Mommy");
        contact.setNewGroupId(1l);
        return contact;
    }

    private static Group newGroup(String name, List<Long> memberList) {
        Group group = new Group();
        group.setGroupName(name);
        group.setGroupMemberList(memberList);
        return group;
    }

    //implement equals() on Contact for this to work!!!
    private static void assertDeepEquals(Group group1, Group group2) {
        assertEquals(group1.getGroupID(), group2.getGroupID());
        assertEquals(group1.getGroupName(), group2.getGroupName());
        assertEquals(group1.getGroupMemberList(), group2.getGroupMemberList());
    }
}