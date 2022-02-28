package org.pandora.agent.runnable;

import org.pandora.agent.socket.connection.Connection;

import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorRunnable {

    private final Connection connection;

    public ExecutorRunnable(final Connection connection) {
        this.connection = connection;
    }

    public void start() {
        final Runnable runnable = () -> {
            if (this.connection.isConnected()) {
                if (this.connection.getPrintWriter() != null) {
                    final PrintWriter printWriter = this.connection.getPrintWriter();
                    printWriter.println("sendActivity");
                    printWriter.flush();
                }
            }
        };
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 0, 45, TimeUnit.SECONDS);
    }
}
