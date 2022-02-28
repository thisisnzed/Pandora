package org.pandora.server.runnable;

import org.pandora.server.Server;
import org.pandora.server.data.UserManager;
import org.pandora.server.socket.TSocket;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorRunnable {

    public final ConcurrentHashMap<TSocket, Long> hashMap;
    public final ConcurrentHashMap<TSocket, Boolean> hashMapBoolean;
    private final Server server;

    public ExecutorRunnable(final Server server) {
        this.hashMap = new ConcurrentHashMap<>();
        this.hashMapBoolean = new ConcurrentHashMap<>();
        this.server = server;
    }

    public void start() {
        final UserManager userManager = this.server.getUserManager();
        final Runnable runnable = () -> this.hashMap.keySet().forEach(tSocket -> {
            final Socket socket = tSocket.getSocket();
            if (userManager.getUserData(socket) != null) {
                final long lastActivity = this.hashMap.get(tSocket);
                if (lastActivity != -1 && (lastActivity < System.currentTimeMillis() - 100000)) {
                    if (!tSocket.isInterrupted() && !socket.isClosed() && this.server.getUserManager().getUserData(socket) != null) {
                        if (!this.hashMapBoolean.get(tSocket)) {
                            this.hashMapBoolean.put(tSocket, true);
                            this.server.getHandlerManager().getRemover().remove(null, socket, userManager.getUserData(socket).getIdentifier(), true);
                            this.remove(tSocket);
                        }
                    } else this.remove(tSocket);
                }
            } else this.remove(tSocket);
        });
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 0, 120, TimeUnit.SECONDS);
    }

    private void remove(final TSocket tSocket) {
        this.hashMap.remove(tSocket);
        this.hashMapBoolean.remove(tSocket);
    }
}
