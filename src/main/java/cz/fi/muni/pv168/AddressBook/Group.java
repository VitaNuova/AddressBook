package cz.fi.muni.pv168.AddressBook; /**
 * Created by Виктория on 10-Mar-15.
 */

import java.util.List;

public class Group {

    private Long groupID;
    private String groupName;
    List<Contact> groupMemberList;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public List<Contact> getGroupMemberList() {
        return groupMemberList;
    }

    public void setGroupMemberList(List<Contact> groupMemberList) {
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

        if (groupID != group.groupID) return false;
        if (groupMemberList != null ? !groupMemberList.equals(group.groupMemberList) : group.groupMemberList != null)
            return false;
        if (!groupName.equals(group.groupName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (groupID ^ (groupID >>> 32));
        result = 31 * result + groupName.hashCode();
        result = 31 * result + (groupMemberList != null ? groupMemberList.hashCode() : 0);
        return result;
    }
}
