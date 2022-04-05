package Client.Model;

import Commons.Dao.FileManager;
import Server.Model.LogList;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ConfigLoader {

    public static void loadConfigs(){
        File configFile=new File(Config.CONFIG_LOCATION);
        if(!configFile.exists()){
            initializeConfigs(configFile);
        }else{
            loadFileConfigs(configFile);
        }

    }

    private static void initializeConfigs(File config){
        try{
            if(config.createNewFile()){
                FileManager.appendFile("Mail: "+Config.USER_MAIL,config.getPath());
                FileManager.appendFile("Ip: "+Config.ADRESS,config.getPath());
                FileManager.appendFile("Port: "+Config.PORT,config.getPath());
                FileManager.appendFile("Thread num: "+Config.THREAD_POOL_NUMBER+"",config.getPath());
                FileManager.appendFile("Refresh seconds: "+Config.REFRESH_TIME,config.getPath());
                FileManager.appendFile("Debug mode: "+Config.DEBUG,config.getPath());
            }
        }catch (IOException ex){
            if(Config.DEBUG)
                ex.printStackTrace();
        }
    }

    private static void loadFileConfigs(File configFile){
        boolean result=true;
        Scanner scanner=null;
        try{
            scanner=new Scanner(configFile);
            for(int i=0;i<6;i++){
                if(scanner.hasNextLine()){
                    String value=scanner.nextLine();
                    setConfigValue(i,value);
                }else{
                    i=11;
                }
            }
        }catch (Exception e){
            if(Server.Model.Config.DEBUG)
                e.printStackTrace();
        }finally {
            if(scanner!=null){
                scanner.close();
            }
        }
    }

    private static void setConfigValue(int index,String value){
        int tmp;
        switch (index){
            case 0:
                value=value.substring(6);
                Config.USER_MAIL=value;
                Config.CLIENT_RECEIVED_LIST=Config.CLIENT_STORAGE+"/"+Config.USER_MAIL+"/"+Config.CLIENT_RECEIVED_LIST;
                Config.CLIENT_SENT_LIST=Config.CLIENT_STORAGE+"/"+Config.USER_MAIL+"/"+Config.CLIENT_SENT_LIST;
                break;
            case 1:
                value=value.substring(4);
                Config.ADRESS=value;
                break;
            case 2:
                value=value.substring(6);
                try{
                    tmp=Integer.parseInt(value);
                    Config.PORT=tmp;
                }catch (NumberFormatException ex){
                    if(Config.DEBUG)
                        ex.printStackTrace();
                }
                break;
            case 3:
                value=value.substring(12);
                try{
                    tmp=Integer.parseInt(value);
                    Config.THREAD_POOL_NUMBER=tmp;
                }catch (NumberFormatException ex){
                    if(Config.DEBUG)
                        ex.printStackTrace();
                }
                break;
            case 4:
                value=value.substring(17);
                try{
                    tmp=Integer.parseInt(value);
                    Config.REFRESH_TIME=tmp;
                }catch (NumberFormatException ex){
                    if(Config.DEBUG)
                        ex.printStackTrace();
                }
                break;
            case 5:
                value=value.substring(12);
                Config.DEBUG=value.equals("true");
                break;
            default:
                break;
        }
    }
}
