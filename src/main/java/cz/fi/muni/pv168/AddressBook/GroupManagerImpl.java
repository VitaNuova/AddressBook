package cz.fi.muni.pv168.AddressBook;

import org.apache.derby.iapi.services.io.ArrayOutputStream;

import javax.sql.DataSource;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Виктория on 10-Mar-15.
 */
public class GroupManagerImpl implements GroupManager {

    private final DataSource dataSource;


    public GroupManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void createGroup(Group group) throws ServiceFailureException {
        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("insert into GROUPS(groupName, groupMemberList) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
                st.setString(1, group.getGroupName());
                List<Long> groupMembers = group.getGroupMemberList();
                st.setObject(2, groupMembers == null ? null : groupMembers.toString());
                st.executeUpdate();
                try(ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        group.setGroupID(id);
                    }
                }
            }
        } catch(SQLException ex) {
            throw new ServiceFailureException("database insert failed", ex);
        }

    }

    public void updateGroup(Group group) throws ServiceFailureException {
        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("update GROUPS set GROUPNAME = ?, GROUPMEMBERLIST = ? where id = ?")) {
                st.setString(1, group.getGroupName());
                List<Long> memberList = group.getGroupMemberList();
                st.setObject(2, memberList == null ? null : memberList.toString());
                st.setLong(3, group.getGroupID());
                int n = st.executeUpdate();
                if(n != 1) {
                    throw new ServiceFailureException("Unable to update group with id" + group.getGroupID(), null);
                }
            }
        } catch(SQLException ex) {
            throw new ServiceFailureException("Database update failed", ex);
        }
    }

    public void deleteGroup(Group group) throws ServiceFailureException {
        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("delete from groups where id = ?")) {
                st.setLong(1, group.getGroupID());
                int n = st.executeUpdate();
                if(n != 1) {
                    throw new ServiceFailureException("Unable to delete group with id" + group.getGroupID(), null);
                }
            }
        } catch(SQLException ex) {
            throw new ServiceFailureException("Database update failed", ex);
        }
    }

    public Group findGroupByID(Long id) throws ServiceFailureException
    {
        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("select * from GROUPS where id = ?")) {
                st.setLong(1, id);
                try(ResultSet rs = st.executeQuery()) {
                    if(rs.next()) {
                        List<Long> memberList = new ArrayList<>();
                        String members = rs.getString(3);
                        String membersWithoutBrackets = members.substring(1, members.length() - 1);
                        StringTokenizer tokens = new StringTokenizer(membersWithoutBrackets, ",");
                        for(int i = 0; i < tokens.countTokens(); i++) {
                            String trimmed = tokens.nextToken().trim();
                            memberList.add(Long.parseLong(trimmed));
                        }
                        return new Group(rs.getLong(1), rs.getString(2), memberList);
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("database select failed", ex);
        }
    }

    public Group findGroupByName(String name) throws ServiceFailureException {
        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("select * from GROUPS where GROUPNAME = ?")) {
                st.setString(1, name);
                try(ResultSet rs = st.executeQuery()) {
                    if(rs.next()) {
                        String members = rs.getString(3);
                        List<Long> memberList = parseString(members);
                        return new Group(rs.getLong(1), rs.getString(2), memberList);
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("database select failed", ex);
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
