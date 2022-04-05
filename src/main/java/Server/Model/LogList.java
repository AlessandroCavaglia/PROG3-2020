package Server.Model;

import Commons.Dao.FileManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class LogList {
    private List<String> realList;
    private ObservableList<String> listprop;
    private ExecutorService executor = newSingleThreadExecutor();

    public LogList(){
        this.listprop= FXCollections.observableArrayList();
    }

    public ObservableList<String> getListprop() {
        return listprop;
    }

    public synchronized void  addLog(String log){
        writeLog(log);
        Platform.runLater(()->{
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            listprop.add(0,log+" | "+formatter.format(date));
        });
    }

    public   void  addError(String log){
        this.addLog("Error: "+log);
    }

    public  void writeLog(String log){
        FileManager.appendFile(log,Config.LOG_FILE);
    }

}
