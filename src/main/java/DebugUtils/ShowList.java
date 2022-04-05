package DebugUtils;

//import Server.Model.Dao.ConfigFileManager;
import Server.Model.Config;
import Server.Model.Dao.ConfigFileManager;
import Server.Model.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class ShowList {
    private static Runtime runtime;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ConfigFileManager.loadConfigs(null);

        List<User> users;
        FileInputStream streamIn = new FileInputStream(Config.SERVER_USER_LIST);
        ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
        users = (List<User>) objectinputstream.readObject();
        streamIn.close();
        System.out.println(users);

//        runtime = Runtime.getRuntime();
////        LocksManager manager = new LocksManager();
////        manager.printUsers();
//        //    InputStreamReader i = new InputStreamReader(System.in);
//        //  i.read();
//        int before = (int) (getRuntime().totalMemory() - getRuntime().freeMemory());
//        LocksManager manager = new LocksManager();
////        UserWithLocks x = new UserWithLocks("a@aaaaaaaaa.aaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//
////        System.out.println(InstrumentationAgent.getObjectSize(new UserWithLocks("a@aaaaaaaaa.aaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")));
////        System.out.println(
////                ObjectSizeFetcher.getObjectSize(
////                        new UserWithLocks("a@aaaaaaaaa.aaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
////                )
////        );
//        int after = (int) (getRuntime().totalMemory() - getRuntime().freeMemory());
//        System.out.println(after - before);
////        System.out.println((getRuntime().totalMemory() - getRuntime().freeMemory())  + "/" + (getRuntime().totalMemory())  + "MB");
//        manager.getUserWithLocks("a@a.aa");
    }

    public static Runtime getRuntime() {
        return runtime;
    }
}
