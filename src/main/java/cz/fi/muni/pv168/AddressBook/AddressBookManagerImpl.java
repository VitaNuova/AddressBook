package cz.fi.muni.pv168.AddressBook;

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

    public AddressBookManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Contact> listContactsByPerson() throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> listGroupsByContact(Contact contact) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> listGroupsByPerson() throws ServiceFailureException {

        try(Connection con = dataSource.getConnection()) {
            System.out.println("Connection ok");
            try(PreparedStatement st = con.prepareStatement("select groupName from groups")) {
                System.out.println("Statement prep ok");
                try(ResultSet rs = st.executeQuery()) {
                    List<String> groupList = new ArrayList<>();
                    while(rs.next()) {
                        String res = rs.getString("name");
                        groupList.add(res);
                    }
                    return groupList;
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Database select failed", ex);
        }


    }


    public List<Contact> listContactsByGroup(Group group) throws ServiceFailureException {
        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("select GROUPMEMBERLIST from GROUPS where id = ?")) {
                st.setLong(1, group.getGroupID());
                try(ResultSet rs = st.executeQuery()) {
                    if(rs.next()) {
                        String members = rs.getString(1);
                        List<Long> memberList = parseString(members);
                        List<Contact> contactsInGroup = new ArrayList<>();
                        ContactManager contactManager = new ContactManagerImpl(dataSource);
                        for(Long member : memberList) {
                            contactsInGroup.add(contactManager.findContactById(member));
                        }
                        return contactsInGroup;
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch(SQLException ex) {
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
