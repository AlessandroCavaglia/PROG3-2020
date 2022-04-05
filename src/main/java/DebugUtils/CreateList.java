package DebugUtils;

import Server.Model.Dao.ConfigFileManager;
import Server.Model.Config;
import Commons.Dao.FileManager;
import Server.Model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CreateList {


    public static void main(String[] args) throws IOException {
        ConfigFileManager.loadConfigs(null);


        List<User> list = new ArrayList<>();
        File f=new File(Config.SERVER_STORAGE);
        f.mkdir();
        f=new File(Config.LOG_FILE);
        f.createNewFile();
        f=new File(Config.USER_STORAGE);
        f.mkdir();
        f=new File(Config.ID_FILE);
        f.createNewFile();
        FileManager.writeFile("0",f.getPath());
        f=new File(Config.SERVER_USER_LIST);
        f.createNewFile();
        FileOutputStream fout = new FileOutputStream(f);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(list);
        fout.close();

        System.out.println("Lista creata correttamente");
    }
}
