package cz.fi.muni.pv168.AddressBook;

import oracle.jrockit.jfr.StringConstantPool;

import java.util.Collection;
import java.util.Map;

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
    private Map<String, String> otherContacts;

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
}
