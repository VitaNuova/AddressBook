package cz.fi.muni.pv168.AddressBook;

import org.apache.commons.dbcp2.BasicDataSource;
import sun.tools.jar.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by viki on 25.3.15.
 */
public class Application {

    public static void main(String[] args) throws IOException {

        //only for testing

        Properties config = new Properties();
        config.load(Main.class.getResourceAsStream("/config.properties"));
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(config.getProperty("jdbc.url"));
        ds.setUsername(config.getProperty("jdbc.user"));
        ds.setPassword(config.getProperty("jdbc.password"));

       /* AddressBookManager addressBookManager;
        addressBookManager = new AddressBookManagerImpl(ds);
        List<String> groupList = addressBookManager.listGroupsByPerson();
        for(String group : groupList) {
            System.out.println(group);
        }
        List<Long> conts = new ArrayList<Long>();
        conts.add(1l);
        conts.add(2l);
        Group gr1 = new Group("Friends", conts);
        GroupManager groupManager = new GroupManagerImpl(ds);
        groupManager.createGroup(gr1);
        Group retrieved = groupManager.findGroupByName(gr1.getGroupName());
        System.out.println(retrieved.getGroupID());
        System.out.println(retrieved.getGroupName());
        System.out.println(retrieved.getGroupMemberList());
        retrieved.setGroupName("Family");
        groupManager.updateGroup(retrieved);
        retrieved = groupManager.findGroupByID(retrieved.getGroupID());
        System.out.println(retrieved.getGroupID());
        System.out.println(retrieved.getGroupName());
        System.out.println(retrieved.getGroupMemberList());
        groupManager.deleteGroup(gr1);
        retrieved = groupManager.findGroupByID(gr1.getGroupID());
        if(retrieved == null) {
            System.out.println("Deleted OK");
        }*/
    }
}
