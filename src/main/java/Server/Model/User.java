package Server.Model;

import java.io.Serializable;
/*
    This class represents the fundamental data of a User
    It's used to store and get data for persistence
    Path represents the path to the main folder of the user's mail box
 */
public class User implements Serializable {
    private String mail;
    private String path;

    public User(String mail, String path) {
        this.mail = mail;
        this.path = path;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "User{" +
                "mail='" + mail + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
