package cz.fi.muni.pv168.AddressBook;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.tools.jar.Main;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class GroupManagerImplTest {

    private GroupManagerImpl manager;

    @Before
    public void setUp() throws SQLException, IOException {
        Properties config = new Properties();
        config.load(Main.class.getResourceAsStream("/config.properties"));
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(config.getProperty("jdbc.url"));
        ds.setUsername(config.getProperty("jdbc.user"));
        ds.setPassword(config.getProperty("jdbc.password"));
        manager = new GroupManagerImpl(ds);
    }

    @Test
    public void testCreateGroup() {
        List<Long> contacts = new ArrayList<>();
        Group group = newGroup("School", contacts);
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

        List<Long> contacts = new ArrayList<>();
        contacts.add(1l);
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
        group = newGroup("Friends", contacts);
        manager.createGroup(group);
        Group result = manager.findGroupByID(group.getGroupID());
        assertNotNull(result);

        List<Long> contacts2 = new ArrayList<>();
        contacts2.add(2l);
        group = newGroup("Collegues", contacts2);
        manager.createGroup(group);
        result = manager.findGroupByID(group.getGroupID());
        assertNotNull(result);

    }

    @Test
    public void testUpdateGroup() {
        List<Long> contacts1 = new ArrayList<>();
        contacts1.add(1l);
        List<Long> contacts2 = new ArrayList<>();
        contacts2.add(2l);
        Group group1 = newGroup("Uni", contacts1);
        Group group2 = newGroup("Traveling", contacts2);
        manager.createGroup(group1);
        manager.createGroup(group2);

        Long id = group1.getGroupID();

        group1 = manager.findGroupByID(id);
        group1.setGroupName("Work");
        manager.updateGroup(group1);
        assertEquals(group1.getGroupName(), "Work");
        assertEquals(group1.getGroupMemberList(), contacts1);


        group1 = manager.findGroupByID(id);
        List<Long> memberList = group1.getGroupMemberList();
        memberList.add(3l);
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
        try {
             manager.updateGroup(group1);
             fail();
        }
        catch(IllegalArgumentException ex) {
            //OK
        }
        assertNull(group1.getGroupName());
        assertNull(group1.getGroupMemberList());

        assertDeepEquals(group2, manager.findGroupByID(group2.getGroupID()));

    }

    @Test
    public void testUpdateGroupWithWrongAttributes() {


        List<Long> contacts = new ArrayList<>();
        contacts.add(1l);
        Group group = newGroup("Holiday", contacts);
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
            group.setGroupID(1l);
            manager.updateGroup(group);
            fail();
        } catch (ServiceFailureException ex) {
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

        List<Long> contacts1 = new ArrayList<>();
        contacts1.add(1l);
        List<Long> contacts2 = new ArrayList<>();
        contacts2.add(2l);
        Group group1 = newGroup("Germany", contacts1);
        Group group2 = newGroup("USA", contacts2);

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

        List<Long> contacts = new ArrayList<>();
        contacts.add(1l);
        Group group = newGroup("Vacation", contacts);

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
        } catch (ServiceFailureException ex) {
            //OK
        }

    }

    @Test
    public void testFindGroupByID() {

        assertNull(manager.findGroupByID(-1l));

        List<Long> contacts = new ArrayList<>();
        Group group = newGroup("Partners", contacts);
        manager.createGroup(group);

        Group result = manager.findGroupByID(group.getGroupID());
        if(group.toString().equals(result.toString())) {
            System.out.println("toString OK");
        }
        if(group.getGroupID().equals(result.getGroupID())) {
            System.out.println("ID OK");
        }
        if(group.getGroupMemberList().equals(result.getGroupMemberList())) {
            System.out.println("Members OK");
        } else {
            System.out.println("Members not OK");
        }

        assertEquals(group, result);
        assertDeepEquals(group, result);

    }

    @Test
    public void testFindGroupByName() {

        assertNull(manager.findGroupByName("Family"));

        List<Long> contacts = new ArrayList<>();
        Group group = newGroup("Parents", contacts);
        manager.createGroup(group);

        Group result = manager.findGroupByName("Parents");
        assertEquals(group, result);
        assertDeepEquals(group, result);
    }

    @After
    public void deleteDataFromDB() throws IOException {
        Properties config = new Properties();
        config.load(Main.class.getResourceAsStream("/config.properties"));
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(config.getProperty("jdbc.url"));
        ds.setUsername(config.getProperty("jdbc.user"));
        ds.setPassword(config.getProperty("jdbc.password"));
        try (Connection con = ds.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("delete from groups")) {
                st.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Database delete failed", ex);
        }
    }

    private static Group newGroup(String name, List<Long> memberList) {
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