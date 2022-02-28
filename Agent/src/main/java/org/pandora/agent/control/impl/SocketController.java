package org.pandora.agent.control.impl;

import org.pandora.agent.socket.connection.Connection;

import java.io.IOException;
import java.net.Socket;

public class SocketController {

    public void restart(final Connection connection, final Socket socket) {
        try {
            socket.close();
            connection.setConnected(false);
        } catch (final IOException ignore) {
        }
    }
}
