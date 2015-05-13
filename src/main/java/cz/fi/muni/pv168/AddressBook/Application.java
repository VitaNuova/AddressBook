package cz.fi.muni.pv168.AddressBook;

import cz.fi.muni.pv168.AddressBook.workers.*;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by viki on 25.3.15.
 */
public class Application extends JFrame {

    public Application() {
        try {
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
            System.exit(1);
        }
    }

    public void initGUI(DataSource ds) {

        JPanel basic = new JPanel();
        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));

        JPanel top = new JPanel();
        top.setBackground(Color.RED);
        JLabel title = new JLabel(ResourceBundle.getBundle("texts").getString("action"));
        title.setFont(new Font("Serif", Font.BOLD, 20));
        top.add(title);

        JPanel createButtons = addCreateButtons(ds);
        JPanel deleteButtons  = addDeleteButtons(ds);
        JPanel showButtons = addShowButtons(ds);

        JPanel exitButtonPanel = new JPanel();
        JButton exitButton = new JButton(ResourceBundle.getBundle("texts").getString("exit"));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitButtonPanel.add(exitButton);

        basic.add(top);
        basic.add(Box.createVerticalGlue());
        basic.add(createButtons);
        basic.add(Box.createVerticalGlue());
        basic.add(deleteButtons);
        basic.add(Box.createVerticalGlue());
        basic.add(showButtons);
        basic.add(Box.createVerticalGlue());
        basic.add(exitButtonPanel);
        basic.add(Box.createVerticalGlue());

        add(basic);
        setTitle(ResourceBundle.getBundle("texts").getString("address_book"));
        setSize(550, 300);
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
                JFrame createContactFrame = new JFrame(ResourceBundle.getBundle("texts").getString("create_contact"));

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

                JLabel addNameLabel = new JLabel(ResourceBundle.getBundle("texts").getString("enter_name"));
                JLabel addAddressLabel = new JLabel(ResourceBundle.getBundle("texts").getString("enter_address"));
                JLabel addPhoneLabel = new JLabel(ResourceBundle.getBundle("texts").getString("enter_phone"));
                JLabel addEmailLabel = new JLabel(ResourceBundle.getBundle("texts").getString("enter_email"));

                JTextField addNameArea = new JTextField();
                JTextField addAddressArea = new JTextField();
                JTextField addPhoneArea = new JTextField();
                JTextField addEmailArea = new JTextField();

                JButton submitButton = new JButton(ResourceBundle.getBundle("texts").getString("submit"));
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
                createContactFrame.setTitle(ResourceBundle.getBundle("texts").getString("add_contact"));
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

                JLabel groupNameLabel = new JLabel(ResourceBundle.getBundle("texts").getString("enter_group_name"));
                JTextField groupNameField = new JTextField();

                JButton createGroupButton = new JButton(ResourceBundle.getBundle("texts").getString("create_group"));
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
                createGroupFrame.setTitle(ResourceBundle.getBundle("texts").getString("add_group"));
                createGroupFrame.setSize(450, 100);
                createGroupFrame.setVisible(true);
            }
        };
        return addGroup;
    }

    private JPanel addCreateButtons(DataSource ds) {

        JPanel createButtons = new JPanel();
        createButtons.setLayout(new BoxLayout(createButtons, BoxLayout.X_AXIS));
        JButton createContactButton = new JButton(ResourceBundle.getBundle("texts").getString("create_contact"));
        JButton createGroupButton = new JButton(ResourceBundle.getBundle("texts").getString("create_group"));
        createButtons.add(Box.createHorizontalGlue());
        createButtons.add(createContactButton);
        createButtons.add(Box.createHorizontalGlue());
        createButtons.add(createGroupButton);
        createButtons.add(Box.createHorizontalGlue());
        ActionListener createContactListener = createListenerForCreateContactButton(ds);
        createContactButton.addActionListener(createContactListener);
        ActionListener createGroupListener = createListenerForCreateGroupButton(ds);
        createGroupButton.addActionListener(createGroupListener);

        return createButtons;
    }

    private JPanel addDeleteButtons(DataSource ds) {
        JPanel deleteButtons = new JPanel();
        deleteButtons.setLayout(new BoxLayout(deleteButtons, BoxLayout.X_AXIS));
        JButton deleteContactButton = new JButton(ResourceBundle.getBundle("texts").getString("delete_contact"));
        JButton deleteGroupButton = new JButton(ResourceBundle.getBundle("texts").getString("delete_group"));
        deleteButtons.add(Box.createHorizontalGlue());
        deleteButtons.add(deleteContactButton);
        deleteButtons.add(Box.createHorizontalGlue());
        deleteButtons.add(deleteGroupButton);
        deleteButtons.add(Box.createHorizontalGlue());

        deleteContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ListContactWorker(null, ds, "delete", null).execute();
            }
        });
        deleteGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ListGroupWorker(ds).execute();
            }
        });
        return deleteButtons;
    }

    private JPanel addShowButtons(DataSource ds) {
        JPanel showButtons = new JPanel();
        showButtons.setLayout(new BoxLayout(showButtons, BoxLayout.X_AXIS));
        JButton showContactButton = new JButton(ResourceBundle.getBundle("texts").getString("show_contacts"));
        JButton showGroupButton = new JButton(ResourceBundle.getBundle("texts").getString("show_groups"));
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
        return showButtons;
    }

}