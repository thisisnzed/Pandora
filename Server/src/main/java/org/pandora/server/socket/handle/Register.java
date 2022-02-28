package org.pandora.server.socket.handle;

import org.pandora.server.Server;
import org.pandora.server.data.UserData;
import org.pandora.server.runnable.ExecutorRunnable;
import org.pandora.server.socket.HandlerManager;
import org.pandora.server.socket.TSocket;
import org.pandora.server.utils.TimeUtils;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Register {

    private final Server server;
    private final HandlerManager handlerManager;

    public Register(final HandlerManager handlerManager, final Server server) {
        this.server = server;
        this.handlerManager = handlerManager;
    }

    public void register(final TSocket tSocket, final ExecutorRunnable executorRunnable, final boolean master, final Socket socket, final Thread thread, final String id, final String data) {
        if (this.handlerManager.getChecker().isExists(socket, id)) {
            this.handlerManager.getRemover().close(socket, thread);
            return;
        }
        final String[] remoteSocketAddress = socket.getRemoteSocketAddress().toString().split(":");
        final String address = remoteSocketAddress[0].replace("/", "");
        final String port = remoteSocketAddress[1];
        final Updater updater = this.handlerManager.getUpdater();
        if (master) {
            final String allowed = this.server.getConfiguration().getAllowed();
            if (allowed.contains(";" + address + ";") || allowed.equalsIgnoreCase("all")) {
                this.server.userData.add(new UserData(thread, socket, true, id, "master"));
                System.out.println(TimeUtils.getDate() + "(Master) Connection established from " + address + ":" + port);
                this.server.userData.stream().filter(userData -> !userData.isMaster()).forEach(userData -> {
                    final String[] userDataSocket = userData.getSocket().getRemoteSocketAddress().toString().split(":");
                    this.server.getHandlerManager().getWriter().writeToMaster(id, "S", new String((this.server.getEncodingManager().encode("addAgent:" + userDataSocket[0].replace("/", "") + ":" + userDataSocket[1] + ":" + userData.getIdentifier().replace(":", "") + ":" + userData.getData())).getBytes(StandardCharsets.UTF_8)));
                });
            } else {
                System.out.println(TimeUtils.getDate() + "(Master) Connection refused from " + address + " due to forbidden IP");
                return;
            }
        } else {
            this.server.userData.add(new UserData(thread, socket, false, id, data));
            updater.addAgent(id, data, address, port);
            System.out.println(TimeUtils.getDate() + "(Agent) Connection established from " + address + ":" + port);
            executorRunnable.hashMapBoolean.put(tSocket, false);
            executorRunnable.hashMap.put(tSocket, -1L);
        }
        updater.updateDashboard();
    }
}
