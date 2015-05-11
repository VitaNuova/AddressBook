package cz.fi.muni.pv168.AddressBook; /**
 * Created by Виктория on 10-Mar-15.
 */

import java.util.ArrayList;
import java.util.List;

public class Group {

    private Long groupID;
    private String groupName;
    List<Long> groupMemberList;

    public Group() {}

    public Group(String groupName) {
        this.groupName = groupName;
        groupMemberList = new ArrayList<>();
    }

    public Group(String groupName, List<Long> groupMemberList) {
        this.groupName = groupName;
        this.groupMemberList = groupMemberList;
    }

    public Group(Long groupID, String groupName, List<Long> groupMemberList) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupMemberList = groupMemberList;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public List<Long> getGroupMemberList() {
        return groupMemberList;
    }

    public void setGroupMemberList(List<Long> groupMemberList) {
        this.groupMemberList = groupMemberList;
    }

    @Override
    public String toString() {
        return "сz.fi.muni.pv168.addressbook.Group {id = " + groupID + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (groupID != null ? !groupID.equals(group.groupID) : group.groupID != null) return false;
        if (groupName != null ? !groupName.equals(group.groupName) : group.groupName != null) return false;
        return !(groupMemberList != null ? !groupMemberList.equals(group.groupMemberList) : group.groupMemberList != null);

    }

    @Override
    public int hashCode() {
        int result = groupID != null ? groupID.hashCode() : 0;
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        result = 31 * result + (groupMemberList != null ? groupMemberList.hashCode() : 0);
        return result;
    }
}
