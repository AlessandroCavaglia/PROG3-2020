package Client.Model;

import Commons.Model.MailMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;

public class UIMailList {
    ObservableList<MailMessageWrapper> observableList=null;
    public UIMailList(){
        observableList= FXCollections.observableArrayList();
    }

    public ObservableList<MailMessageWrapper> getObservableList() {
        return observableList;
    }

    public synchronized void setList(List<MailMessageWrapper> list){
        Platform.runLater(()->{
            observableList.clear();
            observableList.addAll(list);
        });
    }
    public synchronized void addAll(List<MailMessageWrapper> list){
        Platform.runLater(()-> {
            this.observableList.addAll(0,list);
        });
    }
    public synchronized void removeElem(MailMessageWrapper elem){
        Platform.runLater(()-> {
            this.observableList.remove(elem);
        });
    }
}
