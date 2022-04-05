package Client.Controller;

import Client.Model.MailMessageWrapper;
import Client.Model.UIMailList;
import Commons.Model.*;
import Client.Model.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RequestManager {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private boolean connected = false;

    public RequestManager() {
        try {
            this.socket = new Socket(Config.ADRESS, Config.PORT);
            if (!this.openStream())
                closeConnection();

        } catch (IOException e) {
            closeConnection();
            if (Config.DEBUG)
                e.printStackTrace();
        }
        // Non c'è la closeConnection() nel finally perchè se no si chiudono subito gli stream
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean setSeen(int id) {
        return simpleInteraction(new Request(Config.USER_MAIL, RequestAction.SET_EMAIL_SEEN, id)).getResponseAction() == ResponseAction.OK;
    }

    public boolean setNotSeen(int id) {
        return simpleInteraction(new Request(Config.USER_MAIL, RequestAction.SET_EMAIL_NOT_SEEN, id)).getResponseAction() == ResponseAction.OK;
    }

    public boolean setFavourite(int id) {
        return simpleInteraction(new Request(Config.USER_MAIL, RequestAction.SET_EMAIL_FAVOURITE, id)).getResponseAction() == ResponseAction.OK;
    }

    public boolean setNotFavourite(int id) {
        return simpleInteraction(new Request(Config.USER_MAIL, RequestAction.SET_EMAIL_NOT_FAVOURITE, id)).getResponseAction() == ResponseAction.OK;
    }

    public boolean deleteEmail(int id) {
        return simpleInteraction(new Request(Config.USER_MAIL, RequestAction.DELETE_EMAIL, id)).getResponseAction() == ResponseAction.OK;
    }

    public Response sendEmail(Mail mail) {
        return simpleInteraction(new Request(Config.USER_MAIL, RequestAction.SEND_EMAIL, mail));
    }

    public Response simpleInteraction(Request request) {
        Response response = new Response(ResponseAction.INTERNAL_ERROR, "Server offline, riprova più tardi");
        try {
            this.out.writeObject(request);
            this.out.flush();

            Object o = this.in.readObject();
            if (o != null && o.getClass() == Response.class)
                response = (Response) o;

        } catch (IOException | ClassNotFoundException e) {
            if (Config.DEBUG)
                e.printStackTrace();
        } finally {
            closeConnection();
        }
        return response;
    }


    public List<MailMessageWrapper> getNewMail(int id) {
        Request request = new Request(Config.USER_MAIL, RequestAction.GET_NEW_EMAILS, id);
        List<MailMessageWrapper> result = null;
        Response response = null;

        try {
            this.out.writeObject(request);
            this.out.flush();

            Object o = this.in.readObject();
            if (o != null && o.getClass() == Response.class)
                response = (Response) o;
            if (response == null)
                System.out.println("Errore");

            result = new ArrayList<>();
            Object object = response.getData();
            if (object != null && object.getClass() == ArrayList.class) {
                ArrayList tmp = (ArrayList) object;
                for (Object obj : tmp) {
                    if (obj != null && obj.getClass() == MailMessage.class) {
                        result.add(new MailMessageWrapper((MailMessage) obj));
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            if (Config.DEBUG)
                e.printStackTrace();
        } finally {
            closeConnection();
        }
        return result;
    }

    public List<MailMessageWrapper> getSentMail() {
        Request request = new Request(Config.USER_MAIL, RequestAction.GET_ALL_SENT_EMAILS, null);
        List<MailMessageWrapper> result = null;
        Response response = null;

        try {
            this.out.writeObject(request);
            this.out.flush();

            Object o = this.in.readObject();
            if (o != null && o.getClass() == Response.class)
                response = (Response) o;
            if (response == null)
                System.out.println("Errore");


            result = new ArrayList<>();
            Object object = response.getData();
            if (object != null && object.getClass() == ArrayList.class) {
                ArrayList tmp = (ArrayList) object;
                for (Object obj : tmp) {
                    if (obj != null && obj.getClass() == MailMessage.class) {
                        result.add(new MailMessageWrapper((MailMessage) obj));
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            if (Config.DEBUG)
                e.printStackTrace();
        } finally {
            closeConnection();
        }
        return result;
    }

    private boolean openStream() {
        try {
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            if (Config.DEBUG)
                e.printStackTrace();
            return false;
        }
        connected = true;
        return true;
    }

    private void closeConnection() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
            connected = false;
        } catch (IOException e) {
            if (Config.DEBUG)
                e.printStackTrace();
        }
    }

//    public Response getNewEmails() {
//        Object data = this.request.getData();
//        if (data != null && data.getClass() == Integer.class) {
//            int id = (Integer) data;        //We get the integer value of the last recieved mail, we don't need to do any controls on it
//        this.request = new Request(Config.USER_MAIL, RequestAction.GET_NEW_EMAILS, id);
//
//        return null;
//    }

}
