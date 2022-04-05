package Server;

import Server.Controller.LocksManager;
import Server.Controller.ServerFilesChecker;
import Server.Model.Dao.ConfigFileManager;
import Server.Model.SocketManager;
import Server.Controller.UIController;
import Server.Model.Config;
import Server.Model.LogList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;

public class MainServer extends Application {

    private static LogList logList;                    //Loglist used to manage the log view inside the ui
    private static SocketManager socketManager;        //Manager of the socket execution
    private static LocksManager locksManager;

    @Override
    public void start(Stage primaryStage) throws Exception {

        socketManager = new SocketManager(Config.PORT, logList, locksManager);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("server.fxml")); //Loader for the fxml
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getClassLoader().getResource("ServerUI.css")).toExternalForm()
        );
        primaryStage.setResizable(false);

        root.setCenter(loader.load());
        UIController controller = loader.getController();               //Obtaining the controller
        controller.initModel(logList, socketManager);                   //Init the controller model
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e->controller.exitApplication());
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        logList = new LogList();
        ConfigFileManager.loadConfigs(logList);
        if (ServerFilesChecker.checkServerFiles(logList)) {
            locksManager = new LocksManager();
            ServerFilesChecker.checkUserFilesExists(locksManager, logList);

            launch(args);
        } else {
            if (Config.DEBUG)
                System.out.println("Errore manca qualche file");
        }
    }
}
