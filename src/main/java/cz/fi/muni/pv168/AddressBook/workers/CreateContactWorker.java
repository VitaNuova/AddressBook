package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.Contact;
import cz.fi.muni.pv168.AddressBook.ContactManager;
import cz.fi.muni.pv168.AddressBook.ContactManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 11.5.15.
 */
public class CreateContactWorker extends SwingWorker<Void, Void> {

    private DataSource ds;
    private String name;
    private String address;
    private String phone;
    private String email;

    public CreateContactWorker(String name, String address, String phone, String email, DataSource ds) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.ds = ds;
    }

   @Override
   protected Void doInBackground() throws Exception {
        Contact contact = new Contact(name, address, phone, email);
        ContactManager manager = new ContactManagerImpl(ds);
        manager.createContact(contact);

       return null;
    }

    @Override
    protected void done() {
        try {
            get();
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("contact_created"));
        }
        catch(ExecutionException ex) {
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("contact_created_fail"));
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted ", ex);
        }
    }

}
