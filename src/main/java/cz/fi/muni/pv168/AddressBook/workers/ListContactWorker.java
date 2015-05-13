package cz.fi.muni.pv168.AddressBook.workers;

import cz.fi.muni.pv168.AddressBook.AddressBookManager;
import cz.fi.muni.pv168.AddressBook.AddressBookManagerImpl;
import cz.fi.muni.pv168.AddressBook.Contact;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by viki on 11.5.15.
 */
public class ListContactWorker extends SwingWorker<Object[], Void> {

    private DataSource ds;
    private String option;
    private String groupName;
    private JFrame frame;

    public ListContactWorker(JFrame frame, DataSource ds, String option, String groupName) {
        this.ds = ds;
        this.option = option;
        this.groupName = groupName;
        this.frame = frame;
    }

    @Override
    public Object[] doInBackground() {

        AddressBookManager manager = new AddressBookManagerImpl(ds);
        List<Contact> contacts = manager.listContactsByPerson();
        Object[] nameArray = null;
        if(contacts != null) {
            List<String> names = new ArrayList<>();
            for (Contact contact : contacts) {
                if (contact.getName() != null) {
                    names.add(contact.getName());
                }
            }
            nameArray = names.toArray();
        }
        return nameArray;
    }
    @Override
    public void done() {

       try {
           JFrame deleteContactFrame = new JFrame();
           JPanel deleteContactPanel = new JPanel();
           deleteContactPanel.setLayout(new BoxLayout(deleteContactPanel, BoxLayout.Y_AXIS));

           JPanel topPanel = new JPanel();
           JLabel deleteContactLabel = new JLabel(ResourceBundle.getBundle("texts").getString("select_contact"));

           JPanel listPanel = new JPanel();
           JPanel buttonPanel = new JPanel();

           Object[] contactNames = get();
           if(contactNames == null) {
               JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("texts").getString("no_contacts"));
               deleteContactFrame.setVisible(false);
           }
           else {
               JList optionList = new JList(contactNames);
               optionList.setFixedCellWidth(100);
               optionList.setFixedCellHeight(30);

               optionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
               optionList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
               optionList.setVisibleRowCount(-1);


               JButton submitButton = new JButton(ResourceBundle.getBundle("texts").getString("do"));
               submitButton.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       List<String> names = optionList.getSelectedValuesList();
                       if (option.equals("delete")) {
                           new DeleteContactWorker(ds, names).execute();
                       } else if (option.equals("add")) {
                           new AddContactToGroupWorker(frame, ds, names, groupName).execute();
                       }
                       deleteContactFrame.setVisible(false);
                   }
               });

               listPanel.add(optionList);
               topPanel.add(deleteContactLabel);
               buttonPanel.add(submitButton);
               deleteContactPanel.add(topPanel);
               deleteContactPanel.add(listPanel);
               deleteContactPanel.add(buttonPanel);
               deleteContactFrame.add(deleteContactPanel);
               if(option.equals("delete")) {
                   deleteContactFrame.setTitle(ResourceBundle.getBundle("texts").getString("delete") + "contact");
               }
               else {
                   deleteContactFrame.setTitle(ResourceBundle.getBundle("texts").getString("add"));
               }
               deleteContactFrame.setSize(250, 300);
               deleteContactFrame.setVisible(true);
           }
       }
       catch(ExecutionException ex) {
           throw new RuntimeException("Error retrieving contacts", ex);
       }
        catch(InterruptedException ex) {
            throw new RuntimeException("Operation interrupted", ex);
        }
    }

}
