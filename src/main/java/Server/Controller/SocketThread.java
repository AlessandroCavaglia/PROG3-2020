package Server.Controller;

import Server.Model.Config;
import Server.Model.LogList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class SocketThread extends Thread {

    private ServerSocket serverSocket = null;
    private boolean run;
    private final int port;
    private final LogList logList;
    private ExecutorService executor;
    private LocksManager locksManager;

    public SocketThread(int port, LogList logList, LocksManager locksManager) {
        this.port = port;
        this.logList = logList;
        this.locksManager = locksManager;
    }

    public void run() {
        try {
            /* Creazione del thread pool e del server socket */
            run = true;
            executor = newFixedThreadPool(Config.THREAD_POOL_NUMBER);
            this.serverSocket = new ServerSocket(this.port);
            logList.addLog("Server partito");
            while (run) {
                try {

                    /* Attende una nuova richiesta e la fa eseguire a un thread del pool */
                    Socket socket = this.serverSocket.accept();
                    RequestHandler request = new RequestHandler(socket, logList, locksManager);
                    executor.execute(request);

                } catch (SocketException exception) {
                    if (run) {
                        if (Config.DEBUG)
                            exception.printStackTrace();
                        logList.addError("Socket accept failed: " + exception.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            if(Config.DEBUG)
                e.printStackTrace();
            stopServer(); // Non è nel finally perché per uscire dal while(run) è già stata invocata stopServer()
        }
    }

    public void stopServer() {
        logList.addLog("Server Stopped");
        run = false;
        try {
            if (serverSocket != null)
                serverSocket.close();

            if (executor != null) {
                //Interrompe le richieste
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(Config.POOL_SECONDS_TIMEOUT, TimeUnit.SECONDS)) {
                        executor.shutdownNow(); // Termina comunque l'esecuzione
                    }
                } catch (InterruptedException ie) {
                    // Termina comunque l'esecuzione se si verifica un problema
                    executor.shutdownNow();
                }

            }
        } catch (IOException exception) {
            if(Config.DEBUG)
                exception.printStackTrace();
            logList.addError("Stopping server: " + exception.getMessage());
        }
    }
}
