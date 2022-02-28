package org.pandora.server;

import lombok.Getter;
import org.pandora.server.arguments.Configuration;
import org.pandora.server.arguments.InterceptArguments;
import org.pandora.server.data.UserData;
import org.pandora.server.data.UserManager;
import org.pandora.server.decoding.DecodingManager;
import org.pandora.server.encoding.EncodingManager;
import org.pandora.server.runnable.ExecutorRunnable;
import org.pandora.server.socket.HandlerManager;
import org.pandora.server.socket.TSocket;
import org.pandora.server.utils.TimeUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {

    public final ConcurrentLinkedQueue<UserData> userData;
    @Getter
    private final Configuration configuration;
    @Getter
    private final EncodingManager encodingManager;
    @Getter
    private final DecodingManager decodingManager;
    @Getter
    private final UserManager userManager;
    @Getter
    private final HandlerManager handlerManager;
    @Getter
    public ExecutorRunnable executorRunnable;

    public Server(final String[] args) {
        this.userData = new ConcurrentLinkedQueue<>();
        this.executorRunnable = new ExecutorRunnable(this);
        this.encodingManager = new EncodingManager();
        this.decodingManager = new DecodingManager();
        this.handlerManager = new HandlerManager(this);
        this.userManager = new UserManager(this);
        this.configuration = new Configuration("11111", "false", "127.0.0.1", false);

        final InterceptArguments interceptArguments = new InterceptArguments(this.configuration);
        interceptArguments.setArgs(args);
        interceptArguments.loadArguments();
        interceptArguments.refreshConfiguration();
        interceptArguments.quickCheck();
    }

    public void start() {
        this.executorRunnable.start();
        ServerSocket serverSocket = null;
        Socket socket;
        try {
            int port = Integer.parseInt(this.configuration.getPort());
            serverSocket = new ServerSocket(port);
            System.out.println(TimeUtils.getDate() + "Server started with port " + port + "!");
        } catch (final IOException ioException) {
            ioException.printStackTrace();
            System.out.println(TimeUtils.getDate() + "Server returned error : " + ioException.getMessage() + " | " + ioException.getCause().toString());
        }
        while (true) {
            try {
                socket = Objects.requireNonNull(serverSocket).accept();
                new TSocket(this, socket).start();
            } catch (final IOException ioException) {
                ioException.printStackTrace();
                System.out.println(TimeUtils.getDate() + "Connection error (IO Exception - A) : " + ioException.getMessage() + " | " + ioException.getCause().toString());
            }
        }
    }
}
