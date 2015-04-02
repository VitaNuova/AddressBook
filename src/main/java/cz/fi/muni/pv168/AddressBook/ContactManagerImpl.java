package cz.fi.muni.pv168.AddressBook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Виктория on 12-Mar-15.
 */
public class ContactManagerImpl implements ContactManager {

    final static Logger log = LoggerFactory.getLogger(ContactManagerImpl.class);

    private final DataSource dataSource;

    public ContactManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createContact(Contact contact) throws ServiceFailureException {
        if (contact == null) {
            throw new IllegalArgumentException("Contact is null.");
        }
        if (contact.getContactID() != null) {
            throw new IllegalArgumentException("Contact ID was set manually.");
        }
        if (contact.getName() == null && contact.getAddress() == null && (contact.getPhone() == null || contact.getPhone().size() == 0) && (contact.getFax() == null || contact.getFax().size() == 0) && (contact.getEmail() == null || contact.getEmail().size() == 0) && (contact.getOtherContacts() == null || contact.getOtherContacts().size() == 0)) {
            throw new IllegalArgumentException("Contact doesn't have filled any field.");
        }

        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st1 = conn.prepareStatement("INSERT INTO CONTACT (name, address) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                st1.setString(1, contact.getName());
                st1.setString(2, contact.getAddress());
                if (st1.executeUpdate() != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact " + contact);
                }
                ResultSet keyRS = st1.getGeneratedKeys();

                contact.setContactID(getKey(keyRS, contact));
                if (contact.getPhone() != null) {
                    try (PreparedStatement st2 = conn.prepareStatement("INSERT INTO PHONE (contactId, phone) VALUES (?, ?)")) {
                        st2.setLong(1, contact.getContactID());
                        for (String phone : contact.getPhone()) {
                            st2.setString(2, phone);
                            if (st2.executeUpdate() != 1) {
                                throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact phone " + contact);
                            }
                        }
                    }
                }
                if (contact.getFax() != null) {
                    try (PreparedStatement st3 = conn.prepareStatement("INSERT INTO FAX (contactId, fax) VALUES (?, ?)")) {
                        st3.setLong(1, contact.getContactID());
                        for (String fax : contact.getFax()) {
                            st3.setString(2, fax);
                            if (st3.executeUpdate() != 1) {
                                throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact fax " + contact);
                            }
                        }
                    }
                }
                if (contact.getEmail() != null) {
                    try (PreparedStatement st4 = conn.prepareStatement("INSERT INTO EMAIL (contactId, email) VALUES (?, ?)")) {
                        st4.setLong(1, contact.getContactID());
                        for (String email : contact.getEmail()) {
                            st4.setString(2, email);
                            if (st4.executeUpdate() != 1) {
                                throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact email " + contact);
                            }
                        }
                    }
                }
                if (contact.getOtherContacts() != null) {
                    try (PreparedStatement st5 = conn.prepareStatement("INSERT INTO OTHER_CONTACT (contactId, contactType, contact) VALUES (?, ?, ?)")) {
                        st5.setLong(1, contact.getContactID());
                        for (Map.Entry<String, String> entry : contact.getOtherContacts().entrySet()) {
                            st5.setString(2, entry.getKey());
                            st5.setString(3, entry.getValue());
                            if (st5.executeUpdate() != 1) {
                                throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact other contact " + contact);
                            }
                        }
                    }
                }
                if (contact.getGroupIds() != null) {
                    try (PreparedStatement st6 = conn.prepareStatement("INSERT INTO GROUP_ID (contactId, groupId) VALUES (?, ?)")) {
                        st6.setLong(1, contact.getContactID());
                        for (Long id : contact.getGroupIds()) {
                            st6.setLong(2, id);
                            if (st6.executeUpdate() != 1) {
                                throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact to group " + contact);
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    private Long getKey(ResultSet keyRS, Contact contact) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key retrieving failed when trying to insert contact " + contact + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key retrieving failed when trying to insert grave " + contact + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key retrieving failed when trying to insert grave " + contact + " - no key found");
        }
    }

    public void updateContact(Contact contact) throws ServiceFailureException {

    }

    public void deleteContact(Contact contact) throws ServiceFailureException {
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st1 = conn.prepareStatement("DELETE FROM CONTACT WHERE id=?")) {
                if(contact.getPhone() != null) {
                    try(PreparedStatement st2 = conn.prepareStatement("DELETE FROM PHONE WHERE contactId=?")) {
                        st2.setLong(1, contact.getContactID());
                        if (st2.executeUpdate() != contact.getPhone().size()) {
                            throw new ServiceFailureException("Did not delete contact phones " + contact);
                        }
                    }
                }
                if(contact.getFax() != null) {
                    try(PreparedStatement st3 = conn.prepareStatement("DELETE FROM FAX WHERE contactId=?")) {
                        st3.setLong(1, contact.getContactID());
                        if (st3.executeUpdate() != contact.getFax().size()) {
                            throw new ServiceFailureException("Did not delete contact faxes " + contact);
                        }
                    }
                }
                if(contact.getEmail() != null) {
                    try(PreparedStatement st4 = conn.prepareStatement("DELETE FROM EMAIL WHERE contactId=?")) {
                        st4.setLong(1, contact.getContactID());
                        if (st4.executeUpdate() != contact.getEmail().size()) {
                            throw new ServiceFailureException("Did not delete contact emails " + contact);
                        }
                    }
                }
                if(contact.getOtherContacts() != null) {
                    try(PreparedStatement st5 = conn.prepareStatement("DELETE FROM OTHER_CONTACT WHERE contactId=?")) {
                        st5.setLong(1, contact.getContactID());
                        if (st5.executeUpdate() != contact.getOtherContacts().size()) {
                            throw new ServiceFailureException("Did not delete contact other contacts " + contact);
                        }
                    }
                }
                if(contact.getGroupIds() != null) {
                    try(PreparedStatement st6 = conn.prepareStatement("DELETE FROM GROUP_ID WHERE contactId=?")) {
                        st6.setLong(1, contact.getContactID());
                        if (st6.executeUpdate() != contact.getGroupIds().size()) {
                            throw new ServiceFailureException("Did not delete contact group list " + contact);
                        }
                    }
                }
                st1.setLong(1, contact.getContactID());
                if (st1.executeUpdate() != 1) {
                    throw new ServiceFailureException("Did not delete contact " + contact);
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    public Contact findContactById(Long id) throws ServiceFailureException {
        log.debug("finding contact with id " + id);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, address FROM CONTACT WHERE id=?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Contact contact = resultSetToContact(conn, rs);
                    if (rs.next()) {
                        throw new ServiceFailureException("Internal error: More entities with the same id found (source id: " + id + ", found " + contact + " and " + resultSetToContact(conn, rs));
                    }
                    return contact;
                }
                else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    public Collection<Contact> findAllContacts() throws ServiceFailureException {
        log.debug("finding all contacts");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, address FROM CONTACT")) {
                ResultSet rs = st.executeQuery();
                List<Contact> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToContact(conn, rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    public Collection<Contact> findContactByName(String name) {
        log.debug("finding contacts with name " + name);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, address FROM CONTACT WHERE name=?")) {
                st.setString(1, name);
                ResultSet rs = st.executeQuery();
                List<Contact> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToContact(conn, rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    public Collection<Contact> findContactByPhone(String phone) {
        log.debug("finding contacts with phone " + phone);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT contactId FROM PHONE WHERE phone=?")) {
                st.setString(1, phone);
                ResultSet rs = st.executeQuery();
                List<Contact> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(findContactById(rs.getLong("contactId")));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    public Collection<Contact> findContactByFax(String fax) {
        log.debug("finding contacts with fax " + fax);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT contactId FROM FAX WHERE fax=?")) {
                st.setString(1, fax);
                ResultSet rs = st.executeQuery();
                List<Contact> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(findContactById(rs.getLong("contactId")));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    public Collection<Contact> findContactByEmail(String email) {
        log.debug("finding contacts with email " + email);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT contactId FROM EMAIL WHERE email=?")) {
                st.setString(1, email);
                ResultSet rs = st.executeQuery();
                List<Contact> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(findContactById(rs.getLong("contactId")));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    public Collection<Contact> findContactByAddress(String address) {
        log.debug("finding contacts with address " + address);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, address FROM CONTACT WHERE address=?")) {
                st.setString(1, address);
                ResultSet rs = st.executeQuery();
                List<Contact> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToContact(conn, rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    public Collection<Contact> findContactByOtherContactType(String contactType, String contact) {
        log.debug("finding contacts with other contact type " + contactType + ":" + contact);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT contactId FROM OTHER_CONTACT WHERE contactType=? AND contact=?")) {
                st.setString(1, contactType);
                st.setString(2, contact);
                ResultSet rs = st.executeQuery();
                List<Contact> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(findContactById(rs.getLong("contactId")));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all contacts.", ex);
        }
    }

    private Contact resultSetToContact(Connection conn, ResultSet rs1) throws SQLException {
        Contact contact = new Contact();
        contact.setContactID(rs1.getLong("id"));
        contact.setName(rs1.getString("name"));
        contact.setAddress(rs1.getString("address"));
        try (PreparedStatement st1 = conn.prepareStatement("SELECT phone FROM PHONE WHERE contactId=?")) {
            st1.setLong(1, contact.getContactID());
            ResultSet rs2 = st1.executeQuery();
            while (rs2.next()) {
                contact.setNewPhone(rs2.getString("phone"));
            }
        }
        try (PreparedStatement st2 = conn.prepareStatement("SELECT fax FROM FAX WHERE contactId=?")) {
            st2.setLong(1, contact.getContactID());
            ResultSet rs3 = st2.executeQuery();
            while (rs3.next()) {
                contact.setNewFax(rs3.getString("fax"));
            }
        }
        try (PreparedStatement st3 = conn.prepareStatement("SELECT email FROM EMAIL WHERE contactId=?")) {
            st3.setLong(1, contact.getContactID());
            ResultSet rs4 = st3.executeQuery();
            while (rs4.next()) {
                contact.setNewEmail(rs4.getString("email"));
            }
        }
        try (PreparedStatement st4 = conn.prepareStatement("SELECT contactType, contact FROM OTHER_CONTACT WHERE contactId=?")) {
            st4.setLong(1, contact.getContactID());
            ResultSet rs5 = st4.executeQuery();
            while (rs5.next()) {
                contact.setNewOtherContact(rs5.getString("contactType"), rs5.getString("contact"));
            }
        }
        try (PreparedStatement st5 = conn.prepareStatement("SELECT groupId FROM GROUP_ID WHERE contactId=?")) {
            st5.setLong(1, contact.getContactID());
            ResultSet rs6 = st5.executeQuery();
            while (rs6.next()) {
                contact.setNewGroupId(rs6.getLong("groupId"));
            }
        }
        return contact;
    }
}
