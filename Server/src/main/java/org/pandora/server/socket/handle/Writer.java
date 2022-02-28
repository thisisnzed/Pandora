package org.pandora.server.socket.handle;

import org.pandora.server.Server;
import org.pandora.server.data.UserData;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Writer {

    private final Server server;

    public Writer(Server server) {
        this.server = server;
    }

    public void writeToAgent(final String line) {
        final String decoded = new String((this.server.getDecodingManager().decode(line.split(":")[1])).getBytes(StandardCharsets.UTF_8));
        final String text = line.replaceFirst("R:", "");
        final String[] splited = decoded.split(":");
        final String id = splited[0];

        if (id.equals("all_agents")) {
            this.server.userData.stream().filter(agent -> !agent.isMaster()).forEach(userData -> {
                userData.getPrintWriter().println('R' + ":" + text);
                userData.getPrintWriter().flush();
                if (splited[1].equals("restartSocket"))
                    this.server.getHandlerManager().getRemover().remove(null, userData.getSocket(), userData.getIdentifier(), false);
            });
        } else {
            final Optional<UserData> optionalUserData = this.server.userData.stream().filter(u -> u.getIdentifier().equals(id)).findFirst();
            if (optionalUserData.isPresent()) {
                final UserData userData = optionalUserData.get();
                final PrintWriter printWriter = userData.getPrintWriter();
                printWriter.println('R' + ":" + text);
                printWriter.flush();
                if (splited[1].equals("restartSocket"))
                    this.server.getHandlerManager().getRemover().remove(null, userData.getSocket(), userData.getIdentifier(), false);
            }
        }
    }

    public void writeToMaster(final String id, final String from, final String text) {
        if (id.equals("all_masters")) {
            this.server.userData.stream().filter(UserData::isMaster).map(UserData::getPrintWriter).forEach(printWriter -> {
                printWriter.println(from + ":" + text);
                printWriter.flush();
            });
        } else {
            final UserData userData = this.server.getUserManager().getUserData(id);
            if (userData == null) return;
            final PrintWriter printWriter = userData.getPrintWriter();
            printWriter.println(from + ":" + text);
            printWriter.flush();
        }
    }
}