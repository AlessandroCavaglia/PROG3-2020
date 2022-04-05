package Client;

import Client.Model.*;
import Client.Controller.UIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainClient extends Application {
    private static UIMail uiMail;
    private static UIMailList uiMailList;
    private static MailLists mailLists;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("client.fxml")); //Loader for the fxml
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getClassLoader().getResource("glisten.css")).toExternalForm()
        );
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getClassLoader().getResource("client.css")).toExternalForm()
        );
        root.setCenter(loader.load());
        UIController controller = loader.getController();            //Obtaining the controller
        controller.initModel(uiMail, uiMailList, mailLists);                   //Init the controller model
        primaryStage.setTitle("Casella di posta - " + Config.USER_MAIL);
        primaryStage.setMinWidth(1222);
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> controller.exitApplication());
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ConfigLoader.loadConfigs();
        mailLists = new MailLists();
        uiMail = new UIMail(mailLists);
        uiMailList = new UIMailList();
        mailLists.initialize();
        launch(args);
    }
}
