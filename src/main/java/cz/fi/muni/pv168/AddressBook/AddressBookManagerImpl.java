package cz.fi.muni.pv168.AddressBook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Виктория on 10-Mar-15.
 */
public class AddressBookManagerImpl implements AddressBookManager {

    private final DataSource dataSource;

    final static Logger log = LoggerFactory.getLogger(AddressBookManagerImpl.class);

    public AddressBookManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Contact> listContactsByPerson() throws ServiceFailureException {
        log.debug("listing all contacts");
        ContactManager contactManager = new ContactManagerImpl(dataSource);
        List<Contact> result = new ArrayList<>(contactManager.findAllContacts());
        return (result.isEmpty()) ? null : result;
    }

    public List<String> listGroupsByContact(Contact contact) throws ServiceFailureException, NullPointerException {
        if(contact == null || contact.getId() == null) {
            throw new NullPointerException("Nonexistent contact given.");
        }
        List<String> groupList = new ArrayList<>();
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st1 = conn.prepareStatement("SELECT groupId FROM GROUP_ID WHERE contactId=?")) {
                st1.setLong(1, contact.getId());
                ResultSet rs1 = st1.executeQuery();
                Long groupId;
                while(rs1.next()) {
                    groupId = rs1.getLong("groupId");
                    try(PreparedStatement st2 = conn.prepareStatement("SELECT groupName FROM GROUPS WHERE id=?")) {
                        st2.setLong(1, groupId);
                        ResultSet rs2 = st2.executeQuery();
                        if(rs2.next()) {
                            groupList.add(rs2.getString("groupName"));
                            if(rs2.next()) {
                                throw new ServiceFailureException("Internal error: More entities with the same id found.");
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            log.error("Database select failed.", ex);
            throw new ServiceFailureException("Database select failed.", ex);
        }
        return (groupList.isEmpty()) ? null : groupList;
    }

    public List<String> listGroupsByPerson() throws ServiceFailureException {
        log.debug("listGroupsByPerson()");
        List<String> groupList = null;
        String res = null;
        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("select groupName from groups")) {
                try(ResultSet rs = st.executeQuery()) {
                    if(rs.next()) {
                        groupList = new ArrayList<>();
                        res = rs.getString("groupName");
                        groupList.add(res);
                    }
                    while(rs.next()) {
                        res = rs.getString("groupName");
                        groupList.add(res);
                    }
                    return groupList;
                }
            }
        } catch (SQLException ex) {
            log.error("Database select failed", ex);
            throw new ServiceFailureException("Database select failed", ex);
        }

    }


    public List<Contact> listContactsByGroup(Group group) throws ServiceFailureException {

        List<Long> contacts = new ArrayList<>();
        if(group == null) {
            throw new IllegalArgumentException("Group is null");
        }
        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("select GROUPMEMBERLIST from GROUPS where id = ?")) {
                st.setLong(1, group.getGroupID());
                try(ResultSet rs = st.executeQuery()) {
                    if(rs.next()) {
                        String members = rs.getString(1);
                        List<Long> memberList = parseString(members);
                        List<Contact> contactsInGroup = null;
                        if(memberList.size() > 0) {
                            contactsInGroup = new ArrayList<>();
                            ContactManager contactManager = new ContactManagerImpl(dataSource);
                            for (Long member : memberList) {
                                contactsInGroup.add(contactManager.findContactById(member));
                            }
                        }
                        return contactsInGroup;
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch(SQLException ex) {
            log.error("Database select failed", ex);
            throw new ServiceFailureException("Unable to execute select query", ex);
        }
    }

    private List<Long> parseString(String members) {
        List<Long> memberList = new ArrayList<>();
        String membersWithoutBrackets = members.substring(1, members.length() - 1);
        StringTokenizer tokens = new StringTokenizer(membersWithoutBrackets, ",");
        for(int i = 0; i < tokens.countTokens(); i++) {
            String trimmed = tokens.nextToken().trim();
            memberList.add(Long.parseLong(trimmed));
        }
        return memberList;
    }

}
