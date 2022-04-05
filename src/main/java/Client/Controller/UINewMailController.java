package Client.Controller;

import Client.Model.*;
import Commons.Model.Mail;
import Commons.Model.MailMessage;
import Commons.Model.Response;
import Commons.Model.ResponseAction;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UINewMailController {

    @FXML
    private Label newMailFrom;
    @FXML
    private TextField newMailTo;
    @FXML
    private TextField newMailSubject;
    @FXML
    private TextArea newMailBody;
    @FXML
    private Button actionSend;
    @FXML
    private Text newMailLength;
    @FXML
    private Text errorTo;
    @FXML
    private Text errorSubject;
    @FXML
    private Text errorBody;

    private UINewMail uiNewMail = null;
    private UIMailList uiMailList = null;
    private MailLists lists = null;
    private Executor executor = null;
    private UIController uiController = null;
    private Mail replyTo = null;
    private int type=Config.SEND_MAIL;

    public void initModel(UINewMail uiNewMail, UIMailList uiMailList, MailLists lists, Executor executor, UIController uiController) {

        this.uiMailList = uiMailList;
        this.uiNewMail = uiNewMail;
        this.lists = lists;
        this.executor = executor;
        this.uiController = uiController;

        newMailFrom.setText(Config.USER_MAIL);
        uiNewMail.toProperty().bind(newMailTo.textProperty());
        uiNewMail.subjectProperty().bind(newMailSubject.textProperty());
        uiNewMail.bodyProperty().bind(newMailBody.textProperty());

        newMailTo.focusedProperty().addListener((arg, oldValue, newValue) -> checkTo(newValue));
        newMailSubject.focusedProperty().addListener((arg, oldValue, newValue) -> checkSubject(newValue));
        newMailBody.focusedProperty().addListener((arg, oldValue, newValue) -> checkBody(newValue));
        newMailBody.textProperty().addListener((arg, oldValue, newValue) -> updateLength(newValue));
        actionSend.setOnMouseClicked(event -> sendMail());

    }

    public void initModel(UINewMail uiNewMail, UIMailList uiMailList, MailLists lists, Executor executor, UIController uiController, Mail replyTo,int type) {
        this.initModel(uiNewMail,uiMailList,lists,executor,uiController);
        this.replyTo=replyTo;
        this.type=type;
        switch (type){
            case Config.REPLY_TO:
                newMailTo.setText(replyTo.getSender());
                newMailSubject.setText("RE: "+replyTo.getObject());
                break;
            case Config.REPLY_ALL:
                StringBuilder recivers = new StringBuilder(replyTo.getSender());
                for (String s: replyTo.getReceivers()) {
                    if(!s.equals(Config.USER_MAIL)){
                        recivers.append(", ").append(s);
                    }
                }
                newMailSubject.setText("RE: "+replyTo.getObject());
                newMailTo.setText(recivers.toString());
                break;
            case Config.FORWARD:
                newMailSubject.setText("FWD: "+replyTo.getObject());
                newMailBody.setText(replyTo.getText());
                break;
        }
    }

    private void sendMail() {
        if (     // Controlli sui valori inseriti
                checkSubject(false) &
                   checkBody(false) &
                     checkTo(false)
        ) {
            Runnable taskSendMail = () -> {

                /* Invio della mail */
                Mail email = new Mail(
                        Config.USER_MAIL,
                        uiNewMail.getTo().split("[ ,]+"),
                        uiNewMail.getSubject(),
                        uiNewMail.getBody(),
                        new Date()
                );
                if(this.type==Config.REPLY_TO || this.type==Config.REPLY_ALL)
                    email.setRepliedFrom(this.replyTo);

                RequestManager requestManager = new RequestManager();
                if(requestManager.isConnected()) {

                    Response result = requestManager.sendEmail(email);
                    if (result.getResponseAction() == ResponseAction.OK) {

                        Object o = result.getData();
                        if (o != null && o.getClass() == Mail.class)
                            email = (Mail) o;

                        uiController.setSuccessMsg("Mail inviata con successo");

                        /* Aggiungo il messaggio appena inviato alla lista dei messagg inviati (nel model del client) */
                        MailMessageWrapper mailMessageWrapper = new MailMessageWrapper(new MailMessage(email, false, true));
                        List<MailMessageWrapper> tmp = new ArrayList<>();
                        tmp.add(mailMessageWrapper);
                        if (lists.getVisible() == Config.SENT_LIST)
                            uiMailList.addAll(tmp);
                        lists.sentAddAll(tmp);
                        uiController.closeStage();

                    } else {

                        /* Messaggio d'errore */
                        if (result.getResponseAction() == ResponseAction.WRONG_RECEIVER) {
                            System.out.println(result.getData());
                            if (result.getData() != null && result.getData().getClass() == String.class) {
                                errorTo.setText((String) result.getData());
                                errorTo.setVisible(true);
                            }
                        } else {
                            if (result.getData() != null && result.getData().getClass() == String.class) {
                                errorBody.setText((String) result.getData());
                                errorBody.setVisible(true);
                            }
                        }
                    }
                } else {
                    errorBody.setText("Server offline, riprova più tardi");
                    errorBody.setVisible(true);
                }

            };
            executor.execute(taskSendMail);
        }
    }

    private void updateLength(String value) {
        int messageLength = 0;
        if (value != null)
            messageLength = value.length();
        newMailLength.setText("Lunghezza: " + messageLength);
    }

    private boolean checkBody(Boolean newValue) {

        String errorMessage = null;

        if (!newValue) { // Solo quando l'evento è "focus lost"

            String body = uiNewMail.getBody();

            if (body != null)
                if (body.length() == 0)
                    errorMessage = "E' necessario scrivere un contenuto";

            // Imposto messaggio d'errore
            errorBody.setText(errorMessage);
            errorBody.setVisible(errorMessage != null);
        }

        return errorMessage == null;
    }

    private boolean checkSubject(Boolean newValue) {

        String errorMessage = null;

        if (!newValue) { // Solo quando l'evento è "focus lost"

            String subject = uiNewMail.getSubject();

            if (subject != null)
                if (subject.length() == 0)
                    errorMessage = "Inserire un oggetto";
                else if (subject.length() > 256)
                    errorMessage = "L'oggetto è troppo lungo, insierisci massimo 256 caratteri";

            // Imposto messaggio d'errore
            errorSubject.setText(errorMessage);
            errorSubject.setVisible(errorMessage != null);
        }

        return errorMessage == null;
    }

    private boolean checkTo(Boolean newValue) {

        String errorMessage = null;
        if (!newValue) { // Solo quando l'evento è "focus lost"

            String emailString = uiNewMail.getTo();

            if (emailString != null)
                if (emailString.length() == 0) {

                    errorMessage = "Inserire un destinatario";

                } else {
                    // Controlla la validità di tutte le email dei destinatari

                    String[] emails = emailString.split("[ ,]+");
                    for (String email : emails) {
                        if (email.compareTo(Config.USER_MAIL) == 0) {
                            errorMessage = "Non puoi inviare email a te stesso";
                            break;
                        } else if(Collections.frequency(Arrays.asList(emails), email) > 1) {
                            errorMessage = "Indirizzo email duplicato: " + email;
                            break;
                        } else if (!Mail.checkIsValid(email)) {
                            errorMessage = "Indirizzo email errato: " + email;
                            break;
                        }
                    }
                }
            // Imposto messaggio d'errore
            errorTo.setText(errorMessage);
            errorTo.setVisible(errorMessage != null);
        }
        return errorMessage == null;
    }

}
