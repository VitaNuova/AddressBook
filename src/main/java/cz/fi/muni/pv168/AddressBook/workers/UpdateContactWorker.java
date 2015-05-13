package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.Contact;
import cz.fi.muni.pv168.AddressBook.ContactManager;
import cz.fi.muni.pv168.AddressBook.ContactManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 12.5.15.
 */
public class UpdateContactWorker extends SwingWorker<UpdateContactWorker.Type, Void> {

    private DataSource ds;
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private JFrame frame;
    private JLabel label;

    public UpdateContactWorker(JLabel label, JFrame frame, DataSource ds, Long id, String name, String address, String phone, String email) {
        this.ds = ds;
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.frame = frame;
        this.label = label;
    }

    public enum Type {NAME, ADDRESS, PHONE, EMAIL};

    public Type doInBackground() {
        ContactManager manager = new ContactManagerImpl(ds);
        Contact contact = manager.findContactById(id);
        Type returnVal = null;
        if(name != null) {
            contact.setName(name);
            returnVal =  Type.NAME;
        }
        if(address != null) {
            contact.setAddress(address);
            returnVal = Type.ADDRESS;
        }
        if(phone != null) {
            Collection<String> phones = contact.getPhone();
            for(String phone : phones) {
                contact.deletePhone(phone);
            }
            contact.setNewPhone(phone);
            returnVal =  Type.PHONE;
        }
        if(email != null) {
            Collection<String> emails = contact.getEmail();
            for(String email : emails) {
                contact.deleteEmail(email);
            }
            contact.setNewEmail(email);
            returnVal = Type.EMAIL;
        }
        manager.updateContact(contact);
        return returnVal;
    }

    public void done() {
        try {
            Type result = get();
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("updated"));
            if(result == Type.NAME) {
                label.setText(name);
            }
            else if(result == Type.ADDRESS) {
                label.setText(address);
            }
            else if(result == Type.PHONE) {
                label.setText(phone);
            }
            else if(result == Type.EMAIL) {
                label.setText(email);
            }
            frame.revalidate();
            frame.repaint();
        }
        catch(ExecutionException ex) {
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("update_failed"));
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted");
        }
    }
}
