package cz.fi.muni.pv168.AddressBook;

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
        addressBookManager = new AddressBookManagerImpl();
        contactManager = new ContactManagerImpl();
        groupManager = new GroupManagerImpl();
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
        List<Group> groupList = addressBookManager.listGroupsByPerson();
        assertNull(groupList);

        List<Contact> memberList1 = new ArrayList<Contact>();
        Contact contact1 = newContact(1l);
        Contact contact2 = newContact(2l);
        memberList1.add(contact1);
        memberList1.add(contact2);

        List<Contact> memberList2 = new ArrayList<Contact>();
        Contact contact3 = newContact(3l);
        Contact contact4 = newContact(4l);
        memberList2.add(contact3);
        memberList2.add(contact4);

        Group group1 = newGroup("Family", memberList1);
        Group group2 = newGroup("Friends", memberList2);

        groupManager.createGroup(group1);

        groupList = addressBookManager.listGroupsByPerson();
        assertNotNull(groupList);
        assertDeepEquals(group1, groupList.get(0));
        assertEquals(group1, groupList.get(0));
        assertTrue(groupList.contains(group1));
        assertFalse(groupList.contains(group2));

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
        List<Contact> contacts = new ArrayList<Contact>();
        Contact contact1 = newContact(1l);
        Contact contact2 = newContact(2l);
        contacts.add(contact1);

        List<Contact> retrievedContacts = addressBookManager.listContactsByGroup("Family");
        assertNull(retrievedContacts);

        Group group = newGroup("Family", contacts);
        groupManager.createGroup(group);

        retrievedContacts = addressBookManager.listContactsByGroup(group.getGroupName());
        assertNotNull(retrievedContacts);
        assertEquals(retrievedContacts, contacts);
        assertTrue(retrievedContacts.contains(contact1));
        assertFalse(retrievedContacts.contains(contact2));

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

    private static Group newGroup(String name, List<Contact> memberList) {
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