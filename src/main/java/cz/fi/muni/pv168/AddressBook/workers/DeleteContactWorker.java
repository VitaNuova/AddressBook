package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.Contact;
import cz.fi.muni.pv168.AddressBook.ContactManager;
import cz.fi.muni.pv168.AddressBook.ContactManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 11.5.15.
 */
public class DeleteContactWorker extends SwingWorker<Void, Void> {

    private DataSource ds;
    private List<String> names;

    public DeleteContactWorker(DataSource ds, List<String> names) {
        this.ds = ds;
        this.names = names;
    }

    @Override
    public Void doInBackground() {

        ContactManager manager = new ContactManagerImpl(ds);
        for(String name : names) {
            Collection<Contact> contacts = manager.findContactByName(name);
            for(Contact contact : contacts) {
                manager.deleteContact(contact);
            }
        }
        return null;
    }

    @Override
    public void done() {
        try {
            get();
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("contact_deleted"));
        }
        catch(ExecutionException ex) {
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("contact_deleted_fail"));
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupred", ex);
        }
    }
}
