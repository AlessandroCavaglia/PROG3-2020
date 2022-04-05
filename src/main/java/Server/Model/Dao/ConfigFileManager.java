package Server.Model.Dao;

import Commons.Dao.FileManager;
import Server.Model.Config;
import Server.Model.LogList;

import java.io.*;
import java.util.Scanner;

public class ConfigFileManager extends FileManager {

    public static boolean loadConfigs(LogList logList){
        boolean result=true;
        File configFile=new File(Config.CONFIG);
        if(!configFile.exists()){
            result=initializeConfig(configFile);
        }else{
            result=readConfigFile(configFile);
        }
        if(!result && logList!=null){
            logList.addError("Error while loading the configs, the server will now use standard configs");
        }
        return result;
    }
    private static boolean  initializeConfig(File configFile){
        boolean result=false;
        try{
            configFile.createNewFile();
            appendFile("Server storage folder: "+Config.SERVER_STORAGE.substring(0,Config.SERVER_STORAGE.length()-1),configFile.getAbsolutePath());
            appendFile("User storage folder: "+Config.USER_STORAGE.substring(Config.SERVER_STORAGE.length(),Config.USER_STORAGE.length()-1),configFile.getAbsolutePath());
            appendFile("Server user list filename: "+Config.SERVER_USER_LIST.substring(Config.SERVER_STORAGE.length()),configFile.getAbsolutePath());
            appendFile("User recieved messages list filename: "+Config.RECEIVED_LIST,configFile.getAbsolutePath());
            appendFile("User sent messages list filename: "+Config.SENT_LIST,configFile.getAbsolutePath());
            appendFile("User mails storage folder: "+Config.MAIL_STORAGE.substring(0,Config.MAIL_STORAGE.length()-1),configFile.getAbsolutePath());
            appendFile("Log file name: "+Config.LOG_FILE.substring(Config.SERVER_STORAGE.length()),configFile.getAbsolutePath());
            appendFile("Server port: "+Config.PORT,configFile.getAbsolutePath());
            appendFile("Server pool second timeout: "+Config.POOL_SECONDS_TIMEOUT,configFile.getAbsolutePath());
            appendFile("Thread pool number: "+Config.THREAD_POOL_NUMBER,configFile.getAbsolutePath());
            appendFile("Server mail id filename: "+Config.ID_FILE.substring(Config.SERVER_STORAGE.length(),Config.ID_FILE.length()),configFile.getAbsolutePath());
            result=true;
        }catch (IOException e){
            if(Config.DEBUG)
                e.printStackTrace();
            result=false;
        }
        return result;
    }

    private static boolean readConfigFile(File configFile){
        boolean result=true;
        Scanner scanner=null;
        try{
            scanner=new Scanner(configFile);
            for(int i=0;i<11;i++){
                if(scanner.hasNextLine()){
                    String value=scanner.nextLine();
                    setConfigValue(i,value);
                }else{
                    i=11;
                    result=false;
                }
            }
        }catch (Exception e){
            if(Config.DEBUG)
                e.printStackTrace();
            result=false;
        }finally {
            if(scanner!=null){
                scanner.close();
            }
        }
        return result;
    }
    private static void setConfigValue(int index,String value){
        switch (index){
            case 0:
                Config.setServerStorage(value.substring(23));
                break;
            case 1:
                Config.setUserStorage(value.substring(21));
                break;
            case 2:
                Config.setServerUserList(value.substring(27));
                break;
            case 3:
                Config.setReceivedList(value.substring(38));
                break;
            case 4:
                Config.setSentList(value.substring(34));
                break;
            case 5:
                Config.setMailStorage(value.substring(27));
                break;
            case 6:
                Config.setLogFile(value.substring(15));
                break;
            case 7:
                Config.setPORT(Integer.parseInt(value.substring(13)));
                break;
            case 8:
                Config.setPoolSecondsTimeout(Integer.parseInt(value.substring(28)));
                break;
            case 9:
                Config.setThreadPoolNumber(Integer.parseInt(value.substring(20)));
                break;
            case 10:
                Config.setIdFile(value.substring(25));
                break;
        }
    }

}
