package org.pandora.master.scene.impl.menu;

import animatefx.animation.FadeOut;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.scene.SceneManager;
import org.pandora.master.utils.FileUtils;
import org.pandora.master.utils.NumberUtils;
import org.pandora.master.utils.Settings;

public class LoginUI {

    @Getter
    private final Scene scene;
    @Getter
    private final BorderPane borderPane;

    public LoginUI(final SceneManager sceneManager, final BuilderManager builderManager) {
        final AnchorPane anchorPane = new AnchorPane();
        final BorderPane borderPane = new BorderPane();

        final FileUtils fileUtils = sceneManager.getFileUtils();

        final TextField address = builderManager.getTextFieldBuilder().buildTextField(fileUtils.getValue("serverAddress").equals("127.0.0.1") ? "" : fileUtils.getValue("serverAddress"), "Address", 505, 190, 349, 40);
        final TextField port = builderManager.getTextFieldBuilder().buildTextField(fileUtils.getValue("serverPort").equals("1234") ? "" : fileUtils.getValue("serverPort"), "Port", 505, 275, 349, 40);
        final Button button = builderManager.getButtonBuilder().buildButton(null, "Sign In", 580, 365, "WHITE", "Verdana", 12, 170, 45, "login-button");
        final Label error = builderManager.getLabelBuilder().buildLabel("", 450, 516, "RED", "Verdana", 12);
        final ImageView minimize = builderManager.getImageViewBuilder().buildImageView("images/window/minimize/minimize.png", 30, 10, 14, 14);
        final ImageView close = builderManager.getImageViewBuilder().buildImageView("images/window/close/close.png", 10, 10, 14, 14);

        minimize.setOnMouseClicked(event -> sceneManager.getStage().setIconified(true));
        minimize.setOnMouseEntered(event -> minimize.setCursor(Cursor.HAND));
        minimize.setOnMouseExited(event -> minimize.setCursor(Cursor.DEFAULT));

        close.setOnMouseClicked(event -> {
            new FadeOut(borderPane).play();
            System.exit(0);
        });
        close.setOnMouseEntered(event -> close.setCursor(Cursor.HAND));
        close.setOnMouseExited(event -> close.setCursor(Cursor.DEFAULT));

        address.setId("login-field");
        port.setId("login-field");

        this.buttonClick(sceneManager, button, address, port, error, fileUtils);

        borderPane.setBackground(new Background(new BackgroundImage(new Image("images/background/connection.png", 914, 536, false, true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        anchorPane.getChildren().addAll(address, port, button, error, close, minimize);
        borderPane.getChildren().add(anchorPane);

        final Scene scene = new Scene(borderPane);
        scene.getStylesheets().add("css/style.css");
        scene.setFill(Color.TRANSPARENT);

        this.borderPane = borderPane;
        this.scene = scene;
    }

    private void buttonClick(final SceneManager sceneManager, final Button button, final TextField addressField, final TextField portField, final Label error, final FileUtils fileUtils) {
        button.setOnMouseClicked(event -> {
            String address = addressField.getText();
            String port = portField.getText();
            if (address.equals("") || port.equals("") || !NumberUtils.isInteger(port)) {
                error.setTextFill(Color.rgb(221, 68, 95));
                error.setText("Error : please complete all fields correctly");
            } else {
                error.setTextFill(Color.DARKGREEN);
                error.setText("Waiting for master...");
                error.setDisable(true);
                portField.setEditable(false);
                addressField.setEditable(false);
                button.setDisable(true);
                fileUtils.setValue("serverPort", port);
                fileUtils.setValue("serverAddress", address);
                Settings.IP = address;
                Settings.PORT = port;
                new Thread(() -> {
                    try {
                        Thread.sleep(600L);
                    } catch (InterruptedException ignore) {
                    }
                    Platform.runLater(sceneManager::initializeRemote);
                }).start();
            }
        });
    }
}
