package DebugUtils;

import Commons.Model.Mail;
import Commons.Model.Request;
import Commons.Model.RequestAction;
import Commons.Model.Response;
import Server.Model.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class Cliento {

    public static void main(String[] args) throws Exception {

        InetAddress addr = InetAddress.getLocalHost();
        Socket socket = new Socket(addr, Config.PORT);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());


//        List<Mail> emails = new ArrayList<>();

        Mail email = new Mail("a@a.aa", new String[]{"b@b.bb"}, "RE:RE:mailStockns", "STONKS?", new Date());
        Mail mail=new Mail("b@b.bb",new String[]{""},"","",new Date());
        mail.setId(17);
        email.setRepliedFrom(mail);
//        emails.add(new Mail(0, "b@b.bb", new String[]{"a@a.a"}, "", "", new Date(), 2, 1));
//        emails.add(new Mail(0, "a@a.aa", new String[]{"c@c.c"}, "", "", new Date(), 3, 3));
//        emails.add(new Mail(0, "b@b.bb", new String[]{"a@a.a"}, "", "", new Date(), 4, 4));

        //oos.writeObject(new Request("a@a.aa", RequestAction.SEND_EMAIL, email));
        //oos.flush();

        oos.writeObject(new Request("a@a.aa", RequestAction.SEND_EMAIL, email));
        //oos.writeObject(new Request("a@a.aa", RequestAction.DELETE_EMAIL, 1));
        oos.flush();

        Response res=(Response) ois.readObject();
        System.out.println(res);

        System.out.println(((ArrayList<Mail>)(res.getData())).size());
        socket.close();
    }

}
