package org.pandora.master.scene.impl.agent;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.utils.TimeUtils;

import java.util.ArrayList;

public class Shell {

    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    private final AgentData agentData;
    private final Scene scene;
    private final ArrayList<String> waiting;
    private final Master master;
    private Text logText;
    private ScrollPane scrollPane;
    private final ControllerBuilder controllerBuilder;

    public Shell(final ControllerBuilder controllerBuilder) {
        this.controllerBuilder = controllerBuilder;
        this.master = controllerBuilder.getMaster();
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
        this.waiting = controllerBuilder.getWaiting();
        this.scene = controllerBuilder.getScene();
    }

    public void open() {
        this.master.getStageManager().getScenes().put(this.controllerBuilder, this);
        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Remote Shell | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 15, 20, Color.rgb(230, 230, 230), "Consolas", 17);
        final Line line = this.builderManager.getBasicLineBuilder().buildSimpleLine(Color.WHITE, 50, 48, 675, 48);

        final Text logText = this.builderManager.getTextBuilder().buildText(this.getBase() + "Welcome to my shell!", 0, 0, Color.WHITE, "Consolas", 12);

        final ScrollPane scrollPane = this.builderManager.getScrollPaneBuilder().buildScrollPane(true, logText, 15, 60, 695, 350);
        logText.wrappingWidthProperty().bind(this.scene.widthProperty());

        final javafx.scene.control.TextField commandField = this.builderManager.getTextFieldBuilder().buildTextField("", "Write an command to send to the agent", 15, 420, 545, 25);
        final Button sendButton = this.builderManager.getButtonBuilder().buildButton(null, "Run", 660, 420, Color.RED, "Verdana", 12, 50, 25, "agents-shell-execute");
        final Button addUserButton = this.builderManager.getButtonBuilder().buildButton(null, "Add User", 565, 420, Color.RED, "Verdana", 12, 90, 25, "agents-shell-addUser");

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setId("agents-shell-scrollPane");
        commandField.setId("agents-shell-commandField");

        commandField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) this.sendRequest(commandField);
        });
        sendButton.setOnAction(event -> this.sendRequest(commandField));
        addUserButton.setOnAction(event -> commandField.setText("useradd vm --group sudo; echo \"vm:p4ssw0rd96\" | chpasswd"));

        this.logText = logText;
        this.scrollPane = scrollPane;
        this.anchorPane.getChildren().addAll(defaultLabel, line, scrollPane, commandField, sendButton, addUserButton);
    }

    private void sendRequest(final TextField textField) {
        final String id = TimeUtils.getRandomId();
        String text = textField.getText();
        if (!text.equals("")) {
            textField.setText("");
            this.logText.setText(this.logText.getText() + this.getBase() + text.replace("(doubleDot)", ":"));
            text = text.replace(":", "(doubleDot)");
            this.waiting.add(id);
            this.master.getSocketUtils().write(this.agentData.getId(), "shell:" + id + ":" + text);
        }
    }

    public void receiveRequest(final String id, final String response) {
        if (this.waiting.contains(id)) {
            this.waiting.remove(id);
            this.logText.setText(this.logText.getText() + "\n" + response.replace("(doubleDot)", ":").replace("(NEWLINE)", "\n"));
            this.scrollPane.setVvalue(1.0d);
        }
    }

    private String getBase() {
        return "\n " + this.agentData.getUser() + "@" + this.agentData.getComputer() + "$ ";
    }
}
