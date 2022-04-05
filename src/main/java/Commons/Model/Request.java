package Commons.Model;

import java.io.Serializable;
/*
    Object used to tell the server what he has to do passing optional data

 */


public class Request implements Serializable {

    private String user;
    private RequestAction requestAction;
    private Object data;

    public Request(String utente, RequestAction requestAction, Object data) {
        this.user = utente;
        this.requestAction = requestAction;
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String utente) {
        this.user = utente;
    }

    public RequestAction getAction() {
        return requestAction;
    }

    public void setAction(RequestAction requestAction) {
        this.requestAction = requestAction;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Request{" +
                "user='" + user + '\'' +
                ", requestAction=" + requestAction +
                ", data=" + data +
                '}';
    }
}
