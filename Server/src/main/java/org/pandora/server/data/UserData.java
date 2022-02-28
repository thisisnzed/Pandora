package org.pandora.server.data;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class UserData {

    @Getter
    @Setter
    private Socket socket;
    @Getter
    @Setter
    private String identifier, data;
    @Getter
    @Setter
    private Thread thread;
    @Getter
    @Setter
    private PrintWriter printWriter;
    @Getter
    @Setter
    private boolean master;

    public UserData(Thread thread, Socket socket, boolean master, String identifier, String data) {
        this.socket = socket;
        this.thread = thread;
        this.master = master;
        this.identifier = identifier;
        this.data = data;
        try {
            this.printWriter = new PrintWriter(socket.getOutputStream());
        } catch (final IOException ignore) {
            this.printWriter = null;
        }
    }
}
