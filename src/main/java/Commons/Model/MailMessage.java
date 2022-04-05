package Commons.Model;

import java.io.Serializable;

/*
    Class used to represent a Mail
    with it's information (Seen,Favourite)
    Used to send full mail information to the client
    and used to save data on the client persistently
 */
public class MailMessage extends Mail implements Serializable {
    private boolean favourite;
    private boolean seen;
    public MailMessage(Mail mail,boolean favourite,boolean seen) {
        super(mail.getSender(),mail.getReceivers(), mail.getObject(), mail.getText(), mail.getSendDate());
        this.setId(mail.getId());
        this.setRepliedFrom(mail.getRepliedFrom());
        this.favourite=favourite;
        this.seen=seen;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    @Override
    public String toString() {
        return "MailMessage{" +
                "favourite=" + favourite +
                ", seen=" + seen +
                super.toString()+
                '}';
    }
}
