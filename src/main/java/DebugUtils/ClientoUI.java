package DebugUtils;

import Server.Controller.LocksManager;
import Server.Controller.ServerFilesChecker;
import Server.Controller.UIController;
import Server.Model.Config;
import Server.Model.Dao.ConfigFileManager;
import Server.Model.LogList;
import Server.Model.SocketManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.control.*;
import java.io.IOException;
import java.util.Objects;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class ClientoUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("client.fxml")); //Loader for the fxml
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getClassLoader().getResource("glisten.css")).toExternalForm()
        );
        primaryStage.setResizable(false);

        root.setCenter(loader.load());
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
            launch(args);
    }
}