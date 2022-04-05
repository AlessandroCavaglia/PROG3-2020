package Server.Model.Dao;

import Commons.Dao.FileManager;
import Server.Controller.LocksManager;
import Server.Model.Config;
import Commons.Model.Mail;
import Server.Model.MailReference;

import java.io.*;
import java.util.ArrayList;

public class MailReferenceFileManager extends FileManager {

    public static ArrayList<MailReference> readFile(Integer id, String path) {
        ArrayList<MailReference> result = new ArrayList<>();
        File file = new File(path);
        BufferedReader in=null;
        if (file.exists() && file.length()>0) {
            try {
                in = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(file)));
                String line = in.readLine();
                while (line != null) {
                    String[] split = line.split(",");
                    if (Integer.parseInt(split[0]) > id)
                        result.add(new MailReference(Integer.parseInt(split[0]), split[1], Boolean.parseBoolean(split[2]), Boolean.parseBoolean(split[3])));
                    else
                        break;
                    line = in.readLine();
                }
            } catch (Exception e) {
                if(Config.DEBUG)
                    e.printStackTrace();
            }finally {
                if(in!=null)
                    try{
                        in.close();
                    }catch (IOException ignored){ }
            }
        }
        return result;
    }

    public static Mail getMail(MailReference mailReference, LocksManager locksManager) {
        Object obj = readObject(locksManager.getPath(mailReference.getMail(), Config.USER_MAILBOX) + mailReference.getId() + ".mail");
        if (obj != null && obj.getClass() == Mail.class) {
            return (Mail) obj;
        }
        return null;
    }

    public static MailReference getMailReference(Integer id, String path){
        MailReference mail=null;
        BufferedReader br=null;
        FileInputStream fstream=null;
        try {
             fstream = new FileInputStream(path);
             br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] tokens = strLine.split(",");
                if (tokens.length > 0) {
                    if (tokens[0].equals(String.valueOf(id))) {
                        mail=new MailReference(Integer.parseInt(tokens[0]), tokens[1], Boolean.parseBoolean(tokens[2]), Boolean.parseBoolean(tokens[3]));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            if(Config.DEBUG)
                e.printStackTrace();
        }
        finally {
            if(br!=null)
                try{
                    br.close();
                }catch (IOException ignored){ }
        }
        return mail;
    }

        public static boolean alterMail(Integer id, String path, boolean favourite, boolean seen,boolean delete){
        boolean result=false;
        BufferedReader br=null;
        try {
            FileInputStream fstream = new FileInputStream(path);
            br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            StringBuilder fileContent = new StringBuilder();
            while ((strLine = br.readLine()) != null) {
                String[] tokens = strLine.split(",");
                if (tokens.length > 0) {
                    if (tokens[0].equals(String.valueOf(id))) {
                        String newLine = tokens[0] + "," + tokens[1] + "," + favourite + "," + seen;
                        if(!delete)
                            fileContent.append(newLine).append("\n");
                    } else {
                        // update content as it is
                        fileContent.append(strLine).append("\n");
                    }
                }
            }
            // Now fileContent will have updated content, which you can override into file
            writeFile(fileContent.toString(),path);
            result=true;
        } catch (Exception e) {//Catch exception if any
            if(Config.DEBUG)
                e.printStackTrace();
        }finally {
            if(br!=null)
                try{
                    br.close();
                }catch (IOException ignored){ }
        }
        return result;
    }

}
