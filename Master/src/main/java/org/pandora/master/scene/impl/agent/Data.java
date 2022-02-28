package org.pandora.master.scene.impl.agent;


import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.utils.NumberUtils;

public class Data {

    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    private final AgentData agentData;

    public Data(final ControllerBuilder controllerBuilder) {
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
    }

    public void open() {
        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Agent Data | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 52, 28, Color.rgb(230, 230, 230), "Consolas", 17);
        final Line line = this.builderManager.getBasicLineBuilder().buildSimpleLine(Color.WHITE, 40, 75, 610, 75);

        final Label addressLabel = this.builderManager.getLabelBuilder().buildLabel("Address : " + this.agentData.getAddress(), 52, 95, Color.rgb(189, 190, 207), "Helvetica", 14);
        final Label portLabel = this.builderManager.getLabelBuilder().buildLabel("Port : " + this.agentData.getPort(), 52, 129, Color.rgb(189, 190, 207), "Helvetica", 14);
        final Label userLabel = this.builderManager.getLabelBuilder().buildLabel("User : " + this.agentData.getUser(), 52, 95 + 34 * 2, Color.rgb(189, 190, 207), "Helvetica", 14);
        final Label computerLabel = this.builderManager.getLabelBuilder().buildLabel("Computer : " + this.agentData.getComputer(), 52, 95 + 34 * 3, Color.rgb(189, 190, 207), "Helvetica", 14);
        final Label osLabel = this.builderManager.getLabelBuilder().buildLabel("Full OS Version : " + this.agentData.getOs(), 52, 95 + 34 * 4, Color.rgb(189, 190, 207), "Helvetica", 14);
        final Label macLabel = this.builderManager.getLabelBuilder().buildLabel("MAC Address : " + this.agentData.getMac(), 52, 95 + 34 * 5, Color.rgb(189, 190, 207), "Helvetica", 14);
        final Label langLabel = this.builderManager.getLabelBuilder().buildLabel("Lang : " + this.agentData.getLang(), 52, 95 + 34 * 6, Color.rgb(189, 190, 207), "Helvetica", 14);
        final Label modeLabel = this.builderManager.getLabelBuilder().buildLabel("Start Mode : " + this.getMethod(this.agentData.getMode()), 52, 95 + 34 * 7, Color.rgb(189, 190, 207), "Helvetica", 14);
        final Label idLabel = this.builderManager.getLabelBuilder().buildLabel("Private Unique ID : " + this.agentData.getId(), 52, 95 + 34 * 8, Color.rgb(189, 190, 207), "Helvetica", 14);

        this.anchorPane.getChildren().addAll(defaultLabel, line, addressLabel, portLabel, userLabel, computerLabel, osLabel, macLabel, langLabel, modeLabel, idLabel);
    }

    private String getMethod(final String methodInt) {
        if (!NumberUtils.isInteger(methodInt))
            return methodInt;
        final int method = Integer.parseInt(methodInt);
        switch (method) {
            case 0:
                return "by clicking file";
            case 1:
                return "by starting OS";
            case 2:
                return "by launching back (after opening the agent)";
            case 3:
                return "by updating to replace old file by the new";
            case 4:
                return "by updating & after replacement";
            default:
                return methodInt;
        }
    }
}
