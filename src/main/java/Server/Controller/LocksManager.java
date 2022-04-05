package Server.Controller;

import Server.Model.*;

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
    LocksManager is 16 Bytes long
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
            List<User> users=new ArrayList<>();
            Object object= userData.readObject();
            if(object != null && object.getClass() == ArrayList.class){
                ArrayList tmp=(ArrayList)object;
                for (Object o:
                     tmp) {
                    if(o != null && o.getClass() == User.class){
                        users.add((User) o);
                    }
                }
            }


            /* Fill hashmap <userEmail, UserLocks> with users */
            for (User user : users) {
                UserWithLocks userWithLocks = new UserWithLocks(user.getMail(), user.getPath());
                this.addUser(userWithLocks);
            }
        } catch (IOException | ClassNotFoundException e) {
            if(Config.DEBUG)
                e.printStackTrace();
        } finally {
            /* Close filestream */
            try {
                if (userData != null)
                    userData.close();
                if (userDataFile != null)
                    userDataFile.close();
            } catch (IOException e) {
                if(Config.DEBUG)
                    e.printStackTrace();
            }
        }
    }

    public List<UserWithLocks> getUsers(){
        return new ArrayList<>(userLocks.values());
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

    public String getPath(String mail, int type) {
        String root=Config.USER_STORAGE;
        if (userLocks.containsKey(mail)) {
            switch (type) {
                case Config.RECEIVED_FILE:
                    return (root+userLocks.get(mail).getPath() + "/" + Config.RECEIVED_LIST);
                case Config.SENT_FILE:
                    return (root+userLocks.get(mail).getPath() + "/" + Config.SENT_LIST);
                case Config.USER_MAILBOX:
                    return (root+userLocks.get(mail).getPath() + "/" + Config.MAIL_STORAGE);
                default:
                    return (root+userLocks.get(mail).getPath());
            }

        }
        return null;
    }

    public boolean userExist(String mail){
        return userLocks.containsKey(mail);
    }

    public void printUsers() {
        List<Object> list = new ArrayList<>(userLocks.values());
        for (Object user : list) {
            System.out.println(user);
        }
    }
}
