package Client.Model;

import Commons.Model.Mail;
import Commons.Model.MailMessage;
import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.SimpleDateFormat;

public class UIMail {

    private SimpleBooleanProperty visible;
    private SimpleBooleanProperty sentMail;
    private StarIcon favourite;
    private SimpleStringProperty to;
    private SimpleStringProperty from;
    private SimpleStringProperty subject;
    private SimpleStringProperty body;
    private SimpleStringProperty date;
    private MailMessageWrapper mail;
    private MailLists lists;

    public UIMail(MailLists lists) {
        this.visible = new SimpleBooleanProperty();
        this.visible.set(false);
        this.favourite =new StarIcon();
        this.favourite.setFavuorite();
        this.to = new SimpleStringProperty();
        this.to.set("");
        this.from =new SimpleStringProperty();
        this.from.set("");
        this.subject =new SimpleStringProperty();
        this.subject.set("");
        this.body = new SimpleStringProperty();
        this.body.set("");
        this.date = new SimpleStringProperty();
        this.date.set("");
        this.sentMail=new SimpleBooleanProperty();
        this.sentMail.set(false);
        this.lists=lists;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public SimpleBooleanProperty visibleProperty() {
        return visible;
    }

    public MaterialDesignIcon isFavourite() {
        return favourite.getValue();
    }

    public StarIcon favouriteProperty() {
        return favourite;
    }

    public String getTo() {
        return to.get();
    }

    public SimpleStringProperty toProperty() {
        return to;
    }

    public String getFrom() {
        return from.get();
    }

    public SimpleStringProperty fromProperty() {
        return from;
    }

    public String getSubject() {
        return subject.get();
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    public String getBody() {
        return body.get();
    }

    public SimpleStringProperty bodyProperty() {
        return body;
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }
    public boolean isSentMail() { return sentMail.get(); }

    public SimpleBooleanProperty sentMailProperty() { return sentMail; }

    public void setMail(MailMessageWrapper mail){
        this.mail=mail;
        Platform.runLater(()->{
            this.visible.set(true);
            if(mail.isFavourite())
                    this.favourite.setFavuorite();
                else
                    this.favourite.setNotFavuorite();

            this.body.set(generateBody(mail));
            this.from.set(mail.getSender());
            String recievers="";
            for (String m:
                    mail.getReceivers()) {
                recievers=recievers+m+", ";
            }
            recievers=recievers.substring(0,recievers.length()-2);
            this.to.set(recievers);
            this.subject.set(mail.getObject());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            this.date.set(formatter.format(mail.getSendDate()));
            this.sentMail.set(!Config.USER_MAIL.equals(mail.getSender()));
        });
    }


    /* FUNCTIONS USED TO CHANGE MAIL STATE */

    public void setFavourite(boolean fav){  //Change active mail favourite
        Platform.runLater(()-> {
            mail.setFavourite(fav); //Change the mail value
            //Change visualization proprieties
            if (fav) {
                favourite.setFavuorite();
                mail.starIconProperty().setFavuorite();
            } else {
                favourite.setNotFavuorite();
                mail.starIconProperty().setNotFavuorite();
            }
            this.lists.updateList();    //Update the persistency of the lists
        });
    }

    public void setSeen(boolean val){ //Change active mail seen
        Platform.runLater(()-> {
            if(getMail().isSeen()!=val){
                getMail().setSeen(val); //Change the mail value
                //Change visualization proprieties
                FontWeight fw=FontWeight.NORMAL;
                if(!val)
                     fw=FontWeight.BOLD;
                getMail().fontProprietyProperty().setFont(Font.font(Font.getDefault().getFamily(),fw, 16));
                this.lists.updateList(); //Update the persistency of the lists
            }
        });
    }

    public void hide(){
        this.visible.set(false);
    }

    public MailMessageWrapper getMail() {
        return mail;
    }

    private String generateBody(Mail mail){
        String result=mail.getText();
        if(mail.getRepliedFrom()!=null) {
            result+="\n\nIn risposta a "+mail.getRepliedFrom().getSender()+":\n";
            result += generateBody(mail.getRepliedFrom());
        }
        return result;
    }
}
