package org.pandora.master.scene;

import animatefx.animation.FadeIn;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.BasicBuilder;
import org.pandora.master.scene.impl.menu.*;
import org.pandora.master.socket.MSocket;
import org.pandora.master.utils.FileUtils;
import org.pandora.master.utils.Settings;

import java.util.ArrayList;

public class SceneManager {


    @Getter
    private final Stage stage;
    @Getter
    private Master master;
    @Getter private MSocket mSocket;
    private double xOffset = 0;
    private double yOffset = 0;
    @Getter
    private final LoginUI loginUI;
    @Getter
    private final HomeUI homeUI;
    @Getter
    private final AgentsUI agentsUI;
    @Getter
    private final BuilderUI builderUI;
    @Getter
    private final DDoSUI ddosUI;
    @Getter
    private final SettingsUI settingsUI;
    @Getter
    private final FileUtils fileUtils;
    @Getter
    private final ArrayList<Node> connectionInfo;

    public SceneManager(final Stage stage, final Master master) {
        final BuilderManager builderManager = master.getBuilderManager();
        this.stage = stage;
        this.connectionInfo = new ArrayList<>();
        this.master = master;
        this.fileUtils = master.getFileUtils();
        this.loginUI = new LoginUI(this, builderManager);
        this.homeUI = new HomeUI(master, new BasicBuilder(this, builderManager, "Home"));
        this.agentsUI = new AgentsUI(master, new BasicBuilder(this, builderManager, "Agents"));
        this.builderUI = new BuilderUI(master, new BasicBuilder(this, builderManager, "Builder"));
        this.ddosUI = new DDoSUI(master, new BasicBuilder(this, builderManager, "DDoS"));
        this.settingsUI = new SettingsUI(master, new BasicBuilder(this, builderManager, "Settings"));
    }

    public void initialize() {
        this.stage.initStyle(StageStyle.TRANSPARENT);
        this.stage.setTitle("Pandora - Remote Administration Tool");
        this.stage.setHeight(536);
        this.stage.setWidth(914);
        this.stage.setResizable(false);
        this.switchScene(this.loginUI.getScene());
        new FadeIn(this.loginUI.getBorderPane()).play();
        this.loadScenes();
        this.stage.getIcons().add(new Image("images/logo.png"));
        this.stage.show();
    }

    public void initializeRemote() {
        final Stage stage = this.getStage();
        stage.setWidth(1080);
        stage.setHeight(670);

        this.master.startApp = System.currentTimeMillis();

        this.homeUI.addConsoleLine("Trying to connect to " + Settings.IP + ":" + Settings.PORT + "...");

        this.mSocket = new MSocket(this.getMaster(), Settings.IP, Settings.PORT);
        this.mSocket.infiniteConnect();

        this.switchScene(this.homeUI.getScene());
        new FadeIn(this.homeUI.getAnchorPane()).play();
        final Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        stage.setOnCloseRequest(event -> Platform.exit());
    }


    private void loadScenes() {
        this.homeUI.initialize();
        this.agentsUI.initialize();
        this.builderUI.initialize();
        this.ddosUI.initialize();
        this.settingsUI.initialize();
    }

    public void switchScene(final Scene scene) {
        this.stage.setScene(scene);
        scene.getStylesheets().add("css/style.css");
        scene.setOnMousePressed(event -> {
            this.xOffset = this.stage.getX() - event.getScreenX();
            this.yOffset = this.stage.getY() - event.getScreenY();
        });
        scene.setOnMouseDragged(event -> {
            this.stage.setX(event.getScreenX() + this.xOffset);
            this.stage.setY(event.getScreenY() + this.yOffset);
        });
    }
}
