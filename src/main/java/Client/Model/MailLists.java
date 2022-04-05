package Client.Model;

import Commons.Dao.FileManager;
import Commons.Model.MailMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MailLists {
    private List<MailMessageWrapper> received = null;
    private List<MailMessageWrapper> sent = null;
    private int visible = Config.RECEIVED_LIST;

    public MailLists() {
        this.received = new ArrayList<>();
        this.sent = new ArrayList<>();
    }
    public boolean initialize(){
        boolean result=false;
        result=checkAndCreateFiles();
        if(result){
            addStoredToList(Config.CLIENT_RECEIVED_LIST,this.received);
            addStoredToList(Config.CLIENT_SENT_LIST,this.sent);
        }
        return result;
    }

    private boolean checkAndCreateFiles(){
        boolean result=true;
        File clientFolder=new File(Config.CLIENT_STORAGE);
        File userFolder=new File(Config.CLIENT_STORAGE+"/"+Config.USER_MAIL);
        File clientSentList = new File(Config.CLIENT_SENT_LIST);
        File clientRecievedList = new File(Config.CLIENT_RECEIVED_LIST);
        if(!clientFolder.exists())
            result=clientFolder.mkdir();
        if(!userFolder.exists() && result)
            result=userFolder.mkdir();
        if(!clientRecievedList.exists() && result) {
            result=createAndInitliaze(clientRecievedList);
        }
        if(!clientSentList.exists() && result) {
            result=createAndInitliaze(clientSentList);
        }
        return result;
    }

    private boolean createAndInitliaze(File f){
        boolean result;
        FileOutputStream fout = null;
        try {
            result=f.createNewFile();
            if(result){
                fout = new FileOutputStream(f);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(new ArrayList<MailMessageWrapper>());
            }
        } catch (IOException exception) {
            if(Config.DEBUG)
                exception.printStackTrace();
            result=false;
        }finally {
            if(fout!=null) {
                try {
                    fout.close();
                } catch (IOException ignored) { }
            }
        }
        return result;
    }

    private void addStoredToList(String path,List<MailMessageWrapper> list){
        Object object= FileManager.readObject(path);
        if(object != null && object.getClass() == ArrayList.class){
            ArrayList tmp=(ArrayList)object;
            for (Object o:
                    tmp) {
                if(o != null && o.getClass() == MailMessage.class){
                    list.add(new MailMessageWrapper((MailMessage) o));
                }
            }
        }
    }

    public synchronized void receivedAddAll(List<MailMessageWrapper> list){
        this.received.addAll(0,list);
        serializeUpdate(this.received,Config.CLIENT_RECEIVED_LIST);
    }
    public synchronized void sentAddAll(List<MailMessageWrapper> list){
        this.sent.addAll(0,list);
        serializeUpdate(this.sent,Config.CLIENT_SENT_LIST);
    }
    private synchronized void serializeUpdate(List<MailMessageWrapper> list,String path){
        ObjectOutputStream oos = null;
        try {
            List<MailMessage> tmpList=new ArrayList<>();
            for (MailMessageWrapper message:
                 list) {
                tmpList.add(message.asMailMessage());
            }
            oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
            oos.writeObject(tmpList);
        } catch (IOException exception) {
            if(Config.DEBUG)
                exception.printStackTrace();
        }finally {
            if(oos!=null) {
                try {
                    oos.close();
                } catch (IOException ignored) { }
            }
        }
    }

    public synchronized void updatePersistentRecieved(){
        serializeUpdate(this.received,Config.CLIENT_RECEIVED_LIST);
    }

    public synchronized void removeElem(MailMessageWrapper elem){
        if(this.visible==Config.RECEIVED_LIST){
            received.remove(elem);
            updatePersistentRecieved();
        }
        if(this.visible==Config.SENT_LIST){
            sent.remove(elem);
            updatePersistentSent();
        }
    }

    public synchronized void updateList(){
        if(visible==Config.RECEIVED_LIST)
            updatePersistentRecieved();
        else
            updatePersistentSent();
    }

    public synchronized void updatePersistentSent(){
        serializeUpdate(this.sent,Config.CLIENT_SENT_LIST);
    }

    public List<MailMessageWrapper> getReceived() {
        return received;
    }

    public void setReceived(List<MailMessageWrapper> received) {
        this.received = received;
    }

    public List<MailMessageWrapper> getSent() {
        return sent;
    }

    public void setSent(List<MailMessageWrapper> sent) {
        this.sent = sent;
    }

    public int getVisible() {
        return visible;
    }

    public synchronized void setVisible(int visible) {
        this.visible = visible;
    }

}
