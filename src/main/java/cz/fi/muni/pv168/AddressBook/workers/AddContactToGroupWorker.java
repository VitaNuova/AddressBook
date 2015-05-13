package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.*;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 12.5.15.
 */
public class AddContactToGroupWorker extends SwingWorker<Void, Void> {

    private DataSource ds;
    private List<String> contactNames;
    private String groupName;
    private JFrame frame;

    public AddContactToGroupWorker(JFrame frame, DataSource ds, List<String> contactNames, String groupName) {
        this.ds = ds;
        this.contactNames = contactNames;
        this.groupName = groupName;
        this.frame = frame;
    }

    public Void doInBackground() {
        GroupManager groupManager = new GroupManagerImpl(ds);
        ContactManager contactManager = new ContactManagerImpl(ds);
        Group group = groupManager.findGroupByName(groupName);
        List<Long> groupMembers = group.getGroupMemberList();
        for(String name : contactNames) {
            Collection<Contact> contacts = contactManager.findContactByName(name);
            for(Contact contact : contacts) {
                groupMembers.add(contact.getId());
            }
        }
        group.setGroupMemberList(groupMembers);
        groupManager.updateGroup(group);
        return null;
    }

    public void done() {
        try {
            get();
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("contacts_added"));
            frame.setVisible(false);
            new ShowGroupWorker(ds).execute();
        }
        catch(ExecutionException ex) {
            JOptionPane.showMessageDialog(null,ResourceBundle.getBundle("texts").getString("error_contact_add"));
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted", ex);
        }
    }
}
