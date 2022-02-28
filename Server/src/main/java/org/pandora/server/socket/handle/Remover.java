package org.pandora.server.socket.handle;

import org.pandora.server.Server;
import org.pandora.server.data.UserData;
import org.pandora.server.data.UserManager;
import org.pandora.server.socket.HandlerManager;
import org.pandora.server.socket.TSocket;
import org.pandora.server.utils.TimeUtils;

import java.io.IOException;
import java.net.Socket;

public class Remover {

    private final Server server;
    private final HandlerManager handlerManager;

    public Remover(final HandlerManager handlerManager, final Server server) {
        this.server = server;
        this.handlerManager = handlerManager;
    }

    public void remove(final TSocket tSocket, final Socket socket, final String id, final boolean crashed) {
        if (!this.handlerManager.getChecker().isExists(socket, id)) {
            return;
        }
        final UserManager userManager = this.server.getUserManager();
        final UserData userData = userManager.getUserData(socket);
        if (userData == null) return;
        final String[] remoteSocketAddress = socket.getRemoteSocketAddress().toString().split(":");
        final String address = remoteSocketAddress[0].replace("/", "");
        final String port = remoteSocketAddress[1];
        final Updater updater = this.handlerManager.getUpdater();
        final boolean master = userData.isMaster();
        this.close(socket, userData.getThread());
        System.out.println(TimeUtils.getDate() + (master ? "(Master) Connection closed from " : "(Agent) Connection closed from ") + address + ":" + port + (crashed ? " (connection lost)" : ""));
        if (!master)
            updater.removeAgent(id);
        if(tSocket != null) {
            this.server.getExecutorRunnable().hashMap.remove(tSocket);
            this.server.getExecutorRunnable().hashMapBoolean.remove(tSocket);
        }
        userManager.delete(socket);
        updater.updateDashboard();
    }

    public void close(final Socket socket, final Thread thread) {
        try {
            if (socket.isConnected() && !socket.isClosed()) socket.close();
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        }
        if (!thread.isInterrupted() && thread.isAlive()) thread.interrupt();
    }
}
