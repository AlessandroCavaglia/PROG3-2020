package Server.Model;

/*
    Class used to represent a logical link to the actual mail saved in it's archive
    This is also used to store the favourite and seen values of the mail
    This class isn't serializable because we need to reed the file backwards to improve
    performance and so we save it with our protocol
 */

public class MailReference {
    private Integer id;
    private String sender;
    private Boolean favourite;
    private Boolean seen;

    public MailReference(Integer id, String sender, Boolean favourite, Boolean seen) {
        this.id = id;
        this.sender = sender;
        this.favourite = favourite;
        this.seen = seen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMail() {
        return sender;
    }

    public void setMail(String mail) {
        this.sender = mail;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    @Override
    public String toString() {
        return "" +
                + id +
                "," + sender +
                "," + favourite +
                "," + seen;
    }
}
