package org.pandora.server.data;

import org.pandora.server.Server;

import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserManager {

    private final ConcurrentLinkedQueue<UserData> userData;

    public UserManager(final Server server) {
        this.userData = server.userData;
    }

    public Optional<UserData> getUser(final Socket socket) {
        return this.userData.stream().filter(skt -> socket.equals(skt.getSocket())).findFirst();
    }

    public void delete(final Socket socket) {
        this.getUser(socket).ifPresent(this.userData::remove);
    }

    public UserData getUserData(final Socket socket) {
        return this.userData.stream().filter(userData -> userData.getSocket().equals(socket)).findFirst().orElse(null);
    }

    public UserData getUserData(final String id) {
        return this.userData.stream().filter(userData -> userData.getIdentifier().equals(id)).findFirst().orElse(null);
    }
}