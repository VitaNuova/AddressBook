package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.Group;
import cz.fi.muni.pv168.AddressBook.GroupManager;
import cz.fi.muni.pv168.AddressBook.GroupManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 11.5.15.
 */
public class CreateGroupWorker extends SwingWorker<Void, Void> {

    private String groupName;
    private DataSource ds;

    public CreateGroupWorker(String groupName, DataSource ds) {
            this.groupName = groupName;
            this.ds = ds;
    }
    @Override
    public Void doInBackground() {

        Group group = new Group(groupName);
        GroupManager manager = new GroupManagerImpl(ds);
        manager.createGroup(group);

        return null;
    }

    @Override
    public void done() {
        try {
            get();
            JOptionPane.showMessageDialog(null, "Group successfully created");
        }
        catch(ExecutionException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Group creation failed");
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted", ex);
        }
    }
}
