package org.pandora.master.scene;

public enum SceneType {

    CLIPBOARD("Clipboard"),
    DATA("Agent Data"),
    DESKTOP("Desktop"),
    WEBCAM("Webcam"),
    KEYLOGGER("Keylogger"),
    CHROMEGRABBER("Chrome Stealer"),
    OPERAGRABBER("Opera Stealer"),
    DISCORDGRABBER("Discord Grabber"),
    SHELL("Shell"),
    FILEMANAGER("File Manager"),
    PROCESSMANAGER("Process Manager"),
    RANSOMWARE("Ransomware"),
    DOWNLOADER("Downloader");

    private final String sceneType;

    SceneType(final String sceneType) {
        this.sceneType = sceneType;
    }

    public String getSceneType() {
        return this.sceneType;
    }

}
