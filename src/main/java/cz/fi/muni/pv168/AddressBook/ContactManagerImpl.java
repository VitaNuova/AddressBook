package cz.fi.muni.pv168.AddressBook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Created by Виктория on 12-Mar-15.
 */
public class ContactManagerImpl implements ContactManager {

    final static Logger log = LoggerFactory.getLogger(ContactManagerImpl.class);

    private final DataSource dataSource;

    public ContactManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createContact(Contact contact) throws ServiceFailureException, NullPointerException, IllegalArgumentException {
        if (contact == null) {
            throw new NullPointerException("Contact is null.");
        }
        if (contact.getId() != null) {
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
                contact.setId(getKey(keyRS, contact));

                if (contact.getPhone() != null) {
                    try (PreparedStatement st2 = conn.prepareStatement("INSERT INTO PHONE (contactId, phone) VALUES (?, ?)")) {
                        st2.setLong(1, contact.getId());
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
                        st3.setLong(1, contact.getId());
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
                        st4.setLong(1, contact.getId());
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
                        st5.setLong(1, contact.getId());
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
                        st6.setLong(1, contact.getId());
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

    public void updateContact(Contact contact) throws ServiceFailureException, NullPointerException, IllegalArgumentException {
        if (contact == null) {
            throw new NullPointerException("Contact is null.");
        }
        if (contact.getName() == null && contact.getAddress() == null && (contact.getPhone() == null || contact.getPhone().size() == 0) && (contact.getFax() == null || contact.getFax().size() == 0) && (contact.getEmail() == null || contact.getEmail().size() == 0) && (contact.getOtherContacts() == null || contact.getOtherContacts().size() == 0)) {
            throw new IllegalArgumentException("Contact doesn't have filled any field.");
        }

        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st1 = conn.prepareStatement("UPDATE CONTACT SET name=?, address=? WHERE id=?")) {
                st1.setString(1, contact.getName());
                st1.setString(2, contact.getAddress());
                st1.setLong(3, contact.getId());
                if (st1.executeUpdate() != 1) {
                    throw new ServiceFailureException("Internal Error: Cannot update contact " + contact);
                }

                // Get the set of phone numbers contact currently has.
                try(PreparedStatement st2 = conn.prepareStatement("SELECT phone FROM PHONE WHERE contactId=?")) {
                    st2.setLong(1, contact.getId());
                    ResultSet rs1 = st2.executeQuery();
                    HashSet<String> phonesToAdd = new HashSet(contact.getPhone());
                    HashSet<String> phonesToDelete = new HashSet<>();
                    while (rs1.next()) {
                        phonesToDelete.add(rs1.getString("phone"));
                    }
                    phonesToAdd.removeAll(phonesToDelete);
                    phonesToDelete.removeAll(contact.getPhone());
                    // Delete erased phone numbers.
                    if(!phonesToDelete.isEmpty()) {
                        try (PreparedStatement st3 = conn.prepareStatement("DELETE FROM PHONE WHERE contactId=? AND phone=?")) {
                            st3.setLong(1, contact.getId());
                            for(String phone : phonesToDelete) {
                                st3.setString(2, phone);
                                st3.executeUpdate();
                            }
                        }
                    }
                    // Add new phone numbers.
                    if(!phonesToAdd.isEmpty()) {
                        try (PreparedStatement st4 = conn.prepareStatement("INSERT INTO PHONE (contactId, phone) VALUES (?, ?)")) {
                            st4.setLong(1, contact.getId());
                            for(String phone : phonesToAdd) {
                                st4.setString(2, phone);
                                if (st4.executeUpdate() != 1) {
                                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact phone " + contact);
                                }
                            }
                        }
                    }
                }

                // Get the set of faxes contact currently has.
                try(PreparedStatement st2 = conn.prepareStatement("SELECT fax FROM FAX WHERE contactId=?")) {
                    st2.setLong(1, contact.getId());
                    ResultSet rs1 = st2.executeQuery();
                    HashSet<String> faxesToAdd = new HashSet(contact.getFax());
                    HashSet<String> faxesToDelete = new HashSet<>();
                    while (rs1.next()) {
                        faxesToDelete.add(rs1.getString("fax"));
                    }
                    faxesToAdd.removeAll(faxesToDelete);
                    faxesToDelete.removeAll(contact.getFax());
                    // Delete erased fax.
                    if(!faxesToDelete.isEmpty()) {
                        try (PreparedStatement st3 = conn.prepareStatement("DELETE FROM FAX WHERE contactId=? AND fax=?")) {
                            st3.setLong(1, contact.getId());
                            for(String fax : faxesToDelete) {
                                st3.setString(2, fax);
                                st3.executeUpdate();
                            }
                        }
                    }
                    // Add new faxes.
                    if(!faxesToAdd.isEmpty()) {
                        try (PreparedStatement st4 = conn.prepareStatement("INSERT INTO FAX (contactId, fax) VALUES (?, ?)")) {
                            st4.setLong(1, contact.getId());
                            for(String fax : faxesToAdd) {
                                st4.setString(2, fax);
                                if (st4.executeUpdate() != 1) {
                                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact fax " + contact);
                                }
                            }
                        }
                    }
                }

                // Get the set of e-mails contact currently has.
                try(PreparedStatement st2 = conn.prepareStatement("SELECT email FROM EMAIL WHERE contactId=?")) {
                    st2.setLong(1, contact.getId());
                    ResultSet rs1 = st2.executeQuery();
                    HashSet<String> emailsToAdd = new HashSet(contact.getEmail());
                    HashSet<String> emailsToDelete = new HashSet<>();
                    while (rs1.next()) {
                        emailsToDelete.add(rs1.getString("email"));
                    }
                    emailsToAdd.removeAll(emailsToDelete);
                    emailsToDelete.removeAll(contact.getEmail());
                    // Delete erased e-mails.
                    if(!emailsToDelete.isEmpty()) {
                        try (PreparedStatement st3 = conn.prepareStatement("DELETE FROM EMAIL WHERE contactId=? AND email=?")) {
                            st3.setLong(1, contact.getId());
                            for(String email : emailsToDelete) {
                                st3.setString(2, email);
                                st3.executeUpdate();
                            }
                        }
                    }
                    // Add new e-mails.
                    if(!emailsToAdd.isEmpty()) {
                        try (PreparedStatement st4 = conn.prepareStatement("INSERT INTO EMAIL (contactId, email) VALUES (?, ?)")) {
                            st4.setLong(1, contact.getId());
                            for(String email : emailsToAdd) {
                                st4.setString(2, email);
                                if (st4.executeUpdate() != 1) {
                                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact email " + contact);
                                }
                            }
                        }
                    }
                }

                // Get the set of other contacts contact currently has.
                try(PreparedStatement st2 = conn.prepareStatement("SELECT contactType, contact FROM OTHER_CONTACT WHERE contactId=?")) {
                    st2.setLong(1, contact.getId());
                    ResultSet rs1 = st2.executeQuery();
                    HashMap<String, String> otherContactsToAdd = new HashMap(contact.getOtherContacts());
                    HashMap<String, String> otherContactsToDelete = new HashMap<>();
                    while (rs1.next()) {
                        otherContactsToDelete.put(rs1.getString("contactType"), rs1.getString("contact"));
                    }
                    otherContactsToAdd.keySet().removeAll(otherContactsToDelete.keySet());
                    otherContactsToDelete.keySet().removeAll(contact.getOtherContacts().keySet());
                    // Delete erased other contacts.
                    if(!otherContactsToDelete.isEmpty()) {
                        try (PreparedStatement st3 = conn.prepareStatement("DELETE FROM OTHER_CONTACT WHERE contactId=? AND contactType=? AND contact=?")) {
                            st3.setLong(1, contact.getId());
                            for(Map.Entry<String, String> entry : otherContactsToDelete.entrySet()) {
                                st3.setString(2, entry.getKey());
                                st3.setString(3, entry.getValue());
                                st3.executeUpdate();
                            }
                        }
                    }
                    // Add new other contacts.
                    if(!otherContactsToAdd.isEmpty()) {
                        try (PreparedStatement st4 = conn.prepareStatement("INSERT INTO OTHER_CONTACT (contactId, contactType, contact) VALUES (?, ?, ?)")) {
                            st4.setLong(1, contact.getId());
                            for(Map.Entry<String, String> entry : otherContactsToAdd.entrySet()) {
                                st4.setString(2, entry.getKey());
                                st4.setString(3, entry.getValue());
                                if (st4.executeUpdate() != 1) {
                                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact other contact " + contact);
                                }
                            }
                        }
                    }
                }

                // Get the set of group IDs contact currently has.
                try(PreparedStatement st2 = conn.prepareStatement("SELECT groupId FROM GROUP_ID WHERE contactId=?")) {
                    st2.setLong(1, contact.getId());
                    ResultSet rs1 = st2.executeQuery();
                    HashSet<Long> groupIdsToAdd = new HashSet(contact.getGroupIds());
                    HashSet<Long> groupIdsToDelete = new HashSet<>();
                    while (rs1.next()) {
                        groupIdsToDelete.add(rs1.getLong("groupId"));
                    }
                    groupIdsToAdd.removeAll(groupIdsToDelete);
                    groupIdsToDelete.removeAll(contact.getGroupIds());
                    // Delete erased e-mails.
                    if(!groupIdsToDelete.isEmpty()) {
                        try (PreparedStatement st3 = conn.prepareStatement("DELETE FROM GROUP_ID WHERE contactId=? AND groupId=?")) {
                            st3.setLong(1, contact.getId());
                            for(Long groupId : groupIdsToDelete) {
                                st3.setLong(2, groupId);
                                st3.executeUpdate();
                            }
                        }
                    }
                    // Add new e-mails.
                    if(!groupIdsToAdd.isEmpty()) {
                        try (PreparedStatement st4 = conn.prepareStatement("INSERT INTO GROUP_ID (contactId, groupId) VALUES (?, ?)")) {
                            st4.setLong(1, contact.getId());
                            for(Long groupId : groupIdsToAdd) {
                                st4.setLong(2, groupId);
                                if (st4.executeUpdate() != 1) {
                                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert contact email " + contact);
                                }
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

    public void deleteContact(Contact contact) throws ServiceFailureException {
        try(Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st1 = conn.prepareStatement("DELETE FROM CONTACT WHERE id=?")) {
                if(contact.getPhone() != null) {
                    try(PreparedStatement st2 = conn.prepareStatement("DELETE FROM PHONE WHERE contactId=?")) {
                        st2.setLong(1, contact.getId());
                        if (st2.executeUpdate() != contact.getPhone().size()) {
                            throw new ServiceFailureException("Did not delete contact phones " + contact);
                        }
                    }
                }
                if(contact.getFax() != null) {
                    try(PreparedStatement st3 = conn.prepareStatement("DELETE FROM FAX WHERE contactId=?")) {
                        st3.setLong(1, contact.getId());
                        if (st3.executeUpdate() != contact.getFax().size()) {
                            throw new ServiceFailureException("Did not delete contact faxes " + contact);
                        }
                    }
                }
                if(contact.getEmail() != null) {
                    try(PreparedStatement st4 = conn.prepareStatement("DELETE FROM EMAIL WHERE contactId=?")) {
                        st4.setLong(1, contact.getId());
                        if (st4.executeUpdate() != contact.getEmail().size()) {
                            throw new ServiceFailureException("Did not delete contact emails " + contact);
                        }
                    }
                }
                if(contact.getOtherContacts() != null) {
                    try(PreparedStatement st5 = conn.prepareStatement("DELETE FROM OTHER_CONTACT WHERE contactId=?")) {
                        st5.setLong(1, contact.getId());
                        if (st5.executeUpdate() != contact.getOtherContacts().size()) {
                            throw new ServiceFailureException("Did not delete contact other contacts " + contact);
                        }
                    }
                }
                if(contact.getGroupIds() != null) {
                    try(PreparedStatement st6 = conn.prepareStatement("DELETE FROM GROUP_ID WHERE contactId=?")) {
                        st6.setLong(1, contact.getId());
                        if (st6.executeUpdate() != contact.getGroupIds().size()) {
                            throw new ServiceFailureException("Did not delete contact group list " + contact);
                        }
                    }
                }
                st1.setLong(1, contact.getId());
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
        if(id == null) {
            throw new NullPointerException("Find contact by id with null.");
        }
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
        if(name == null) {
            throw new NullPointerException("Find contact by name with null.");
        }
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
        if(phone == null) {
            throw new NullPointerException("Find contact by phone with null.");
        }
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
        if(fax == null) {
            throw new NullPointerException("Find contact by fax with null.");
        }
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
        if(email == null) {
            throw new NullPointerException("Find contact by email with null.");
        }
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
        if(address == null) {
            throw new NullPointerException("Find contact by address with null.");
        }
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
        if(contactType == null || contact == null) {
            throw new NullPointerException("Find contact by other contact type with null.");
        }
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
        Contact contact = new Contact(rs1.getString("name"));
        contact.setId(rs1.getLong("id"));
        contact.setAddress(rs1.getString("address"));
        try (PreparedStatement st1 = conn.prepareStatement("SELECT phone FROM PHONE WHERE contactId=?")) {
            st1.setLong(1, contact.getId());
            ResultSet rs2 = st1.executeQuery();
            while (rs2.next()) {
                contact.setNewPhone(rs2.getString("phone"));
            }
        }
        try (PreparedStatement st2 = conn.prepareStatement("SELECT fax FROM FAX WHERE contactId=?")) {
            st2.setLong(1, contact.getId());
            ResultSet rs3 = st2.executeQuery();
            while (rs3.next()) {
                contact.setNewFax(rs3.getString("fax"));
            }
        }
        try (PreparedStatement st3 = conn.prepareStatement("SELECT email FROM EMAIL WHERE contactId=?")) {
            st3.setLong(1, contact.getId());
            ResultSet rs4 = st3.executeQuery();
            while (rs4.next()) {
                contact.setNewEmail(rs4.getString("email"));
            }
        }
        try (PreparedStatement st4 = conn.prepareStatement("SELECT contactType, contact FROM OTHER_CONTACT WHERE contactId=?")) {
            st4.setLong(1, contact.getId());
            ResultSet rs5 = st4.executeQuery();
            while (rs5.next()) {
                contact.setNewOtherContact(rs5.getString("contactType"), rs5.getString("contact"));
            }
        }
        try (PreparedStatement st5 = conn.prepareStatement("SELECT groupId FROM GROUP_ID WHERE contactId=?")) {
            st5.setLong(1, contact.getId());
            ResultSet rs6 = st5.executeQuery();
            while (rs6.next()) {
                contact.setNewGroupId(rs6.getLong("groupId"));
            }
        }
        return contact;
    }
}
