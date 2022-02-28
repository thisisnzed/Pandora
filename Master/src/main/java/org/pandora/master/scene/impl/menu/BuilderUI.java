package org.pandora.master.scene.impl.menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import lombok.Getter;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.BasicBuilder;
import org.pandora.master.utils.NumberUtils;

import java.io.File;
import java.io.IOException;

public class BuilderUI {

    @Getter
    private final BasicBuilder basicBuilder;
    @Getter
    private final Master master;
    @Getter
    private AnchorPane anchorPane;
    @Getter
    private Scene scene;
    private final ObservableList<String> jdkData;

    public BuilderUI(final Master master, final BasicBuilder basicBuilder) {
        this.basicBuilder = basicBuilder;
        this.master = master;
        this.jdkData = FXCollections.observableArrayList();
    }

    public void initialize() {
        final Scene scene = this.basicBuilder.getScene();
        final AnchorPane anchorPane = this.basicBuilder.getAnchorPane();
        final BuilderManager builderManager = this.basicBuilder.getBuilderManager();

        this.basicBuilder.getBuilder().setId("currentNavButton");

        final Label hostLabel = builderManager.getLabelBuilder().buildLabel("Host (IP/DNS)", 33, 150, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label portLabel = builderManager.getLabelBuilder().buildLabel("Port", 33, 190, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label autoStartLabel = builderManager.getLabelBuilder().buildLabel("Toggle OS Auto Start", 33, 230, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label downloadBuildLabel = builderManager.getLabelBuilder().buildLabel("Allow to build agent with other file", 33, 270, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label downloadLinkBuildLabel = builderManager.getLabelBuilder().buildLabel("Direct download link of the other file", 33, 310, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label archivePathLabel = builderManager.getLabelBuilder().buildLabel("Archive file path", 33, 350, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label jdkLabel = builderManager.getLabelBuilder().buildLabel("(Optional) Choose your JDK", 33, 390, Color.rgb(163, 164, 175), "Verdana", 15);
        final Label lastBuild = builderManager.getLabelBuilder().buildLabel("There is no recent build", 4, 556, Color.rgb(163, 164, 175), "Consolas", 12);

        final TextField hostField = builderManager.getTextFieldBuilder().buildTextField("", "127.0.0.1", 155, 147, 120, 20);
        final TextField portField = builderManager.getTextFieldBuilder().buildTextField("", "5907", 79, 187, 70, 20);
        final CheckBox autoStartBox = builderManager.getCheckBoxBuilder().buildCheckBox(207, 230);
        final CheckBox downloadBuildBox = builderManager.getCheckBoxBuilder().buildCheckBox(307, 270);
        final TextField downloadLinkBuildField = builderManager.getTextFieldBuilder().buildTextField("", "https://cdn.discordapp.com/attachments/xxx/xxx/xxx.jar", 323, 307, 315, 20);
        final TextField archivePathField = builderManager.getTextFieldBuilder().buildTextField("", "Folder 1/Folder 2/file.txt", 169, 347, 215, 20);
        final ChoiceBox<?> jdkChoice = builderManager.getChoiceBoxBuilder().buildChoiceBox(this.jdkData, 259, 387, 260, 32);

        this.searchJDKs("Program Files", "Program Files (x86)");

        autoStartBox.setSelected(true);
        downloadBuildBox.setSelected(true);

        hostField.setId("builderField");
        portField.setId("builderField");
        downloadLinkBuildField.setId("builderField");
        archivePathField.setId("builderField");
        autoStartBox.setId("builderBox");
        downloadBuildBox.setId("builderBox");
        jdkChoice.setId("jdkChoice");

        final Button buildButton = builderManager.getButtonBuilder().buildButton(null,"Build Agent", 33, 440, "WHITE", "Verdana", 13, 165, 34, "build-agent");
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));

        buildButton.setOnMouseClicked(event -> {
            if (hostField.getText().equals("") || portField.getText() == null || !NumberUtils.isInteger(portField.getText())) {
                lastBuild.setText("Error while trying to build the last time, please check the followings fields : 'host' and 'port'");
                return;
            }
            buildButton.setDisable(true);
            final File selectedDirectory = directoryChooser.showDialog(this.getMaster().getSceneManager().getStage());
            if (selectedDirectory == null || !selectedDirectory.exists()) {
                lastBuild.setText("Error while trying to build the last time, please check the export folder");
                buildButton.setDisable(false);
                return;
            }
            lastBuild.setText("Building...");
            if (jdkChoice.getValue() != null) {
                try {
                    Runtime.getRuntime().exec("setx PATH \"" + jdkChoice.getValue() + "\\bin;%PATH%\"");
                } catch (IOException e) {
                    lastBuild.setText("Error while trying to set JDK : " + e.getMessage());
                    e.printStackTrace();
                }
            }
            this.getMaster().getExport().export(buildButton, selectedDirectory, lastBuild, hostField.getText(), Integer.parseInt(portField.getText()), autoStartBox.isSelected(), downloadBuildBox.isSelected(), downloadLinkBuildField.getText(), archivePathField.getText());
        });

        anchorPane.getChildren().addAll(jdkChoice, jdkLabel, lastBuild, buildButton, autoStartBox, downloadBuildBox, portField, downloadLinkBuildField, archivePathField, hostField, hostLabel, portLabel, autoStartLabel, downloadBuildLabel, archivePathLabel, downloadLinkBuildLabel);
        this.anchorPane = anchorPane;
        this.scene = scene;
    }

    private void searchJDKs(final String... programDir) {
        for (final String dir : programDir) {
            final File dirProgram = new File("C:\\" + dir + "\\");
            final String[] programFiles = dirProgram.list();
            if (programFiles == null) return;
            for (final String programFileName : programFiles)
                if (programFileName.toLowerCase().startsWith("java")) {
                    final File javaFolder = new File(dirProgram.getPath() + "\\" + programFileName);
                    final String[] javaFiles = javaFolder.list();
                    if (javaFiles == null) return;
                    for (final String javaFileName : javaFiles)
                        if (javaFileName.startsWith("jdk"))
                            this.jdkData.add(javaFolder.getPath() + "\\" + javaFileName);
                }
        }
    }
}
