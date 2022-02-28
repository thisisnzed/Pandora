package org.pandora.master.utils;

import org.pandora.master.Master;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class SocketUtils {

    private final Master master;

    public SocketUtils(final Master master) {
        this.master = master;
    }

    public void write(final String id, final String text) {
        if (this.master.getSceneManager().getMSocket().getPrintWriter() == null) return;
        final PrintWriter printWriter = this.master.getSceneManager().getMSocket().getPrintWriter();
        printWriter.println("R:" + new String((this.master.getEncodingManager().encode(id + ":" + text)).getBytes(StandardCharsets.UTF_8)));
        printWriter.flush();
    }
}