package Client.Controller;

import Client.Controller.RequestManager;
import Client.Model.Config;
import Client.Model.MailMessageWrapper;
import Client.Model.UIMail;
import Commons.Model.MailMessage;
import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Cella extends ListCell<MailMessageWrapper> {
    private UIMail uiMail;
    private Function<Integer,Void> function;
    @FXML
    Icon favouriteIcon;
    @FXML
    Label mailField;
    @FXML
    Label subject;
    @FXML
    Label mailDate;
    @FXML
    AnchorPane anchorPane;

    public Cella(UIMail uiMail, Function<Integer,Void> function) {
        this.uiMail = uiMail;
        this.function=function;
    }

    @Override
    public void updateItem(MailMessageWrapper item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("cella.fxml")); //Loader for the fxml
            loader.setController(this);
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            favouriteIcon.contentProperty().bind(item.starIconProperty());
            mailField.setText(item.getSender());
            subject.setText(item.getObject());

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            mailDate.setText(formatter.format(item.getSendDate()));

            if (Config.USER_MAIL.equals(item.getSender())) {
                StringBuilder dest = new StringBuilder();
                for (String rec :
                        item.getReceivers()) {
                    dest.append(rec).append(", ");
                }
                mailField.setText(dest.substring(0, dest.length() - 2));
                this.setOnMouseClicked(event -> {
                    this.uiMail.setMail(item);
                });
                item.fontProprietyProperty().setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 16));
                favouriteIcon.setManaged(false);
            } else {
                this.setOnMouseClicked(event -> {
                    this.uiMail.setMail(item);
                    function.apply(item.getId());
                });
            }
            mailField.fontProperty().bind(item.fontProprietyProperty());
            mailDate.fontProperty().bind(item.fontProprietyProperty());
            subject.fontProperty().bind(item.fontProprietyProperty());
            setGraphic(anchorPane);
        }
    }
}
