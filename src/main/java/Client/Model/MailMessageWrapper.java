package Client.Model;

import Commons.Model.Mail;
import Commons.Model.MailMessage;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MailMessageWrapper extends MailMessage{
    private StarIcon starIcon;
    private FontPropriety fontPropriety;

    public MailMessageWrapper(MailMessage mailMessage) {
        super(mailMessage, mailMessage.isFavourite(), mailMessage.isSeen());
        starIcon=new StarIcon();
        fontPropriety=new FontPropriety();
        Platform.runLater(()->{
            if(!mailMessage.isSeen())
                fontPropriety.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16));
            if(mailMessage.isFavourite())
                starIcon.setFavuorite();
        });
    }

    public MaterialDesignIcon getStarIcon() {
        return starIcon.getValue();
    }

    public StarIcon starIconProperty() {
        return starIcon;
    }

    public Font getFontPropriety() {
        return fontPropriety.getValue();
    }

    public FontPropriety fontProprietyProperty() {
        return fontPropriety;
    }

    public class FontPropriety extends ObservableValueBase<Font> {
        Font font=null;
        FontPropriety(){
            this.font=Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 16);
        }
        @Override
        public Font getValue() {
            return font;
        }
        public void setFont(Font font){
            this.font=font;
            fireValueChangedEvent();
        }
    }

    public MailMessage asMailMessage(){
        Mail mail= new Mail(this.getSender(),this.getReceivers(),this.getObject(),this.getText(),this.getSendDate());
        mail.setId(this.getId());
        mail.setRepliedFrom(this.getRepliedFrom());
        return new MailMessage(mail,this.isFavourite(),this.isSeen());
    }
}
