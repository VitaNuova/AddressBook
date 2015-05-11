package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.*;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 11.5.15.
 */
public class ShowGroupWorker extends SwingWorker<HashMap<String, List<String>>, Void> {

    private DataSource ds;

    public ShowGroupWorker(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public HashMap<String, List<String>> doInBackground() {
        AddressBookManager manager = new AddressBookManagerImpl(ds);
        GroupManager groupManager = new GroupManagerImpl(ds);
        List<String> groupNames = manager.listGroupsByPerson();
        HashMap<String, List<String>> groupsWithContacts = new HashMap<>();
        if(groupNames != null) {
            for (String name : groupNames) {
                Group group = groupManager.findGroupByName(name);
                List<Contact> contacts = manager.listContactsByGroup(group);
                List<String> contactNames = new ArrayList<>();
                if(contacts != null) {
                    for(Contact contact: contacts) {
                        contactNames.add(contact.getName());
                    }
                }
                groupsWithContacts.put(name, contactNames);
            }
        }
        return groupsWithContacts;
    }

    @Override
    public void done() {
        try {
            HashMap<String, List<String>> groups = get();
            JFrame showGroupFrame = new JFrame();
            JPanel showGroupPanel = new JPanel();
            showGroupPanel.setLayout(new BoxLayout(showGroupPanel, BoxLayout.Y_AXIS));
            if(groups.size() == 0) {
                JOptionPane.showMessageDialog(null, "No groups in address book");
            }
            else {
                Iterator it = groups.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    JPanel nextGroupPanel = new JPanel();
                    nextGroupPanel.setLayout(new BoxLayout(nextGroupPanel, BoxLayout.Y_AXIS));
                    JPanel groupName = new JPanel();
                    groupName.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
                    JLabel groupNameLabel = new JLabel("Group name: ");
                    JLabel groupNameValueLabel = new JLabel(pair.getKey().toString());
                    groupName.add(groupNameLabel);
                    groupName.add(Box.createRigidArea(new Dimension(100, 0)));
                    groupName.add(groupNameValueLabel);
                    nextGroupPanel.add(groupName);

                    List<String> contactNames = (List<String>)pair.getValue();
                    int index = 1;
                    for(String name : contactNames) {
                        JPanel contactInGroup = new JPanel();
                        contactInGroup.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
                        JLabel contactName = new JLabel("Contact name " + index + ": ");
                        index++;
                        JLabel contactNameValue = new JLabel(name);
                        contactInGroup.add(contactName);
                        contactInGroup.add(Box.createRigidArea(new Dimension(100, 0)));
                        contactInGroup.add(contactNameValue);
                        nextGroupPanel.add(contactInGroup);
                    }
                    showGroupPanel.add(nextGroupPanel);
                }
                JPanel buttonPanel = new JPanel();
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showGroupFrame.setVisible(false);
                    }
                });
                buttonPanel.add(okButton);
                showGroupPanel.add(buttonPanel);
                showGroupFrame.add(showGroupPanel);
                showGroupFrame.setTitle("Show groups");
                showGroupFrame.setSize(450, 300);
                showGroupFrame.setVisible(true);
            }
        }
        catch (ExecutionException ex) {
            throw new RuntimeException("Unable to retrieve groups", ex);
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted", ex);
        }
    }
}
