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
import java.util.ResourceBundle;
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
                JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("no_contacts"));
                showContactsFrame.setVisible(false);
            }
            else {
                for (Contact contact : contacts) {
                    JPanel nextContactPanel = new JPanel();
                    nextContactPanel.setLayout(new BoxLayout(nextContactPanel, BoxLayout.Y_AXIS));
                    if (contact.getName() != null) {
                        JPanel contactName = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                        JLabel contactNameLabel = new JLabel(ResourceBundle.getBundle("texts").getString("contact_name"));
                        contactNameLabel.setPreferredSize(new Dimension(200, 20));
                        JLabel contactNameValueLabel = new JLabel(contact.getName());
                        contactNameValueLabel.setPreferredSize(new Dimension(200, 20));
                        JButton updateButton = new JButton(ResourceBundle.getBundle("texts").getString("update"));
                        updateButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                JFrame updateContactFrame = new JFrame();
                                JPanel updateContactPanel = new JPanel();
                                updateContactPanel.setLayout(new BoxLayout(updateContactPanel, BoxLayout.X_AXIS));
                                JLabel updateContactLabel = new JLabel(ResourceBundle.getBundle("texts").getString("new_name"));
                                JTextField updateContactField = new JTextField();
                                JButton submitButton = new JButton(ResourceBundle.getBundle("texts").getString("submit"));
                                submitButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        new UpdateContactWorker(contactNameValueLabel, showContactsFrame, ds, contact.getId(), updateContactField.getText(), null, null, null).execute();
                                        updateContactFrame.setVisible(false);
                                    }
                                });
                                updateContactPanel.add(updateContactLabel);
                                updateContactPanel.add(updateContactField);
                                updateContactPanel.add(submitButton);
                                updateContactFrame.add(updateContactPanel);
                                updateContactFrame.setTitle(ResourceBundle.getBundle("texts").getString("update"));
                                updateContactFrame.setSize(400, 70);
                                updateContactFrame.setVisible(true);
                            }
                        });
                        contactName.add(contactNameLabel);
                        contactName.add(contactNameValueLabel);
                        contactName.add(updateButton);
                        nextContactPanel.add(contactName);
                    }
                    if (contact.getAddress() != null) {
                        JPanel contactAddress = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                        JLabel contactAddressLabel = new JLabel(ResourceBundle.getBundle("texts").getString("contact_address"));
                        contactAddressLabel.setPreferredSize(new Dimension(200, 20));
                        JLabel contactAddressValueLabel = new JLabel(contact.getAddress());
                        contactAddressValueLabel.setPreferredSize(new Dimension(200, 20));
                        JButton updateButton = new JButton(ResourceBundle.getBundle("texts").getString("update"));
                        updateButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                JFrame updateContactFrame = new JFrame();
                                JPanel updateContactPanel = new JPanel();
                                updateContactPanel.setLayout(new BoxLayout(updateContactPanel, BoxLayout.X_AXIS));
                                JLabel updateContactLabel = new JLabel(ResourceBundle.getBundle("texts").getString("new_address"));
                                JTextField updateContactField = new JTextField();
                                JButton submitButton = new JButton(ResourceBundle.getBundle("texts").getString("submit"));
                                submitButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        new UpdateContactWorker(contactAddressValueLabel, showContactsFrame, ds, contact.getId(), updateContactField.getText(), null, null, null).execute();
                                        updateContactFrame.setVisible(false);
                                    }
                                });
                                updateContactPanel.add(updateContactLabel);
                                updateContactPanel.add(updateContactField);
                                updateContactPanel.add(submitButton);
                                updateContactFrame.add(updateContactPanel);
                                updateContactFrame.setTitle(ResourceBundle.getBundle("texts").getString("update"));
                                updateContactFrame.setSize(400, 70);
                                updateContactFrame.setVisible(true);
                            }
                        });
                        contactAddress.add(contactAddressLabel);
                        contactAddress.add(contactAddressValueLabel);
                        contactAddress.add(updateButton);
                        nextContactPanel.add(contactAddress);
                    }
                    if (contact.getPhone() != null) {
                        Collection<String> phones = contact.getPhone();
                        for (String phone : phones) {
                            JPanel contactPhone = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                            JLabel contactPhoneLabel = new JLabel(ResourceBundle.getBundle("texts").getString("contact_phone"));
                            contactPhoneLabel.setPreferredSize(new Dimension(200, 20));
                            JLabel contactPhoneValueLabel = new JLabel(phone);
                            contactPhoneValueLabel.setPreferredSize(new Dimension(200, 20));
                            JButton updateButton = new JButton(ResourceBundle.getBundle("texts").getString("update"));
                            updateButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    JFrame updateContactFrame = new JFrame();
                                    JPanel updateContactPanel = new JPanel();
                                    updateContactPanel.setLayout(new BoxLayout(updateContactPanel, BoxLayout.X_AXIS));
                                    JLabel updateContactLabel = new JLabel(ResourceBundle.getBundle("texts").getString("new_phone"));
                                    JTextField updateContactField = new JTextField();
                                    JButton submitButton = new JButton(ResourceBundle.getBundle("texts").getString("submit"));
                                    submitButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            new UpdateContactWorker(contactPhoneValueLabel, showContactsFrame, ds, contact.getId(), updateContactField.getText(), null, null, null).execute();
                                            updateContactFrame.setVisible(false);
                                        }
                                    });
                                    updateContactPanel.add(updateContactLabel);
                                    updateContactPanel.add(updateContactField);
                                    updateContactPanel.add(submitButton);
                                    updateContactFrame.add(updateContactPanel);
                                    updateContactFrame.setTitle(ResourceBundle.getBundle("texts").getString("update"));
                                    updateContactFrame.setSize(400, 70);
                                    updateContactFrame.setVisible(true);
                                }
                            });
                            contactPhone.add(contactPhoneLabel);
                            contactPhone.add(contactPhoneValueLabel);
                            contactPhone.add(updateButton);
                            nextContactPanel.add(contactPhone);
                        }
                    }
                    if (contact.getEmail() != null) {
                        Collection<String> emails = contact.getEmail();
                        for (String email : emails) {
                            JPanel contactEmail = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                            JLabel contactEmailLabel = new JLabel(ResourceBundle.getBundle("texts").getString("contact_email"));
                            contactEmailLabel.setPreferredSize(new Dimension(200, 20));
                            JLabel contactEmailValueLabel = new JLabel(email);
                            contactEmailValueLabel.setPreferredSize(new Dimension(200, 20));
                            JButton updateButton = new JButton(ResourceBundle.getBundle("texts").getString("update"));
                            updateButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    JFrame updateContactFrame = new JFrame();
                                    JPanel updateContactPanel = new JPanel();
                                    updateContactPanel.setLayout(new BoxLayout(updateContactPanel, BoxLayout.X_AXIS));
                                    JLabel updateContactLabel = new JLabel(ResourceBundle.getBundle("texts").getString("new_email"));
                                    JTextField updateContactField = new JTextField();
                                    JButton submitButton = new JButton(ResourceBundle.getBundle("texts").getString("submit"));
                                    submitButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            new UpdateContactWorker(contactEmailValueLabel, showContactsFrame, ds, contact.getId(), updateContactField.getText(), null, null, null).execute();
                                            updateContactFrame.setVisible(false);
                                        }
                                    });
                                    updateContactPanel.add(updateContactLabel);
                                    updateContactPanel.add(updateContactField);
                                    updateContactPanel.add(submitButton);
                                    updateContactFrame.add(updateContactPanel);
                                    updateContactFrame.setTitle(ResourceBundle.getBundle("texts").getString("update"));
                                    updateContactFrame.setSize(400, 70);
                                    updateContactFrame.setVisible(true);
                                }
                            });
                            contactEmail.add(contactEmailLabel);
                            contactEmail.add(contactEmailValueLabel);
                            contactEmail.add(updateButton);
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
                showContactsFrame.setTitle(ResourceBundle.getBundle("texts").getString("show_contacts"));
                showContactsFrame.setSize(600, 400);
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
