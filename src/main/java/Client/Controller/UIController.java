package Client.Controller;

import Client.Model.*;
import com.gluonhq.charm.glisten.control.Icon;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import java.awt.TrayIcon.MessageType;


public class UIController {
    @FXML
    private Label contentFrom;
    @FXML
    private Label contentTo;
    @FXML
    private Label contentSubject;
    @FXML
    private Label contentDate;
    @FXML
    private TextArea contentBody;
    @FXML
    private AnchorPane mailBox;
    @FXML
    private Icon iconFavuorite;
    @FXML
    private ListView<MailMessageWrapper> contentMailList;
    @FXML
    private Label actionWrite;
    @FXML
    private Label actionRefresh;
    @FXML
    private Label actionForward;
    @FXML
    private Label actionSetNotSeen;
    @FXML
    private Menu actionSetNotSeenMenu;
    @FXML
    private Menu actionSetFavouriteMenu;
    @FXML
    private Label actionSetFavourite;
    @FXML
    private Label actionAnswer;
    @FXML
    private Label actionAnswerAll;
    @FXML
    private Label actionDelete;
    @FXML
    private Label actionReceived;
    @FXML
    private Label actionFavourite;
    @FXML
    private Label actionNotSeen;
    @FXML
    private Label actionSent;
    @FXML
    private Label noMessagesError;
    @FXML
    private Text errorMsg;
    @FXML
    private Text successMsg;


    private UIMail uiMail = null;
    private UIMailList uiMailList = null;
    private Stage stage = null;
    private MailLists mailLists = null;
    private ScheduledExecutorService executor;
    private ScheduledFuture<?> future = null;
    private int delay = Config.REFRESH_TIME;
    private SimpleStringProperty errorProp;
    private SimpleStringProperty successProp;
    private SimpleBooleanProperty empty;

    public void initModel(UIMail uiMail, UIMailList uiMailList, MailLists mailLists) {
        this.uiMail = uiMail;
        this.uiMailList = uiMailList;
        this.mailLists = mailLists;
        this.executor = newScheduledThreadPool(Config.THREAD_POOL_NUMBER);
        this.errorProp = new SimpleStringProperty();
        this.successProp = new SimpleStringProperty();
        this.empty = new SimpleBooleanProperty();

        mailBox.visibleProperty().bind(this.uiMail.visibleProperty());
        contentFrom.textProperty().bind(this.uiMail.fromProperty());
        contentTo.textProperty().bind(this.uiMail.toProperty());
        contentSubject.textProperty().bind(this.uiMail.subjectProperty());
        contentDate.textProperty().bind(this.uiMail.dateProperty());
        contentBody.textProperty().bind(this.uiMail.bodyProperty());
        iconFavuorite.contentProperty().bind(this.uiMail.favouriteProperty());
        contentMailList.setItems(uiMailList.getObservableList());
        actionSetNotSeenMenu.visibleProperty().bind(uiMail.sentMailProperty());
        actionSetFavouriteMenu.visibleProperty().bind(uiMail.sentMailProperty());
        errorMsg.textProperty().bind(this.errorProp);
        successMsg.textProperty().bind(this.successProp);
        noMessagesError.visibleProperty().bind(this.empty);

        //EVENTI
        this.contentMailList.setCellFactory(list -> new Cella(uiMail, new Function<Integer, Void>() {
            @Override
            public Void apply(Integer integer) {
                return setEmailSeen(integer);
            }
        }));
        this.actionWrite.setOnMouseClicked(event -> showNewMailCreator(Config.SEND_MAIL));
        this.actionRefresh.setOnMouseClicked(event -> getNewMailsWrapper());
        this.actionSetFavourite.setOnMouseClicked(event -> setMailFavourite());
        this.actionSetNotSeen.setOnMouseClicked(event -> setMailNotSeen());
        this.actionDelete.setOnMouseClicked(event -> deleteMail());
        this.actionAnswer.setOnMouseClicked(event -> showNewMailCreator(Config.REPLY_TO));
        this.actionAnswerAll.setOnMouseClicked(event -> showNewMailCreator(Config.REPLY_ALL));
        this.actionForward.setOnMouseClicked(event -> showNewMailCreator(Config.FORWARD));
        this.actionSent.setOnMouseClicked(event -> showSentMails());
        this.actionReceived.setOnMouseClicked(event -> showRecievedMails());
        this.actionFavourite.setOnMouseClicked(event -> showFavouritedMails());
        this.actionNotSeen.setOnMouseClicked(event -> showNotSeenMails());
        //Setting standard UI
        getSentMails(); //Download list of all the sent mails
        uiMailList.setList(mailLists.getReceived());

        // GET NEW EMAILS EVERY X SECONDS
        future = executor.scheduleAtFixedRate(getNewMails(true), 1, Config.REFRESH_TIME, TimeUnit.SECONDS);

    }

    public void setErrorMsg(String msg) {
        Platform.runLater(() -> {
            errorProp.set(msg);
        });
    }

    public void setSuccessMsg(String msg) {
        Platform.runLater(() -> {
            successProp.set(msg);
            clearSuccessMsg();
        });
    }

    private void getNewMailsWrapper() {
        executor.execute(getNewMails(false));
    }

    private void clearSuccessMsg() {

        Runnable taskSendMail = () -> {
            try {
                Thread.sleep(delay * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                successProp.set("");
            }
        };
        executor.execute(taskSendMail);
    }

    private  Runnable getNewMails(boolean repeat) {
        UIController self=this;
        return () -> {
            synchronized (self){
                RequestManager requestManager = new RequestManager();
                if (!requestManager.isConnected()) {
                    if (repeat) {                     //If we are a repeated task
                        future.cancel(false);   //Recreate the task
                        if (delay < Config.REFRESH_TIME * 4)
                            delay += 1;                                //Increade the delay
                        future = executor.scheduleAtFixedRate(getNewMails(true), delay, delay, TimeUnit.SECONDS);
                        setErrorMsg("Tentativo di connessione fallito, riprovo tra " + delay + " secondi.");
                    } else {
                        setErrorMsg("Tentativo di connessione fallito");
                    }
                    return;
                }
                if (repeat) {           //If we are a repeated task
                    delay = Config.REFRESH_TIME;  //Set the delay to standard value
                    future.cancel(false);
                    future = executor.scheduleAtFixedRate(getNewMails(true), delay, delay, TimeUnit.SECONDS);
                }
                setErrorMsg("");

                int index = -1;
                if (mailLists.getReceived().size() > 0)
                    index = mailLists.getReceived().get(0).getId();
                List<MailMessageWrapper> mailMessageWrappers = requestManager.getNewMail(index);
                if (mailMessageWrappers == null || mailMessageWrappers.size()<=0)
                    return;
                if(SystemTray.isSupported())
                    sendNotification(mailMessageWrappers.size());

                if (mailLists.getVisible() == Config.RECEIVED_LIST)
                    uiMailList.addAll(mailMessageWrappers);
                mailLists.receivedAddAll(mailMessageWrappers);
            }
        };
    }

    private void getSentMails() {
        Runnable taskGetSentMail = () -> {
            RequestManager requestManager = new RequestManager();
            if (!requestManager.isConnected()) {
                setErrorMsg("Tentativo di connessione fallito");
                return;
            }
            setErrorMsg("");
            List<MailMessageWrapper> mailMessageWrappers = requestManager.getSentMail();
            if (mailMessageWrappers == null)
                return;
            mailLists.setSent(mailMessageWrappers);
            if (mailLists.getVisible() == Config.SENT_LIST)
                uiMailList.setList(mailLists.getSent());
            mailLists.updatePersistentSent();
        };
        executor.execute(taskGetSentMail);
    }

    private void setMailFavourite() {
        Runnable taskSetMailFavourite = () -> {
            RequestManager requestManager = new RequestManager();
            if (!requestManager.isConnected()) {
                setErrorMsg("Tentativo di connessione fallito");
                return;
            }
            setErrorMsg("");
            if (uiMail.getMail().isFavourite()) {
                if (requestManager.setNotFavourite(uiMail.getMail().getId())) {
                    uiMail.setFavourite(false);
                }
            } else {
                if (requestManager.setFavourite(uiMail.getMail().getId())) {
                    uiMail.setFavourite(true);
                }
            }
        };
        executor.execute(taskSetMailFavourite);
    }

    private void setMailNotSeen() {
        Runnable taskSetMailFavourite = () -> {
            RequestManager requestManager = new RequestManager();
            if (!requestManager.isConnected()) {
                setErrorMsg("Tentativo di connessione fallito");
                return;
            }
            setErrorMsg("");
            if (requestManager.setNotSeen(uiMail.getMail().getId())) {
                uiMail.setSeen(false);
                uiMail.hide();
            }

        };
        executor.execute(taskSetMailFavourite);
    }

    private Void setEmailSeen(int id) {
        Runnable taskSetMailFavourite = () -> {
            RequestManager requestManager = new RequestManager();
            if (!requestManager.isConnected()) {
                setErrorMsg("Tentativo di connessione fallito");
                return;
            }
            setErrorMsg("");
            if (requestManager.setSeen(uiMail.getMail().getId())) {
                uiMail.setSeen(true);
            }

        };
        executor.execute(taskSetMailFavourite);
        return null;
    }

    private void deleteMail() {
        Runnable taskDeleteMail = () -> {
            RequestManager requestManager = new RequestManager();
            if (!requestManager.isConnected()) {
                setErrorMsg("Tentativo di connessione fallito");
                return;
            }
            if (requestManager.deleteEmail(uiMail.getMail().getId())) {
                uiMail.hide();
                mailLists.removeElem(uiMail.getMail());
                uiMailList.removeElem(uiMail.getMail());
                setErrorMsg("");
                setSuccessMsg("Email eliminata con successo");
            } else {
                setErrorMsg("Si è verificato un problema nell'eliminazione. Riprova più tardi");
            }
        };
        executor.execute(taskDeleteMail);

    }

    private void showNewMailCreator(int type) {
        try {

            stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("newEmail.fxml")); //Loader for the fxml
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getClassLoader().getResource("client.css")).toExternalForm()
            );
            root.setCenter(loader.load());
            UINewMailController controller = loader.getController();            //Obtaining the controller
            UINewMail uiNewMail = new UINewMail();
            if(type==Config.SEND_MAIL)
                controller.initModel(uiNewMail, uiMailList, mailLists, executor, this);                   //Init the controller model
            else
                controller.initModel(uiNewMail, uiMailList, mailLists, executor, this,uiMail.getMail().asMailMessage(),type);
            stage.setTitle("Scrivi una nuova mail");
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            closeStage();
            e.printStackTrace();
        }
    }

    public void closeStage() {
        if (stage != null) {
            Platform.runLater(() -> {
                stage.close();
            });
        }
    }

    private void showSentMails() {
        this.uiMailList.setList(mailLists.getSent());
        this.empty.set(mailLists.getSent().size() == 0);
        mailLists.setVisible(Config.SENT_LIST);
    }

    private void showRecievedMails() {
        this.uiMailList.setList(mailLists.getReceived());
        this.empty.set(mailLists.getReceived().size() == 0);
        mailLists.setVisible(Config.RECEIVED_LIST);
    }

    private void showFavouritedMails() {
        List<MailMessageWrapper> tmp = new ArrayList<>();
        for (MailMessageWrapper m : mailLists.getReceived()) {
            if (m.isFavourite())
                tmp.add(m);
        }
        this.uiMailList.setList(tmp);
        this.empty.set(tmp.size() == 0);
        mailLists.setVisible(Config.RECEIVED_LIST);
    }

    private void showNotSeenMails() {
        List<MailMessageWrapper> tmp = new ArrayList<>();
        for (MailMessageWrapper m : mailLists.getReceived()) {
            if (!m.isSeen())
                tmp.add(m);
        }
        this.uiMailList.setList(tmp);
        this.empty.set(tmp.size() == 0);
        mailLists.setVisible(Config.RECEIVED_LIST);
    }

    private void closeExecutor() {
        if (executor != null) {
            //Interrompe le richieste
            executor.shutdown();
            try {
                if (!executor.awaitTermination(Server.Model.Config.POOL_SECONDS_TIMEOUT, TimeUnit.SECONDS)) {
                    executor.shutdownNow(); // Termina comunque l'esecuzione
                }
            } catch (InterruptedException ie) {
                // Termina comunque l'esecuzione se si verifica un problema
                executor.shutdownNow();
            }

        }
    }

    private void sendNotification(int size){
        try{
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "Client posta");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("Client posta");

            tray.add(trayIcon);
        if(size>1){
            trayIcon.displayMessage("Nuove mail per "+Config.USER_MAIL, "Hai "+size+" nuove mail", MessageType.INFO);
        }else{
            trayIcon.displayMessage("Nuova mail per "+Config.USER_MAIL, "Hai ricevuto una nuova mail", MessageType.INFO);
        }
        }catch (Exception ignored){}
    }

    public void exitApplication() {
        closeExecutor();
        Platform.exit();
    }

}
