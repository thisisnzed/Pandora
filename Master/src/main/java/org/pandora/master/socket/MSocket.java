package org.pandora.master.socket;

import lombok.Getter;
import org.pandora.master.Master;
import org.pandora.master.decoding.DecodingManager;
import org.pandora.master.encoding.EncodingManager;
import org.pandora.master.utils.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class MSocket {

    private final String ip;
    private final int port;
    @Getter
    private final SocketHandler socketHandler;
    private final EncodingManager encodingManager;
    private final DecodingManager decodingManager;
    @Getter
    private PrintWriter printWriter;

    public MSocket(final Master master, final String ip, final String port) {
        this.ip = ip;
        this.encodingManager = master.getEncodingManager();
        this.decodingManager = master.getDecodingManager();
        this.port = Integer.parseInt(port);
        this.socketHandler = new SocketHandler(master);
    }

    public void infiniteConnect() {
        new Thread(() -> {
            while (!this.socketHandler.isConnected()) {
                this.socketHandler.close(null, false);
                this.read();
            }
        }).start();
    }

    public void read() {
        try {
            final Socket socket = new Socket(this.ip, this.port);
            final PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            this.printWriter = printWriter;
            printWriter.println("M-SRV:" + new String((this.encodingManager.encode("master-" + Settings.RANDOM)).getBytes(StandardCharsets.UTF_8)));
            printWriter.flush();
            final InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                try {
                    this.getSocketHandler().connected(true);
                    String line = bufferedReader.readLine();
                    if ((line == null) || line.equalsIgnoreCase("")) {
                        this.getSocketHandler().disconnected("by server");
                        socket.close();
                        return;
                    }
                    line = line.contains("all_masters") ? line.split(":")[0] + ":" + new String((this.decodingManager.decode(line.split(":")[2])).getBytes(StandardCharsets.UTF_8)) : line.split(":")[0] + ":" + new String((this.decodingManager.decode(line.split(":")[1])).getBytes(StandardCharsets.UTF_8));
                    if (line.startsWith("A:")) {
                        this.getSocketHandler().readFromAgent(line.replaceFirst("A:", ""));
                    } else if (line.startsWith("S:")) {
                        this.getSocketHandler().readFromServer(line.replaceFirst("S:", ""));
                    }
                } catch (final SocketException socketException) {
                    this.getSocketHandler().disconnected(socketException.getMessage());
                    return;
                } catch (final IOException ioException) {
                    return;
                }
            }
        } catch (final IOException ioException) {
            this.getSocketHandler().disconnected(ioException.getMessage());
        }
    }
}
