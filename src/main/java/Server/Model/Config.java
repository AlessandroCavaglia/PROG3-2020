package Server.Model;

public class Config {
    public static String SERVER_STORAGE = "serverData/";


    public static String ID_FILE = SERVER_STORAGE + "mailId.txt";
    public static String USER_STORAGE = SERVER_STORAGE + "userData/";
    public static String SERVER_USER_LIST = SERVER_STORAGE + "storage.obj";
    public static String RECEIVED_LIST = "receivedMessages.csv";
    public static String SENT_LIST = "sentMessages.csv";
    public static String MAIL_STORAGE = "mailStorage/";
    public static String LOG_FILE = SERVER_STORAGE + "logfile.txt";
    public static String CONFIG = "ServerConfig.txt";
    public static boolean DEBUG = true;

    /* Server Requests */
    public static int THREAD_POOL_NUMBER = 5;
    public static int POOL_SECONDS_TIMEOUT = 10;
    public static int PORT = 8080;

    public static final int RECEIVED_FILE = 1;
    public static final int SENT_FILE = 2;
    public static final int USER_MAILBOX = 3;

    public static void setServerStorage(String serverStorage) {
        String oldServer = SERVER_STORAGE;
        SERVER_STORAGE = serverStorage + "/";
        setUserStorage(USER_STORAGE.substring(oldServer.length()));
        setServerUserList(SERVER_USER_LIST.substring(oldServer.length()));
        setLogFile(LOG_FILE.substring(oldServer.length()));
        setIdFile(ID_FILE.substring(oldServer.length()));
    }

    public static void setUserStorage(String userStorage) {
        USER_STORAGE = SERVER_STORAGE + userStorage + "/";
    }

    public static void setServerUserList(String serverUserList) {
        SERVER_USER_LIST = SERVER_STORAGE + serverUserList;
    }

    public static void setReceivedList(String receivedList) {
        RECEIVED_LIST = receivedList;
    }

    public static void setSentList(String sentList) {
        SENT_LIST = sentList;
    }

    public static void setMailStorage(String mailStorage) {
        MAIL_STORAGE = mailStorage + "/";
    }

    public static void setLogFile(String logFile) {
        LOG_FILE = SERVER_STORAGE + logFile;
    }

    public static void setThreadPoolNumber(int threadPoolNumber) {
        THREAD_POOL_NUMBER = threadPoolNumber;
    }

    public static void setPoolSecondsTimeout(int poolSecondsTimeout) {
        POOL_SECONDS_TIMEOUT = poolSecondsTimeout;
    }

    public static void setPORT(int PORT) {
        Config.PORT = PORT;
    }

    public static void setIdFile(String idFile) {
        ID_FILE = SERVER_STORAGE + idFile;
    }
}
