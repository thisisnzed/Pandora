package org.pandora.master.builder.impl.scene;

import animatefx.animation.FadeOut;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.scene.SceneType;

import java.util.ArrayList;

public class ControllerBuilder {

    @Getter
    private final ArrayList<String> waiting;
    @Getter
    private final Master master;
    @Getter
    private final AnchorPane anchorPane;
    @Getter
    private final Scene scene;
    @Getter
    private final Stage stage;
    @Getter
    private final BuilderManager builderManager;
    @Getter
    private final AgentData agentData;
    @Getter
    private final SceneType sceneType;
    private double xOffset = 0;
    private double yOffset = 0;
    public int screen;

    public ControllerBuilder(final SceneType sceneType, final String title, final BuilderManager builderManager, final AgentData agentData, final Master master, final int width, final int height) {
        final AnchorPane anchorPane = new AnchorPane();
        final Scene scene = new Scene(anchorPane);

        this.screen = -1;
        this.stage = new Stage();
        this.waiting = new ArrayList<>();
        this.agentData = agentData;
        this.master = master;
        this.sceneType = sceneType;

        scene.setFill(Color.TRANSPARENT);

        final ImageView minimize = builderManager.getImageViewBuilder().buildImageView("images/window/minimize/orange.png", width - 43, 8, 14, 14);
        final ImageView close = builderManager.getImageViewBuilder().buildImageView("images/window/close/red.png", width - 23, 8, 14, 14);

        minimize.setOnMouseClicked(event -> this.stage.setIconified(true));
        close.setOnMouseClicked(event -> {
            new FadeOut(anchorPane).play();
            this.stage.close();
            this.master.getStageManager().getControllers().remove(this);
            this.master.getStageManager().getScenes().remove(this);
            if (sceneType.equals(SceneType.KEYLOGGER))
                this.master.getSocketUtils().write(agentData.getId(), "keylogger:stop");
            if (sceneType.equals(SceneType.WEBCAM))
                this.master.getSocketUtils().write(agentData.getId(), "webcam:stop");
            if (sceneType.equals(SceneType.DESKTOP))
                this.master.getSocketUtils().write(agentData.getId(), "desktop:state:stop:" + this.screen);
        });
        minimize.setOnMouseEntered(event -> minimize.setCursor(Cursor.HAND));
        minimize.setOnMouseExited(event -> minimize.setCursor(Cursor.DEFAULT));
        close.setOnMouseEntered(event -> close.setCursor(Cursor.HAND));
        close.setOnMouseExited(event -> close.setCursor(Cursor.DEFAULT));

        anchorPane.getChildren().addAll(minimize, close);

        this.initialize(title, scene, width, height);

        this.builderManager = builderManager;
        this.anchorPane = anchorPane;
        this.scene = scene;
    }

    private void initialize(final String title, final Scene scene, final int width, final int height) {
        this.master.getStageManager().getControllers().put(this, this.agentData.getId());
        this.stage.setWidth(width);
        this.stage.setHeight(height);

        final Rectangle2D rectangle2D = Screen.getPrimary().getVisualBounds();
        this.stage.setX((rectangle2D.getWidth() - this.stage.getWidth()) / 2);
        this.stage.setY((rectangle2D.getHeight() - this.stage.getHeight()) / 2);

        scene.getStylesheets().add("css/style.css");
        scene.setOnMousePressed(event -> {
            this.xOffset = this.stage.getX() - event.getScreenX();
            this.yOffset = this.stage.getY() - event.getScreenY();
        });
        scene.setOnMouseDragged(event -> {
            this.stage.setX(event.getScreenX() + this.xOffset);
            this.stage.setY(event.getScreenY() + this.yOffset);
        });
        scene.setFill(Color.TRANSPARENT);

        this.stage.setTitle(title + " [" + this.agentData.getUser() + "@" + this.agentData.getComputer() + "]");
        this.stage.getIcons().add(new Image("images/logo.png"));
        this.stage.initStyle(StageStyle.TRANSPARENT);
        this.stage.setScene(scene);
        this.stage.show();
    }
}
