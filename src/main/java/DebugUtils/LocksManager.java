package DebugUtils;

import Server.Model.Config;
import Server.Model.User;
import Server.Model.UserWithLocks;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    Class instantiated at server start,
    holds Read/Write locks for each user mailbox on this server
*/
public class LocksManager {

    private Map<String, UserWithLocks> userLocks;

    public LocksManager() {

        this.userLocks = new HashMap<>();
        FileInputStream userDataFile = null;
        ObjectInputStream userData = null;

        try {
            /* Read user data file */
            userDataFile = new FileInputStream(Config.SERVER_USER_LIST);
            userData = new ObjectInputStream(userDataFile);
            List<User> users = (List<User>) userData.readObject();

            /* Fill hashmap <userEmail, UserLocks> with users */
            for (User user : users) {
                UserWithLocks userWithLocks = new UserWithLocks(user.getMail(), user.getPath());
                this.addUser(userWithLocks);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            /* Close filestream */
            try {
                if (userData != null)
                    userData.close();
                if (userDataFile != null)
                    userDataFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addUser(UserWithLocks user) {
        if (userLocks.containsKey(user.getMail()))
            return false;
        userLocks.put(user.getMail(), user);
        return true;
    }

    public UserWithLocks getUserWithLocks(String email) {
        return userLocks.get(email);
    }

    public void printUsers() {
        List<Object> list = new ArrayList<>(userLocks.values());
        for (Object user : list) {
            System.out.println(user);
        }
    }
}
