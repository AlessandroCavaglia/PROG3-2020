package Server.Controller;

import Server.Model.Config;
import Commons.Dao.FileManager;
import Server.Model.LogList;
import Server.Model.User;
import Server.Model.UserWithLocks;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerFilesChecker {

    public static boolean  checkServerFiles(LogList logList){
        if(!checkResourceExist(logList,Config.SERVER_STORAGE)){
            File f=new File(Config.SERVER_STORAGE);
                if(!f.mkdir()){
                    logList.addError("The Server Storage Folder didn't exist and we couldn't create it!");
                    return false;
                }
        }
        if(!checkResourceExist(logList,Config.USER_STORAGE)){
            File f=new File(Config.USER_STORAGE);
                if(!f.mkdir()){
                    logList.addError("The User storage Folder didn't exist and we couldn't create it!");
                    return false;
                }
        }
        if(!checkResourceExist(logList,Config.LOG_FILE)){
            File f=new File(Config.LOG_FILE);
            try {
                if(!f.createNewFile()){
                    logList.addError("The Log file didn't exist and we couldn't create it!");
                    return false;
                }
            } catch (IOException exception) {
                if(Config.DEBUG)
                    exception.printStackTrace();
            }
        }
        if(!checkResourceExist(logList,Config.SERVER_USER_LIST)){
            File f=new File(Config.SERVER_USER_LIST);
            FileOutputStream fout = null;
            try {
                if(!f.createNewFile()){
                    logList.addError("The User list didn't exist and we couldn't create it!");
                    return false;
                }else{
                        fout = new FileOutputStream(f);
                        ObjectOutputStream oos = new ObjectOutputStream(fout);
                        oos.writeObject(new ArrayList<User>());
                        fout.close();
                }
            } catch (IOException ex) {
                if(Config.DEBUG)
                    ex.printStackTrace();
                logList.addError("The User list didn't exist and we couldn't create it!");
            }finally {
                if(fout!=null) {
                    try {
                        fout.close();
                    } catch (IOException ignored) { }
                }
            }
        }
        if(!checkResourceExist(logList,Config.ID_FILE)){
            File f=new File(Config.ID_FILE);
            try {
                if(!f.createNewFile()){
                    logList.addError("The File with the Id's Folder didn't exist and we couldn't create it!");
                    return false;
                }else{
                    FileManager.writeFile("0",f.getPath());
                }
            } catch (IOException exception) {
                if(Config.DEBUG)
                    exception.printStackTrace();
            }
        }
        return true;
    }

    static boolean checkResourceExist(LogList logList, String path){
        if(!Files.exists(Paths.get(path))){
            return false;
        }else{
            return true;
        }
    }

    public static boolean checkUserFilesExists(LocksManager lm,LogList logList){
        boolean result=true;
        List<UserWithLocks> users=  lm.getUsers();
        for (UserWithLocks usr:
             users) {
            File userFolder=new File(lm.getPath(usr.getMail(),Config.USER_MAILBOX));
            File inbox=new File(lm.getPath(usr.getMail(),Config.RECEIVED_FILE));
            File sent=new File(lm.getPath(usr.getMail(),Config.SENT_FILE));
            if(!userFolder.exists() || !inbox.exists() || !sent.exists()){
                logList.addError("The user "+usr.getMail()+" is missing some files");
                result=false;
                break;
            }
        }
        return result;
    }
}
