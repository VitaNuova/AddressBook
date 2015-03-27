package cz.fi.muni.pv168.AddressBook;

import org.apache.derby.iapi.services.io.ArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    final static Logger log = LoggerFactory.getLogger(GroupManagerImpl.class);

    public GroupManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void createGroup(Group group) throws ServiceFailureException {
        log.debug("create group({})", group);

        if(group == null) {
            throw new IllegalArgumentException("group is null");
        }
        if(group.getGroupID() != null) {
            throw new IllegalArgumentException("group id is set");
        }
        if(group.getGroupName() == null) {
            throw new IllegalArgumentException("group name cannot be null");
        }
        if(group.getGroupName().equals("")) {
            throw new IllegalArgumentException("group name cannot be empty");
        }
        if(group.getGroupMemberList() == null) {
            throw new IllegalArgumentException("group member list cannot be null");
        }

        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("insert into GROUPS(groupName, groupMemberList) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
                st.setString(1, group.getGroupName());
                List<Long> groupMembers = group.getGroupMemberList();
                st.setObject(2, groupMembers == null ? null : groupMembers.toString());
                int addedRows = st.executeUpdate();
                if(addedRows != 1) {
                    throw new ServiceFailureException("Error: more rows inserted when trying to insert group" + group);
                }
                try(ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long id = keys.getLong(1);
                        group.setGroupID(id);
                    }
                }
            }
        } catch(SQLException ex) {
            log.error("cannot insert group", ex);
            throw new ServiceFailureException("database insert failed", ex);
        }

    }

    public void updateGroup(Group group) throws ServiceFailureException {
        log.debug("update group({})", group);

        if(group == null) {
            throw new IllegalArgumentException("group is null");
        }
        if(group.getGroupID() == null) {
            throw new IllegalArgumentException("group with null id cannot be updated");
        }
        if(group.getGroupID() < 0) {
            throw new IllegalArgumentException("group is cannot be less than zero");
        }
        if(group.getGroupName() == null) {
            throw new IllegalArgumentException("group name cannot be null");
        }
        if(group.getGroupName().isEmpty()) {
            throw new IllegalArgumentException("group name cannot be empty");
        }
        if(group.getGroupMemberList() == null) {
            throw new IllegalArgumentException("group member list cannot be null");
        }

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
            log.error("cannot update group", ex);
            throw new ServiceFailureException("Database update failed", ex);
        }
    }

    public void deleteGroup(Group group) throws ServiceFailureException {
        log.debug("delete group({})", group);

        if(group == null) {
            throw new IllegalArgumentException("Cannot delete group which is null");
        }
        if(group.getGroupID() == null) {
            throw new IllegalArgumentException("Cannot delete group with null id");
        }
        if(group.getGroupID() < 0) {
            throw new IllegalArgumentException("Cannot delete group with negative id");
        }

        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("delete from groups where id = ?")) {
                st.setLong(1, group.getGroupID());
                int n = st.executeUpdate();
                if(n != 1) {
                    throw new ServiceFailureException("Unable to delete group with id " + group.getGroupID(), null);
                }
            }
        } catch(SQLException ex) {
            log.error("Database delete failed", ex);
            throw new ServiceFailureException("Database delete failed", ex);
        }
    }

    public Group findGroupByID(Long id) throws ServiceFailureException
    {
        log.debug("findGroupByID({})", id);
        try(Connection con = dataSource.getConnection()) {
            try(PreparedStatement st = con.prepareStatement("select * from GROUPS where id = ?")) {
                st.setLong(1, id);
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
            log.error("cannot select from database", ex);
            throw new ServiceFailureException("database select failed", ex);
        }
    }

    public Group findGroupByName(String name) throws ServiceFailureException {
        log.debug("find group by name({})", name);
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
            log.error("database select failed", ex);
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
