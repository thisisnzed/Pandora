package org.pandora.agent.control.impl;

import java.io.IOException;

public class ComputerStateController {

    public void shutdown() {
        try {
            Runtime.getRuntime().exec("shutdown -s -t 0");
        } catch (final IOException ignore) {
        }
    }

    public void reboot() {
        try {
            Runtime.getRuntime().exec("shutdown -r -t 0");
        } catch (final IOException ignore) {
        }
    }
}
