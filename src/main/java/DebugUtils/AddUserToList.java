package DebugUtils;

import Server.Model.Dao.ConfigFileManager;
import Server.Model.Config;
import Server.Model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AddUserToList {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ConfigFileManager.loadConfigs(null);


        List<User> users;
        FileInputStream streamIn = new FileInputStream(Config.SERVER_USER_LIST);
        ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
        users = (List<User>) objectinputstream.readObject();
        streamIn.close();

        String mail;
        String path;

        if(args.length>2){
            addUserToList(args[1],args[2],users);
        }else{
            addUserToList("a@a.aa","a",users);
            addUserToList("b@b.bb","b",users);
            addUserToList("c@c.cc","c",users);
        }

    }

    private static void addUserToList(String mail, String path, List<User> users) throws IOException {
        File folder=new File(Config.USER_STORAGE+"/"+path);
        folder.mkdir();
        File readList=new File(folder.getPath()+"/"+ Config.RECEIVED_LIST);
        readList.createNewFile();
        readList=new File(folder.getPath()+"/"+ Config.SENT_LIST);
        readList.createNewFile();
        readList=new File(folder.getPath()+"/"+ Config.MAIL_STORAGE);
        readList.mkdir();
        /*
            Crea i 2 file e lo storage nella cartella
         */
        users.add(new User(mail,path));

        FileOutputStream fout = new FileOutputStream(Config.SERVER_USER_LIST);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(users);
        fout.close();

        System.out.println("User added correctly");
    }

}
