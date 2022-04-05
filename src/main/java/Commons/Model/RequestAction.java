package Commons.Model;

import java.io.Serializable;
/*
    Used to represent the codes of the different requests

 */


public enum RequestAction implements Serializable {

    GET_NEW_EMAILS(1),
    SEND_EMAIL(2),
    DELETE_EMAIL(3),
    SET_EMAIL_SEEN(4),
    SET_EMAIL_NOT_SEEN(5),
    SET_EMAIL_FAVOURITE(6),
    SET_EMAIL_NOT_FAVOURITE(7),
    GET_ALL_SENT_EMAILS(8);

    public int code;

    RequestAction(int code) {this.code=code;}
}
