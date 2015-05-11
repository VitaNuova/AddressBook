package cz.fi.muni.pv168.AddressBook;

import cz.fi.muni.pv168.AddressBook.workers.*;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

/**
 * Created by viki on 25.3.15.
 */
public class Application extends JFrame {

    public Application() {
        try {
            //Class.forName("org.apache.derby.jdbc.ClientDriver");
            Properties conf = new Properties();
            conf.load(Application.class.getResourceAsStream("/config.properties"));
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl(conf.getProperty("jdbc.url"));
            ds.setUsername(conf.getProperty("jdbc.user"));
            ds.setPassword(conf.getProperty("jdbc.password"));
            initGUI(ds);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Unable to connect to db");
            System.exit(1);
        }
    }

    public void initGUI(DataSource ds) {

        JPanel basic = new JPanel();
        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));

        JPanel top = new JPanel();
        top.setBackground(Color.RED);
        JLabel title = new JLabel("Choose the action ");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        top.add(title);

        JPanel createButtons = new JPanel();
        createButtons.setLayout(new BoxLayout(createButtons, BoxLayout.X_AXIS));
        JButton createContactButton = new JButton("Create contact");
        JButton createGroupButton = new JButton("Create group");
        createButtons.add(Box.createHorizontalGlue());
        createButtons.add(createContactButton);
        createButtons.add(Box.createHorizontalGlue());
        createButtons.add(createGroupButton);
        createButtons.add(Box.createHorizontalGlue());

        ActionListener createContactListener = createListenerForCreateContactButton(ds);
        createContactButton.addActionListener(createContactListener);
        ActionListener createGroupListener = createListenerForCreateGroupButton(ds);
        createGroupButton.addActionListener(createGroupListener);

        JPanel changeButtons = new JPanel();
        changeButtons.setLayout(new BoxLayout(changeButtons, BoxLayout.X_AXIS));
        JButton changeContactButton = new JButton("Update contact");
        JButton changeGroupButton = new JButton("Update group");
        changeButtons.add(Box.createHorizontalGlue());
        changeButtons.add(changeContactButton);
        changeButtons.add(Box.createHorizontalGlue());
        changeButtons.add(changeGroupButton);
        changeButtons.add(Box.createHorizontalGlue());

        JPanel deleteButtons = new JPanel();
        deleteButtons.setLayout(new BoxLayout(deleteButtons, BoxLayout.X_AXIS));
        JButton deleteContactButton = new JButton("Delete contact");
        JButton deleteGroupButton = new JButton("Delete group");
        deleteButtons.add(Box.createHorizontalGlue());
        deleteButtons.add(deleteContactButton);
        deleteButtons.add(Box.createHorizontalGlue());
        deleteButtons.add(deleteGroupButton);
        deleteButtons.add(Box.createHorizontalGlue());

        deleteContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ListContactWorker(ds).execute();
            }
        });
        deleteGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ListGroupWorker(ds).execute();
            }
        });

        JPanel showButtons = new JPanel();
        showButtons.setLayout(new BoxLayout(showButtons, BoxLayout.X_AXIS));
        JButton showContactButton = new JButton("Show contacts");
        JButton showGroupButton = new JButton("Show groups");
        showButtons.add(Box.createHorizontalGlue());
        showButtons.add(showContactButton);
        showButtons.add(Box.createHorizontalGlue());
        showButtons.add(showGroupButton);
        showButtons.add(Box.createHorizontalGlue());
        showContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ShowContactWorker(ds).execute();
            }
        });
        showGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ShowGroupWorker(ds).execute();
            }
        });

        JPanel findButtons = new JPanel();
        findButtons.setLayout(new BoxLayout(findButtons, BoxLayout.X_AXIS));
        JButton findContactButton = new JButton("Find contact");
        JButton findGroupButton = new JButton("Find group");
        findButtons.add(Box.createHorizontalGlue());
        findButtons.add(findContactButton);
        findButtons.add(Box.createHorizontalGlue());
        findButtons.add(findGroupButton);
        findButtons.add(Box.createHorizontalGlue());

        basic.add(top);
        basic.add(Box.createVerticalGlue());
        basic.add(createButtons);
        basic.add(Box.createVerticalGlue());
        basic.add(changeButtons);
        basic.add(Box.createVerticalGlue());
        basic.add(deleteButtons);
        basic.add(Box.createVerticalGlue());
        basic.add(showButtons);
        basic.add(Box.createVerticalGlue());
        basic.add(findButtons);
        basic.add(Box.createVerticalGlue());

        add(basic);
        setTitle("Address Book");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Application app = new Application();
                app.setVisible(true);
            }
        });
    }

    private ActionListener createListenerForCreateContactButton(DataSource ds) {

        ActionListener addContact = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame createContactFrame = new JFrame("Create contact");

                JPanel createContactPanel = new JPanel();
                createContactPanel.setLayout(new BoxLayout(createContactPanel, BoxLayout.Y_AXIS));

                JPanel addNamePanel = new JPanel();
                JPanel addAddressPanel = new JPanel();
                JPanel addPhonePanel = new JPanel();
                JPanel addEmailPanel = new JPanel();

                addNamePanel.setLayout(new BoxLayout(addNamePanel, BoxLayout.X_AXIS));
                addAddressPanel.setLayout(new BoxLayout(addAddressPanel, BoxLayout.X_AXIS));
                addPhonePanel.setLayout(new BoxLayout(addPhonePanel, BoxLayout.X_AXIS));
                addEmailPanel.setLayout(new BoxLayout(addEmailPanel, BoxLayout.X_AXIS));

                JLabel addNameLabel = new JLabel("Enter name:");
                JLabel addAddressLabel = new JLabel("Enter address: ");
                JLabel addPhoneLabel = new JLabel("Enter phone: ");
                JLabel addEmailLabel = new JLabel("Enter email: ");

                JTextField addNameArea = new JTextField();
                JTextField addAddressArea = new JTextField();
                JTextField addPhoneArea = new JTextField();
                JTextField addEmailArea = new JTextField();

                JButton submitButton = new JButton("Submit ");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String name = addNameArea.getText();
                        if(name.length() == 0) {
                            name = null;
                        }
                        String address = addAddressArea.getText();
                        if(address.length() == 0) {
                            address = null;
                        }
                        String phone = addPhoneArea.getText();
                        if(phone.length() == 0) {
                            phone = null;
                        }
                        String email = addEmailArea.getText();
                        if(email.length() == 0) {
                            email = null;
                        }
                        new CreateContactWorker(name, address, phone, email, ds).execute();
                        createContactFrame.setVisible(false);
                    }
                });

                addNamePanel.add(addNameLabel);
                addNamePanel.add(addNameArea);
                addAddressPanel.add(addAddressLabel);
                addAddressPanel.add(addAddressArea);
                addPhonePanel.add(addPhoneLabel);
                addPhonePanel.add(addPhoneArea);
                addEmailPanel.add(addEmailLabel);
                addEmailPanel.add(addEmailArea);

                createContactPanel.add(addNamePanel);
                createContactPanel.add(addAddressPanel);
                createContactPanel.add(addPhonePanel);
                createContactPanel.add(addEmailPanel);
                createContactPanel.add(submitButton);

                createContactFrame.add(createContactPanel);
                createContactFrame.setTitle("Add contact");
                createContactFrame.setSize(450, 300);
                createContactFrame.setVisible(true);
            }
        };
        return addContact;
    }

    private ActionListener createListenerForCreateGroupButton(DataSource ds) {

        ActionListener addGroup = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame createGroupFrame = new JFrame();

                JPanel createGroupPanel = new JPanel();
                createGroupPanel.setLayout(new BoxLayout(createGroupPanel, BoxLayout.Y_AXIS));

                JPanel groupNamePanel = new JPanel();
                groupNamePanel.setLayout(new BoxLayout(groupNamePanel, BoxLayout.X_AXIS));

                JPanel buttonsPanel = new JPanel();
                buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

                JLabel groupNameLabel = new JLabel("Enter group name: ");
                JTextField groupNameField = new JTextField();

                JButton createGroupButton = new JButton("Create group");
                createGroupButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String groupName = groupNameField.getText();
                        if(groupName.length() == 0) {
                            groupName = null;
                        }
                        new CreateGroupWorker(groupName, ds).execute();
                        createGroupFrame.setVisible(false);
                    }
                });

                buttonsPanel.add(createGroupButton);
                groupNamePanel.add(groupNameLabel);
                groupNamePanel.add(groupNameField);

                createGroupPanel.add(groupNamePanel);
                createGroupPanel.add(buttonsPanel);

                createGroupFrame.add(createGroupPanel);
                createGroupFrame.setTitle("Add group");
                createGroupFrame.setSize(450, 100);
                createGroupFrame.setVisible(true);
            }
        };
        return addGroup;
    }
}