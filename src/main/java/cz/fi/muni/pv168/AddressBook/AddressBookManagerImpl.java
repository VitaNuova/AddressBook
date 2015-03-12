package cz.fi.muni.pv168.AddressBook;

import java.util.List;

/**
 * Created by Виктория on 10-Mar-15.
 */
public class AddressBookManagerImpl implements AddressBookManager {
    @Override
    public List<Contact> listContactsByPerson() throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Group> listGroupsByPerson() throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Contact> listContactsByGroup(String groupName) throws ServiceFailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
