package Commons.Model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*

    Class that represents the mail object
    Used to store the mail persistently

 */

public class Mail implements Serializable {
    //Fundamentals fields
    private Integer id=null;
    private String sender;
    private String[] receivers;
    private String object;
    private String text;
    private Date sendDate;

    //Field used for technical stuff
    private Integer linkCount=null;
    private Mail  repliedFrom=null;

    public Mail(String sender, String[] receivers, String object, String text, Date sendDate) {
        this.sender = sender;
        this.receivers = receivers;
        this.object = object;
        this.text = text;
        this.sendDate = sendDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Integer getLinkCount() {
        return linkCount;
    }

    public void setLinkCount(Integer linkCount) {
        this.linkCount = linkCount;
    }

    public Mail getRepliedFrom() {
        return repliedFrom;
    }

    public void setRepliedFrom(Mail repliedFrom) {
        this.repliedFrom = repliedFrom;
    }

    public static boolean checkIsValid(String mail) {
        if(mail == null) return false;
        Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
        return matcher.find();
    }

    @Override
    public String toString() {
        return "Mail{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", receivers=" + Arrays.toString(receivers) +
                ", object='" + object + '\'' +
                ", text='" + text + '\'' +
                ", sendDate=" + sendDate +
                ", linkCount=" + linkCount +
                ", repliedFrom=" + repliedFrom +
                '}';
    }
}
