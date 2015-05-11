package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.Group;
import cz.fi.muni.pv168.AddressBook.GroupManager;
import cz.fi.muni.pv168.AddressBook.GroupManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 11.5.15.
 */
public class DeleteGroupWorker extends SwingWorker<Void, Void> {

    private DataSource ds;
    private List<String> names;

    public DeleteGroupWorker(DataSource ds, List<String> names) {
        this.ds = ds;
        this.names = names;
    }

    @Override
    public Void doInBackground() {

        GroupManager manager = new GroupManagerImpl(ds);
        for(String name : names) {
            Group group = manager.findGroupByName(name);
            manager.deleteGroup(group);
        }
        return null;
    }

    @Override
    public void done() {
        try {
            get();
            JOptionPane.showMessageDialog(null, "Group deletion successful");
        }
        catch(ExecutionException ex) {
            JOptionPane.showMessageDialog(null, "Group deletion failed");
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupred", ex);
        }
    }
}
