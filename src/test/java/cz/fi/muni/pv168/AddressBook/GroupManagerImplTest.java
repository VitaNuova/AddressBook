package cz.fi.muni.pv168.AddressBook;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GroupManagerImplTest {

    private GroupManagerImpl manager;

    @Before
    public void setUp() throws SQLException {
        manager = new GroupManagerImpl();
    }

    @Test
    public void testCreateGroup() {
        List<Contact> contacts = new ArrayList<Contact>();
        Group group = newGroup("Family", contacts);
        manager.createGroup(group);
        assertNotNull(group.getGroupID());

        Group result = manager.findGroupByID(group.getGroupID());
        assertNotNull(result);
        assertEquals(group, result);
        assertNotSame(group, result);
        assertDeepEquals(group, result);

    }

    @Test
    public void testAddGroupWithWrongAttributes() {

        try {
            manager.createGroup(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        List<Contact> contacts = new ArrayList<Contact>();
        Contact contact1 = newContact(1l);
        contacts.add(contact1);
        Group group = newGroup("Family", contacts);
        group.setGroupID(1l);
        try {
            manager.createGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        group = newGroup(null, contacts);
        try {
            manager.createGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        group = newGroup("Family", null);
        try {
            manager.createGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        // these variants should be ok
        group = newGroup("Family", contacts);
        manager.createGroup(group);
        Group result = manager.findGroupByID(group.getGroupID());
        assertNotNull(result);

        List<Contact> contacts2 = new ArrayList<Contact>();
        Contact contact2 = newContact(2l);
        contacts2.add(contact2);
        group = newGroup("Friends", contacts2);
        manager.createGroup(group);
        result = manager.findGroupByID(group.getGroupID());
        assertNotNull(result);

    }

    @Test
    public void testUpdateGroup() {
        List<Contact> contacts1 = new ArrayList<Contact>();
        Contact contact1 = newContact(1l);
        contacts1.add(contact1);
        List<Contact> contacts2 = new ArrayList<Contact>();
        Contact contact2 = newContact(2l);
        contacts2.add(contact2);
        Group group1 = newGroup("Family", contacts1);
        Group group2 = newGroup("Friends", contacts2);
        manager.createGroup(group1);
        manager.createGroup(group2);

        Long id = group1.getGroupID();

        group1 = manager.findGroupByID(id);
        group1.setGroupName("Work");
        manager.updateGroup(group1);
        assertEquals(group1.getGroupName(), "Work");
        assertEquals(group1.getGroupMemberList(), contacts1);


        Contact contact4 = newContact(4l);
        group1 = manager.findGroupByID(id);
        List<Contact> memberList = group1.getGroupMemberList();
        memberList.add(contact4);
        group1.setGroupMemberList(memberList);
        manager.updateGroup(group1);
        assertEquals(group1.getGroupName(), "Work");
        assertEquals(group1.getGroupMemberList(), memberList);

        memberList = group1.getGroupMemberList();
        memberList.remove(0);
        group1.setGroupMemberList(memberList);
        manager.updateGroup(group1);
        assertEquals(group1.getGroupName(), "Work");
        assertEquals(group1.getGroupMemberList(), memberList);


        group1 = manager.findGroupByID(id);
        group1.setGroupName(null);
        group1.setGroupMemberList(null);
        manager.updateGroup(group1);
        assertNull(group1.getGroupName());
        assertNull(group1.getGroupMemberList());

        assertDeepEquals(group2, manager.findGroupByID(group2.getGroupID()));

    }

    @Test
    public void testUpdateGroupWithWrongAttributes() {


        List<Contact> contacts = new ArrayList<Contact>();
        Contact contact = newContact(1l);
        contacts.add(contact);
        Group group = newGroup("Friends", contacts);
        manager.createGroup(group);

        Long id = group.getGroupID();

        try {
            manager.updateGroup(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            group = manager.findGroupByID(id);
            group.setGroupID(null);
            manager.updateGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            group = manager.findGroupByID(id);
            group.setGroupID(id - 1);
            manager.updateGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            group = manager.findGroupByID(id);
            group.setGroupName("");
            manager.updateGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            group = manager.findGroupByID(id);
            group.setGroupName(null);
            manager.updateGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            group = manager.findGroupByID(id);
            group.setGroupMemberList(null);
            manager.updateGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

    }

    @Test
    public void testDeleteGroup() {

        List<Contact> contacts1 = new ArrayList<Contact>();
        Contact contact1 = newContact(1l);
        contacts1.add(contact1);
        List<Contact> contacts2 = new ArrayList<Contact>();
        Contact contact2 = newContact(2l);
        contacts2.add(contact2);
        Group group1 = newGroup("Family", contacts1);
        Group group2 = newGroup("Friends", contacts2);

        manager.createGroup(group1);
        manager.createGroup(group2);

        assertNotNull(manager.findGroupByID(group1.getGroupID()));
        assertNotNull(manager.findGroupByID(group2.getGroupID()));

        manager.deleteGroup(group1);

        assertNull(manager.findGroupByID(group1.getGroupID()));
        assertNotNull(manager.findGroupByID(group2.getGroupID()));

    }

    @Test
    public void testDeleteGroupWithWrongAttributes() {

        List<Contact> contacts = new ArrayList<Contact>();
        Contact contact1 = newContact(1l);
        contacts.add(contact1);
        Group group = newGroup("Family", contacts);

        try {
            manager.deleteGroup(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            group.setGroupID(null);
            manager.deleteGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            group.setGroupID(1l);
            manager.deleteGroup(group);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

    }

    @Test
    public void testFindGroupByID() {

        assertNull(manager.findGroupByID(1l));

        List<Contact> contacts = new ArrayList<Contact>();
        Group group = newGroup("Family", contacts);
        manager.createGroup(group);

        Group result = manager.findGroupByID(group.getGroupID());
        assertEquals(group, result);
        assertDeepEquals(group, result);

    }

    @Test
    public void testFindGroupByName() {

        assertNull(manager.findGroupByName("Family"));

        List<Contact> contacts = new ArrayList<Contact>();
        Group group = newGroup("Friends", contacts);
        manager.createGroup(group);

        Group result = manager.findGroupByName("Friends");
        assertEquals(group, result);
        assertDeepEquals(group, result);
    }

    @Test
    public void testFindGroupByMember() throws Exception {

        assertNull(manager.findGroupByMember(newContact(1l)));

        List<Contact> contacts = new ArrayList<Contact>();
        Contact contact = newContact(2l);
        contacts.add(contact);
        Group group = newGroup("Friends", contacts);
        manager.createGroup(group);

        List<Group> result = manager.findGroupByMember(contact);
        assertNotNull(result.contains(contact));

    }

    private static Group newGroup(String name, List<Contact> memberList) {
        Group group = new Group();
        group.setGroupName(name);
        group.setGroupMemberList(memberList);
        return group;
    }

    private static Contact newContact(Long id) {
        Contact contact = new Contact();
        contact.setContactID(id);
        return contact;
    }

    //implement equals() on Contact for this to work!!!
    private void assertDeepEquals(Group expected, Group actual) {
        assertEquals(expected.getGroupID(), actual.getGroupID());
        assertEquals(expected.getGroupName(), actual.getGroupName());
        assertEquals(expected.getGroupMemberList(), actual.getGroupMemberList());
    }


}