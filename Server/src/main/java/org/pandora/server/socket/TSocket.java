package org.pandora.server.socket;

import lombok.Getter;
import org.pandora.server.Server;
import org.pandora.server.decoding.DecodingManager;
import org.pandora.server.runnable.ExecutorRunnable;
import org.pandora.server.utils.TimeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class TSocket extends Thread {

    private final Server server;
    private final DecodingManager decodingManager;
    private final HandlerManager handlerManager;
    @Getter
    public final Socket socket;
    private final boolean debug;
    private final ExecutorRunnable executorRunnable;
    private BufferedReader bufferedReader;

    public TSocket(final Server server, final Socket socket) {
        this.server = server;
        this.debug = Boolean.parseBoolean(server.getConfiguration().getDebug());
        this.decodingManager = server.getDecodingManager();
        this.handlerManager = server.getHandlerManager();
        this.executorRunnable = server.getExecutorRunnable();
        this.socket = socket;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (final IOException ioException) {
            ioException.printStackTrace();
            System.out.println(TimeUtils.getDate() + "Connection error (IO Exception - B) : " + ioException.getMessage() + " | " + ioException.getCause().toString());
        }
    }

    public void run() {
        while (true) {
            try {
                final String line = this.bufferedReader.readLine();
                if (this.debug) System.out.println("[DEBUG/INFO] " + line);
                if (line == null || line.equals("")) {
                    this.socket.close();
                    return;
                } else {
                    if (line.equals("sendActivity")) {
                        this.executorRunnable.hashMap.put(this, System.currentTimeMillis());
                    } else if (line.startsWith("A-SRV:")) { //-> Agent to Server Only
                        this.handlerManager.getRegister().register(this, this.executorRunnable, false, this.socket, this, new String((this.decodingManager.decode(line.split(":")[1])).getBytes(StandardCharsets.UTF_8)), this.decodingManager.decode(line.split(":")[2]));
                    } else if (line.startsWith("M-SRV:")) { //-> Master to Server Only
                        this.handlerManager.getRegister().register(this, this.executorRunnable, true, this.socket, this, new String((this.decodingManager.decode(line.replaceFirst("M-SRV:", ""))).getBytes(StandardCharsets.UTF_8)), "master");
                    } else if (line.startsWith("R:")) { //-> Master to Specific Agent (or all with : all_agents)
                        this.handlerManager.getWriter().writeToAgent(line);
                    } else if (line.startsWith("A:") || line.startsWith("A-REG:")) { //-> Agent to all Masters
                        if (this.server.getUserManager().getUserData(this.socket) != null) {
                            final String from = line.split(":")[0];
                            this.handlerManager.getWriter().writeToMaster("all_masters", from, line.replaceFirst(from + ":", ""));
                        }
                    }
                }
            } catch (final SocketTimeoutException | SocketException socketException) {
                this.close();
                return;
            } catch (final IOException ioException) {
                ioException.printStackTrace();
                this.close();
                return;
            } finally {
                if (this.socket.isClosed() && this.server.getUserManager().getUserData(this.socket) != null)
                    this.handlerManager.getRemover().remove(this, this.socket, this.server.getUserManager().getUserData(this.socket).getIdentifier(), false);
            }
        }
    }

    private void close() {
        try {
            this.socket.close();
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        }
        super.interrupt();
    }
}