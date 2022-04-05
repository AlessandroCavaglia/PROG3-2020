package Client.Model;

public class Config {

    public static String CLIENT_STORAGE = "clientData/";
    public static String CLIENT_SENT_LIST =  "sentList.obj";
    public static String CLIENT_RECEIVED_LIST = "receivedList.obj";
    public static String CONFIG_LOCATION = "ClientConfig.txt";

    public static int RECEIVED_LIST = 0;
    public static int SENT_LIST = 1;

    public static String USER_MAIL = "a@a.aa";
    public static String ADRESS = "localhost";
    public static int PORT = 8080;
    public static int THREAD_POOL_NUMBER = 3;
    public static int REFRESH_TIME = 5;
    public static boolean DEBUG = false;

    public static final int SEND_MAIL=0;
    public static final int REPLY_TO=1;
    public static final int REPLY_ALL=2;
    public static final int FORWARD=3;

}
