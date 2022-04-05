package Server.Controller;

import Commons.Model.*;
import Server.Model.*;
import Commons.Dao.FileManager;
import Server.Model.Dao.MailReferenceFileManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequestHandler implements Runnable {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Socket socket;
    private LogList logList;
    private LocksManager locksManager;

    public RequestHandler(Socket socket, LogList logList, LocksManager locksManager) {
        this.socket = socket;
        this.logList = logList;
        this.locksManager = locksManager;
    }

    @Override
    public void run() {
        try {
            logList.addLog("Connessione aperta");
            if (!openStream()) {
                closeConnection();
                return;
            }
            Request request;
            Object requestRaw = in.readObject();
            if (requestRaw != null && requestRaw.getClass() == Request.class) {
                request = (Request) requestRaw;
                out.writeObject(handleAction(request));
            } else {
                logList.addError("Abbiamo ricevuto una richiesta malformata");
                out.writeObject(new Response(ResponseAction.MALFORMED_REQUEST, null));
            }
            out.flush();
        } catch (IOException | ClassNotFoundException e) {
            logList.addError("C'è stata una eccezione nella gestione della richiesta");
            if (Config.DEBUG)
                e.printStackTrace();
        } finally {
            closeConnection();
            logList.addLog("Connessione chiusa");
        }
    }

    private Response handleAction(Request request) {
        if (!locksManager.userExist(request.getUser())) {               //Check if the user that made the request actually exists
            logList.addError("Mittente della richiesta sconosciuto");
            return new Response(ResponseAction.UNAUTHORIZED, null);
        }
        Response result = null;
        switch (request.getAction()) {
            case GET_NEW_EMAILS:
                result = manageGetNewEmails(request);
                break;
            case SEND_EMAIL:
                result = manageSendEmail(request);
                break;
            case DELETE_EMAIL:
                result = manageDeleteEmail(request);
                break;
            case SET_EMAIL_SEEN:
                result = manageSetSeen(request);
                break;
            case SET_EMAIL_NOT_SEEN:
                result = manageSetNotSeen(request);
                break;
            case SET_EMAIL_FAVOURITE:
                result = manageSetFavourite(request);
                break;
            case SET_EMAIL_NOT_FAVOURITE:
                result = manageSetNotFavourite(request);
                break;
            case GET_ALL_SENT_EMAILS:
                result = manageGetSentEmails(request);
                break;
        }
        return result;
    }


    private Response manageSendEmail(Request request) {
        Object data = request.getData();
        if (data != null && data.getClass() == Mail.class) {

            Mail mail = (Mail) data;
            mail.setSendDate(new Date());   // Imposta data di invio
            String[] receivers = mail.getReceivers();   // Imposta numero di link alla mail (mittente + destinatari)
            int receiversCount = receivers == null ? 0 : receivers.length;
            mail.setLinkCount(receiversCount + 1);

            /* CONTROLS */

            //Check if the user that sent the mail is the same one that sent the request
            if (!Mail.checkIsValid(mail.getSender()) || !mail.getSender().equals(request.getUser())) {
                logList.addError("Il mittente della richiesta era malformato");
                return new Response(ResponseAction.WRONG_SENDER, "Errore interno");
            }

            //Check if there is at least one sender
            if (mail.getReceivers().length <= 0) {
                logList.addError("I riceventi della richiesta sono malformati");
                return new Response(ResponseAction.WRONG_RECEIVER, "E' necessario inserire almeno un destinatario");
            }

            //Check each reciever is well formed and also if it exists in our system
            for (String reciever : mail.getReceivers()) {
                if (!Mail.checkIsValid(reciever) || !locksManager.userExist(reciever)) {
                    logList.addError("I riceventi della richiesta sono malformati");
                    return new Response(ResponseAction.WRONG_RECEIVER, "Un destinatario non esiste");
                }
            }

            //Check if the email actually contains content
            if (mail.getText() == null || mail.getText().length() <= 0) {
                logList.addError("Il testo della richiesta era malformato");
                return new Response(ResponseAction.WRONG_MAIL_CONTENT, "Il corpo della mail e' malformato");
            }

            // TODO: FINIRE CONTROLLI

            if (mail.getRepliedFrom() != null) {        //This mail is sent in response of another one
                Mail original = mail.getRepliedFrom();        //We get the parent mail
                MailReference mailReference = new MailReference(original.getId(), original.getSender(), false, false);   //We build a temporary mailReference
                locksManager.getUserWithLocks(mail.getSender()).lockRead();
                Mail storedMail = MailReferenceFileManager.getMail(mailReference, locksManager);     //We try to load the parent mail from fileSystem
                locksManager.getUserWithLocks(mail.getSender()).releaseRead();
                if (storedMail != null) {       //If we found the mail on our server
                    if (Arrays.asList(storedMail.getReceivers()).contains(request.getUser()) || storedMail.getSender().equals(request.getUser())) {   //If we are either on of the receivers or the sender
                        mail.setRepliedFrom(storedMail);    //We set our stored mail as the parent to make sure it's the original mail
                    }
                } else {
                    logList.addError("La richiesta rispondeva a una mail malformata");
                    return new Response(ResponseAction.MALFORMED_REQUEST, "Errore interno");
                }
            }

            mail.setId(FileManager.getID());
            MailReference mailReference = new MailReference(mail.getId(), mail.getSender(), false, false);

            /* Scrittura della mail nella lista delle mail inviate */
            UserWithLocks currentUser = locksManager.getUserWithLocks(mail.getSender());
            currentUser.lockWrite();
            if (!FileManager.appendFile(mailReference.toString(), locksManager.getPath(mail.getSender(), Config.SENT_FILE))) {
                logList.addError("Problema nell'interazione con i file");
                return new Response(ResponseAction.INTERNAL_ERROR, "Errore interno");
            }
            String pathMail = locksManager.getPath(mail.getSender(), Config.USER_MAILBOX) + mail.getId() + ".mail";
            if (!FileManager.createFile(pathMail) || !FileManager.writeObject(mail, pathMail)) {
                logList.addError("Problema nell'interazione con i file");
                return new Response(ResponseAction.INTERNAL_ERROR, "Errore interno");
            }
            currentUser.releaseWrite();

            /* Scrittura della mail nella lista delle mail ricevute per ogni mittente */
            for (String reciever : mail.getReceivers()) {
                currentUser = locksManager.getUserWithLocks(reciever);
                currentUser.lockWrite();
                if (!FileManager.appendFile(mailReference.toString(), locksManager.getPath(reciever, Config.RECEIVED_FILE))) {
                    logList.addError("Problema nell'interazione con i file");
                    return new Response(ResponseAction.INTERNAL_ERROR, "Errore interno");
                }
                currentUser.releaseWrite();
            }
            logList.addLog("Una mail è stata inviata corretamente da " + request.getUser());
            return new Response(ResponseAction.OK, mail);
        } else {
            logList.addError("La richiesta conteneva una mail malformata");
            return new Response(ResponseAction.MALFORMED_REQUEST, "Errore interno");
        }
    }

    private Response manageSetNotFavourite(Request request) {
        return manageAlterMailReference(request, false, "changeFavourite",Config.RECEIVED_FILE);
    }

    private Response manageSetFavourite(Request request) {
        return manageAlterMailReference(request, true, "changeFavourite",Config.RECEIVED_FILE);
    }

    private Response manageSetNotSeen(Request request) {
        return manageAlterMailReference(request, false, "changeSeen",Config.RECEIVED_FILE);
    }

    private Response manageSetSeen(Request request) {
        return manageAlterMailReference(request, true, "changeSeen",Config.RECEIVED_FILE);
    }

    private Response manageAlterMailReference(Request request, boolean value, String action,int type) {

        Object data = request.getData();
        Response response = null;
        if (data != null && data.getClass() == Integer.class) {
            int mailID = (Integer) data;
            UserWithLocks currentUser = locksManager.getUserWithLocks(request.getUser());
            currentUser.lockWrite();
            MailReference currentMailReference = MailReferenceFileManager.getMailReference(mailID, locksManager.getPath(request.getUser(), type));
            if (currentMailReference != null) {
                System.out.println(currentMailReference.toString());
                boolean newFavourite = currentMailReference.getFavourite(),
                        newSeen = currentMailReference.getSeen(),
                        newDelete = false;
                if (action.equals("changeFavourite")) {
                    newFavourite = value;
                }
                if (action.equals("changeSeen")) {
                    newSeen = value;
                }
                if (action.equals("Delete")) {            //If we delete
                    if (!currentMailReference.getMail().equals(request.getUser())) {  //If the user that has sent the mail isn't us
                        locksManager.getUserWithLocks(currentMailReference.getMail()).lockWrite();      //We get the lock
                    }
                    Mail mail = MailReferenceFileManager.getMail(currentMailReference, locksManager);      //We get the mail
                    if (mail != null) {
                        mail.setLinkCount(mail.getLinkCount() - 1);                                       //We decrease the value
                        MailReferenceFileManager.writeObject(mail, locksManager.getPath(mail.getSender(), Config.USER_MAILBOX) + mail.getId() + ".mail"); //We re-serialize it
                    }
                    if (!currentMailReference.getMail().equals(request.getUser())) { //If the user that has sent the mail isn't us
                        locksManager.getUserWithLocks(currentMailReference.getMail()).releaseWrite();  //We release the lock
                    }
                    newDelete = true;
                }
                MailReferenceFileManager.alterMail(mailID, locksManager.getPath(request.getUser(), type), newFavourite, newSeen, newDelete);
                response = new Response(ResponseAction.OK, null);
            } else {
                response = new Response(ResponseAction.INTERNAL_ERROR, null);
            }
            currentUser.releaseWrite();
        } else {
            logList.addError("Problema nell'interazione con i file");
            response = new Response(ResponseAction.MALFORMED_REQUEST, null);
        }
        return response;
    }

    private Response manageDeleteEmail(Request request) {
        Response res1=manageAlterMailReference(request, true, "Delete",Config.RECEIVED_FILE);
        Response res2=manageAlterMailReference(request, true, "Delete",Config.SENT_FILE);
        if(res1.getResponseAction()!=ResponseAction.OK && res2.getResponseAction()!=ResponseAction.OK){
            return res1;
        }else if(res1.getResponseAction()==ResponseAction.OK)
                return res1;
            else
                return res2;
    }

    private Response manageGetNewEmails(Request request) {
        Object data = request.getData();
        if (data != null && data.getClass() == Integer.class) {
            int id = (Integer) data;        //We get the integer value of the last recieved mail, we don't need to do any controls on it
            /* Lettura delle mail ricevute */
            List<MailMessage> mailArray = new ArrayList<>();
            UserWithLocks currentUser = locksManager.getUserWithLocks(request.getUser());
            currentUser.lockRead();
            List<MailReference> mailReferences = MailReferenceFileManager.readFile(id, locksManager.getPath(request.getUser(), Config.RECEIVED_FILE));
            currentUser.releaseRead();

            for (MailReference mail : mailReferences) {

                /* Lettura delle singole mail, ottieni lock per l'utente mittente */
                currentUser = locksManager.getUserWithLocks(mail.getMail());
                currentUser.lockRead();
                Mail tmp = MailReferenceFileManager.getMail(mail, locksManager);
                currentUser.releaseRead();

                if (tmp != null)
                    mailArray.add(new MailMessage(tmp, mail.getFavourite(), mail.getSeen()));
                else
                    logList.addError("Problema nell'interazione con i file");
            }
            logList.addLog("L'utente " + request.getUser() + " ha richiesto le sue nuove mail e ne ha ricevute " + mailArray.size());
            return new Response(ResponseAction.OK, mailArray);
        } else {
            logList.addError("La richiesta conteneva un id malformato");
            return new Response(ResponseAction.MALFORMED_REQUEST, null);
        }
    }

    private Response manageGetSentEmails(Request request) {
        /* Lettura delle mail inviate */
        List<MailMessage> mailArray = new ArrayList<>();
        UserWithLocks currentUser = locksManager.getUserWithLocks(request.getUser());
        currentUser.lockRead();
        List<MailReference> mailReferences = MailReferenceFileManager.readFile(-1, locksManager.getPath(request.getUser(), Config.SENT_FILE));
        for (MailReference mail : mailReferences) {
            Mail tmp = MailReferenceFileManager.getMail(mail, locksManager);
            if (tmp != null)
                mailArray.add(new MailMessage(tmp, mail.getFavourite(), mail.getSeen()));
            else
                logList.addError("Problema nell'interazione con i file");
        }
        currentUser.releaseRead();
        logList.addLog("L'utente " + request.getUser() + " ha richiesto le sue  mail inviate e ne ha ricevute " + mailArray.size());
        return new Response(ResponseAction.OK, mailArray);
    }

    private boolean openStream() {
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            logList.addError("Apertura degli stream del socket fallita");
            if (Config.DEBUG)
                e.printStackTrace();
            return false;
        }
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
        } catch (IOException e) {
            logList.addError("Chiusura degli stream del socket fallita");
            if (Config.DEBUG)
                e.printStackTrace();
        }
    }

}
