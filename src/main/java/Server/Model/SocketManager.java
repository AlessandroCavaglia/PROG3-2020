package Server.Model;

import Server.Controller.LocksManager;
import Server.Controller.SocketThread;
import Server.Model.LogList;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class SocketManager {
    private int port;
    private SimpleBooleanProperty running;
    private SimpleBooleanProperty stopped;
    private SocketThread socketThread=null;
    private LocksManager locksManager;
    private LogList logList;

    public SocketManager(int port, LogList logList, LocksManager locksManager) {
        this.locksManager = locksManager;
        running=new SimpleBooleanProperty();
        stopped=new SimpleBooleanProperty();
        this.port = port;
        running.set(false);
        stopped.set(true);
        this.logList=logList;
    }

    public boolean isStopped() {
        return stopped.get();
    }

    public SimpleBooleanProperty stoppedProperty() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped.set(stopped);
    }

    public void start(){
        this.socketThread=new SocketThread(this.port,this.logList, this.locksManager);
        socketThread.start();
        running.set(true);
        stopped.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }

    public SimpleBooleanProperty runningProperty() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }
    public void stopServer()  {
        if(socketThread!=null){
            socketThread.stopServer();
            socketThread=null;
        }
        running.set(false);
        stopped.set(true);
    }
}
