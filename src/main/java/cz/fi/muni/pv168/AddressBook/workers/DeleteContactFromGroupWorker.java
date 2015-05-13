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
public class DeleteContactFromGroupWorker extends SwingWorker<Void, Void> {

    private DataSource ds;
    private JFrame frame;
    private String contactName;
    private String groupName;

    public DeleteContactFromGroupWorker(JFrame frame, DataSource ds, String contactName, String groupName) {
        this.ds = ds;
        this.frame = frame;
        this.contactName = contactName;
        this.groupName = groupName;
    }

    public Void doInBackground() {
        ContactManager contactManager = new ContactManagerImpl(ds);
        GroupManager groupManager = new GroupManagerImpl(ds);
        Collection<Contact> contacts = contactManager.findContactByName(contactName);
        Group group = groupManager.findGroupByName(groupName);
        List<Long> groupMemberList = group.getGroupMemberList();
        for(Contact contact : contacts) {
            groupMemberList.remove(contact.getId());
        }
        group.setGroupMemberList(groupMemberList);
        groupManager.updateGroup(group);
        return null;
    }

    public void done() {
        try {
            get();
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("member_deleted"));
            frame.setVisible(false);
            new ShowGroupWorker(ds).execute();
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted", ex);
        }
        catch(ExecutionException ex) {
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("member_deleted_fail"));
        }
    }
}
