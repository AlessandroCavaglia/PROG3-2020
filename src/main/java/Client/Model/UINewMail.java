package Client.Model;

import javafx.beans.property.SimpleStringProperty;

public class UINewMail {

    private SimpleStringProperty to;
    private SimpleStringProperty from;
    private SimpleStringProperty subject;
    private SimpleStringProperty body;

    public UINewMail() {
        this.to = new SimpleStringProperty();
        this.from = new SimpleStringProperty();
        this.subject = new SimpleStringProperty();
        this.body = new SimpleStringProperty();
    }

    public String getTo() {
        return to.get();
    }

    public SimpleStringProperty toProperty() {
        return to;
    }

    public void setTo(String to) {
        this.to.set(to);
    }

    public String getFrom() {
        return from.get();
    }

    public SimpleStringProperty fromProperty() {
        return from;
    }

    public void setFrom(String from) {
        this.from.set(from);
    }

    public String getSubject() {
        return subject.get();
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public String getBody() {
        return body.get();
    }

    public SimpleStringProperty bodyProperty() {
        return body;
    }

    public void setBody(String body) {
        this.body.set(body);
    }
}
