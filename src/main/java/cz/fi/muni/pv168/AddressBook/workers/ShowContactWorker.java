package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.Contact;
import cz.fi.muni.pv168.AddressBook.ContactManager;
import cz.fi.muni.pv168.AddressBook.ContactManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 11.5.15.
 */
public class ShowContactWorker extends SwingWorker<Collection<Contact>, Void> {

    private DataSource ds;

    public ShowContactWorker(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public Collection<Contact> doInBackground() {

        ContactManager manager = new ContactManagerImpl(ds);
        Collection<Contact> contacts = manager.findAllContacts();

        return contacts;
    }

    @Override
    public void done() {
        try {
            Collection<Contact> contacts = get();
            JFrame showContactsFrame = new JFrame();
            JPanel showContactsPanel = new JPanel();
            showContactsPanel.setLayout(new BoxLayout(showContactsPanel, BoxLayout.Y_AXIS));

            if(contacts.size() == 0) {
                JOptionPane.showMessageDialog(null, "No contacts in address book");
                showContactsFrame.setVisible(false);
            }
            else {
                for (Contact contact : contacts) {
                    JPanel nextContactPanel = new JPanel();
                    nextContactPanel.setLayout(new BoxLayout(nextContactPanel, BoxLayout.Y_AXIS));
                    if (contact.getName() != null) {
                        JPanel contactName = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                        JLabel contactNameLabel = new JLabel("Contact name:");
                        JLabel contactNameValueLabel = new JLabel(contact.getName());
                        contactName.add(contactNameLabel);
                        contactName.add(Box.createRigidArea(new Dimension(100, 0)));
                        contactName.add(contactNameValueLabel);
                        nextContactPanel.add(contactName);
                    }
                    if (contact.getAddress() != null) {
                        JPanel contactAddress = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                        JLabel contactAddressLabel = new JLabel("Contact address: ");
                        JLabel contactAddressValueLabel = new JLabel(contact.getAddress());
                        contactAddress.add(contactAddressLabel);
                        contactAddress.add(Box.createRigidArea(new Dimension(75, 0)));
                        contactAddress.add(contactAddressValueLabel);
                        nextContactPanel.add(contactAddress);
                    }
                    if (contact.getPhone() != null) {
                        Collection<String> phones = contact.getPhone();
                        int index = 1;
                        for (String phone : phones) {
                            JPanel contactPhone = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                            JLabel contactPhoneLabel = new JLabel("Contact phone " + index + ": ");
                            index++;
                            JLabel contactPhoneValueLabel = new JLabel(phone);
                            contactPhone.add(contactPhoneLabel);
                            contactPhone.add(Box.createRigidArea(new Dimension(75, 0)));
                            contactPhone.add(contactPhoneValueLabel);
                            nextContactPanel.add(contactPhone);
                        }
                    }
                    if (contact.getEmail() != null) {
                        Collection<String> emails = contact.getEmail();
                        int index = 1;
                        for (String email : emails) {
                            JPanel contactEmail = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                            JLabel contactEmailLabel = new JLabel("Contact email " + index + ": ");
                            index++;
                            JLabel contactEmailValueLabel = new JLabel(email);
                            contactEmail.add(contactEmailLabel);
                            contactEmail.add(Box.createRigidArea(new Dimension(83, 0)));
                            contactEmail.add(contactEmailValueLabel);
                            nextContactPanel.add(contactEmail);
                        }
                    }
                    showContactsPanel.add(nextContactPanel);
                    showContactsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                }
                JPanel buttonPanel = new JPanel();
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showContactsFrame.setVisible(false);
                    }
                });
                buttonPanel.add(okButton);
                showContactsPanel.add(buttonPanel);
                showContactsFrame.add(showContactsPanel);
                showContactsFrame.setTitle("Show contacts");
                showContactsFrame.setSize(400, 400);
                showContactsFrame.setVisible(true);
            }
        }
        catch(ExecutionException ex) {
            throw new RuntimeException("Contact retrieval failed", ex);
        }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted", ex);
        }
    }
}
