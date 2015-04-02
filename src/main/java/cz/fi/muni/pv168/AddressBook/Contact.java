package cz.fi.muni.pv168.AddressBook;

import java.util.*;

/**
 * Created by Виктория on 10-Mar-15.
 */
public class Contact {
    private Long id;
    private String name;
    private String address;
    private Collection<String> phone;
    private Collection<String> fax;
    private Collection<String> email;
    private Map<String, String> otherContacts;  // Map allows only one contact for each other contact type (e.g. only one Skype etc.). Is it correct?
    private Set<Long> groupIds;

    public Contact() {
        // TODO proper ID setup
    }

    public Contact(String name) {
        this();
        this.name = name;
    }

    public Contact(String name, String phone) {
        this(name);
        //this.phone = new HashSet<>();
        this.phone = new ArrayList<>();
        this.phone.add(phone);
    }

    public Contact(String name, String phone, String email) {
        this(name, phone);
        //this.email = new HashSet<>();
        this.email = new ArrayList<>();
        this.email.add(email);
    }

    public Long getContactID() {
        return id;
    }

    public void setContactID(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNewPhone(String phone) {
        if(this.phone == null) {
            //this.phone = new HashSet<>();
            this.phone = new ArrayList<>();
        }
        this.phone.add(phone);
    }

    public void setNewFax(String fax) {
        if(this.fax == null) {
            //this.fax = new HashSet<>();
            this.fax = new ArrayList<>();
        }
        this.fax.add(fax);
    }

    public void setNewEmail(String email) {
        if(this.email == null) {
            //this.email = new HashSet<>();
            this.email = new ArrayList<>();
        }
        this.email.add(email);
    }

    public void setNewOtherContact(String contactType, String contact) {
        if(this.otherContacts == null) {
            this.otherContacts = new HashMap<>();
        }
        this.otherContacts.put(contactType, contact);
    }

    public void setNewGroupId(Long id) {
        if(this.groupIds == null) {
            this.groupIds = new HashSet<>();
        }
        this.groupIds.add(id);
    }

    public void deletePhone(String phone) {
        this.phone.remove(phone);
        if(this.phone.isEmpty()) {
            this.phone = null;
        }
    }

    public void deleteFax(String fax) {
        this.fax.remove(fax);
        if(this.fax.isEmpty()) {
            this.fax = null;
        }
    }

    public void deleteEmail(String email) {
        this.email.remove(email);
        if(this.email.isEmpty()) {
            this.email = null;
        }
    }

    public void deleteOtherContact(String contactType, String contact) {
        this.otherContacts.remove(contactType, contact);
        if(this.otherContacts.isEmpty()) {
            this.otherContacts = null;
        }
    }

    public void deleteGroupId(Long id) {
        this.groupIds.remove(id);
        if(this.groupIds.isEmpty()) {
            this.groupIds = null;
        }
    }

    public Collection<String> getPhone() {
        return Collections.unmodifiableCollection(phone);
    }

    public Collection<String> getFax() {
        return Collections.unmodifiableCollection(fax);
    }

    public Collection<String> getEmail() {
        return Collections.unmodifiableCollection(email);
    }

    public Map<String, String> getOtherContacts() {
        return Collections.unmodifiableMap(otherContacts);
    }

    public Collection<Long> getGroupIds() {
        return Collections.unmodifiableCollection(groupIds);
    }

    // TODO make correct collection printing
    @Override
    public String toString() {
        return "Contact{" +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone=" + phone +
                ", fax=" + fax +
                ", email=" + email +
                ", otherContacts=" + otherContacts +
                ", groupIds=" + groupIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (address != null ? !address.equals(contact.address) : contact.address != null) return false;
        if (email != null ? !email.equals(contact.email) : contact.email != null) return false;
        if (fax != null ? !fax.equals(contact.fax) : contact.fax != null) return false;
        if (id != null ? !id.equals(contact.id) : contact.id != null) return false;
        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        if (otherContacts != null ? !otherContacts.equals(contact.otherContacts) : contact.otherContacts != null)
            return false;
        if (phone != null ? !phone.equals(contact.phone) : contact.phone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (otherContacts != null ? otherContacts.hashCode() : 0);
        return result;
    }
}
