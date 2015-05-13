package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.AddressBookManager;
import cz.fi.muni.pv168.AddressBook.AddressBookManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 11.5.15.
 */
public class ListGroupWorker extends SwingWorker<Object[], Void> {

    private DataSource ds;

    public ListGroupWorker(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public Object[] doInBackground() {

        AddressBookManager manager = new AddressBookManagerImpl(ds);
        List<String> groups = manager.listGroupsByPerson();
        Object[] nameArray = null;
        if(groups != null) {
            nameArray = groups.toArray();
        }
        return nameArray;
    }

    @Override
    public void done() {
        try {
            JFrame deleteGroupFrame = new JFrame();
            JPanel deleteGroupPanel = new JPanel();
            deleteGroupPanel.setLayout(new BoxLayout(deleteGroupPanel, BoxLayout.Y_AXIS));

            JPanel topPanel = new JPanel();
            JLabel deleteContactLabel = new JLabel(ResourceBundle.getBundle("texts").getString("select_groups"));

            JPanel listPanel = new JPanel();
            JPanel buttonPanel = new JPanel();

            Object[] groupNames = get();
            if(groupNames == null) {
                JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("no groups"));
                deleteGroupFrame.setVisible(false);
            }
            else {
                JList optionList = new JList(groupNames);
                optionList.setFixedCellWidth(100);
                optionList.setFixedCellHeight(30);

                optionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                optionList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                optionList.setVisibleRowCount(-1);


                JButton submitButton = new JButton(ResourceBundle.getBundle("texts").getString("delete"));
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List<String> names = optionList.getSelectedValuesList();
                        new DeleteGroupWorker(ds, names).execute();
                        deleteGroupFrame.setVisible(false);
                    }
                });

                listPanel.add(optionList);
                topPanel.add(deleteContactLabel);
                buttonPanel.add(submitButton);
                deleteGroupPanel.add(topPanel);
                deleteGroupPanel.add(listPanel);
                deleteGroupPanel.add(buttonPanel);
                deleteGroupFrame.add(deleteGroupPanel);
                deleteGroupFrame.setTitle(ResourceBundle.getBundle("texts").getString("delete_group"));
                deleteGroupFrame.setSize(250, 300);
                deleteGroupFrame.setVisible(true);
            }
        }
        catch(ExecutionException ex) {
            throw new RuntimeException("Error retrieving groups", ex);
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted", ex);
        }
    }

}
