package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.Contact;
import cz.fi.muni.pv168.AddressBook.Group;
import cz.fi.muni.pv168.AddressBook.GroupManager;
import cz.fi.muni.pv168.AddressBook.GroupManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 12.5.15.
 */
public class UpdateGroupWorker extends SwingWorker<Void, Void> {

    private JFrame frame;
    private JLabel label;
    private DataSource ds;
    private String newName;
    private String oldName;

    public UpdateGroupWorker(JFrame frame, JLabel label, DataSource ds, String newName, String oldName) {
        this.frame = frame;
        this.label = label;
        this.ds = ds;
        this.newName = newName;
        this.oldName = oldName;
    }

    public Void doInBackground() {
        if(newName != null && oldName != null) {
            GroupManager groupManager = new GroupManagerImpl(ds);
            Group group = groupManager.findGroupByName(oldName);
            group.setGroupName(newName);
            groupManager.updateGroup(group);
        }
            return null;
    }

    public void done() {
        try {
            get();
            label.setText(newName);
            frame.revalidate();
            frame.repaint();
        }
        catch(ExecutionException ex) {
            JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("update_failed"));
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted", ex);
        }
    }


}
