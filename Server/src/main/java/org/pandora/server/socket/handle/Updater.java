package org.pandora.server.socket.handle;

import org.pandora.server.Server;
import org.pandora.server.data.UserData;
import org.pandora.server.encoding.EncodingManager;
import org.pandora.server.socket.HandlerManager;

import java.nio.charset.StandardCharsets;

public class Updater {

    private final Server server;
    private final Writer writer;
    private final EncodingManager encodingManager;

    public Updater(final HandlerManager handlerManager, final Server server) {
        this.server = server;
        this.writer = handlerManager.getWriter();
        this.encodingManager = server.getEncodingManager();
    }

    public void updateDashboard() {
        final int countAgent = (int) this.server.userData.stream().filter(userData -> !userData.isMaster()).count();
        final int countMaster = (int) this.server.userData.stream().filter(UserData::isMaster).count();
        this.writer.writeToMaster("all_masters", "S", new String((this.encodingManager.encode("updateDashboard:" + countAgent + ":" + countMaster)).getBytes(StandardCharsets.UTF_8)));
    }

    public void addAgent(final String id, final String data, final String address, final String port) {
        this.writer.writeToMaster("all_masters", "S", new String((this.encodingManager.encode("addAgent:" + address + ":" + port + ":" + this.removeColons(id) + ":" + data)).getBytes(StandardCharsets.UTF_8)));
    }

    public void removeAgent(final String id) {
        this.writer.writeToMaster("all_masters", "S", new String((this.encodingManager.encode("removeAgent:" + id)).getBytes(StandardCharsets.UTF_8)));
    }

    private String removeColons(final String text) {
        return text.replace(":", "");
    }
}
