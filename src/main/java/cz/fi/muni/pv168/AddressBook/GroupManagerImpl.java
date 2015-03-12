package cz.fi.muni.pv168.AddressBook;

import java.util.List;

/**
 * Created by Виктория on 10-Mar-15.
 */
public class GroupManagerImpl implements GroupManager {
    @Override
    public void createGroup(Group group) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateGroup(Group group) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteGroup(Group group) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Group findGroupByID(Long id) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Group findGroupByName(String name) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Group> findGroupByMember(Contact contact) throws ServiceFailureException {
        return null;
    }
}
