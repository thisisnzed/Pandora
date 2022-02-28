package org.pandora.server.socket.handle;

import org.pandora.server.Server;
import org.pandora.server.data.UserManager;

import java.net.Socket;

public class Checker {

    private final Server server;

    public Checker(final Server server) {
        this.server = server;
    }

    public boolean isExists(final Socket socket, final String id) {
        final UserManager userManager = this.server.getUserManager();
        return userManager.getUserData(socket) != null || userManager.getUserData(id) != null;
    }
}
