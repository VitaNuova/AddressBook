package cz.fi.muni.pv168.AddressBook;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AddressBookManagerImplTest {
    private AddressBookManagerImpl addressBookManager;
    private ContactManagerImpl contactManager;
    private GroupManagerImpl groupManager;

    @Before
    public void setUp() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby://localhost:1527/Databases/AddressBookDB;user=app;password=passwd");
        ds.setUsername("app");
        ds.setPassword("passwd");
        addressBookManager = new AddressBookManagerImpl(ds);
        contactManager = new ContactManagerImpl(ds);
        groupManager = new GroupManagerImpl(ds);
    }


    @Test
    //don't forget to override equals() in Contact class, otherwise ArrayList.contains() will not work properly!!!
    public void testListContactsByPerson() {
        List<Contact> retrievedContacts = addressBookManager.listContactsByPerson();
        assertNull(retrievedContacts);

        Contact contact1 = newContact(1l);
        Contact contact2 = newContact(2l);
        Contact contact3 = newContact(3l);
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
        catch (ArrayIndexOutOfBoundsException ex) {
            //OK
        }
    }

    @Test
    public void testListContactsByGroup() {
        List<Long> contacts = new ArrayList<>();
        contacts.add(1l);

        Group group = newGroup("Family", contacts);
        List<Contact> retrievedContacts = addressBookManager.listContactsByGroup(group);
        assertNull(retrievedContacts);

        groupManager.createGroup(group);

        retrievedContacts = addressBookManager.listContactsByGroup(group);
        List<Long> retrievedContactsID = new ArrayList<>();
        for(Contact contact : retrievedContacts) {
            retrievedContactsID.add(contact.getContactID());
        }
        assertNotNull(retrievedContacts);
        assertEquals(retrievedContactsID, contacts);
        assertTrue(retrievedContactsID.contains(1l));
        assertFalse(retrievedContactsID.contains(2l));

        try {
            retrievedContacts.get(1);
            fail();
        }
        catch(ArrayIndexOutOfBoundsException ex) {
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

    private static Contact newContact(Long id) {
        Contact contact = new Contact();
        contact.setContactID(id);
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