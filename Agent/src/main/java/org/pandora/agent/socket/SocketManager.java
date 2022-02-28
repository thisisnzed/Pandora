package org.pandora.agent.socket;

import lombok.Getter;
import lombok.Setter;
import org.pandora.agent.Client;
import org.pandora.agent.socket.connection.Connection;

public class SocketManager {

    @Getter
    @Setter
    private String address;
    @Getter
    @Setter
    private int port;
    @Getter
    private final Connection connection;

    public SocketManager(final Client client) {
        this.address = "127.0.0.1";
        this.port = 11111;
        this.connection = new Connection(client, this);
    }
}
