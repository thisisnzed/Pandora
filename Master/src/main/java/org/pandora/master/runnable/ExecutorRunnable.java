package org.pandora.master.runnable;

import org.pandora.master.Master;
import org.pandora.master.utils.FileUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorRunnable {

    private final Master master;
    private final FileUtils fileUtils;
    public int requests;

    public ExecutorRunnable(final Master master) {
        this.master = master;
        this.fileUtils = master.getFileUtils();
        this.requests = 0;
    }

    public void start() {
        final Runnable runnable = () -> {
            if (this.requests > 25) {
                if (this.master.getSceneManager() != null && this.master.getSceneManager().getHomeUI() != null && this.master.getSceneManager().getAgentsUI() != null) {
                    if (Boolean.parseBoolean(this.fileUtils.getValue("clientLogs")))
                        this.master.getSceneManager().getHomeUI().addConsoleLine(this.requests + " more agents have been connected to the server!");
                    this.master.getSceneManager().getAgentsUI().updateList(true);
                }
                this.requests = 0;
            }
        };
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);
    }
}
