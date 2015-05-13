package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.*;

import javax.sql.DataSource;
import javax.swing.*;
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
        HashMap<String, List<String>> groupsWithContacts = new HashMap<String, List<String>>();
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
                JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("no_groups"));
            }
            else {
                Iterator it = groups.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    JPanel nextGroupPanel = new JPanel();
                    nextGroupPanel.setLayout(new BoxLayout(nextGroupPanel, BoxLayout.Y_AXIS));
                    JPanel groupName = new JPanel();
                    groupName.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
                    JLabel groupNameLabel = new JLabel(ResourceBundle.getBundle("texts").getString("group_name"));
                    groupNameLabel.setPreferredSize(new Dimension(100, 20));
                    JLabel groupNameValueLabel = new JLabel(pair.getKey().toString());
                    groupNameValueLabel.setPreferredSize(new Dimension(100, 20));
                    JButton updateNameButton = new JButton(ResourceBundle.getBundle("texts").getString("update"));
                    updateNameButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JFrame updateGroupFrame = new JFrame();
                            JPanel updateGroupPanel = new JPanel();
                            updateGroupPanel.setLayout(new BoxLayout(updateGroupPanel, BoxLayout.X_AXIS));
                            JLabel updateGroupLabel = new JLabel(ResourceBundle.getBundle("texts").getString("new_name"));
                            JTextField updateGroupField = new JTextField();
                            JButton submitButton = new JButton(ResourceBundle.getBundle("texts").getString("submit"));
                            submitButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String newName = updateGroupField.getText();
                                    new UpdateGroupWorker(showGroupFrame, groupNameValueLabel, ds, newName, pair.getKey().toString()).execute();
                                    updateGroupFrame.setVisible(false);
                                }
                            });
                            updateGroupPanel.add(updateGroupLabel);
                            updateGroupPanel.add(updateGroupField);
                            updateGroupPanel.add(submitButton);
                            updateGroupFrame.add(updateGroupPanel);
                            updateGroupFrame.setTitle(ResourceBundle.getBundle("texts").getString("update"));
                            updateGroupFrame.setSize(400, 70);
                            updateGroupFrame.setVisible(true);
                        }
                    });
                    JButton addMemberButton = new JButton(ResourceBundle.getBundle("texts").getString("add_contact"));
                    addMemberButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new ListContactWorker(showGroupFrame, ds, "add", pair.getKey().toString()).execute();
                        }
                    });
                    groupName.add(groupNameLabel);
                    groupName.add(groupNameValueLabel);
                    groupName.add(updateNameButton);
                    groupName.add(addMemberButton);
                    nextGroupPanel.add(groupName);

                    List<String> contactNames = (List<String>)pair.getValue();
                    for(String name : contactNames) {
                        JPanel contactInGroup = new JPanel();
                        contactInGroup.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
                        JLabel contactName = new JLabel(ResourceBundle.getBundle("texts").getString("contact_name"));
                        contactName.setPreferredSize(new Dimension(200, 20));
                        JLabel contactNameValue = new JLabel(name);
                        contactNameValue.setPreferredSize(new Dimension(100, 20));
                        JButton deleteButton = new JButton(ResourceBundle.getBundle("texts").getString("delete"));
                        deleteButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                new DeleteContactFromGroupWorker(showGroupFrame, ds, name, pair.getKey().toString()).execute();
                            }
                        });
                        contactInGroup.add(contactName);
                        contactInGroup.add(contactNameValue);
                        contactInGroup.add(deleteButton);
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
                showGroupFrame.setTitle(ResourceBundle.getBundle("texts").getString("show_groups"));
                showGroupFrame.setSize(450, 300);
                showGroupFrame.setVisible(true);
            }
        }
        catch (ExecutionException ex) {
            throw new RuntimeException(ResourceBundle.getBundle("texts").getString("no_groups"), ex);
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted", ex);
        }
    }
}
