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
    private Collection<Long> groupIds;


    public Contact() {
        this.phone = new HashSet<>();
        this.fax = new HashSet<>();
        this.email = new HashSet<>();
        this.otherContacts = new HashMap<>();
        this.groupIds = new HashSet<>();
    }

    public Contact(String name) {
        this();
        this.name = name;
    }

    public Contact(String name, String phone) {
        this(name);
        this.phone.add(phone);
    }

    public Contact(String name, String phone, String email) {
        this(name, phone);
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
        this.phone.add(phone);
    }

    public void setNewFax(String fax) {
        this.fax.add(fax);
    }

    public void setNewEmail(String email) {
        this.email.add(email);
    }

    public void setNewOtherContact(String contactType, String contact) {
        this.otherContacts.put(contactType, contact);
    }

    public void setNewGroupId(Long id) {
        this.groupIds.add(id);
    }

    public void deletePhone(String phone) {
        this.phone.remove(phone);
    }

    public void deleteFax(String fax) {
        this.fax.remove(fax);
    }

    public void deleteEmail(String email) {
        this.email.remove(email);
    }

    public void deleteOtherContact(String contactType, String contact) {
        this.otherContacts.remove(contactType, contact);
    }

    public void deleteGroupId(Long id) {
        this.groupIds.remove(id);
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
