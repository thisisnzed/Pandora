package org.pandora.master.scene.impl.menu;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.BasicBuilder;
import org.pandora.master.utils.FileUtils;

public class SettingsUI {

    @Getter
    private final BasicBuilder basicBuilder;
    @Getter
    private final Master master;
    @Getter
    private AnchorPane anchorPane;
    @Getter
    private Scene scene;

    public SettingsUI(final Master master, final BasicBuilder basicBuilder) {
        this.basicBuilder = basicBuilder;
        this.master = master;
    }

    public void initialize() {
        final Scene scene = this.basicBuilder.getScene();
        final AnchorPane anchorPane = this.basicBuilder.getAnchorPane();
        final BuilderManager builderManager = this.basicBuilder.getBuilderManager();
        final FileUtils fileUtils = this.getMaster().getFileUtils();

        this.basicBuilder.getSettings().setId("currentNavButton");

        final Label connectionLabel = builderManager.getLabelBuilder().buildLabel("Displays a notification each time an agent connects", 33, 150, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label disconnectionLabel = builderManager.getLabelBuilder().buildLabel("Displays a notification each time an agent disconnects", 33, 190, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label agentsLog = builderManager.getLabelBuilder().buildLabel("Displays in the main menu when an agent logs in or logs out", 33, 230, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label debugModeLabel = builderManager.getLabelBuilder().buildLabel("Automatically switches to debugging mode", 33, 270, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label downloadLabel = builderManager.getLabelBuilder().buildLabel("Download path for documents, images, videos", 33, 310, Color.rgb(163, 164, 175), "Verdana", 15);

        final StackPane connectionSelector = builderManager.getSelectorBuilder().buildSelector(fileUtils, 428, 153, "loginNotification");
        final StackPane disconnectionSelector = builderManager.getSelectorBuilder().buildSelector(fileUtils, 450, 193, "logoutNotification");
        final StackPane agentsSelector = builderManager.getSelectorBuilder().buildSelector(fileUtils, 499, 233, "clientLogs");
        final StackPane debugModeSelector = builderManager.getSelectorBuilder().buildSelector(fileUtils, 363, 273, "debugMode");
        final TextField downloadField = builderManager.getTextFieldBuilder().buildTextField(fileUtils.getValue("download"), "Download Path", 391, 308, 220, 20);
        downloadField.setId("downloadField");

        downloadField.setOnKeyReleased(e -> fileUtils.setValue("download", downloadField.getText()));

        anchorPane.getChildren().addAll(connectionLabel, disconnectionLabel, downloadLabel, agentsLog, debugModeLabel, connectionSelector, disconnectionSelector, agentsSelector, debugModeSelector, downloadField);
        this.anchorPane = anchorPane;
        this.scene = scene;
    }
}
