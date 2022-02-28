package org.pandora.agent.utils;

import org.pandora.agent.Client;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class SocketUtils {

    private final Client client;

    public SocketUtils(final Client client) {
        this.client = client;
    }

    public void write(final String text) {
        if (this.client.getSocketManager().getConnection().getPrintWriter() == null) return;
        final PrintWriter printWriter = this.client.getSocketManager().getConnection().getPrintWriter();
        printWriter.println("A:" + "all_masters" + ":" + new String((this.client.getEncodingManager().encode(text)).getBytes(StandardCharsets.UTF_8)));
        printWriter.flush();
    }
}