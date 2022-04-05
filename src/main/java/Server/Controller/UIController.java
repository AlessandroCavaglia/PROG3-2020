package Server.Controller;

import Server.Model.Cella;
import Server.Model.LogList;
import Server.Model.SocketManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class UIController {


    @FXML
    private Button startServer;
    @FXML
    private Button stopServer;
    @FXML
    private ListView<String> logListView;


    private SocketManager socketManager =null;
    private LogList logList=null;
    public void initModel(LogList logList, SocketManager controller){
        if(this.logList!=null){
            System.out.println("Il model può essere inizializzato una volta sola");
            return;
        }
        this.logList=logList;
        this.socketManager =controller;
        this.startServer.disableProperty().bind(socketManager.runningProperty());
        this.stopServer.disableProperty().bind(socketManager.stoppedProperty());
        this.logListView.setItems(logList.getListprop());
        this.logListView.setCellFactory(list -> new Cella());
    }

    public void StartServer(ActionEvent actionEvent) {
        socketManager.start();
    }

    public void StopServer(ActionEvent actionEvent) {
        if(socketManager !=null){
            socketManager.stopServer();
        }
    }

    public void exitApplication() {
        if(socketManager !=null){
            socketManager.stopServer();
        }
        Platform.exit();
    }
}
